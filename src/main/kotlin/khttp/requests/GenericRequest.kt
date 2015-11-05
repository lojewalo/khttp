/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.requests

import khttp.structures.authorization.Authorization
import khttp.structures.files.FileLike
import khttp.structures.parameters.Parameters
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONWriter
import java.io.StringWriter
import java.net.IDN
import java.net.URI
import java.net.URL
import java.util.UUID

class GenericRequest internal constructor(
    override val method: String,
    url: String,
    override val params: Map<String, String>,
    headers: Map<String, String>,
    data: Any?,
    override val json: Any?,
    override val auth: Authorization?,
    override val cookies: Map<String, String>?,
    override val timeout: Double,
    allowRedirects: Boolean?,
    override val stream: Boolean,
    override val files: List<FileLike>
) : Request {

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
        val DEFAULT_UPLOAD_HEADERS = mapOf(
            "Content-Type" to "multipart/form-data; boundary=%s"
        )
        val DEFAULT_JSON_HEADERS = mapOf(
            "Content-Type" to "application/json"
        )
    }

    // Request
    override val url: String
    override val headers: Map<String, String>
    override val data: Any?
    override val allowRedirects = allowRedirects ?: (this.method != "HEAD")

    init {
        this.url = this.makeRoute(url)
        if (URI(this.url).scheme !in setOf("http", "https")) {
            throw IllegalArgumentException("Invalid schema. Only http:// and https:// are supported.")
        }
        val json = this.json
        val mutableHeaders = headers.toLinkedMap()
        if (json == null) {
            this.data = data
            if (data != null) {
                mutableHeaders += GenericRequest.DEFAULT_DATA_HEADERS
            }
        } else {
            this.data = this.coerceToJSON(json)
            mutableHeaders += GenericRequest.DEFAULT_JSON_HEADERS
        }
        for ((key, value) in GenericRequest.DEFAULT_HEADERS) {
            if (key !in mutableHeaders) {
                mutableHeaders[key] = value
            }
        }
        if (this.files.isNotEmpty()) {
            mutableHeaders += GenericRequest.DEFAULT_UPLOAD_HEADERS
            mutableHeaders["Content-Type"] = mutableHeaders["Content-Type"]!!.format(UUID.randomUUID().toString().replace("-", ""))
        } else if (this.data is Map<*, *>) {
            mutableHeaders += GenericRequest.DEFAULT_FORM_HEADERS
        }
        val auth = this.auth
        if (auth != null) {
            val header = auth.header
            mutableHeaders[header.first] = header.second
        }
        this.headers = mutableHeaders
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

    private fun URL.toIDN(): URL {
        val newHost = IDN.toASCII(this.host)
        this.javaClass.getDeclaredField("host").apply { this.isAccessible = true }.set(this, newHost)
        this.javaClass.getDeclaredField("authority").apply { this.isAccessible = true }.set(this, if (this.port == -1) this.host else "${this.host}:${this.port}")
        return URL(this.toURI().toASCIIString())
    }

    private fun makeRoute(route: String) = URL(route + if (this.params.size > 0) "?${Parameters(this.params)}" else "").toIDN().toString()

}
