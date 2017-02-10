/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.parameters

import java.net.URLEncoder

class Parameters(vararg val parameters: Pair<String, String>) : Map<String, String> by mapOf(*parameters) {

    constructor(parameters: Map<String, String>) : this(*parameters.toList().toTypedArray())

    override fun toString(): String {
        if (this.isEmpty()) return ""
        return buildString {
            for ((key, value) in this@Parameters) {
                if (this.isNotEmpty()) this.append("&")
                this.append(key, "=", URLEncoder.encode(value, "UTF-8"))
            }
        }
    }

}
