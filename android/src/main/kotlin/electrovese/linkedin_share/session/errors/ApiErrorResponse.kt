package electrovese.linkedin_share.session.errors

import org.json.JSONException
import org.json.JSONObject

class ApiErrorResponse private constructor(
    private val jsonApiErrorResponse: JSONObject,
    val errorCode: Int,
    val message: String?,
    val requestId: String?,
    val status: Int,
    val timestamp: Long
) {

    companion object {
        const val ERROR_CODE = "errorCode"
        const val MESSAGE = "message"
        const val REQUEST_ID = "requestId"
        const val STATUS = "status"
        const val TIMESTAMP = "timestamp"

        @Throws(JSONException::class)
        fun build(apiErrorResponseData: ByteArray): ApiErrorResponse {
            return build(JSONObject(String(apiErrorResponseData)))
        }

        @Throws(JSONException::class)
        fun build(jsonErr: JSONObject): ApiErrorResponse {
            return ApiErrorResponse(
                jsonErr,
                jsonErr.optInt(ERROR_CODE, -1),
                jsonErr.optString(MESSAGE),
                jsonErr.optString(REQUEST_ID),
                jsonErr.optInt(STATUS, -1),
                jsonErr.optLong(TIMESTAMP, 0)
            )
        }
    }

    // Remove the explicit getter methods
    override fun toString(): String {
        return try {
            jsonApiErrorResponse.toString(2)
        } catch (e: JSONException) {
            "Error converting to JSON"
        }
    }
}
