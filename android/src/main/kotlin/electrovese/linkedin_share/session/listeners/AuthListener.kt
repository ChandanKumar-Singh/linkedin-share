package electrovese.linkedin_share.session.listeners

import electrovese.linkedin_share.session.errors.LIAuthError
import electrovese.linkedin_share.session.errors.LIDeepLinkError

interface AuthListener {

    /**
     * Called when the application has been granted authorization to access the LinkedIn member's data.
     * See [com.linkedin.platform.APIHelper] and [com.linkedin.platform.DeepLinkHelper] for
     * how to access LinkedIn data and interact with the LinkedIn application.
     */
    fun onAuthSuccess()

    /**
     * Called when the application has not been granted authorization to access the LinkedIn member's
     * data.
     * @param error information on why the authorization did not occur.
     */
    fun onAuthError(error: LIAuthError)
}
