package electrovese.linkedin_share.session


interface LISession {

    /**
     * @return an AccessToken representing the user's authorization of the application
     */
    fun getAccessToken(): AccessToken

    /**
     * @return true if the AccessToken is valid
     * false is returned if the user has not granted the application access or if the
     * access token has expired
     */
    fun isValid(): Boolean
}
