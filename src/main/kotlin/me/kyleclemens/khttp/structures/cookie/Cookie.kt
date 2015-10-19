/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.structures.cookie

data class Cookie(val key: String, val value: Any, val attributes: Map<String, Any> = mapOf()) {
    val valueWithAttributes: String
        get() {
            if (this.attributes.size() < 1) {
                return this.value.toString()
            }
            return this.value.toString() + "; " + this.attributes.asSequence().joinToString { "${it.getKey()}=${it.getValue()}" }
        }
}
