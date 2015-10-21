/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.structures.cookie

import me.kyleclemens.khttp.MavenSpek
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull

class CookieJarSpec : MavenSpek() {
    override fun test() {
        given("a CookieJar constructed with Cookies") {
            val cookies = listOf(Cookie("test1", "value1"), Cookie("test2", "value2", mapOf("attr1" to "attrv1")))
            val cookieJar = CookieJar(*cookies.toTypedArray())
            on("inspecting the cookie jar") {
                val size = cookieJar.size()
                it("should have two cookies") {
                    assertEquals(2, size)
                }
            }
            on("accessing a cookie by name") {
                val cookie = cookieJar.getCookie("test1")
                it("should not be null") {
                    assertNotNull(cookie)
                }
                it("should have the same name") {
                    assertEquals("test1", cookie!!.key)
                }
                it("should have the same value") {
                    assertEquals("value1", cookie!!.value)
                }
                it("should have the no attributes") {
                    assertEquals(0, cookie!!.attributes.size())
                }
            }
            on("accessing another cookie by name") {
                val cookie = cookieJar.getCookie("test2")
                it("should not be null") {
                    assertNotNull(cookie)
                }
                it("should have the same name") {
                    assertEquals("test2", cookie!!.key)
                }
                it("should have the same value") {
                    assertEquals("value2", cookie!!.value)
                }
                it("should have the same attributes") {
                    assertEquals(mapOf("attr1" to "attrv1"), cookie!!.attributes)
                }
            }
            on("accessing a cookie that doesn't exist") {
                val cookie = cookieJar.getCookie("test3")
                it("should be null") {
                    assertNull(cookie)
                }
            }
            on("adding a cookie to the cookie jar") {
                val cookie = Cookie("delicious", "cookie", mapOf("edible" to "damn straight"))
                cookieJar.setCookie(cookie)
                val size = cookieJar.size()
                val added = cookieJar.getCookie("delicious")
                it("should have three cookies") {
                    assertEquals(3, size)
                }
                it("should have the same cookie as was added") {
                    assertEquals(added, cookie)
                }
            }
        }
        given("a CookieJar constructed with a map") {
            val cookies = mapOf("test1" to "value1", "test2" to "value2; attr1=attrv1")
            val cookieJar = CookieJar(cookies)
            on("inspecting the cookie jar") {
                val size = cookieJar.size()
                it("should have two cookies") {
                    assertEquals(2, size)
                }
            }
            on("accessing a cookie by name") {
                val cookie = cookieJar.getCookie("test1")
                it("should not be null") {
                    assertNotNull(cookie)
                }
                it("should have the same name") {
                    assertEquals("test1", cookie!!.key)
                }
                it("should have the same value") {
                    assertEquals("value1", cookie!!.value)
                }
                it("should have the no attributes") {
                    assertEquals(0, cookie!!.attributes.size())
                }
            }
            on("accessing another cookie by name") {
                val cookie = cookieJar.getCookie("test2")
                it("should not be null") {
                    assertNotNull(cookie)
                }
                it("should have the same name") {
                    assertEquals("test2", cookie!!.key)
                }
                it("should have the same value") {
                    assertEquals("value2", cookie!!.value)
                }
                it("should have the same attributes") {
                    assertEquals(mapOf("attr1" to "attrv1"), cookie!!.attributes)
                }
            }
            on("accessing a cookie that doesn't exist") {
                val cookie = cookieJar.getCookie("test3")
                it("should be null") {
                    assertNull(cookie)
                }
            }
            on("adding a cookie to the cookie jar") {
                val cookie = Cookie("delicious", "cookie", mapOf("edible" to "damn straight"))
                cookieJar.setCookie(cookie)
                val size = cookieJar.size()
                val added = cookieJar.getCookie("delicious")
                it("should have three cookies") {
                    assertEquals(3, size)
                }
                it("should have the same cookie as was added") {
                    assertEquals(added, cookie)
                }
            }
        }
    }
}
