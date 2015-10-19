/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import me.kyleclemens.khttp.structures.Parameters
import kotlin.test.assertEquals

class KHttpGetSpec : MavenSpek() {
    override fun test() {
        given("a get request") {
            val request = get("http://httpbin.org/range/26")
            on("accessing the string") {
                val string = request.text
                it("should equal the alphabet in lowercase") {
                    assertEquals("abcdefghijklmnopqrstuvwxyz", string)
                }
            }
        }
        given("a json object get request with parameters") {
            val request = get("http://httpbin.org/get", parameters = Parameters("a" to "b", "c" to "d"))
            on("accessing the json") {
                val json = request.jsonObject
                it("should contain the parameters") {
                    val args = json.getJSONObject("args")
                    assertEquals("b", args.getString("a"))
                    assertEquals("d", args.getString("c"))
                }
            }
        }
    }
}
