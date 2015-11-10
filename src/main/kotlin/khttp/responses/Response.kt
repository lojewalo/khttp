/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.responses

import khttp.requests.Request
import khttp.structures.cookie.CookieJar
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream
import java.net.HttpURLConnection
import java.nio.charset.Charset

interface Response {

    /**
     * The request that generated this response.
     */
    val request: Request
    /**
     * The status code from the request.
     */
    val statusCode: Int
    /**
     * The headers from the request.
     */
    val headers: Map<String, String>
    /**
     * The raw response from the request.
     */
    val raw: InputStream
    /**
     * The response as a ByteArray.
     */
    val content: ByteArray
    /**
     * The response as a String.
     */
    val text: String
    /**
     * The response as a JSON object.
     *
     * @throws[org.json.JSONException] If the response is not a valid JSON object
     */
    val jsonObject: JSONObject
    /**
     * The response as a JSON array.
     *
     * @throws[org.json.JSONException] If the response is not a valid JSON array
     */
    val jsonArray: JSONArray
    /**
     * The cookies from the request.
     */
    val cookies: CookieJar
    /**
     * The URL that this request ended up going to.
     */
    val url: String
    /**
     * The encoding in which to decode text. If not specified, uses the Content-Type header. If there is no such header,
     * uses UTF-8.
     */
    var encoding: Charset
    /**
     * A list of KHttpResponse objects from the history of the request. Any redirect responses will end up here. The
     * list is sorted from the oldest to the most recent request.
     */
    val history: List<Response>
    /**
     * The connection used for the request.
     */
    val connection: HttpURLConnection

    /**
     * Gets an [Iterator] that provides [ByteArray]s of [chunkSize] for the content.
     *
     * If used in streaming mode, this will stream content from the server.
     *
     * If used outside of streaming mode, this will iterate over the downloaded content.
     */
    fun contentIterator(chunkSize: Int = 1): Iterator<ByteArray>

    /**
     * Gets an [Iterator] that provides [ByteArray]s of lines separated by [delimiter].
     *
     * If [delimiter] is not specified, the delimiter will be `\r?\n`.
     *
     * [chunkSize] sets the size of chunks used by [contentIterator] internally when scanning for lines.
     *
     * This method converts lines into Strings and back to ByteArrays using [encoding].
     */
    fun lineIterator(chunkSize: Int = 512, delimiter: ByteArray? = null): Iterator<ByteArray>

}
