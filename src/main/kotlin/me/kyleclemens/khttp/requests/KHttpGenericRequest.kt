package me.kyleclemens.khttp.requests

import me.kyleclemens.khttp.structures.FormParameters
import me.kyleclemens.khttp.structures.Parameters
import org.json.JSONArray
import org.json.JSONObject
import org.json.JSONWriter
import java.io.InputStream
import java.io.StringWriter
import java.net.HttpURLConnection
import java.net.URI
import java.net.URL
import java.net.URLEncoder

abstract class KHttpGenericRequest(route: String, override val parameters: Parameters, headers: MutableMap<String, String>, data: Any?, override val json: Any?) : KHttpRequest {

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

    override val route: String
    override val headers: Map<String, String>

    private val defaultStartInitializers: MutableList<(HttpURLConnection) -> Unit> = arrayListOf(
        { connection ->
            for ((key, value) in this@KHttpGenericRequest.headers) {
                connection.addRequestProperty(key, value)
            }
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

    private var _connection: HttpURLConnection? = null
    private val connection: HttpURLConnection
        get() {
            if (this._connection == null) {
                this._connection = (URL(this.route).openConnection() as HttpURLConnection).apply {
                    (this@KHttpGenericRequest.defaultStartInitializers + this@KHttpGenericRequest.initializers + this@KHttpGenericRequest.defaultEndInitializers).forEach { it(this) }
                    this.connect()
                }
            }
            return this._connection ?: throw IllegalStateException("Set to null by another thread")
        }

    override val status: Int
        get() = this.connection.responseCode
    override val data: Any?
    override val raw: InputStream
        get() = this.connection.inputStream

    override val string: String
        get() = this.raw.reader().use { it.readText() }

    override val jsonObject: JSONObject
        get() = JSONObject(this.string)

    override val jsonArray: JSONArray
        get() = JSONArray(this.string)

    init {
        this.route = this.makeRoute(route)
        if (URI(this.route).scheme !in setOf("http", "https")) {
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

    protected fun makeParams(parameters: Parameters = this.parameters): String {
        if (parameters.size() < 1) return ""
        val builder = StringBuilder()
        for ((key, value) in parameters) {
            builder.append(if (builder.length() < 1) "?" else "&").append(key).append("=").append(URLEncoder.encode(value, "UTF-8"))
        }
        return builder.toString()
    }

}
