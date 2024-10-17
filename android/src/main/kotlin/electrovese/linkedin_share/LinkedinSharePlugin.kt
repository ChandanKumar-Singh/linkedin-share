package electrovese.linkedin_share

import android.app.Activity
import android.content.Intent
import android.util.Log
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.embedding.engine.plugins.activity.ActivityAware
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding

class LinkedinSharePlugin : FlutterPlugin, ActivityAware {
  private var linkedInHelper: LinkedInHelper? = null
  private var sessionManagerHelper: SessionManagerHelper? = null
  private var activity: Activity? = null // Store the Activity reference
  private var mainBinding: FlutterPlugin.FlutterPluginBinding? = null

  override fun onAttachedToEngine(flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    linkedInHelper = LinkedInHelper(flutterPluginBinding.applicationContext)
    linkedInHelper?.init(flutterPluginBinding.binaryMessenger)
    mainBinding = flutterPluginBinding
  }

  override fun onDetachedFromEngine(binding: FlutterPlugin.FlutterPluginBinding) {
    linkedInHelper = null
    sessionManagerHelper = null // Clean up the session manager
  }

  // Implement the methods required for ActivityAware
  override fun onAttachedToActivity(binding: ActivityPluginBinding) {
    activity = binding.activity // Store the Activity reference
    sessionManagerHelper = SessionManagerHelper(activity!!) // Reinitialize session manager with Activity context

    mainBinding?.let { binding ->
      sessionManagerHelper!!.init(binding.binaryMessenger) // Correctly access binaryMessenger
    }

    binding.addActivityResultListener { requestCode, resultCode, data ->
      onActivityResult(requestCode, resultCode, data)
      true
    }
  }

  override fun onDetachedFromActivityForConfigChanges() {
    // Handle if the activity is detached due to configuration changes
    activity = null
  }

  override fun onReattachedToActivityForConfigChanges(binding: ActivityPluginBinding) {
    activity = binding.activity // Restore the Activity reference
    sessionManagerHelper = SessionManagerHelper(activity!!) // Reinitialize session manager with Activity context

    mainBinding?.let { binding ->
      sessionManagerHelper!!.init(binding.binaryMessenger) // Correctly access binaryMessenger
    }
  }

  override fun onDetachedFromActivity() {
    activity = null
  }

  private fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    Log.d("LinkedinSharePlugin", "onActivityResult called with requestCode: $requestCode and resultCode: $resultCode and data: $data")
    // Ensure activity is not null before calling onActivityResult
    activity?.let {
      linkedInHelper?.onActivityResult(it, requestCode, resultCode, data)
      sessionManagerHelper?.onActivityResult(it, requestCode, resultCode, data) // Forward activity result
    }
  }
}
