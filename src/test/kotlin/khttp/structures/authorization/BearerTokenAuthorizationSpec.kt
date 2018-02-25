/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.authorization

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

class BearerTokenAuthorizationSpec : Spek({
    given("a BearerTokenAuthorization object") {
        // Sample JWT token. See https://jwt.io/
        val token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiYWRtaW4iOnRydWV9.TJVA95OrM7E2cBab30RMHrHDcEfxjoYZgeFONFh7HgQ"
        val auth = BearerTokenAuthorization(token)

        on("checking the header") {
            val (header, value) = auth.header
            it("should have a first value of \"Authorization\"") {
                assertEquals("Authorization", header)
            }
            it("should have a second value of the Base64 encoding") {
                assertEquals("Bearer $token", value)
            }
        }
    }
})
