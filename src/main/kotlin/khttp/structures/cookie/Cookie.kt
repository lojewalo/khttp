/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.cookie

data class Cookie(val key: String, val value: Any, val attributes: Map<String, Any?> = mapOf()) {

    companion object {
        private fun String.toCookie(): Cookie {
            val split = this.split("=", limit = 2)
            require(split.size == 2) { "\"$this\" is not a cookie." }
            val key = split[0].trim()
            val valueSplit = split[1].split(";")
            val value = valueSplit[0].trim()
            val attributes = if (valueSplit.size < 2) mapOf<String, Any?>() else {
                valueSplit.subList(1, valueSplit.size).associate { it.split("=")[0].trim() to it.split("=").getOrNull(1)?.trim() }
            }
            return Cookie(key, value, attributes)
        }
    }

    constructor(string: String) : this(string.toCookie())

    // TODO: This seems dumb. There must be a better way.
    internal constructor(cookie: Cookie) : this(cookie.key, cookie.value, cookie.attributes)

    val valueWithAttributes: String
        get() {
            if (this.attributes.isEmpty()) {
                return this.value.toString()
            }
            return this.value.toString() + "; " + this.attributes.asSequence().joinToString("; ") { if (it.value != null) "${it.key}=${it.value}" else "${it.key}" }
        }
}
