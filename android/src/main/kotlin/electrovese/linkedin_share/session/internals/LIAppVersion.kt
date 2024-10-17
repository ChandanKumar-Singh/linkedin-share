package electrovese.linkedin_share.session.internals

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

object LIAppVersion {

    const val LI_APP_PACKAGE_NAME = "com.linkedin.android"

    fun isLIAppCurrent(ctx: Context): Boolean {
        return isLIAppCurrent(ctx, LI_APP_PACKAGE_NAME)
    }

    private fun isLIAppCurrent(ctx: Context, packageName: String): Boolean {
        val packageManager = ctx.packageManager
        return try {
            val packageInfo: PackageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES)
            packageInfo.versionCode >= BuildConfig.LI_APP_SUPPORTED_VER_CODE
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
}
