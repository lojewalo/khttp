/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import me.kyleclemens.khttp.requests.KHttpGenericRequest
import me.kyleclemens.khttp.responses.KHttpGenericResponse
import me.kyleclemens.khttp.responses.KHttpResponse
import me.kyleclemens.khttp.structures.authorization.Authorization

@JvmOverloads
fun delete(route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("DELETE", route, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun get(route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("GET", route, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun head(route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("HEAD", route, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun options(route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("OPTIONS", route, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun patch(route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("PATCH", route, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun post(route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("POST", route, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun put(route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return request("PUT", route, headers, params, data, json, auth, cookies, timeout, allowRedirects)
}

@JvmOverloads
fun request(method: String, route: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = 30.0, allowRedirects: Boolean = true): KHttpResponse {
    return KHttpGenericResponse(KHttpGenericRequest(method, route, params, headers, data, json, auth, cookies, timeout, allowRedirects))
}
