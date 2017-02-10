/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.requests

import khttp.extensions.putAllIfAbsent
import khttp.extensions.writeAndFlush
import khttp.structures.authorization.Authorization
import khttp.structures.files.FileLike
import khttp.structures.maps.CaseInsensitiveMutableMap
import khttp.structures.parameters.Parameters
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONWriter
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
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
    private var _body: ByteArray? = null
    override val body: ByteArray
        get() {
            if (this._body == null) {
                val requestData = this.data
                val files = this.files
                // If we have no requestData and no files, there is no body
                if (requestData == null && files.isEmpty()) {
                    this._body = ByteArray(0)
                    return this._body ?: throw IllegalStateException("Set to null by another thread")
                }
                val data: Any? = if (requestData != null) {
                    if (requestData is Map<*, *> && requestData !is Parameters) {
                        // If it's a map, but not a Parameters instance, make it a Parameters instance (for toString)
                        Parameters(requestData.mapKeys { it.key.toString() }.mapValues { it.value.toString() })
                    } else {
                        // Otherwise, leave it be
                        requestData
                    }
                } else {
                    null
                }
                // If we have data AND files
                if (data != null && files.isNotEmpty()) {
                    // Require that the data is a map
                    require(data is Map<*, *>) { "data must be a Map" }
                }
                // Create the byte OutputStream containing the body of the request
                val bytes = ByteArrayOutputStream()
                // If we're dealing with a non-streaming file upload
                if (files.isNotEmpty()) {
                    // Get the boundary from the header set in GenericRequest
                    val boundary = this.headers["Content-Type"]!!.split("boundary=")[1]
                    // Make a writer for convenience
                    val writer = bytes.writer()
                    // Add the form data
                    if (data != null) {
                        for ((key, value) in data as Map<*, *>) {
                            writer.writeAndFlush("--$boundary\r\n")
                            val keyString = key.toString()
                            writer.writeAndFlush("Content-Disposition: form-data; name=\"$keyString\"\r\n\r\n")
                            writer.writeAndFlush(value.toString())
                            writer.writeAndFlush("\r\n")
                        }
                    }
                    // Add the files
                    files.forEach {
                        writer.writeAndFlush("--$boundary\r\n")
                        writer.writeAndFlush("Content-Disposition: form-data; name=\"${it.name}\"; filename=\"${it.name}\"\r\n\r\n")
                        bytes.write(it.contents)
                        writer.writeAndFlush("\r\n")
                    }
                    writer.writeAndFlush("--$boundary--\r\n")
                    writer.close()
                } else if (data !is File && data !is InputStream) {
                    // Append the bytes of the data as a String if not a File and not meant for streaming
                    bytes.write(data.toString().toByteArray())
                }
                this._body = bytes.toByteArray()
            }
            return this._body ?: throw IllegalStateException("Set to null by another thread")
        }

    init {
        this.url = this.makeRoute(url)
        if (URI(this.url).scheme !in setOf("http", "https")) {
            throw IllegalArgumentException("Invalid schema. Only http:// and https:// are supported.")
        }
        val json = this.json
        val mutableHeaders = CaseInsensitiveMutableMap(headers.toSortedMap())
        if (json == null) {
            this.data = data
            if (data != null && this.files.isEmpty()) {
                if (data is Map<*, *>) {
                    mutableHeaders.putAllIfAbsent(GenericRequest.DEFAULT_FORM_HEADERS)
                } else {
                    mutableHeaders.putAllIfAbsent(GenericRequest.DEFAULT_DATA_HEADERS)
                }
            }
        } else {
            this.data = this.coerceToJSON(json)
            mutableHeaders.putAllIfAbsent(GenericRequest.DEFAULT_JSON_HEADERS)
        }
        mutableHeaders.putAllIfAbsent(GenericRequest.DEFAULT_HEADERS)
        if (this.files.isNotEmpty()) {
            mutableHeaders.putAllIfAbsent(GenericRequest.DEFAULT_UPLOAD_HEADERS)
            mutableHeaders["Content-Type"] = mutableHeaders["Content-Type"]!!.format(UUID.randomUUID().toString().replace("-", ""))
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

    private fun makeRoute(route: String) = URL(route + if (this.params.isNotEmpty()) "?${Parameters(this.params)}" else "").toIDN().toString()

}
