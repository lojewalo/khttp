package khttp.structures

import khttp.DEFAULT_TIMEOUT
import khttp.structures.authorization.Authorization
import khttp.structures.files.FileLike
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext

/**
 * This data class represents of a http request requires and optional parameters
 */
data class Builder(var method: String,
                   var url: String,
                   var headers: Map<String, String?> = mapOf(),
                   var params: Map<String, String> = mapOf(),
                   var data: Any? = null,
                   var json: Any? = null,
                   var auth: Authorization? = null,
                   var cookies: Map<String, String>? = null,
                   var timeout: Double = DEFAULT_TIMEOUT,
                   var allowRedirects: Boolean? = null,
                   var stream: Boolean = false,
                   var files: List<FileLike> = listOf(),
                   var sslContext: SSLContext? = null,
                   var hostnameVerifier: HostnameVerifier? = null)