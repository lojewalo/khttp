/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import me.kyleclemens.khttp.structures.authorization.BasicAuthorization
import me.kyleclemens.khttp.structures.parameters.Parameters
import org.jetbrains.spek.api.shouldThrow
import java.net.SocketTimeoutException
import java.net.URLEncoder
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        given("a get request with basic auth") {
            val request = get("http://httpbin.org/basic-auth/khttp/isawesome", auth = BasicAuthorization("khttp", "isawesome"))
            on("accessing the json") {
                val json = request.jsonObject
                it("should be authenticated") {
                    assertTrue(json.getBoolean("authenticated"))
                }
                it("should have the correct user") {
                    assertEquals("khttp", json.getString("user"))
                }
            }
        }
        given("a get request with cookies") {
            val request = get("http://httpbin.org/cookies", cookies = mapOf("test" to "success"))
            on("accessing the json") {
                val json = request.jsonObject
                it("should have the same cookies") {
                    val cookies = json.getJSONObject("cookies")
                    assertEquals("success", cookies.getString("test"))
                }
            }
        }
        given("a get request that redirects and allowing redirects") {
            val request = get("http://httpbin.org/redirect-to?url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}")
            on("accessing the json") {
                val json = request.jsonObject
                it("should have the redirected url") {
                    assertEquals("http://httpbin.org/get", json.getString("url"))
                }
            }
        }
        given("a get request that redirects and disallowing redirects") {
            val request = get("http://httpbin.org/redirect-to?url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", allowRedirects = false)
            on("accessing the status code") {
                val code = request.status
                it("should be 302") {
                    assertEquals(302, code)
                }
            }
        }
        given("a get request that takes ten seconds to complete") {
            val request = get("http://httpbin.org/delay/10", timeout = 1)
            on("accessing anything") {
                it("should throw a timeout exception") {
                    shouldThrow(SocketTimeoutException::class.java) {
                        request.raw
                    }
                }
            }
        }
    }
}
