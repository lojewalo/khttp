/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.requests

import me.kyleclemens.khttp.structures.Parameters

abstract class KHttpMethodRequest(method: String, route: String, parameters: Parameters, headers: MutableMap<String, String>, data: Any?, json: Any?) : KHttpGenericRequest(route, parameters, headers, data, json) {

    init {
        this.initializers.add { it.requestMethod = method }
    }

}
