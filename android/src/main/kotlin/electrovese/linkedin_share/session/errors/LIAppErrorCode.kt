package electrovese.linkedin_share.session.errors

enum class LIAppErrorCode(private val description: String) {
    NONE("none"),
    INVALID_REQUEST("Invalid request"),
    NETWORK_UNAVAILABLE("Unavailable network connection"),
    USER_CANCELLED("User canceled action"),
    UNKNOWN_ERROR("Unknown or not defined error"),
    SERVER_ERROR("Server side error"),
    LINKEDIN_APP_NOT_FOUND("LinkedIn application not found"),
    NOT_AUTHENTICATED("User is not authenticated in LinkedIn app");

    companion object {
        private val liAuthErrorCodeHashMap: Map<String, LIAppErrorCode> = buildMap()

        private fun buildMap(): Map<String, LIAppErrorCode> {
            return values().associateBy { it.name }
        }

        fun findErrorCode(errorCode: String): LIAppErrorCode {
            return liAuthErrorCodeHashMap[errorCode] ?: UNKNOWN_ERROR
        }
    }

    fun getDescription(): String {
        return description
    }
}
