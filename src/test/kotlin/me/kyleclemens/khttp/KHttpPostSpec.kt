/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import me.kyleclemens.khttp.structures.FormParameters
import me.kyleclemens.khttp.structures.Parameters
import kotlin.test.assertEquals

class KHttpPostSpec : MavenSpek() {
    override fun test() {
        given("a post request with raw data") {
            val request = post("http://httpbin.org/post", data = "Hello, world!")
            on("accessing json") {
                val json = request.jsonObject
                it("should contain the data") {
                    assertEquals("Hello, world!", json.getString("data"))
                }
            }
        }
        given("a post request with parameters") {
            val request = post("http://httpbin.org/post", data = Parameters("a" to "b", "c" to "d"))
            on("accessing json") {
                val json = request.jsonObject
                it("should contain the data") {
                    assertEquals("a=b&c=d", json.getString("data"))
                }
            }
        }
        given("a post form request") {
            val request = post("http://httpbin.org/post", data = FormParameters("a" to "b", "c" to "d"))
            on("accessing json") {
                val json = request.jsonObject
                it("should contain the form data") {
                    val form = json.getJSONObject("form")
                    assertEquals("b", form.getString("a"))
                    assertEquals("d", form.getString("c"))
                }
            }
        }
        given("a request with json as a Map") {
            val jsonMap = mapOf("books" to listOf(mapOf("title" to "Pride and Prejudice", "author" to "Jane Austen")))
            val request = post("http://httpbin.org/post", json = jsonMap)
            on("accessing the json") {
                val json = request.jsonObject
                val returnedJSON = json.getJSONObject("json")
                val returnedBooks = returnedJSON.getJSONArray("books")
                it("should be the same length") {
                    assertEquals(jsonMap.size(), returnedJSON.length())
                }
                it("should have the same book length") {
                    assertEquals(jsonMap.get("books")!!.size(), returnedBooks.length())
                }
                val firstBook = jsonMap.get("books")!!.get(0)
                val firstReturnedBook = returnedBooks.getJSONObject(0)
                it("should have the same book title") {
                    assertEquals(firstBook["title"], firstReturnedBook.getString("title"))
                }
                it("should have the same book author") {
                    assertEquals(firstBook["author"], firstReturnedBook.getString("author"))
                }
            }
        }
    }
}
