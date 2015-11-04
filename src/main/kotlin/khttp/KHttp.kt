/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
@file:JvmName("KHttp")

package khttp

import khttp.requests.KHttpGenericRequest
import khttp.responses.KHttpGenericResponse
import khttp.responses.KHttpResponse
import khttp.structures.authorization.Authorization

@JvmOverloads
fun delete(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("DELETE", url, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun get(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("GET", url, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun head(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("HEAD", url, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun options(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("OPTIONS", url, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun patch(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("PATCH", url, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun post(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("POST", url, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun put(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("PUT", url, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun request(method: String, url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return KHttpGenericResponse(KHttpGenericRequest(method, url, params, headers, data, json, auth, cookies, timeout, allowRedirects)).run {
        this.connection // connect
        this._history.last().apply {
            this@run._history.remove(this)
        }
    }
}
