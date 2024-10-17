/*
    Copyright 2014 LinkedIn Corp.

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
*/

package electrovese.linkedin_share.session

import android.content.Context
import android.text.TextUtils
import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import electrovese.linkedin_share.session.errors.LIApiError
import electrovese.linkedin_share.session.internals.BuildConfig
import electrovese.linkedin_share.session.internals.QueueManager
import electrovese.linkedin_share.session.listeners.ApiListener
import electrovese.linkedin_share.session.listeners.ApiResponse
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException

/**
 * Helper class to make authenticated REST api calls to retrieve LinkedIn data.
 * The LISession must be properly initialized before using this class.
 * @see <a href="https://developer.linkedin.com/rest">https://developer.linkedin.com/rest</a>
 * for information on type of calls available and the information returned.
 * Data is returned in json format.
 */
class APIHelper private constructor() {

    companion object {
        private const val TAG = "APIHelper"
        private const val LOCATION_HEADER = "Location"
        private const val HTTP_STATUS_CODE = "StatusCode"
        private const val DATA = "responseData"
        private const val HEADER_CONTENT_TYPE = "Content-Type"
        private const val HEADER_AUTHORIZATION = "Authorization"
        private const val HEADER_SRC = "x-li-src"
        private const val HEADER_LI_FORMAT = "x-li-format"
        private const val HEADER_LI_VER = "x-li-msdk-ver"
        private const val CONTENT_VALUE = "application/json"
        private const val HEADER_SRC_VALUE = "msdk"
        private const val HEADER_LI_FORMAT_VALUE = "json"
        private const val HEADER_LI_PLFM = "x-li-plfm"
        private const val HEADER_LI_PLFM_ANDROID = "ANDROID_SDK"

        @Volatile
        private var instance: APIHelper? = null

        fun getInstance(ctx: Context): APIHelper {
            return instance ?: synchronized(this) {
                instance ?: APIHelper().also {
                    QueueManager.initQueueManager(ctx)
                    instance = it
                }
            }
        }
    }

    private fun getLiHeaders(accessToken: String): Map<String, String> {
        return mapOf(
            HEADER_CONTENT_TYPE to CONTENT_VALUE,
            HEADER_AUTHORIZATION to "Bearer $accessToken",
            HEADER_SRC to HEADER_SRC_VALUE,
            HEADER_LI_FORMAT to HEADER_LI_FORMAT_VALUE,
            HEADER_LI_VER to BuildConfig.MSDK_VERSION,
            HEADER_LI_PLFM to HEADER_LI_PLFM_ANDROID
        )
    }

    private fun buildRequest(
        accessToken: String,
        method: Int,
        url: String,
        body: JSONObject?,
        apiListener: ApiListener?
    ): JsonObjectRequest {
        return object : JsonObjectRequest(method, url, body,
            Response.Listener { response ->
                apiListener?.onApiSuccess(ApiResponse.buildApiResponse(response))
            },
            Response.ErrorListener { error ->
                apiListener?.onApiError(LIApiError.buildLiApiError(error))
            }
        ) {
            override fun parseNetworkResponse(response: NetworkResponse): Response<JSONObject> {
                return try {
                    val responseData = JSONObject().apply {
                        put(HTTP_STATUS_CODE, response.statusCode)
                        val location = response.headers?.get(LOCATION_HEADER)
                        if (!TextUtils.isEmpty(location)) {
                            put(LOCATION_HEADER, location)
                        }
                        if (response.data.isNotEmpty()) {
                            val responseDataAsString = java.lang.String(
                                response.data,
                                HttpHeaderParser.parseCharset(response.headers)
                            )
                            put(DATA, responseDataAsString)
                        }
                    }
                    Response.success(responseData, HttpHeaderParser.parseCacheHeaders(response))
                } catch (e: UnsupportedEncodingException) {
                    Response.error(ParseError(e))
                } catch (je: JSONException) {
                    Response.error(ParseError(je))
                }
            }

            override fun getHeaders(): Map<String, String> {
                return getLiHeaders(accessToken)
            }
        }
    }

    private fun request(
        context: Context,
        method: Int,
        url: String,
        body: JSONObject?,
        apiListener: ApiListener?
    ) {
        val session = LISessionManager.getInstance(context.applicationContext).session
        if (!session.isValid()) {
            apiListener?.onApiError(LIApiError(LIApiError.ErrorType.accessTokenIsNotSet, "access token is not set", null))
            return
        }
        val jsonObjectRequest = buildRequest(session.getAccessToken().accessTokenValue, method, url, body, apiListener)
        jsonObjectRequest.tag = context
        QueueManager.getInstance(context).getRequestQueue().add(jsonObjectRequest)
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using GET Method
     *
     * @param context
     * @param url         rest api endpoint to call. example: "https://api.linkedin.com/v1/people/~:(first-name,last-name,public-profile-url)"
     * @param apiListener
     */
    fun getRequest(context: Context, url: String, apiListener: ApiListener?) {
        request(context, Request.Method.GET, url, null, apiListener)
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using POST Method
     *  @param context
     * @param url
     * @param body
     * @param apiListener
     */
    fun postRequest(context: Context, url: String, body: JSONObject?, apiListener: ApiListener?) {
        request(context, Request.Method.POST, url, body, apiListener)
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using POST Method
     *  @param context
     * @param url
     * @param body
     * @param apiListener
     */
    fun postRequest(context: Context, url: String, body: String?, apiListener: ApiListener?) {
        try {
            val bodyObject = body?.let { JSONObject(it) }
            postRequest(context, url, bodyObject, apiListener)
        } catch (e: JSONException) {
            apiListener?.onApiError(LIApiError("Unable to convert body to json object ${e.message}", e))
        }
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using PUT Method
     *  @param context
     * @param url
     * @param body
     * @param apiListener
     */
    fun putRequest(context: Context, url: String, body: JSONObject?, apiListener: ApiListener?) {
        request(context, Request.Method.PUT, url, body, apiListener)
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using PUT method with
     * string body
     * @param context
     * @param url
     * @param body
     * @param apiListener
     */
    fun putRequest(context: Context, url: String, body: String?, apiListener: ApiListener?) {
        try {
            val bodyObject = body?.let { JSONObject(it) }
            putRequest(context, url, bodyObject, apiListener)
        } catch (e: JSONException) {
            apiListener?.onApiError(LIApiError("Unable to convert body to json object ${e.message}", e))
        }
    }

    /**
     * Helper method to make authenticated HTTP requests to LinkedIn REST api using DELETE Method
     *
     * @param context
     * @param url
     * @param apiListener
     */
    fun deleteRequest(context: Context, url: String, apiListener: ApiListener?) {
        request(context, Request.Method.DELETE, url, null, apiListener)
    }

    /**
     * Cancel any unsent API calls
     * @param context
     */
    fun cancelCalls(context: Context) {
        QueueManager.getInstance(context).getRequestQueue().cancelAll(context)
    }
}
