/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import me.kyleclemens.khttp.structures.authorization.BasicAuthorization
import org.jetbrains.spek.api.shouldThrow
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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
            val response = get("http://httpbin.org/get", params = mapOf("a" to "b", "c" to "d"))
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
            val response = get("http://httpbin.org/get", params = mapOf("a" to "b", "c" to "d"))
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
            val response = get("http://httpbin.org/delay/10", timeout = 1.0)
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
        given("a get request that sets multiple cookies with redirects") {
            val cookieNameOne = "test"
            val cookieValueOne = "quite"
            val cookieNameTwo = "derp"
            val cookieValueTwo = "herp"
            val response = get("http://httpbin.org/cookies/set?$cookieNameOne=$cookieValueOne&$cookieNameTwo=$cookieValueTwo")
            on("connection") {
                val cookies = response.cookies
                it("should set two cookies") {
                    assertEquals(2, cookies.size())
                }
                val cookie = cookies.getCookie(cookieNameOne)
                val text = cookies[cookieNameOne]
                it("should have the specified cookie name") {
                    assertNotNull(cookie)
                }
                it("should have the specified text") {
                    assertNotNull(text)
                }
                it("should have the same value") {
                    assertEquals(cookieValueOne, cookie!!.value)
                }
                it("should have the same text value") {
                    // Attributes ignored
                    assertEquals(cookieValueOne, text!!.toString().split(";")[0])
                }
                val cookieTwo = cookies.getCookie(cookieNameTwo)
                val textTwo = cookies[cookieNameTwo]
                it("should have the specified cookie name") {
                    assertNotNull(cookieTwo)
                }
                it("should have the specified text") {
                    assertNotNull(textTwo)
                }
                it("should have the same value") {
                    assertEquals(cookieValueTwo, cookieTwo!!.value)
                }
                it("should have the same text value") {
                    // Attributes ignored
                    assertEquals(cookieValueTwo, textTwo!!.toString().split(";")[0])
                }
            }
        }
        given("a gzip get request") {
            val response = get("https://httpbin.org/gzip")
            on("accessing the stream") {
                val stream = response.raw
                it("should be a GZIPInputStream") {
                    assertTrue(stream is GZIPInputStream)
                }
            }
            on("accessing the json") {
                val json = response.jsonObject
                it("should be gzipped") {
                    assertTrue(json.getBoolean("gzipped"))
                }
            }
        }
        given("a deflate get request") {
            val response = get("https://httpbin.org/deflate")
            on("accessing the stream") {
                val stream = response.raw
                it("should be a InflaterInputStream") {
                    assertTrue(stream is InflaterInputStream)
                }
            }
            on("accessing the json") {
                val json = response.jsonObject
                it("should be deflated") {
                    assertTrue(json.getBoolean("deflated"))
                }
            }
        }
        given("a get request that returns 418") {
            val response = get("https://httpbin.org/status/418")
            on("accessing the status code") {
                val status = response.statusCode
                it("should be 418") {
                    assertEquals(418, status)
                }
            }
            on("accessing the text") {
                val text = response.text
                it("should contain \"teapot\"") {
                    assertTrue(text.contains("teapot"))
                }
            }
        }
        given("a get request for a UTF-8 document") {
            val response = get("https://httpbin.org/encoding/utf8")
            on("checking the encoding") {
                val encoding = response.encoding
                it("should be UTF-8") {
                    assertEquals(Charsets.UTF_8, encoding)
                }
            }
            on("reading the text") {
                val text = response.text
                it("should contain ∮") {
                    assertTrue(text.contains("∮"))
                }
            }
            on("changing the encoding") {
                response.encoding = Charsets.ISO_8859_1
                val encoding = response.encoding
                it("should be ISO-8859-1") {
                    assertEquals(Charsets.ISO_8859_1, encoding)
                }
            }
            on("reading the text") {
                val text = response.text
                it("should not contain ∮") {
                    assertFalse(text.contains("∮"))
                }
            }
        }
        given("an unsupported khttp schema") {
            on("construction") {
                it("should throw an IllegalArgumentException") {
                    shouldThrow(IllegalArgumentException::class.java) {
                        get("ftp://google.com")
                    }
                }
            }
        }
        given("an unsupported Java schema") {
            on("construction") {
                it("should throw a MalformedURLException") {
                    shouldThrow(MalformedURLException::class.java) {
                        get("gopher://google.com")
                    }
                }
            }
        }
        given("a request with a user agent set") {
            val userAgent = "khttp/test"
            val request = get("https://httpbin.org/user-agent", headers = mapOf("User-Agent" to userAgent))
            on("accessing the json") {
                val json = request.jsonObject
                val responseUserAgent = json.getString("user-agent")
                it("should have the same user agent") {
                    assertEquals(userAgent, responseUserAgent)
                }
            }
        }
        given("a request with a port") {
            val request = get("https://httpbin.org:443/get")
            on("accessing the json") {
                val json = request.jsonObject
                it("should not be null") {
                    assertNotNull(json)
                }
            }
        }
    }
}
