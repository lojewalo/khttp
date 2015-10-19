/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.requests

import me.kyleclemens.khttp.structures.authorization.Authorization
import me.kyleclemens.khttp.structures.cookie.CookieJar
import me.kyleclemens.khttp.structures.parameters.FormParameters
import me.kyleclemens.khttp.structures.parameters.Parameters
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONWriter
import java.io.InputStream
import java.io.StringWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder

abstract class KHttpGenericRequest(
    /**
     * The URL to perform this request on.
     */
    url: String,
    /**
     * The URL parameters to use for this request.
     */
    val params: Parameters,
    /**
     * The headers to use for this request.
     */
    headers: MutableMap<String, String>,
    /**
     * The data for the body of this request.
     */
    data: Any?,
    /**
     * An object to use as the JSON payload for this request. Some special things happen if this isn't `null`.
     *
     * If this is not `null`,
     * - whatever is specified in [data] will be overwritten
     * - the `Content-Type` header becomes `application/json`
     * - the object specified is coerced into either a [JSONArray] or a [JSONObject]
     *   - JSONObjects and JSONArrays are treated as such and will not undergo coercion
     *   - Maps become JSONObjects by using the appropriate constructor. Keys are converted to Strings, with `null`
     *     becoming `"null"`
     *   - Collections becomes JSONArrays by using the appropriate constructor.
     *   - Arrays become JSONArrays by using the appropriate constructor.
     *   - any other Iterables becomes JSONArrays using a custom method.
     *   - any other object throws an [IllegalArgumentException]
     */
    val json: Any?,
    /**
     * The HTTP basic auth username and password.
     */
    val auth: Authorization?,
    /**
     * A Map of cookies to send with this request. Note that
     * [CookieJar][me.kyleclemens.khttp.structures.cookie.CookieJar] is a map. It also has a constructor that takes a
     * map, for easy conversion.
     */
    val cookies: Map<String, Any>?,
    /**
     * The amount of time to wait, in seconds, for the server to send data.
     */
    val timeout: Int,
    /**
     * If redirects should be followed.
     */
    val allowRedirects: Boolean
) : KHttpRequest {

    companion object {
        val DEFAULT_HEADERS = mapOf(
            "Accept" to "*/*",
            "Accept-Encoding" to "gzip, deflate",
            "User-Agent" to "khttp/1.0.0-SNAPSHOT"
        )
        val DEFAULT_DATA_HEADERS = mapOf(
            "Content-Type" to "text/plain"
        )
        val DEFAULT_FORM_HEADERS = mapOf(
            "Content-Type" to "application/x-www-form-urlencoded"
        )
        val DEFAULT_JSON_HEADERS = mapOf(
            "Content-Type" to "application/json"
        )
    }

    // Request
    val url: String
    val headers: Map<String, String>
    val data: Any?

    // Response
    private var _connection: HttpURLConnection? = null
    private val connection: HttpURLConnection
        get() {
            if (this._connection == null) {
                this._connection = (URL(this.url).openConnection() as HttpURLConnection).apply {
                    (this@KHttpGenericRequest.defaultStartInitializers + this@KHttpGenericRequest.initializers + this@KHttpGenericRequest.defaultEndInitializers).forEach { it(this) }
                    this.connect()
                }
            }
            return this._connection ?: throw IllegalStateException("Set to null by another thread")
        }

    override val status: Int
        get() = this.connection.responseCode

    override val raw: InputStream
        get() = this.connection.inputStream

    private var _text: String? = null
    override val text: String
        get() {
            if (this._text == null) {
                this._text = this.raw.reader().use { it.readText() }
            }
            return this._text ?: throw IllegalStateException("Set to null by another thread")
        }

    override val jsonObject: JSONObject
        get() = JSONObject(this.text)

    override val jsonArray: JSONArray
        get() = JSONArray(this.text)

    // Initializers
    private val defaultStartInitializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf(
        { connection ->
            for ((key, value) in this@KHttpGenericRequest.headers) {
                connection.setRequestProperty(key, value)
            }
        },
        { connection ->
            val cookies = this.cookies
            if (cookies != null) {
                val cookieJar = if (cookies is CookieJar) cookies else CookieJar(cookies)
                connection.setRequestProperty("Cookie", cookieJar.toString())
            }
        },
        { connection ->
            connection.connectTimeout = this.timeout * 1000
            connection.readTimeout = this.timeout * 1000
        },
        { connection ->
            connection.instanceFollowRedirects = this.allowRedirects
        }
    )
    private val defaultEndInitializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf(
        { connection ->
            if (this@KHttpGenericRequest.data != null) {
                connection.doOutput = true
                connection.outputStream.writer().use {
                    val bodyData = this@KHttpGenericRequest.data
                    it.write(if (bodyData is Parameters) {
                        this@KHttpGenericRequest.makeParams(bodyData).let { string -> if (string.isNotEmpty()) string.substring(1) else string }
                    } else {
                        bodyData.toString()
                    })
                }
            }
        }
    )
    val initializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf()

    init {
        this.url = this.makeRoute(url)
        if (URI(this.url).scheme !in setOf("http", "https")) {
            throw IllegalArgumentException("Invalid schema. Only http:// and https:// are supported.")
        }
        val json = this.json
        if (json == null) {
            this.data = data
            if (data != null) {
                headers += KHttpGenericRequest.DEFAULT_DATA_HEADERS
            }
        } else {
            this.data = this.coerceToJSON(json)
            headers += KHttpGenericRequest.DEFAULT_JSON_HEADERS
        }
        for ((key, value) in KHttpGenericRequest.DEFAULT_HEADERS) {
            if (key !in headers) {
                headers[key] = value
            }
        }
        if (this.data is FormParameters) {
            headers += KHttpGenericRequest.DEFAULT_FORM_HEADERS
        }
        val auth = this.auth
        if (auth != null) {
            val header = auth.header
            headers[header.first] = header.second
        }
        this.headers = headers
    }

    private fun coerceToJSON(any: Any): String {
        if (any is JSONObject || any is JSONArray) {
            return any.toString()
        } else if (any is Map<*, *>) {
            return JSONObject(any.mapKeys { it.key.toString() }).toString()
        } else if (any is Collection<*>) {
            return JSONArray(any).toString()
        } else if (any is Iterable<*>) {
            return any.withJSONWriter { jsonWriter, iterable ->
                jsonWriter.array()
                for (thing in any) {
                    jsonWriter.value(thing)
                }
                jsonWriter.endArray()
            }
        } else if (any is Array<*>) {
            return JSONArray(any).toString()
        } else {
            throw IllegalArgumentException("Could not coerce ${any.javaClass.simpleName} to JSON.")
        }
    }

    private fun <T> T.withJSONWriter(converter: (JSONWriter, T) -> Unit): String {
        val stringWriter = StringWriter()
        val writer = JSONWriter(stringWriter)
        converter(writer, this)
        return stringWriter.toString()
    }

    private fun makeRoute(route: String) = route + this.makeParams()

    protected fun makeParams(parameters: Parameters = this.params): String {
        if (parameters.size() < 1) return ""
        val builder = StringBuilder()
        for ((key, value) in parameters) {
            builder.append(if (builder.length() < 1) "?" else "&").append(key).append("=").append(URLEncoder.encode(value, "UTF-8"))
        }
        return builder.toString()
    }

}
