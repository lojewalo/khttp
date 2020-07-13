/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.requests

import khttp.structures.authorization.Authorization
import khttp.structures.files.FileLike
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

interface Request {

    /**
     * The HTTP method for this request.
     */
    val method: String
    /**
     * The URL to perform this request on.
     */
    val url: String
    /**
     * The URL parameters to use for this request.
     */
    val params: Map<String, String>
    /**
     * The headers to use for this request.
     */
    val headers: Map<String, String>
    /**
     * The data for the body of this request.
     */
    val data: Any?
    /**
     * An object to use as the JSON payload for this request. Some special things happen if this isn't `null`.
     *
     * If this is not `null`,
     * - whatever is specified in [data] will be overwritten
     * - the `Content-Type` header becomes `application/json`
     * - the object specified is coerced into either a [JSONArray][org.json.JSONArray] or a
     *   [JSONObject][org.json.JSONObject]
     *   - JSONObjects and JSONArrays are treated as such and will not undergo coercion
     *   - Maps become JSONObjects by using the appropriate constructor. Keys are converted to Strings, with `null`
     *     becoming `"null"`
     *   - Collections becomes JSONArrays by using the appropriate constructor.
     *   - Arrays become JSONArrays by using the appropriate constructor.
     *   - any other Iterables becomes JSONArrays using a custom method.
     *   - any other object throws an [IllegalArgumentException]
     */
    val json: Any?
    /**
     * The HTTP basic auth username and password.
     */
    val auth: Authorization?
    /**
     * A Map of cookies to send with this request. Note that
     * [CookieJar][khttp.structures.cookie.CookieJar] is a map. It also has a constructor that takes a
     * map, for easy conversion.
     */
    val cookies: Map<String, String>?
    /**
     * The amount of time to wait, in seconds, for the server to send data.
     */
    val timeout: Double
    /**
     * If redirects should be followed.
     */
    val allowRedirects: Boolean
    /**
     * If the response content should be immediately downloaded (`false`) or streamed (`true`).
     */
    val stream: Boolean
    /**
     * List of [FileLike] objects to include in the request.
     */
    val files: List<FileLike>
    /**
     * The body of the request to be sent.
     */
    val body: ByteArray
    /**
     * SSL configuration for the request
     */
    val sslContext: SSLContext?
    /**
     * URL's hostname and the server's identification hostname validation
     */
    val hostnameVerifier: HostnameVerifier
}
