/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.responses

import me.kyleclemens.khttp.requests.KHttpRequest
import me.kyleclemens.khttp.structures.cookie.Cookie
import me.kyleclemens.khttp.structures.cookie.CookieJar
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.ProtocolException
import java.net.URL

class KHttpGenericResponse(override val request: KHttpRequest) : KHttpResponse {

    private fun URL.openRedirectingConnection(receiver: HttpURLConnection.() -> Unit): HttpURLConnection {
        val connection = (this.openConnection() as HttpURLConnection).apply {
            this.instanceFollowRedirects = false
            this.receiver()
            this.connect()
        }
        if (this@KHttpGenericResponse.request.allowRedirects && connection.responseCode in 301..303) {
            return this.toURI().resolve(connection.getHeaderField("Location")).toURL().openRedirectingConnection(receiver)
        }
        return connection
    }

    private var _connection: HttpURLConnection? = null
    private val connection: HttpURLConnection
        get() {
            if (this._connection == null) {
                this._connection = URL(this.request.url).openRedirectingConnection {
                    (this@KHttpGenericResponse.defaultStartInitializers + this@KHttpGenericResponse.initializers + this@KHttpGenericResponse.defaultEndInitializers).forEach { it(this) }
                }
            }
            return this._connection ?: throw IllegalStateException("Set to null by another thread")
        }

    override val statusCode: Int
        get() = this.connection.responseCode

    override val headers: Map<String, String>
        get() = this.connection.headerFields.mapValues { it.value.last() }

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

    private val _cookies = CookieJar()
    override val cookies: CookieJar
        get() {
            this.connection // Ensure that we've connected
            return this._cookies
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
            connection.connectTimeout = this.request.timeout * 1000
            connection.readTimeout = this.request.timeout * 1000
        },
        { connection ->
            connection.instanceFollowRedirects = false
        }
    )
    private val defaultEndInitializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf(
        { connection ->
            if (this@KHttpGenericResponse.request.data != null) {
                connection.doOutput = true
                connection.outputStream.writer().use {
                    val bodyData = this@KHttpGenericResponse.request.data
                    it.write(bodyData.toString())
                }
            }
        },
        { connection ->
            // Add all the cookies from every response to our cookie jar
            this._cookies.putAll(
                CookieJar(*connection.headerFields.filter { it.key == "Set-Cookie" }.flatMap { it.value }.map { Cookie(it) }.toTypedArray())
            )
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

}
