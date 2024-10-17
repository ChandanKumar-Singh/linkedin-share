package electrovese.linkedin_share

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.StrictMode
import android.util.Log
import android.widget.Toast
import androidx.core.content.FileProvider
import io.flutter.plugin.common.BinaryMessenger
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import java.io.File

class LinkedInHelper(private val context: Context) : MethodCallHandler {

    private lateinit var channel: MethodChannel

    fun init(messenger: BinaryMessenger) {
        channel = MethodChannel(messenger, "linkedin_share")
        channel.setMethodCallHandler(this)
    }

    private fun isLinkedInInstalled(): Boolean {
        return try {
            context.packageManager.getPackageInfo("com.linkedin.android", PackageManager.GET_ACTIVITIES)
            Log.d("LinkedInHelper", "LinkedIn app found on device.")
            true
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("LinkedInHelper", "LinkedIn app not found", e)
            Toast.makeText(context, "LinkedIn app is not installed", Toast.LENGTH_SHORT).show()
            false
        }
    }

    private fun shareTextToLinkedIn(text: String) {
        if (isLinkedInInstalled()) {
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                setClassName("com.linkedin.android", "com.linkedin.android.publishing.sharing.SharingDeepLinkActivity")
            }

            if (intent.resolveActivity(context.packageManager) != null) {
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Unable to open LinkedIn app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareFileWithTextToLinkedIn(text: String, fileUri: Uri) {
        if (isLinkedInInstalled()) {
            val file = File(fileUri.path!!)
            Log.d("LinkedInHelper", "File path: ${fileUri.path}")

            if (!file.exists()) {
                Log.e("LinkedInHelper", "File does not exist: $fileUri")
                Toast.makeText(context, "File does not exist", Toast.LENGTH_SHORT).show()
                return
            }

            val mimeType = when {
                arrayOf(".doc", ".docx", ".txt", ".pdf").any { fileUri.toString().endsWith(it, ignoreCase = true) } -> {
                    "application/*"
                }
                arrayOf(".png", ".jpg", ".jpeg").any { fileUri.toString().endsWith(it, ignoreCase = true) } -> {
                    "image/*"
                }
                arrayOf(".mp4", ".3gp", ".mkv").any { fileUri.toString().endsWith(it, ignoreCase = true) } -> {
                    "video/*"
                }
                else -> {
                    "text/plain"
                }
            }

            val uri: Uri = getUriForFile(context, file)
            Log.d("LinkedInHelper", "Sharing $mimeType to LinkedIn: $uri")
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())

            val intent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_TEXT, text)
                type = mimeType
                putExtra(Intent.EXTRA_STREAM, uri)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                setClassName("com.linkedin.android", "com.linkedin.android.publishing.sharing.SharingDeepLinkActivity")
            }

            copyToClipBoard(text)
            if (intent.resolveActivity(context.packageManager) != null) {
                Log.d("LinkedInHelper", "Sharing file to LinkedIn $intent")
                context.startActivity(intent)
            } else {
                Toast.makeText(context, "Unable to open LinkedIn app", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareMultipleFilesWithTextToLinkedIn(text: String, fileUris: ArrayList<Uri>) {
        if (isLinkedInInstalled()) {
            val validUris = ArrayList<Uri>()
            Log.d("LinkedInHelper", "Sharing multiple files to LinkedIn $fileUris")
            
            for (fileUri in fileUris) {
                val file = File(fileUri.path!!)
                Log.d("LinkedInHelper", "File path: ${fileUri.path}")
                if (file.exists()) {
                    val uri = getUriForFile(context, file)
                    validUris.add(uri)
                    Log.d("LinkedInHelper", "Valid file Uri added: $uri")
                } else {
                    Log.e("LinkedInHelper", "File does not exist: $fileUri")
                }
            }

            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())

            if (validUris.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                    type = "image/*"
                    putExtra(Intent.EXTRA_TEXT, text)
                    putParcelableArrayListExtra(Intent.EXTRA_STREAM, validUris)
                    setClassName("com.linkedin.android", "com.linkedin.android.publishing.sharing.SharingDeepLinkActivity")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }
                copyToClipBoard(text)

                if (intent.resolveActivity(context.packageManager) != null) {
                    Log.d("LinkedInHelper", "Sharing multiple files to LinkedIn $intent")
                    context.startActivity(intent)
                } else {
                    Toast.makeText(context, "Unable to open LinkedIn app", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(context, "No valid files to share", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun copyToClipBoard(text: String) {
        if (text.isNotEmpty()) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("SharedText", text)
            clipboard.setPrimaryClip(clip)
        }
    }

    @SuppressLint("LongLogTag")
    private fun getUriForFile(context: Context, file: File): Uri {
//        val uri: Uri = FileProvider().getUriForPath(
//            "${context.packageName}.fileprovider",
//            file.path
//        )

        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
        Log.d ("LinkedInHelper getUriForFile ", "Uri for file: $uri")
        return  uri;
    }

    override fun onMethodCall(call: MethodCall, result: MethodChannel.Result) {
        when (call.method) {
            "shareText" -> {
                val text = call.argument<String>("text") ?: ""
                shareTextToLinkedIn(text)
                result.success(null)
            }
            "shareFileWithText" -> {
                val text = call.argument<String>("text") ?: ""
                val fileUri = Uri.parse(call.argument<String>("fileUri"))
                shareFileWithTextToLinkedIn(text, fileUri)
                result.success(null)
            }
            "shareMultipleFilesWithText" -> {
                val text = call.argument<String>("text") ?: ""
                val fileUris = call.argument<List<String>>("fileUris")?.map { Uri.parse(it) } ?: emptyList()
                Log.d("LinkedInHelper shareMultipleFilesWithText", "Sharing multiple files with text: $fileUris")
                
                shareMultipleFilesWithTextToLinkedIn(text, ArrayList(fileUris))
                result.success(null)
            }
            else -> result.notImplemented()
        }
    }

    fun onActivityResult(activity: Activity, requestCode: Int, resultCode: Int, data: Intent?) {

    }
}
