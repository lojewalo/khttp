/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import khttp.helpers.SslContextUtils
import khttp.responses.Response
import khttp.structures.authorization.BasicAuthorization
import org.awaitility.kotlin.await
import org.json.JSONObject
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.net.MalformedURLException
import java.net.SocketTimeoutException
import java.net.URLEncoder
import java.util.concurrent.TimeUnit
import java.util.zip.GZIPInputStream
import java.util.zip.InflaterInputStream
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KHttpAsyncGetSpec : Spek({
    describe("an async get request") {
        val url = "http://httpbin.org/range/26"
        var error: Throwable? = null
        var response: Response? = null

        async.get(url, onError = { error = this }, onResponse = {response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the string") {
            if (error != null) throw error!!
            val string = response!!.text
            it("should equal the alphabet in lowercase") {
                assertEquals("abcdefghijklmnopqrstuvwxyz", string)
            }
        }
        context("accessing the url") {
            if (error != null) throw error!!
            val resultantURL = response!!.url
            it("should equal the starting url") {
                assertEquals(url, resultantURL)
            }
        }
        context("accessing the status code") {
            if (error != null) throw error!!
            val statusCode = response!!.statusCode
            it("should be 200") {
                assertEquals(200, statusCode)
            }
        }
        context("converting it to a string") {
            if (error != null) throw error!!
            val string = response!!.toString()
            it("should be correct") {
                assertEquals("<Response [200]>", string)
            }
        }
    }
    describe("an async json object get request with parameters") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/get", params = mapOf("a" to "b", "c" to "d"), onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should contain the parameters") {
                val args = json.getJSONObject("args")
                assertEquals("b", args.getString("a"))
                assertEquals("d", args.getString("c"))
            }
        }
    }
    describe("an async json object get request with a map of parameters") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/get", params = mapOf("a" to "b", "c" to "d"), onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should contain the parameters") {
                val args = json.getJSONObject("args")
                assertEquals("b", args.getString("a"))
                assertEquals("d", args.getString("c"))
            }
        }
    }
    describe("an async get request with basic auth") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/basic-auth/khttp/isawesome", auth = BasicAuthorization("khttp", "isawesome"), onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should be authenticated") {
                assertTrue(json.getBoolean("authenticated"))
            }
            it("should have the correct user") {
                assertEquals("khttp", json.getString("user"))
            }
        }
    }
    describe("an async get request with cookies") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/cookies", cookies = mapOf("test" to "success"), onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should have the same cookies") {
                val cookies = json.getJSONObject("cookies")
                assertEquals("success", cookies.getString("test"))
            }
        }
    }
    describe("an async get request that redirects and allowing redirects") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/redirect-to?url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should have the redirected url") {
                assertEquals("http://httpbin.org/get", json.getString("url"))
            }
        }
    }
    describe("an async get request that redirects with HTTP 307 and allowing redirects") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/redirect-to?status_code=307&url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should have the redirected url") {
                assertEquals("http://httpbin.org/get", json.getString("url"))
            }
        }
    }
    describe("an async get request that redirects with HTTP 308 and allowing redirects") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/redirect-to?status_code=308&url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should have the redirected url") {
                assertEquals("http://httpbin.org/get", json.getString("url"))
            }
        }
    }
    describe("an async get request that redirects and disallowing redirects") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/redirect-to?url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", allowRedirects = false, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the status code") {
            if (error != null) throw error!!
            val code = response!!.statusCode
            it("should be 302") {
                assertEquals(302, code)
            }
        }
    }
    describe("an async get request that redirects with HTTP 307 and disallowing redirects") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/redirect-to?status_code=307&url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", allowRedirects = false, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the status code") {
            if (error != null) throw error!!
            val code = response!!.statusCode
            it("should be 307") {
                assertEquals(307, code)
            }
        }
    }
    describe("an async get request that redirects with HTTP 308 and disallowing redirects") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/redirect-to?status_code=308&url=${URLEncoder.encode("http://httpbin.org/get", "utf-8")}", allowRedirects = false, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the status code") {
            if (error != null) throw error!!
            val code = response!!.statusCode
            it("should be 308") {
                assertEquals(308, code)
            }
        }
    }
    describe("an async get request that redirects five times") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/redirect/5", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should have the get url") {
                assertEquals("http://httpbin.org/get", json.getString("url"))
            }
        }
    }
    describe("an async get request that takes ten seconds to complete") {
        var error: Throwable? = null

        async.get("http://httpbin.org/delay/10", timeout = 1.0, onError = { error = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { error != null }

        context("request") {
            it("should throw a timeout exception") {
                assertFailsWith(SocketTimeoutException::class) {
                    throw error!!
                }
            }
        }
    }
    describe("an async get request that sets cookies without redirects") {
        val cookieName = "test"
        val cookieValue = "quite"

        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/cookies/set?$cookieName=$cookieValue", allowRedirects = false, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("inspecting the cookies") {
            if (error != null) throw error!!
            val cookies = response!!.cookies
            it("should set a cookie") {
                assertEquals(1, cookies.size)
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
    describe("an async get request that sets cookies with redirects") {
        val cookieName = "test"
        val cookieValue = "quite"

        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/cookies/set?$cookieName=$cookieValue", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("inspecting the cookies") {
            if (error != null) throw error!!
            val cookies = response!!.cookies
            it("should set a cookie") {
                assertEquals(1, cookies.size)
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
    describe("an async get request that sets multiple cookies with redirects") {
        val cookieNameOne = "test"
        val cookieValueOne = "quite"
        val cookieNameTwo = "derp"
        val cookieValueTwo = "herp"

        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/cookies/set?$cookieNameOne=$cookieValueOne&$cookieNameTwo=$cookieValueTwo", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("inspecting the cookies") {
            if (error != null) throw error!!
            val cookies = response!!.cookies
            it("should set two cookies") {
                assertEquals(2, cookies.size)
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
    describe("an async gzip get request") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("https://httpbin.org/gzip", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the stream") {
            if (error != null) throw error!!
            val stream = response!!.raw
            it("should be a GZIPInputStream") {
                assertTrue(stream is GZIPInputStream)
            }
        }
        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should be gzipped") {
                assertTrue(json.getBoolean("gzipped"))
            }
        }
    }
    describe("an async deflate get request") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("https://httpbin.org/deflate", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the stream") {
            if (error != null) throw error!!
            val stream = response!!.raw
            it("should be a InflaterInputStream") {
                assertTrue(stream is InflaterInputStream)
            }
        }
        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should be deflated") {
                assertTrue(json.getBoolean("deflated"))
            }
        }
    }
    describe("an async get request that returns 418") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("https://httpbin.org/status/418", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 418") {
                assertEquals(418, status)
            }
        }
        context("accessing the text") {
            if (error != null) throw error!!
            val text = response!!.text
            it("should contain \"teapot\"") {
                assertTrue(text.contains("teapot"))
            }
        }
    }
    describe("an async get request for a UTF-8 document") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("https://httpbin.org/encoding/utf8", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("checking the encoding") {
            if (error != null) throw error!!
            val encoding = response!!.encoding
            it("should be UTF-8") {
                assertEquals(Charsets.UTF_8, encoding)
            }
        }
        context("reading the text") {
            if (error != null) throw error!!
            val text = response!!.text
            it("should contain ∮") {
                assertTrue(text.contains("∮"))
            }
        }
        context("changing the encoding") {
            if (error != null) throw error!!
            response!!.encoding = Charsets.ISO_8859_1
            val encoding = response!!.encoding
            it("should be ISO-8859-1") {
                assertEquals(Charsets.ISO_8859_1, encoding)
            }
        }
        context("reading the text") {
            if (error != null) throw error!!
            val text = response!!.text
            it("should not contain ∮") {
                assertFalse(text.contains("∮"))
            }
        }
    }
    describe("an async unsupported khttp schema") {
        var error: Throwable? = null

        async.get("ftp://google.com", onError = { error = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { error != null }

        context("construction") {
            it("should throw an IllegalArgumentException") {
                assertFailsWith(IllegalArgumentException::class) {
                    throw error!!
                }
            }
        }
    }
    describe("an async unsupported Java schema") {
        var error: Throwable? = null

        async.get("gopher://google.com", onError = { error = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { error != null }

        context("construction") {
            it("should throw a MalformedURLException") {
                assertFailsWith(MalformedURLException::class) {
                    throw error!!
                }
            }
        }
    }
    describe("an async request with a user agent set") {
        val userAgent = "khttp/test"

        var error: Throwable? = null
        var response: Response? = null

        async.get("https://httpbin.org/user-agent", headers = mapOf("User-Agent" to userAgent), onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val responseUserAgent = json.getString("user-agent")
            it("should have the same user agent") {
                assertEquals(userAgent, responseUserAgent)
            }
        }
    }
    describe("an async request with a port") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("https://httpbin.org:443/get", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should not be null") {
                assertNotNull(json)
            }
        }
    }
    describe("an async get request for a JSON array") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://jsonplaceholder.typicode.com/users", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonArray
            it("should have ten items") {
                assertEquals(10, json.length())
            }
        }
    }
    describe("an async non-streaming get request") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("https://httpbin.org/get", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("checking the bytes available to be read") {
            if (error != null) throw error!!
            val available = response!!.raw.available()
            it("should be 0") {
                assertEquals(0, available)
            }
        }
    }
    describe("an async streaming get request with a streaming line response") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/stream/4", stream = true, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("iterating over the lines") {
            if (error != null) throw error!!
            val iterator = response!!.lineIterator()
            var counter = 0
            for (line in iterator) {
                val json = JSONObject(line.toString(response!!.encoding))
                assertEquals(counter++, json.getInt("id"))
            }
            it("should have iterated 4 times") {
                assertEquals(4, counter)
            }
        }
    }
    describe("an async streaming get request with a streaming byte response") {
        var error: Throwable? = null
        var response: Response? = null

        async.get("http://httpbin.org/stream-bytes/4?seed=1", stream = true, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("iterating over the bytes") {
            if (error != null) throw error!!
            val iterator = response!!.contentIterator(chunkSize = 1)
            var counter = 0
            val expected = byteArrayOf(0x22, 0xD8.toByte(), 0xC3.toByte(), 0x41)
            for (byte in iterator) {
                assertEquals(1, byte.size)
                assertEquals(expected[counter++], byte[0])
            }
            it("should have iterated 4 times") {
                assertEquals(4, counter)
            }
        }
    }
    describe("an async streaming get request without even lines") {
        val url = "https://httpbin.org/bytes/1690?seed=1"

        var error: Throwable? = null
        var response: Response? = null

        async.get(url, stream = true, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("iterating the lines") {
            if (error != null) throw error!!
            val iterator = response!!.lineIterator()
            val bytes = iterator.asSequence().toList().flatMap { it.toList() }
            val contentWithoutBytes = get(url).content.toList().filter { it != '\r'.toByte() && it != '\n'.toByte() }
            it("should be the same as the content without line breaks") {
                assertEquals(contentWithoutBytes, bytes)
            }
        }
    }

    describe("an async request where the client needs to authenticate itself by certificates") {
        val url = "https://httpbin.org/bytes/1690?seed=1"
        val sslContext = SslContextUtils.createFromKeyMaterial("keystores/badssl.com-client.p12", "badssl.com".toCharArray())

        var error: Throwable? = null
        var response: Response? = null

        async.get(url, sslContext = sslContext, stream = true, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("iterating the lines") {
            if (error != null) throw error!!
            val statusCode = response!!.statusCode
            it("should be ok") {
                if (statusCode == 400) {
                    print("WARNING: Certificate may have expired and needs to be updated")
                } else {
                    assertEquals(200, statusCode)
                }
            }
        }
    }
})
