/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class KHttpHeadSpec : Spek({
    describe("a head request") {
        val response = head("https://httpbin.org/get")
        context("accessing the status code") {
            val status = response.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
    describe("a head request to a redirecting URL") {
        val response = head("https://httpbin.org/redirect/2")
        context("accessing the status code") {
            val status = response.statusCode
            it("should be 302") {
                assertEquals(302, status)
            }
        }
    }
    describe("a head request to a redirecting URL, specifically allowing redirects") {
        val response = head("https://httpbin.org/redirect/2", allowRedirects = true)
        context("accessing the status code") {
            val status = response.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
})
