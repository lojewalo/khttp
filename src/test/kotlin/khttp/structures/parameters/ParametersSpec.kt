/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.parameters

import org.jetbrains.spek.api.Spek
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ParametersSpec : Spek({
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
            val result: Any? = params.get(null as String?)
            it("should be null") {
                assertNull(result)
            }
        }
        on("checking for key null") {
            val result = params.containsKey(null as String?)
            it("should be false") {
                assertFalse(result)
            }
        }
        on("checking for value null") {
            val result = params.containsValue(null as String?)
            it("should be false") {
                assertFalse(result)
            }
        }
    }
})
