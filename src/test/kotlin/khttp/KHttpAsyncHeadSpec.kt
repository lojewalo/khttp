/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp

import khttp.helpers.AsyncUtil
import khttp.helpers.AsyncUtil.Companion.error
import khttp.helpers.AsyncUtil.Companion.errorCallback
import khttp.helpers.AsyncUtil.Companion.response
import khttp.helpers.AsyncUtil.Companion.responseCallback
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import kotlin.test.assertEquals

class KHttpAsyncHeadSpec : Spek({
    describe("an async head request") {
        beforeGroup {
            AsyncUtil.execute { async.head("https://httpbin.org/get", onError = errorCallback, onResponse = responseCallback) }
        }
        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
    describe("an async head request to a redirecting URL") {
        beforeGroup {
            AsyncUtil.execute { async.head("https://httpbin.org/redirect/2", onError = errorCallback, onResponse = responseCallback) }
        }
        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 302") {
                assertEquals(302, status)
            }
        }
    }
    describe("an async head request to a redirecting URL, specifically allowing redirects") {
        beforeGroup {
            AsyncUtil.execute { async.head("https://httpbin.org/redirect/2", allowRedirects = true, onError = errorCallback, onResponse = responseCallback) }
        }
        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
})
