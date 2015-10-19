package me.kyleclemens.khttp.requests

import me.kyleclemens.khttp.structures.Parameters
import org.json.JSONArray
import org.json.JSONObject
import java.io.InputStream

interface KHttpRequest {

    // Request
    /**
     * The URL to perform this request on.
     */
    val route: String
    /**
     * The headers to use for this request.
     */
    val headers: Map<String, String>
    /**
     * The URL parameters to use for this request.
     */
    val parameters: Parameters
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
     * - the object specified is coerced into either a [JSONArray] or a [JSONObject]
     *   - JSONObjects and JSONArrays are treated as such and will not undergo coercion
     *   - Maps become JSONObjects by using the appropriate constructor. Keys are converted to Strings, with `null`
     *     becoming `"null"`
     *   - Collections becomes JSONArrays by using the appropriate constructor.
     *   - Arrays become JSONArrays by using the appropriate constructor.
     *   - any other Iterables becomes JSONArrays using a custom method.
     *   - any other object throws an [IllegalArgumentException]
     */
    val json: Any?

    // Response
    val status: Int
    /**
     * The raw response from the request.
     */
    val raw: InputStream
    /**
     * The response as a UTF-8-encoded String.
     */
    val string: String
    /**
     * The response as a UTF-8-encoded JSON object.
     */
    val jsonObject: JSONObject
    /**
     * The response as a UTF-8-encode JSON array.
     */
    val jsonArray: JSONArray

}
