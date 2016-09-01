/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.cookie

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull
import kotlin.test.assertTrue

class CookieSpec : Spek({
    given("a cookie as a string") {
        val key = "password"
        val value = "hunter2"
        val cookieString = "$key=$value; Path=/"
        val cookie = Cookie(cookieString)
        on("accessing the key") {
            val cookieKey = cookie.key
            it("should be the same as in the string") {
                assertEquals(key, cookieKey)
            }
        }
        on("accessing the value") {
            val cookieValue = cookie.value
            it("should be the same as in the string") {
                assertEquals(value, cookieValue)
            }
        }
        on("accessing the value with attributes") {
            it("should be the same as in the string") {
                assertEquals("$value; Path=/", cookie.valueWithAttributes)
            }
        }
        on("accessing the attributes") {
            val attributes = cookie.attributes
            it("should have one") {
                assertEquals(1, attributes.size)
            }
            it("should have a Path key") {
                assertTrue("Path" in attributes)
            }
            it("should have a / value for the Path key") {
                assertEquals("/", attributes["Path"])
            }
        }
    }
    given("a cookie as a string with an attribute without a value") {
        val key = "password"
        val value = "hunter2"
        val cookieString = "$key=$value; Path=/; Awesome"
        val cookie = Cookie(cookieString)
        on("accessing the value with attributes") {
            it("should be the same as in the string") {
                assertEquals("$value; Path=/; Awesome", cookie.valueWithAttributes)
            }
        }
        on("accessing the attributes") {
            val attributes = cookie.attributes
            it("should have two") {
                assertEquals(2, attributes.size)
            }
            it("should have a Path key") {
                assertTrue("Path" in attributes)
            }
            it("should have a / value for the Path key") {
                assertEquals("/", attributes["Path"])
            }
            it("should have an Awesome key") {
                assertTrue("Awesome" in attributes)
            }
            it("should have a null value for the Awesome key") {
                assertNull(attributes["Awesome"])
            }
        }
    }
    given("an invalid cookie as a string") {
        on("construction") {
            it("should throw an IllegalStateException") {
                assertFailsWith(IllegalArgumentException::class) {
                    Cookie("wow")
                }
            }
        }
    }
})
