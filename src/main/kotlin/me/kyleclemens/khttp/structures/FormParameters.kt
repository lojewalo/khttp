package me.kyleclemens.khttp.structures

class FormParameters(vararg parameters: Pair<String, String>) : Parameters(*parameters)
