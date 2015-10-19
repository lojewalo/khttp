/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import me.kyleclemens.khttp.helpers.StringIterable
import me.kyleclemens.khttp.structures.parameters.FormParameters
import me.kyleclemens.khttp.structures.parameters.Parameters
import org.jetbrains.spek.api.shouldThrow
import org.json.JSONArray
import org.json.JSONObject
import kotlin.test.assertEquals
import kotlin.test.assertTrue

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
        given("a request with json as an Iterable") {
            val jsonArray = StringIterable("a word")
            val request = post("http://httpbin.org/post", json = jsonArray)
            on("accessing the json") {
                val json = request.jsonObject
                val returnedJSON = json.getJSONArray("json")
                it("should be equal") {
                    assertEquals(jsonArray.string, String(returnedJSON.mapIndexed { i, any -> returnedJSON.getString(i).charAt(0) }.toCharArray()))
                }
            }
        }
        given("a request with json as a List") {
            val jsonList = listOf("A thing", "another thing")
            val request = post("https://httpbin.org/post", json = jsonList)
            on("accessing the json") {
                val json = request.jsonObject
                val returnedJSON = json.getJSONArray("json")
                it("should have an equal first element") {
                    assertEquals(jsonList[0], returnedJSON.getString(0))
                }
                it("should have an equal second element") {
                    assertEquals(jsonList[1], returnedJSON.getString(1))
                }
            }
        }
        given("a request with json as an Array") {
            val jsonArray = arrayOf("A thing", "another thing")
            val request = post("https://httpbin.org/post", json = jsonArray)
            on("accessing the json") {
                val json = request.jsonObject
                val returnedJSON = json.getJSONArray("json")
                it("should have an equal first element") {
                    assertEquals(jsonArray[0], returnedJSON.getString(0))
                }
                it("should have an equal second element") {
                    assertEquals(jsonArray[1], returnedJSON.getString(1))
                }
            }
        }
        given("a request with json as a JSONObject") {
            val jsonObject = JSONObject("""{"valid": true}""")
            val request = post("https://httpbin.org/post", json = jsonObject)
            on("accessing the json") {
                val json = request.jsonObject
                val returnedJSON = json.getJSONObject("json")
                it("should have a true value for the key \"valid\"") {
                    assertTrue(returnedJSON.getBoolean("valid"))
                }
            }
        }
        given("a request with json as a JSONArray") {
            val jsonObject = JSONArray("[true]")
            val request = post("https://httpbin.org/post", json = jsonObject)
            on("accessing the json") {
                val json = request.jsonObject
                val returnedJSON = json.getJSONArray("json")
                it("should have a true value for the first key") {
                    assertTrue(returnedJSON.getBoolean(0))
                }
            }
        }
        given("a request with invalid json") {
            on("construction") {
                it("should throw an IllegalArgumentException") {
                    shouldThrow(IllegalArgumentException::class.java) {
                        post("https://httpbin.org/post", json = object {})
                    }
                }
            }
        }
    }
}
