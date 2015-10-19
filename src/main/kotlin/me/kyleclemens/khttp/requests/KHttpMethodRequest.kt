/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.requests

import me.kyleclemens.khttp.structures.authorization.Authorization
import me.kyleclemens.khttp.structures.parameters.Parameters
import java.net.HttpURLConnection
import java.net.ProtocolException

abstract class KHttpMethodRequest(
    method: String,
    route: String,
    parameters: Parameters,
    headers: MutableMap<String, String>,
    data: Any?,
    json: Any?,
    auth: Authorization?,
    cookies: Map<String, Any>?,
    timeout: Int,
    allowRedirects: Boolean
) : KHttpGenericRequest(route, parameters, headers, data, json, auth, cookies, timeout, allowRedirects) {

    init {
        this.initializers.add {
            it.forceMethod(method)
        }
    }

    private fun HttpURLConnection.forceMethod(method: String) {
        try {
            this.requestMethod = method
        } catch (ex: ProtocolException) {
            try {
                (this.javaClass.getDeclaredField("delegate").apply { this.isAccessible = true }.get(this) as HttpURLConnection?)?.forceMethod(method)
            } catch (ex: NoSuchFieldException) {
                // ignore
            }
            (this.javaClass.getSuperclasses() + this.javaClass).forEach {
                try {
                    it.getDeclaredField("method").apply { this.isAccessible = true }.set(this, method)
                } catch (ex: NoSuchFieldException) {
                    // ignore
                }
            }
        }
    }

    private fun <T> Class<T>.getSuperclasses(): List<Class<in T>> {
        val list = arrayListOf<Class<in T>>()
        var superclass = this.superclass
        while (superclass != null) {
            list.add(superclass)
            superclass = superclass.superclass
        }
        return list
    }

}
