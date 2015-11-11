/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.parameters

import khttp.MavenSpek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class ParametersSpec : MavenSpek() {
    override fun test() {
        given("Parameters generated from an empty map") {
            val params = Parameters(mapOf())
            on("toString") {
                val string = params.toString()
                it("should be empty") {
                    assertTrue(string.isEmpty())
                }
            }
        }
        given("Parameters generated from a populated map") {
            val map = mapOf("test" to "value", "jest" to "lalue")
            val params = Parameters(map)
            on("inspecting the object") {
                val size = params.size
                val containsTest = "test" in params
                val containsJest = "jest" in params
                val containsTestValue = params.containsValue("value")
                val containsJestValue = params.containsValue("lalue")
                val test = params["test"]
                val jest = params["jest"]
                val toString = params.toString()
                it("should have two items") {
                    assertEquals(2, size)
                }
                it("should have a test mapping") {
                    assertTrue(containsTest)
                }
                it("should have a jest mapping") {
                    assertTrue(containsJest)
                }
                it("should have a value value") {
                    assertTrue(containsTestValue)
                }
                it("should have a lalue value") {
                    assertTrue(containsJestValue)
                }
                it("should not have a null test mapping") {
                    assertNotNull(test)
                }
                it("should not have a null jest mapping") {
                    assertNotNull(jest)
                }
                it("should have a test mapping equal to value") {
                    assertEquals("value", test)
                }
                it("should have a jest mapping equal to lalue") {
                    assertEquals("lalue", jest)
                }
                it("should generate a parameter string on toString") {
                    assertEquals("test=value&jest=lalue", toString)
                }
            }
            on("getting null") {
                val result: Any? = params.getRaw(null)
                it("should be null") {
                    assertEquals(Unit, result)
                    // KT-9963
                    // assertNull(result)
                }
            }
            on("checking for key null") {
                val result = params.containsKeyRaw(null)
                it("should be false") {
                    assertFalse(result)
                }
            }
            on("checking for value null") {
                val result = params.containsValueRaw(null)
                it("should be false") {
                    assertFalse(result)
                }
            }
        }
    }
}
