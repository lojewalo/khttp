/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.requests

import me.kyleclemens.khttp.structures.authorization.Authorization
import me.kyleclemens.khttp.structures.parameters.Parameters

abstract class KHttpMethodRequest(
    method: String,
    route: String,
    parameters: Parameters,
    headers: MutableMap<String, String>,
    data: Any?,
    json: Any?,
    auth: Authorization?,
    cookies: Map<String, Any>?,
    timeout: Int,
    allowRedirects: Boolean
) : KHttpGenericRequest(route, parameters, headers, data, json, auth, cookies, timeout, allowRedirects) {

    init {
        this.initializers.add { it.requestMethod = method }
    }

}
