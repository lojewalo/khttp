package me.kyleclemens.khttp.structures

open class Parameters(vararg val parameters: Pair<String, String>) : Map<String, String> by mapOf(*parameters)
