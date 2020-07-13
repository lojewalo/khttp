/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class KHttpOptionsSpec : Spek({
    describe("an options request") {
        val request = options("https://httpbin.org/get")
        context("accessing the status code") {
            val status = request.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
})
