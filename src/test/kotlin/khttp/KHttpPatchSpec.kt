/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import kotlin.test.assertEquals

class KHttpPatchSpec : MavenSpek() {
    override fun test() {
        given("a patch request") {
            val url = "https://httpbin.org/patch"
            val request = patch(url)
            on("accessing the json") {
                val json = request.jsonObject
                it("should have the same url") {
                    assertEquals(url, json.getString("url"))
                }
            }
        }
    }
}
