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
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KHttpGetSpec : MavenSpek() {
    override fun test() {
        given("a get request") {
            val url = "http://httpbin.org/range/26"
            val response = get(url)
            on("accessing the string") {
                val string = response.text
                it("should equal the alphabet in lowercase") {
                    assertEquals("abcdefghijklmnopqrstuvwxyz", string)
                }
            }
            on("accessing the url") {
                val resultantURL = response.url
                it("should equal the starting url") {
                    assertEquals(url, resultantURL)
                }
            }
        }
        given("a json object get request with parameters") {
            val response = get("http://httpbin.org/get", parameters = Parameters("a" to "b", "c" to "d"))
            on("accessing the json") {
                val json = response.jsonObject
                it("should contain the parameters") {
                    val args = json.getJSONObject("args")
                    assertEquals("b", args.getString("a"))
                    assertEquals("d", args.getString("c"))
                }
            }
        }
        given("a json object get request with a map of parameters") {
            val response = get("http://httpbin.org/get", parameters = mapOf("a" to "b", "c" to "d"))
            on("accessing the json") {
                val json = response.jsonObject
                it("should contain the parameters") {
                    val args = json.getJSONObject("args")
                    assertEquals("b", args.getString("a"))
                    assertEquals("d", args.getString("c"))
                }
            }
        }
        given("a get request with basic auth") {
            val response = get("http://httpbin.org/basic-auth/khttp/isawesome", auth = BasicAuthorization("khttp", "isawesome"))
            on("accessing the json") {
                val json = response.jsonObject
                it("should be authenticated") {
                    assertTrue(json.getBoolean("authenticated"))
                }
                it("should have the correct user") {
                    assertEquals("khttp", json.getString("user"))
                }
            }
        }
        given("a get request with cookies") {
            val response = get("http://httpbin.org/cookies", cookies = mapOf("test" to "success"))
            on("accessing the json") {
                val json = response.jsonObject
                it("should have the same cookies") {
                    val cookies = json.getJSONObject("cookies")
                    assertEquals("success", cookies.getString("test"))
                }
            }
        }
        given("a get request that redirects and allowing redirects") {
            val response = get("http://httpbin.org/redirect-to?url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}")
            on("accessing the json") {
                val json = response.jsonObject
                it("should have the redirected url") {
                    assertEquals("http://httpbin.org/get", json.getString("url"))
                }
            }
        }
        given("a get request that redirects and disallowing redirects") {
            val response = get("http://httpbin.org/redirect-to?url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", allowRedirects = false)
            on("accessing the status code") {
                val code = response.statusCode
                it("should be 302") {
                    assertEquals(302, code)
                }
            }
        }
        given("a get request that redirects five times") {
            val response = get("http://httpbin.org/redirect/5")
            on("accessing the json") {
                val json = response.jsonObject
                it("should have the get url") {
                    assertEquals("http://httpbin.org/get", json.getString("url"))
                }
            }
        }
        given("a get request that takes ten seconds to complete") {
            val response = get("http://httpbin.org/delay/10", timeout = 1)
            on("accessing anything") {
                it("should throw a timeout exception") {
                    shouldThrow(SocketTimeoutException::class.java) {
                        response.raw
                    }
                }
            }
        }
        given("a get request that sets cookies without redirects") {
            val cookieName = "test"
            val cookieValue = "quite"
            val response = get("http://httpbin.org/cookies/set?$cookieName=$cookieValue", allowRedirects = false)
            on("connection") {
                val cookies = response.cookies
                it("should set a cookie") {
                    assertEquals(1, cookies.size())
                }
                val cookie = cookies.getCookie(cookieName)
                val text = cookies[cookieName]
                it("should have the specified cookie name") {
                    assertNotNull(cookie)
                }
                it("should have the specified text") {
                    assertNotNull(text)
                }
                it("should have the same value") {
                    assertEquals(cookieValue, cookie!!.value)
                }
                it("should have the same text value") {
                    // Attributes ignored
                    assertEquals(cookieValue, text!!.toString().split(";")[0])
                }
            }
        }
        given("a get request that sets cookies with redirects") {
            val cookieName = "test"
            val cookieValue = "quite"
            val response = get("http://httpbin.org/cookies/set?$cookieName=$cookieValue")
            on("connection") {
                val cookies = response.cookies
                it("should set a cookie") {
                    assertEquals(1, cookies.size())
                }
                val cookie = cookies.getCookie(cookieName)
                val text = cookies[cookieName]
                it("should have the specified cookie name") {
                    assertNotNull(cookie)
                }
                it("should have the specified text") {
                    assertNotNull(text)
                }
                it("should have the same value") {
                    assertEquals(cookieValue, cookie!!.value)
                }
                it("should have the same text value") {
                    // Attributes ignored
                    assertEquals(cookieValue, text!!.toString().split(";")[0])
                }
            }
        }
    }
}
