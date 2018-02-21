/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.authorization

import java.util.Base64

data class BearerTokenAuthorization(val token: String) : Authorization {
    override val header: Pair<String, String>
        get() {
            return "Authorization" to "Bearer $token"
        }
}
