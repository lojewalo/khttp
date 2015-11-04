/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.authorization

import java.util.Base64

data class BasicAuthorization(val user: String, val password: String) : Authorization {
    override val header: Pair<String, String>
        get() {
            val b64 = Base64.getEncoder().encode("${this.user}:${this.password}".toByteArray()).toString(Charsets.UTF_8)
            return "Authorization" to "Basic $b64"
        }
}
