package electrovese.linkedin_share.session.internals

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Build
import electrovese.linkedin_share.R

object AppStore {

    fun goAppStore(activity: Activity, showDialog: Boolean) {
        if (!showDialog) {
            goToAppStore(activity)
            return
        }

        val builder = AlertDialog.Builder(activity)
        builder.setMessage(R.string.update_linkedin_app_message)
            .setTitle(R.string.update_linkedin_app_title)
            .setPositiveButton(R.string.update_linkedin_app_download) { dialogInterface, _ ->
                goToAppStore(activity)
                dialogInterface.dismiss()
            }
            .setNegativeButton(R.string.update_linkedin_app_cancel) { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()
            .show()
    }

    private fun goToAppStore(activity: Activity) {
        val appStore = SupportedAppStore.fromDeviceManufacturer()
        val appStoreUri = appStore.appStoreUri
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(appStoreUri))
        try {
            activity.startActivity(intent)
        } catch (e: android.content.ActivityNotFoundException) {
            // Should not happen
        }
    }

    private enum class SupportedAppStore(private val deviceManufacturer: String, val appStoreUri: String) {
        amazonAppstore("amazon", "amzn://apps/android?p=com.linkedin.android"),
        googlePlay("google", "market://details?id=com.linkedin.android"),
        samsungApps("samsung", "samsungapps://ProductDetail/com.linkedin.android");

        companion object {
            fun fromDeviceManufacturer(): SupportedAppStore {
                for (appStore in values()) {
                    if (appStore.deviceManufacturer.equals(Build.MANUFACTURER, ignoreCase = true)) {
                        return appStore
                    }
                }
                // Return Google Play by default
                return googlePlay
            }
        }
    }
}
