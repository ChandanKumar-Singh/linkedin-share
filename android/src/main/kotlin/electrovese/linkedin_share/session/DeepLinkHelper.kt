package electrovese.linkedin_share.session

import  android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.annotation.NonNull
import electrovese.linkedin_share.session.errors.LIAppErrorCode
import electrovese.linkedin_share.session.errors.LIDeepLinkError
import electrovese.linkedin_share.session.internals.AppStore
import electrovese.linkedin_share.session.internals.LIAppVersion
import electrovese.linkedin_share.session.listeners.DeepLinkListener

/**
 * DeepLinkHelper enables linking to pages within the LinkedIn application
 */
class DeepLinkHelper private constructor() {

    companion object {
        const val LI_SDK_CROSSLINK_REQUEST_CODE = 13287
        private const val CURRENTLY_LOGGED_IN_MEMBER = "you"
        private const val DEEPLINK_ERROR_CODE_EXTRA_NAME = "com.linkedin.thirdparty.deeplink.EXTRA_ERROR_CODE"
        private const val DEEPLINK_ERROR_MESSAGE_EXTRA_NAME = "com.linkedin.thirdparty.deeplink.EXTRA_ERROR_MESSAGE"
        private var deepLinkHelper: DeepLinkHelper? = null

        @JvmStatic
        fun getInstance(): DeepLinkHelper {
            if (deepLinkHelper == null) {
                deepLinkHelper = DeepLinkHelper()
            }
            return deepLinkHelper!!
        }
    }

    private var deepLinkListener: DeepLinkListener? = null

    /**
     * opens up a view which shows the profile of the user that is currently logged in to the
     * LinkedIn app.
     * @param activity
     * @param callback
     */
    fun openCurrentProfile(@NonNull activity: Activity, callback: DeepLinkListener) {
        openOtherProfile(activity, CURRENTLY_LOGGED_IN_MEMBER, callback)
    }

    /**
     * opens a view which shows the profile of the given member
     * @param activity
     * @param memberId obtained through an api call
     * @param callback
     */
    private fun openOtherProfile(@NonNull activity: Activity, memberId: String, callback: DeepLinkListener) {
        this.deepLinkListener = callback

        val session = LISessionManager.getInstance(activity.applicationContext).session
        if (!session.isValid()) {
            callback.onDeepLinkError(LIDeepLinkError(LIAppErrorCode.NOT_AUTHENTICATED, "there is no access token"))
            return
        }
        try {
            if (!LIAppVersion.isLIAppCurrent(activity)) {
                AppStore.goAppStore(activity, true)
                return
            }
            deepLinkToProfile(activity, memberId, session.getAccessToken())
        } catch (e: ActivityNotFoundException) {
            callback.onDeepLinkError(LIDeepLinkError(LIAppErrorCode.LINKEDIN_APP_NOT_FOUND,
                    "LinkedIn app needs to be either installed or updated"))
            deepLinkListener = null
        }
    }

    private fun deepLinkToProfile(@NonNull activity: Activity, memberId: String, @NonNull accessToken: AccessToken) {
        val intent = Intent(Intent.ACTION_VIEW)
        val uriBuilder = Uri.Builder().apply {
            scheme("linkedin")
            authority(if (CURRENTLY_LOGGED_IN_MEMBER == memberId) CURRENTLY_LOGGED_IN_MEMBER else "profile")
            if (CURRENTLY_LOGGED_IN_MEMBER != memberId) appendPath(memberId)
            appendQueryParameter("accessToken", accessToken.accessTokenValue)
            appendQueryParameter("src", "sdk")
        }
        intent.data = uriBuilder.build()
        activity.startActivityForResult(intent, LI_SDK_CROSSLINK_REQUEST_CODE)
    }

    /**
     * call this method in your activity's onActivityResult method.
     * Handles any response code from LinkedIn and calls the DeepLinkListener callback
     * @param activity
     * @param requestCode
     * @param resultCode
     * @param data
     */
    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == LI_SDK_CROSSLINK_REQUEST_CODE && deepLinkListener != null) {
            if (resultCode == Activity.RESULT_OK) {
                deepLinkListener!!.onDeepLinkSuccess()
            } else if (resultCode == Activity.RESULT_CANCELED) {
                if (data == null || data.extras == null) {
                    deepLinkListener!!.onDeepLinkError(LIDeepLinkError(LIAppErrorCode.USER_CANCELLED, ""))
                } else {
                    val errorMessage = data.extras!!.getString(DEEPLINK_ERROR_MESSAGE_EXTRA_NAME)
                    val errorCode = data.extras!!.getString(DEEPLINK_ERROR_CODE_EXTRA_NAME)
                    deepLinkListener!!.onDeepLinkError(LIDeepLinkError(errorCode.toString(), errorMessage))
                }
            }
        }
    }
}
