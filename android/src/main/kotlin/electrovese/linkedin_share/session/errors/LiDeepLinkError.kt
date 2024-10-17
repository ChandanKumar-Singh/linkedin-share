package electrovese.linkedin_share.session.errors

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class LIDeepLinkError {

    private val TAG = LIDeepLinkError::class.java.name

    private var errorCode: LIAppErrorCode
    private var errorMsg: String?

    constructor(errorInfo: String, errorMsg: String?) {
        errorCode = LIAppErrorCode.findErrorCode(errorInfo)
        this.errorMsg = errorMsg
    }

    constructor(errorCode: LIAppErrorCode, errorMsg: String?) {
        this.errorCode = errorCode
        this.errorMsg = errorMsg
    }

    override fun toString(): String {
        return try {
            val jsonObject = JSONObject()
            jsonObject.put("errorCode", errorCode.name)
            jsonObject.put("errorMessage", errorMsg)
            jsonObject.toString(2)
        } catch (e: JSONException) {
            Log.d(TAG, e.message ?: "JSON Exception")
            "Error converting to JSON"
        }
    }
}
