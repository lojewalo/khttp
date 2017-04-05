/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.cookie

import java.util.HashMap

class CookieJar(vararg val cookies: Cookie = arrayOf()) : MutableMap<String, String> by (cookies.associate { it.key to it.valueWithAttributes } as HashMap<String, String>) {

    companion object {
        private fun Map<String, Any>.toCookieArray(): Array<Cookie> {
            return this.map {
                val valueList = it.value.toString().split(";").map(String::trim)
                val value = valueList[0]
                val attributes = if (valueList.size < 2) mapOf<String, String>() else {
                    valueList.subList(1, valueList.size).associate {
                        val k = it.split("=")[0].trim()
                        val split = it.split("=")
                        val v = (if (split.size > 1) split[1] else null)?.trim()
                        k to v
                    }
                }
                Cookie(it.key, value, attributes)
            }.toTypedArray()
        }
    }

    constructor(cookies: Map<String, Any>) : this(*cookies.toCookieArray())

    fun getCookie(key: String): Cookie? {
        val value = this[key] ?: return null
        return Cookie("$key=$value")
    }

    fun setCookie(cookie: Cookie) {
        this[cookie.key] = cookie.valueWithAttributes
    }

    override fun toString() = this.cookies.joinToString("; ") { "${it.key}=${it.value}" }
}
