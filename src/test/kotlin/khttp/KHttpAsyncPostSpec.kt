/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import khttp.extensions.fileLike
import khttp.helpers.AsyncUtil
import khttp.helpers.AsyncUtil.Companion.error
import khttp.helpers.AsyncUtil.Companion.errorCallback
import khttp.helpers.AsyncUtil.Companion.response
import khttp.helpers.AsyncUtil.Companion.responseCallback
import khttp.helpers.StringIterable
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.util.Base64
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class KHttpAsyncPostSpec : Spek({
    given("an async post request with raw data") {
        beforeGroup {
            AsyncUtil.execute { async.post("http://httpbin.org/post", data = "Hello, world!", onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should contain the data") {
                assertEquals("Hello, world!", json.getString("data"))
            }
        }
    }
    given("an async post form request") {
        beforeGroup {
            AsyncUtil.execute { async.post("http://httpbin.org/post", data = mapOf("a" to "b", "c" to "d"), onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should contain the form data") {
                val form = json.getJSONObject("form")
                assertEquals("b", form.getString("a"))
                assertEquals("d", form.getString("c"))
            }
        }
    }
    given("an async request with json as a Map") {
        val jsonMap = mapOf("books" to listOf(mapOf("title" to "Pride and Prejudice", "author" to "Jane Austen")))
        beforeGroup {
            AsyncUtil.execute { async.post("http://httpbin.org/post", json = jsonMap, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val returnedJSON = json.getJSONObject("json")
            val returnedBooks = returnedJSON.getJSONArray("books")
            it("should be the same length") {
                assertEquals(jsonMap.size, returnedJSON.length())
            }
            it("should have the same book length") {
                assertEquals(jsonMap.get("books")!!.size, returnedBooks.length())
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
    given("an async request with json as an Iterable") {
        val jsonArray = StringIterable("a word")
        beforeGroup {
            AsyncUtil.execute { async.post("http://httpbin.org/post", json = jsonArray, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val returnedJSON = json.getJSONArray("json")
            it("should be equal") {
                assertEquals(jsonArray.string, String(returnedJSON.mapIndexed { i, any -> returnedJSON.getString(i)[0] }.toCharArray()))
            }
        }
    }
    given("an async request with json as a List") {
        val jsonList = listOf("A thing", "another thing")
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", json = jsonList, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val returnedJSON = json.getJSONArray("json")
            it("should have an equal first element") {
                assertEquals(jsonList[0], returnedJSON.getString(0))
            }
            it("should have an equal second element") {
                assertEquals(jsonList[1], returnedJSON.getString(1))
            }
        }
    }
    given("an async request with json as an Array") {
        val jsonArray = arrayOf("A thing", "another thing")
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", json = jsonArray, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val returnedJSON = json.getJSONArray("json")
            it("should have an equal first element") {
                assertEquals(jsonArray[0], returnedJSON.getString(0))
            }
            it("should have an equal second element") {
                assertEquals(jsonArray[1], returnedJSON.getString(1))
            }
        }
    }
    given("an async request with json as a JSONObject") {
        val jsonObject = JSONObject("""{"valid": true}""")
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", json = jsonObject, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val returnedJSON = json.getJSONObject("json")
            it("should have a true value for the key \"valid\"") {
                assertTrue(returnedJSON.getBoolean("valid"))
            }
        }
    }
    given("an async request with json as a JSONArray") {
        val jsonObject = JSONArray("[true]")
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", json = jsonObject, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val returnedJSON = json.getJSONArray("json")
            it("should have a true value for the first key") {
                assertTrue(returnedJSON.getBoolean(0))
            }
        }
    }
    given("an async request with invalid json") {
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", json = object {}, onError = { AsyncUtil.set(err = this) }) }
        }
        on("construction") {
            it("should throw an IllegalArgumentException") {
                assertFailsWith(IllegalArgumentException::class) {
                    throw error!!
                }
            }
        }
    }
    given("an async file upload without form parameters") {
        val file = "hello".fileLike("derp")
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", files = listOf(file), onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val files = json.getJSONObject("files")
            it("should have one file") {
                assertEquals(1, files.length())
            }
            it("should have the same name") {
                assertNotNull(files.optString(file.fieldName, null))
            }
            it("should have the same contents") {
                assertEquals(file.contents.toString(Charsets.UTF_8), files.optString(file.fieldName))
            }
        }
    }
    given("an async file upload with form parameters") {
        val file = "hello".fileLike("derp")
        val params = mapOf("top" to "kek")
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", files = listOf(file), data = params, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val files = json.getJSONObject("files")
            val form = json.getJSONObject("form")
            it("should have one file") {
                assertEquals(1, files.length())
            }
            it("should have the same name") {
                assertNotNull(files.optString(file.fieldName, null))
            }
            it("should have the same contents") {
                assertEquals(file.contents.toString(Charsets.UTF_8), files.optString(file.fieldName))
            }
            it("should have one parameter") {
                assertEquals(1, form.length())
            }
            it("should have the same name") {
                assertNotNull(form.optString("top", null))
            }
            it("should have the same contents") {
                assertEquals("kek", form.optString("top"))
            }
        }
    }
    given("an async streaming file upload") {
        // Get our file to stream (a beautiful rare pepe)
        val file = File("src/test/resources/rarest_of_pepes.png")
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", data = file, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the data") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val data = json.getString("data")
            it("should start with a base64 header") {
                assertTrue(data.startsWith("data:application/octet-stream;base64,"))
            }
            val base64 = data.split("data:application/octet-stream;base64,")[1]
            val rawData = Base64.getDecoder().decode(base64)
            it("should be the same decoded content") {
                assertEquals(file.readBytes().asList(), rawData.asList())
            }
        }
    }
    given("an async streaming InputStream upload") {
        // Get our file to stream (a beautiful rare pepe)
        val file = File("src/test/resources/rarest_of_pepes.png")
        val inputStream = file.inputStream()
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", data = inputStream, onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the data") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            val data = json.getString("data")
            it("should start with a base64 header") {
                assertTrue(data.startsWith("data:application/octet-stream;base64,"))
            }
            val base64 = data.split("data:application/octet-stream;base64,")[1]
            val rawData = Base64.getDecoder().decode(base64)
            it("should be the same decoded content") {
                assertEquals(file.readBytes().asList(), rawData.asList())
            }
        }
    }
    given("an async JSON request") {
        val expected = """{"test":true}"""
        beforeGroup {
            AsyncUtil.execute { async.post("https://httpbin.org/post", json = mapOf("test" to true), onError = errorCallback, onResponse = responseCallback) }
        }
        on("accessing the request body") {
            if (error != null) throw error!!
            val body = response!!.request.body
            it("should be the expected valid json") {
                assertEquals(expected, body.toString(Charsets.UTF_8))
            }
        }
    }
})
