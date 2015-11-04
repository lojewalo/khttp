/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.responses

import khttp.requests.KHttpGenericRequest
import khttp.requests.KHttpRequest
import khttp.structures.cookie.Cookie
import khttp.structures.cookie.CookieJar
import khttp.structures.maps.CaseInsensitiveMap
import khttp.structures.parameters.Parameters
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL
import java.nio.charset.Charset
import java.util.Collections
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream

class KHttpGenericResponse internal constructor(override val request: KHttpRequest) : KHttpResponse {

    private val HttpURLConnection.cookieJar: CookieJar
        get() = CookieJar(*this.headerFields.filter { it.key == "Set-Cookie" }.flatMap { it.value }.map { Cookie(it) }.toTypedArray())

    private fun URL.openRedirectingConnection(first: KHttpResponse, receiver: HttpURLConnection.() -> Unit): HttpURLConnection {
        val connection = (this.openConnection() as HttpURLConnection).apply {
            this.instanceFollowRedirects = false
            this.receiver()
            this.connect()
        }
        if (first.request.allowRedirects && connection.responseCode in 301..303) {
            val cookies = connection.cookieJar
            val req = with(first.request) {
                KHttpGenericResponse(
                    KHttpGenericRequest(
                        method = this.method,
                        url = this@openRedirectingConnection.toURI().resolve(connection.getHeaderField("Location")).toASCIIString(),
                        headers = this.headers,
                        params = this.params,
                        data = this.data,
                        json = this.json,
                        auth = this.auth,
                        cookies = cookies + (this.cookies ?: mapOf()),
                        timeout = this.timeout,
                        allowRedirects = false
                    )
                )
            }
            req._cookies.putAll(cookies)
            req._history.addAll(first.history)
            (first as KHttpGenericResponse)._history.add(req)
            if (req._connection == null) {
                req.connection // Ensure connection
            }
        }
        return connection
    }

    internal var _history: MutableList<KHttpResponse> = arrayListOf()
    override val history: List<KHttpResponse>
        get() = Collections.unmodifiableList(this._history)

    private var _connection: HttpURLConnection? = null
    override val connection: HttpURLConnection
        get() {
            if (this._connection == null) {
                this._connection = URL(this.request.url).openRedirectingConnection(this._history.firstOrNull() ?: this.apply { this._history.add(this) }) {
                    (this@KHttpGenericResponse.defaultStartInitializers + this@KHttpGenericResponse.initializers + this@KHttpGenericResponse.defaultEndInitializers).forEach { it(this) }
                }
            }
            return this._connection ?: throw IllegalStateException("Set to null by another thread")
        }

    override val statusCode: Int
        get() = this.connection.responseCode

    override val headers: Map<String, String>
        get() = CaseInsensitiveMap(this.connection.headerFields.mapValues { it.value.joinToString(", ") }.filterKeys { it != null })

    private val HttpURLConnection.realInputStream: InputStream
        get() {
            val stream = try {
                this.inputStream
            } catch (ex: IOException) {
                this.errorStream
            }
            return when (this@KHttpGenericResponse.headers["Content-Encoding"]?.toLowerCase()) {
                "gzip" -> GZIPInputStream(stream)
                "deflate" -> InflaterInputStream(stream)
                else -> stream
            }
        }

    private var _raw: InputStream? = null
    override val raw: InputStream
        get() {
            if (this._raw == null) {
                this._raw = this.connection.realInputStream
            }
            return this._raw ?: throw IllegalStateException("Set to null by another thread")
        }

    private var _contents: ByteArray? = null
    override val content: ByteArray
        get() {
            if (this._contents == null) {
                this._contents = this.raw.use { it.readBytes() }
            }
            return this._contents ?: throw IllegalStateException("Set to null by another thread")
        }

    override val text: String
        get() = this.content.toString(this.encoding)

    override val jsonObject: JSONObject
        get() = JSONObject(this.text)

    override val jsonArray: JSONArray
        get() = JSONArray(this.text)

    private val _cookies = CookieJar()
    override val cookies: CookieJar
        get() {
            this.connection // Ensure that we've connected
            return this._cookies
        }

    override val url: String
        get() = this.connection.url.toString()

    private var _encoding: Charset? = null
        set(value) {
            field = value
        }
    override var encoding: Charset
        get() {
            if (this._encoding != null) {
                return this._encoding ?: throw IllegalStateException("Set to null by another thread")
            }
            this.headers["Content-Type"]?.let {
                val charset = it.split(";").map { it.split("=") }.filter { it[0].trim().toLowerCase() == "charset" }.filter { it.size == 2 }.map { it[1] }.firstOrNull()
                return Charset.forName(charset?.toUpperCase() ?: Charsets.UTF_8.name())
            }
            return Charsets.UTF_8
        }
        set(value) {
            this._encoding = value
        }

    // Initializers
    private val defaultStartInitializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf(
        { connection ->
            connection.forceMethod(this.request.method)
        },
        { connection ->
            for ((key, value) in this@KHttpGenericResponse.request.headers) {
                connection.setRequestProperty(key, value)
            }
        },
        { connection ->
            val cookies = this.request.cookies
            if (cookies != null) {
                // Get the cookies specified in the request and add the cookies from the response
                val cookieJar = CookieJar(cookies + this._cookies)
                // Set the merged cookies in the request
                connection.setRequestProperty("Cookie", cookieJar.toString())
            }
        },
        { connection ->
            val timeout = (this.request.timeout * 1000.0).toInt()
            connection.connectTimeout = timeout
            connection.readTimeout = timeout
        },
        { connection ->
            connection.instanceFollowRedirects = false
        }
    )
    private val defaultEndInitializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf(
        { connection ->
            val requestData = this@KHttpGenericResponse.request.data
            if (requestData != null) {
                @Suppress("IMPLICIT_CAST_TO_UNIT_OR_ANY") // Shouldn't warn, since I'm explicitly casting
                val data: Any = if (requestData is Map<*, *> && requestData !is Parameters) {
                    Parameters(requestData.mapKeys { it.key.toString() }.mapValues { it.value.toString() })
                } else {
                    requestData
                }
                connection.doOutput = true
                connection.outputStream.writer().use {
                    it.write(data.toString())
                }
            }
        },
        { connection ->
            // Add all the cookies from every response to our cookie jar
            this._cookies.putAll(connection.cookieJar)
        }
    )
    val initializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf()

    private fun HttpURLConnection.forceMethod(method: String) {
        try {
            this.requestMethod = method
        } catch (ex: ProtocolException) {
            try {
                (this.javaClass.getDeclaredField("delegate").apply { this.isAccessible = true }.get(this) as HttpURLConnection?)?.forceMethod(method)
            } catch (ex: NoSuchFieldException) {
                // ignore
            }
            (this.javaClass.getSuperclasses() + this.javaClass).forEach {
                try {
                    it.getDeclaredField("method").apply { this.isAccessible = true }.set(this, method)
                } catch (ex: NoSuchFieldException) {
                    // ignore
                }
            }
        }
    }

    private fun <T> Class<T>.getSuperclasses(): List<Class<in T>> {
        val list = arrayListOf<Class<in T>>()
        var superclass = this.superclass
        while (superclass != null) {
            list.add(superclass)
            superclass = superclass.superclass
        }
        return list
    }

    override fun toString(): String {
        return "<Response [${this.statusCode}]>"
    }

}
