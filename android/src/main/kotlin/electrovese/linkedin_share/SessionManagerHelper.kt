package electrovese.linkedin_share

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.util.Base64
import android.util.Log
import android.widget.Toast
import electrovese.linkedin_share.session.LISessionManager
import electrovese.linkedin_share.session.errors.LIAuthError
import electrovese.linkedin_share.session.listeners.AuthListener
import electrovese.linkedin_share.session.utils.Scope
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/// TODO : 
/// cliend id : 86nsaapxu7q3ko
/// Primary Client Secret: WPL_AP1.jsytar3eHDn4j0k1.l7mmVg==
/// 

class SessionManagerHelper(private val activity: Activity) : MethodChannel.MethodCallHandler {

    private val TAG = SessionManagerHelper::class.java.simpleName
    private lateinit var channel: MethodChannel

    // Initialize the plugin
    fun init(messenger: BinaryMessenger) {
        channel = MethodChannel(messenger, "linkedin_session")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "login" -> initiateLogin(result)
            "logout" -> logout(result)
            "showPackageHash" -> showPackageHash(result)
            else -> result.notImplemented()
        }
    }

    private fun initiateLogin(result: MethodChannel.Result) {
        // Now using Activity context directly
        LISessionManager.getInstance(activity).init(
            activity,
            buildScope(),
            object : AuthListener {
                override fun onAuthSuccess() {
                    Log.d(TAG, "Authorization successful")
                    result.success(getAccessToken())
                    Toast.makeText(activity, "Login Successful", Toast.LENGTH_LONG).show()
                }

                override fun onAuthError(error: LIAuthError) {
                    Log.d(TAG, "Authorization error: ${error.toString()} - ${error.errorMsg}")
                    result.error(error.errorCode.toString(), error.errorMsg, null)
                    Toast.makeText(activity, "Login Failed: ${error.errorMsg}", Toast.LENGTH_LONG).show()
                }
            },
            true
        )
    }

    private fun logout(result: MethodChannel.Result) {
        LISessionManager.getInstance(activity).clearSession()
        result.success("Logged out successfully")
        Toast.makeText(activity, "Logged out", Toast.LENGTH_SHORT).show()
    }

    private fun showPackageHash(result: MethodChannel.Result) {
        try {
            val packageInfo: PackageInfo = activity.packageManager.getPackageInfo(
                activity.packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in packageInfo.signatures) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hash = Base64.encodeToString(md.digest(), Base64.NO_WRAP)
                Log.d(TAG, "Package Hash: $hash")
                result.success(hash)
                break // Only return the first signature hash
            }
        } catch (e: PackageManager.NameNotFoundException) {
            Log.d(TAG, e.message.toString(), e)
            result.error("PACKAGE_NOT_FOUND", "Package not found", null)
        } catch (e: NoSuchAlgorithmException) {
            Log.d(TAG, e.message.toString(), e)
            result.error("HASH_ERROR", "Hash algorithm not found", null)
        }
    }

    private fun getAccessToken(): String {
        return if (LISessionManager.getInstance(activity).getSession().isValid()) {
            LISessionManager.getInstance(activity).getSession().getAccessToken().accessTokenValue
        } else {
            "Session is not valid"
        }
    }

    private fun buildScope(): Scope {
        return Scope.build(Scope.R_LITEPROFILE, Scope.UGC_POSTS)
    }

    fun onActivityResult(activity: Activity,requestCode: Int, resultCode: Int, data: Intent?) {
        LISessionManager.getInstance(activity).onActivityResult(activity, requestCode, resultCode, data)
    }
}
