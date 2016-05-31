/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.authorization

import org.jetbrains.spek.api.Spek
import java.util.Base64
import kotlin.test.assertEquals

class BasicAuthorizationSpec : Spek({
    given("a BasicAuthorization object") {
        val username = "test"
        val password = "hunter2"
        val base64 = "Basic " + Base64.getEncoder().encode("$username:$password".toByteArray()).toString(Charsets.UTF_8)
        val auth = BasicAuthorization(username, password)
        on("checking the username") {
            val authUsername = auth.user
            it("should equal the original") {
                assertEquals(username, authUsername)
            }
        }
        on("checking the password") {
            val authPassword = auth.password
            it("should equal the original") {
                assertEquals(password, authPassword)
            }
        }
        on("checking the header") {
            val (header, value) = auth.header
            it("should have a first value of \"Authorization\"") {
                assertEquals("Authorization", header)
            }
            it("should have a second value of the Base64 encoding") {
                assertEquals(base64, value)
            }
        }
    }
})
