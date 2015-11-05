/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
@file:JvmName("KHttp")

package khttp

import khttp.requests.GenericRequest
import khttp.responses.GenericResponse
import khttp.responses.Response
import khttp.structures.authorization.Authorization

@JvmOverloads
fun delete(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return request("DELETE", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream)
}

@JvmOverloads
fun get(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return request("GET", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream)
}

@JvmOverloads
fun head(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return request("HEAD", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream)
}

@JvmOverloads
fun options(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return request("OPTIONS", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream)
}

@JvmOverloads
fun patch(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return request("PATCH", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream)
}

@JvmOverloads
fun post(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return request("POST", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream)
}

@JvmOverloads
fun put(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return request("PUT", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream)
}

@JvmOverloads
fun request(method: String, url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean? = null, stream: Boolean = false): Response {
    return GenericResponse(GenericRequest(method, url, params, headers, data, json, auth, cookies, timeout, allowRedirects, stream)).run {
        this.init()
        this._history.last().apply {
            this@run._history.remove(this)
        }
    }
}
