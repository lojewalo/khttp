/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
@file:JvmName("KHttp")

package khttp

import kotlin.concurrent.thread

import khttp.requests.GenericRequest
import khttp.responses.GenericResponse
import khttp.responses.Response
import khttp.structures.authorization.Authorization
import khttp.structures.files.FileLike
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

/**
 * The default number of seconds to wait before timing out a request.
 */
const val DEFAULT_TIMEOUT = 30.0

@JvmOverloads
fun delete(url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return request("DELETE", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)
}

@JvmOverloads
fun get(url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return request("GET", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)
}

@JvmOverloads
fun head(url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return request("HEAD", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)
}

@JvmOverloads
fun options(url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return request("OPTIONS", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)
}

@JvmOverloads
fun patch(url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return request("PATCH", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)
}

@JvmOverloads
fun post(url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return request("POST", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)
}

@JvmOverloads
fun put(url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return request("PUT", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)
}

@JvmOverloads
fun request(method: String, url: String, headers: Map<String, String?> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null): Response {
    return GenericResponse(GenericRequest(method, url, params, headers, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier)).run {
        this.init()
        this._history.last().apply {
            this@run._history.remove(this)
        }
    }
}

/**
 * Provides a library interface for performing asynchronous requests
 */
class async {
    companion object {

        @JvmOverloads
        fun delete(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            request("DELETE", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier, onError, onResponse)
        }

        @JvmOverloads
        fun get(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            request("GET", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier, onError, onResponse)
        }

        @JvmOverloads
        fun head(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            request("HEAD", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier, onError, onResponse)
        }

        @JvmOverloads
        fun options(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            request("OPTIONS", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier, onError, onResponse)
        }

        @JvmOverloads
        fun patch(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            request("PATCH", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier, onError, onResponse)
        }

        @JvmOverloads
        fun post(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            request("POST", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier, onError, onResponse)
        }

        @JvmOverloads
        fun put(url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            request("PUT", url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier, onError, onResponse)
        }

        @JvmOverloads
        fun request(method: String, url: String, headers: Map<String, String> = mapOf(), params: Map<String, String> = mapOf(), data: Any? = null, json: Any? = null, auth: Authorization? = null, cookies: Map<String, String>? = null, timeout: Double = DEFAULT_TIMEOUT, allowRedirects: Boolean? = null, stream: Boolean = false, files: List<FileLike> = listOf(), sslContext: SSLContext? = null, hostnameVerifier: HostnameVerifier? = null, onError: Throwable.() -> Unit = { throw this }, onResponse: Response.() -> Unit = {}): Unit {
            thread {
                try {
                    onResponse(khttp.request(method, url, headers, params, data, json, auth, cookies, timeout, allowRedirects, stream, files, sslContext, hostnameVerifier))
                } catch (e: Throwable) {
                    onError(e)
                }
            }
        }

    }
}
