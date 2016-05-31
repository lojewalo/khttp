/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals

class KHttpHeadSpec : Spek({
    given("a head request") {
        val response = head("https://httpbin.org/get")
        on("accessing the status code") {
            val status = response.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
    given("a head request to a redirecting URL") {
        val response = head("https://httpbin.org/redirect/2")
        on("accessing the status code") {
            val status = response.statusCode
            it("should be 302") {
                assertEquals(302, status)
            }
        }
    }
    given("a head request to a redirecting URL, specifically allowing redirects") {
        val response = head("https://httpbin.org/redirect/2", allowRedirects = true)
        on("accessing the status code") {
            val status = response.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
})
