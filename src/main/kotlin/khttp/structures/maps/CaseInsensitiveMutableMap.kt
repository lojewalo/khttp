/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.maps

class CaseInsensitiveMutableMap<V>(private val map: MutableMap<String, V>) : MutableMap<String, V> by map {

    override fun containsKey(key: String): Boolean {
        return this.map.keys.any { it.equals(key, ignoreCase = true) }
    }

    override fun get(key: String): V? {
        return this.map.filter { it.key.equals(key, ignoreCase = true) }.map { it.value }.firstOrNull()
    }

    override fun remove(key: String): V? {
        return this.map.filter { it.key.equals(key, ignoreCase = true) }.map { it.key }.firstOrNull()?.let {
            this.map.remove(it)
        }
    }

    override fun put(key: String, value: V): V? {
        val old = this.remove(key)
        this.map.put(key, value)
        return old
    }

    override fun putAll(from: Map<out String, V>) {
        for ((key, value) in from) {
            this.put(key, value)
        }
    }

    override fun toString(): String {
        return this.map.toString()
    }

}
