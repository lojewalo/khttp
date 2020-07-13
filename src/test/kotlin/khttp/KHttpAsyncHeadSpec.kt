/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import khttp.responses.Response
import org.awaitility.kotlin.await
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.util.concurrent.TimeUnit
import kotlin.test.assertEquals

class KHttpAsyncHeadSpec : Spek({
    describe("an async head request") {
        var error: Throwable? = null
        var response: Response? = null

        async.head("https://httpbin.org/get", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
    describe("an async head request to a redirecting URL") {
        var error: Throwable? = null
        var response: Response? = null

        async.head("https://httpbin.org/redirect/2", onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 302") {
                assertEquals(302, status)
            }
        }
    }
    describe("an async head request to a redirecting URL, specifically allowing redirects") {
        var error: Throwable? = null
        var response: Response? = null

        async.head("https://httpbin.org/redirect/2", allowRedirects = true, onError = { error = this }, onResponse = { response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
})
