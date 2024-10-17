package electrovese.linkedin_share.session

import android.util.Log
import org.json.JSONException
import org.json.JSONObject
import java.io.Serializable

class AccessToken(
    val accessTokenValue: String,
    val expiresOn: Long
) : Serializable {

    companion object {
        private const val ACCESS_TOKEN_VALUE = "accessTokenValue"
        private const val EXPIRES_ON = "expiresOn"
        private val TAG = AccessToken::class.java.simpleName

        /**
         * Build an AccessToken from a previously retrieved value
         * @param accessToken obtained by calling [AccessToken.toString()]
         * @return AccessToken instance or null if the input is invalid
         */
        @Synchronized
        fun buildAccessToken(accessToken: String?): AccessToken? {
            return if (accessToken.isNullOrEmpty()) {
                null
            } else {
                try {
                    val jsonObject = JSONObject(accessToken)
                    AccessToken(jsonObject)
                } catch (e: JSONException) {
                    Log.d(TAG, e.message ?: "Error parsing AccessToken")
                    null
                }
            }
        }

        /**
         * Build an AccessToken from a JSONObject
         * @param accessToken JSON object representing the access token
         * @return AccessToken instance or null if parsing fails
         */
        @Synchronized
        fun buildAccessToken(accessToken: JSONObject?): AccessToken? {
            return accessToken?.let {
                try {
                    AccessToken(it)
                } catch (e: JSONException) {
                    Log.d(TAG, e.message ?: "Error parsing AccessToken")
                    null
                }
            }
        }
    }

    private constructor(accessTokenJson: JSONObject) : this(
        accessTokenJson.getString(ACCESS_TOKEN_VALUE),
        accessTokenJson.getLong(EXPIRES_ON)
    )

    /**
     * Check if the access token is expired.
     * @return true if expired, false otherwise
     */
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expiresOn
    }

    override fun toString(): String {
        return try {
            JSONObject().apply {
                put(ACCESS_TOKEN_VALUE, accessTokenValue)
                put(EXPIRES_ON, expiresOn)
            }.toString()
        } catch (e: JSONException) {
            Log.d(TAG, e.message ?: "Error converting AccessToken to String")
            ""
        }
    }
}
