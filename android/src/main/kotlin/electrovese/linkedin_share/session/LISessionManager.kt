package electrovese.linkedin_share.session

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import electrovese.linkedin_share.session.errors.LIAppErrorCode
import electrovese.linkedin_share.session.errors.LIAuthError
import electrovese.linkedin_share.session.internals.AppStore
import electrovese.linkedin_share.session.internals.LIAppVersion
import electrovese.linkedin_share.session.listeners.AuthListener
import electrovese.linkedin_share.session.utils.Scope
import electrovese.linkedin_share.session.AccessToken

/**
 * LISessionManager manages the authorizations needed for an application
 * to access LinkedIn data and to view LinkedIn profiles.
 *
 * A typical usage flow is:
 *
 * LISessionManager.init(activity, Scope.build(LIPermission.R_BASICPROFILE), callback, true);
 *
 * When callback.onAuthSuccess() is called, calls to {@link electrovese.linkedin_share.session.APIHelper}
 * to retrieve LinkedIn data or calls to {@link electrovese.linkedin_share.session.DeepLinkHelper} can be
 * made to view LinkedIn profiles.
 * {@link electrovese.linkedin_share.session.LISession#isValid()} should be used to validate the session before
 * making API calls or deep link calls.
 */
class LISessionManager private constructor() {

    private val TAG = LISessionManager::class.java.simpleName
    private val LI_SDK_AUTH_REQUEST_CODE = 3672
    private val AUTH_TOKEN = "token"
    private val AUTH_STATE = "state"
    private val LI_APP_PACKAGE_NAME = "com.linkedin.android"
    private val LI_APP_AUTH_CLASS_NAME = "com.linkedin.android.liauthlib.thirdparty.LiThirdPartyAuthorizeActivity"
    private val SCOPE_DATA = "com.linkedin.thirdpartysdk.SCOPE_DATA"
    private val LI_APP_ACTION_AUTHORIZE_APP = "com.linkedin.android.auth.AUTHORIZE_APP"
    private val LI_APP_CATEGORY = "com.linkedin.android.auth.thirdparty.authorize"
    private val LI_ERROR_INFO = "com.linkedin.thirdparty.authorize.RESULT_ACTION_ERROR_INFO"
    private val LI_ERROR_DESCRIPTION = "com.linkedin.thirdparty.authorize.RESULT_ACTION_ERROR_DESCRIPTION"

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var instance: LISessionManager? = null

        fun getInstance(context: Context): LISessionManager {
            return instance ?: synchronized(this) {
                instance ?: LISessionManager().also { manager ->
                    manager.ctx = context.applicationContext
                    instance = manager
                }
            }
        }
    }

    private lateinit var ctx: Context
    public val session = LISessionImpl()
    private var authListener: AuthListener? = null

    /**
     * Initializes LISession using previously obtained AccessToken.
     * The passed in access token should be one that was obtained from the LinkedIn Mobile SDK.
     *
     * @param accessToken access token
     */
    fun init(accessToken: AccessToken) {
        session.setAccessToken(accessToken)
    }

    /**
     * Brings the user to an authorization screen which allows the user to authorize
     * the application to access their LinkedIn data. When the user authorizes the application
     * {@link electrovese.linkedin_share.session.listeners.AuthListener#onAuthSuccess()} is called.
     * If the user has previously authorized the application, onAuthSuccess will be called without
     * the authorization screen being shown.
     *
     * If there is no user logged into the LinkedIn application, the user will be prompted to login
     * to LinkedIn, after which the authorization screen will be shown.
     *
     * Either this method or {@link electrovese.linkedin_share.session.LISessionManager#init(AccessToken)} must be
     * called before the application can make API calls or DeepLink calls.
     *
     * @param activity               Activity to return to after initialization
     * @param scope                  The type of LinkedIn data that for which access is requested.
     *                               see {@link electrovese.linkedin_share.session.utils.Scope}
     * @param callback               Listener to execute on completion
     * @param showGoToAppStoreDialog Determines behaviour when the LinkedIn app is not installed
     *                               if true, a dialog is shown which prompts the user to install
     *                               the LinkedIn app via the app store. If false, the user is
     *                               taken directly to the app store.
     */
    fun init(activity: Activity, scope: Scope, callback: AuthListener, showGoToAppStoreDialog: Boolean) {
        /// Check if LinkedIn app version is current
        Log.d(TAG, "Checking LinkedIn app version... ")
        if (!LIAppVersion.isLIAppCurrent(ctx)) {
            Log.d(TAG, "LinkedIn app version is not current")
            AppStore.goAppStore(activity, showGoToAppStoreDialog)
            return
        }
        authListener = callback
        val intent = Intent().apply {
            setClassName(LI_APP_PACKAGE_NAME, LI_APP_AUTH_CLASS_NAME)
            putExtra(SCOPE_DATA, scope.createScope())
            action = LI_APP_ACTION_AUTHORIZE_APP
            addCategory(LI_APP_CATEGORY)
        }
        try {
            Log.d(TAG, "Starting authorization activity... with intent $intent")
            activity.startActivityForResult(intent, LI_SDK_AUTH_REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            Log.d(TAG, e.message ?: "Activity not found")
        }
    }

    /**
     * This method must be called in the calling Activity's onActivityResult in order to
     * process the response to
     * {@link electrovese.linkedin_share.session.LISessionManager#init(android.app.Activity, electrovese.linkedin_share.session.utils.Scope, electrovese.linkedin_share.session.listeners.AuthListener, boolean)}
     *
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG,
            "onActivityResult called 📍 with requestCode $requestCode and result $resultCode \ndata $data"
        )

        if (authListener != null && requestCode == LI_SDK_AUTH_REQUEST_CODE) {
            when (resultCode) {
                Activity.RESULT_OK -> {
                    val token = data?.getStringExtra(AUTH_TOKEN)
                    val expiresOn = data?.getLongExtra("expiresOn", 0L) ?: 0L
                    val accessToken = AccessToken(token ?: "", expiresOn)
                    init(accessToken)
                    authListener?.onAuthSuccess()
                }
                Activity.RESULT_CANCELED -> {
                    authListener?.onAuthError(LIAuthError(LIAppErrorCode.USER_CANCELLED, "User canceled"))
                }
                else -> {
                    val errorInfo = data?.getStringExtra(LI_ERROR_INFO)
                    val errorDesc = data?.getStringExtra(LI_ERROR_DESCRIPTION)
                    authListener?.onAuthError(LIAuthError(errorInfo ?: "Unknown error", errorDesc ?: ""))
                }
            }
            authListener = null
        }
    }

    /**
     * @return the LISession
     */
    fun getSession(): LISession {
        return session
    }

    /**
     * Clears the session. Calls to retrieve LinkedIn data or to view profiles will no longer
     * work.
     */
    fun clearSession() {
        session.setAccessToken(null)
    }

    /**
     * Builds scope based on List of permissions.
     *
     * @param perms List of permissions
     * @return Comma-separated permissions
     */
    private fun createScope(perms: List<String>?): String {
        return if (perms.isNullOrEmpty()) {
            ""
        } else {
            perms.joinToString(" ")
        }
    }

    /**
     * Private implementation of LISession
     * Takes care of saving and restoring session to/from shared preferences
     */
    inner class LISessionImpl : LISession {

        private val LI_SDK_SHARED_PREF_STORE = "li_shared_pref_store"
        private val SHARED_PREFERENCES_ACCESS_TOKEN = "li_sdk_access_token"
        private var accessToken: AccessToken? = null

        override fun getAccessToken(): AccessToken {
            if (accessToken == null) {
                recover()
            }
            return accessToken ?: AccessToken("", 0L) // Return a default value if null
        }

        fun setAccessToken(accessToken: AccessToken?) {
            this.accessToken = accessToken
            save()
        }

        /**
         * @return true if a valid accessToken is present. Note that if the member revokes
         * access to this application, this will still return true
         */
        override fun isValid(): Boolean {
            val at = getAccessToken()
            return at.accessTokenValue.isNotEmpty() && !at.isExpired()
        }

        /**
         * Clears session. (Kills it)
         */
        fun clear() {
            setAccessToken(null)
        }

        /**
         * Storage
         */
        private fun getSharedPref(): SharedPreferences {
            return ctx.getSharedPreferences(LI_SDK_SHARED_PREF_STORE, Context.MODE_PRIVATE)
        }

        private fun save() {
            val edit = getSharedPref().edit()
            edit.putString(SHARED_PREFERENCES_ACCESS_TOKEN, accessToken?.toString())
            edit.apply() // Use apply for asynchronous saving
        }

        private fun recover() {
            val sharedPref = getSharedPref()
            val accessTokenStr = sharedPref.getString(SHARED_PREFERENCES_ACCESS_TOKEN, null)
            accessToken = accessTokenStr?.let { AccessToken.buildAccessToken(it) }
        }
    }
}
