/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class KHttpDeleteSpec : Spek({
    describe("a delete request") {
        val url = "https://httpbin.org/delete"
        val request = delete(url)
        context("accessing the json") {
            val json = request.jsonObject
            it("should have the same url") {
                assertEquals(url, json.getString("url"))
            }
        }
    }
})
