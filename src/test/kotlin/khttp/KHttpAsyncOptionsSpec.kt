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

class KHttpAsyncOptionsSpec : Spek({
    describe("an async options request") {
        AsyncUtil.execute { async.options("https://httpbin.org/get", onError = errorCallback, onResponse = responseCallback) }

        context("accessing the status code") {
            if (error != null) throw error!!
            val status = response!!.statusCode
            it("should be 200") {
                assertEquals(200, status)
            }
        }
    }
})
