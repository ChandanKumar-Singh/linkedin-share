package electrovese.linkedin_share.session.errors

import com.android.volley.VolleyError
import org.json.JSONException

class LIApiError : Exception {

    enum class ErrorType {
        accessTokenIsNotSet,
        apiErrorResponse,
        other
    }

    private var volleyError: VolleyError? = null
    var httpStatusCode: Int = -1
        private set
    var apiErrorResponse: ApiErrorResponse? = null
        private set
    var errorType: ErrorType? = null
        private set

    companion object {
        fun buildLiApiError(volleyError: VolleyError): LIApiError {
            return LIApiError(volleyError)
        }
    }

    constructor(detailMessage: String?, throwable: Throwable?) : this(ErrorType.other, detailMessage, throwable)

    constructor(errorType: ErrorType, detailMessage: String?, throwable: Throwable?) : super(detailMessage, throwable) {
        this.errorType = errorType
    }

    constructor(volleyError: VolleyError) : super(volleyError.message, volleyError.fillInStackTrace()) {
        this.volleyError = volleyError
        if (volleyError.networkResponse != null) {
            httpStatusCode = volleyError.networkResponse.statusCode
            try {
                apiErrorResponse = ApiErrorResponse.build(volleyError.networkResponse.data)
                errorType = ErrorType.apiErrorResponse
            } catch (e: JSONException) {
                errorType = ErrorType.other
            }
        }
    }

    override fun toString(): String {
        return apiErrorResponse?.toString() ?: "exceptionMsg: ${super.message}"
    }
}
