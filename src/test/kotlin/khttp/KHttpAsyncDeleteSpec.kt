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
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals

class KHttpAsyncDeleteSpec : Spek({
    given("an async delete request") {
        val url = "https://httpbin.org/delete"
        beforeGroup {
            AsyncUtil.execute { async.delete(url, onError = errorCallback, onResponse =  responseCallback) }
        }
        on("accessing the json") {
            if (error != null) throw error!!
            val json = response!!.jsonObject
            it("should have the same url") {
                assertEquals(url, json.getString("url"))
            }
        }
    }
})
