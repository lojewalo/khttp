package me.kyleclemens.khttp.requests

import me.kyleclemens.khttp.structures.Parameters

abstract class KHttpMethodRequest(method: String, route: String, parameters: Parameters, headers: MutableMap<String, String>, data: Any?, json: Any?) : KHttpGenericRequest(route, parameters, headers, data, json) {

    init {
        this.initializers.add { it.requestMethod = method }
    }

}
