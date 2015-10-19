/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.structures.cookie

class CookieJar(vararg val cookies: Cookie = arrayOf()) : Map<String, Any> by cookies.toMap({ it.key }, { it.value }) {

    companion object {
        private fun Map<String, Any>.toCookieArray(): Array<Cookie> {
            return this.map {
                val valueList = it.value.toString().split(";").map { it.trim() }
                val value = valueList[0]
                val attributes = if (valueList.size() < 2) mapOf() else {
                    valueList.subList(1, valueList.size()).toMap({ it.split("=")[0].trim() }, { it.split("=")[1].trim() })
                }
                Cookie(it.key, value, attributes)
            }.toTypedArray()
        }
    }

    constructor(cookies: Map<String, Any>) : this(*cookies.toCookieArray())

    override fun toString() = this.cookies.joinToString("; ") { "${it.key}=${it.value}" }
}
