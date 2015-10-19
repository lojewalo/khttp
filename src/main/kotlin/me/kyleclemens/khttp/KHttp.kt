/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import me.kyleclemens.khttp.requests.KHttpMethodRequest
import me.kyleclemens.khttp.structures.Parameters

@JvmOverloads
fun delete(route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return request("DELETE", route, headers, parameters, data, json)
}

@JvmOverloads
fun get(route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return request("GET", route, headers, parameters, data, json)
}

@JvmOverloads
fun head(route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return request("HEAD", route, headers, parameters, data, json)
}

@JvmOverloads
fun options(route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return request("OPTIONS", route, headers, parameters, data, json)
}

@JvmOverloads
fun patch(route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return request("PATCH", route, headers, parameters, data, json)
}

@JvmOverloads
fun post(route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return request("POST", route, headers, parameters, data, json)
}

@JvmOverloads
fun put(route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return request("PUT", route, headers, parameters, data, json)
}

@JvmOverloads
fun request(method: String, route: String, headers: MutableMap<String, String> = hashMapOf(), parameters: Parameters = Parameters(), data: Any? = null, json: Any? = null): KHttpMethodRequest {
    return object : KHttpMethodRequest(method, route, parameters, headers, data, json) {}
}
