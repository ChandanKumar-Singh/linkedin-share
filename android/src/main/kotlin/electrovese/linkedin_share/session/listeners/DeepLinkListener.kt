package electrovese.linkedin_share.session.listeners

import electrovese.linkedin_share.session.errors.LIDeepLinkError

interface DeepLinkListener {

    fun onDeepLinkSuccess()

    fun onDeepLinkError(error: LIDeepLinkError)
}
