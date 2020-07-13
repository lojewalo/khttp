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

class KHttpAsyncDeleteSpec : Spek({
    describe("an async delete request") {
        val url = "https://httpbin.org/delete"
        var error: Throwable? = null
        var response: Response? = null

        async.delete(url, onError = { error = this }, onResponse = {response = this })
        await.atMost(5, TimeUnit.SECONDS)
                .until { response != null }

        context("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should have the same url") {
                assertEquals(url, json.getString("url"))
            }
        }
    }
})
