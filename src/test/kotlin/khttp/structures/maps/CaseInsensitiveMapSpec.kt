/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.maps

import khttp.MavenSpek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CaseInsensitiveMapSpec : MavenSpek() {
    override fun test() {
        given("a case-insensitive map initialized with a backing map") {
            val backingMap = mapOf("a" to "b", "c" to "d")
            val caseInsensitiveMap = CaseInsensitiveMap(backingMap)
            on("accessing a mapping") {
                val backed = backingMap["a"]
                val lower = caseInsensitiveMap["a"]
                val upper = caseInsensitiveMap["A"]
                it("should be the same value as in the backing map") {
                    assertEquals(backed, lower)
                }
                it("should be the same value as in the backing map") {
                    assertEquals(backed, upper)
                }
                it("should have an equal upper and lower value") {
                    assertTrue(upper == lower)
                }
            }
            on("checking if a mapping is present") {
                val backed = backingMap.containsKey("c")
                val lower = caseInsensitiveMap.containsKey("c")
                val upper = caseInsensitiveMap.containsKey("C")
                it("should be the same value as the result for the backing map") {
                    assertEquals(backed, lower)
                }
                it("should be the same value as the result for the backing map") {
                    assertEquals(backed, upper)
                }
                it("should have an equal upper and lower value") {
                    assertTrue(upper == lower)
                }
            }
            on("toString") {
                val backing = backingMap.toString()
                val insensitive = caseInsensitiveMap.toString()
                it("should be the same string returned by the backing map") {
                    assertEquals(backing, insensitive)
                }
            }
            on("checking for invalid mappings") {
                it("should be null") {
                    assertFalse(caseInsensitiveMap.containsKeyRaw(null))
                }
                it("should be null") {
                    assertFalse(caseInsensitiveMap.containsKeyRaw(object {}))
                }
                it("should be null") {
                    assertEquals<Any?>(Unit, caseInsensitiveMap.getRaw(null))
                    // KT-9963
                    // assertNull(caseInsensitiveMap.getRaw(null))
                }
                it("should be null") {
                    assertEquals<Any?>(Unit, caseInsensitiveMap.getRaw(object {}))
                    // KT-9963
                    // assertNull(caseInsensitiveMap.getRaw(object {}))
                }
                it("should be false") {
                    assertFalse(caseInsensitiveMap.containsKey("b"))
                }
                it("should be false") {
                    assertFalse(caseInsensitiveMap.containsKey("B"))
                }
            }
        }
        given("a case-insensitive map initialized with an empty backing map") {
            val backingMap = mapOf<String, String>()
            val caseInsensitiveMap = CaseInsensitiveMap(backingMap)
            on("checking for invalid mappings") {
                it("should be null") {
                    assertFalse(caseInsensitiveMap.containsKeyRaw(null))
                }
                it("should be null") {
                    assertFalse(caseInsensitiveMap.containsKeyRaw(object {}))
                }
            }
        }
    }
}
