package electrovese.linkedin_share.session.listeners

import android.util.Log
import org.json.JSONException
import org.json.JSONObject

class ApiResponse private constructor(
    val statusCode: Int,
    val responseData: String,
    val locationHeader: String?
) {

    companion object {
        private const val TAG = "ApiResponse"
        private const val LOCATION = "Location"
        private const val STATUS_CODE = "StatusCode"
        private const val DATA = "responseData"

        @Synchronized
        fun buildApiResponse(apiResponseAsJson: JSONObject?): ApiResponse? {
            return try {
                val statusCode = apiResponseAsJson?.optInt(STATUS_CODE) ?: -1
                val locationHeader = apiResponseAsJson?.optString(LOCATION)
                val responseData = apiResponseAsJson?.getString(DATA) ?: ""
                ApiResponse(statusCode, responseData, locationHeader)
            } catch (e: JSONException) {
                Log.d(TAG, e.message ?: "JSON parsing error")
                null
            }
        }
    }

    fun getResponseDataAsJson(): JSONObject? {
        if (responseData.isEmpty()) return null
        return try {
            JSONObject(responseData)
        } catch (e: JSONException) {
            Log.d(TAG, e.message ?: "JSON parsing error", e)
            null
        }
    }

    override fun toString(): String {
        val apiResponseAsJson = JSONObject()
        return try {
            apiResponseAsJson.put(STATUS_CODE, statusCode)
            apiResponseAsJson.put(DATA, responseData)
            apiResponseAsJson.put(LOCATION, locationHeader)
            apiResponseAsJson.toString()
        } catch (e: JSONException) {
            Log.d(TAG, e.message ?: "JSON toString error")
            ""
        }
    }
}
