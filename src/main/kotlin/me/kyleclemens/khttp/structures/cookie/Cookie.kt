/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.structures.cookie

data class Cookie(val key: String, val value: Any, val attributes: Map<String, Any> = mapOf()) {

    companion object {
        private fun String.toCookie(): Cookie {
            val split = this.split("=", limit = 2)
            check(split.size == 2) { "\"$this\" is not a cookie." }
            val key = split[0].trim()
            val valueSplit = split[1].split(";")
            val value = valueSplit[0].trim()
            val attributes = if (valueSplit.size < 2) mapOf() else {
                valueSplit.subList(1, valueSplit.size).toMap({ it.split("=")[0].trim() }, { it.split("=")[1].trim() })
            }
            return Cookie(key, value, attributes)
        }
    }

    constructor(string: String) : this(string.toCookie())

    // TODO: This seems dumb. There must be a better way.
    constructor(cookie: Cookie) : this(cookie.key, cookie.value, cookie.attributes)

    val valueWithAttributes: String
        get() {
            if (this.attributes.size < 1) {
                return this.value.toString()
            }
            return this.value.toString() + "; " + this.attributes.asSequence().joinToString { "${it.key}=${it.value}" }
        }
}
