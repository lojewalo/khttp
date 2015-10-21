/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp.structures.maps

class CaseInsensitiveMap<V>(private val map: Map<String, V>) : Map<String, V> by map {

    override fun containsKey(key: Any?): Boolean {
        if (key == null) return this.map.containsKey(null)
        return this.map.keySet().any { it.equals(key.toString().toLowerCase(), ignoreCase = true) }
    }

    override fun get(key: Any?): V? {
        if (key == null) return this.map.get(key)
        return this.map.filter { it.key.equals(key.toString().toLowerCase(), ignoreCase = true) }.map { it.value }.firstOrNull()
    }

    override fun toString(): String {
        return this.map.toString()
    }

}
