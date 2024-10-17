package electrovese.linkedin_share.session.listeners

import electrovese.linkedin_share.session.errors.LIApiError

interface ApiListener {
    fun onApiSuccess(apiResponse: ApiResponse?)
    fun onApiError(apiError: LIApiError)
}
