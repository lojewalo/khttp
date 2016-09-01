/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.extensions

import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class ExtensionsSpec : Spek({
    given("a ByteArray") {
        val string = "\"Goddammit\", he said\nThis is a load of bullshit.\r\nPlease, just kill me now.\r"
        val byteArray = string.toByteArray()
        on("splitting by lines") {
            val split = byteArray.splitLines().map { it.toString(Charsets.UTF_8) }
            val expected = string.split(Regex("(\r\n|\r|\n)"))
            it("should be split by lines") {
                assertEquals(expected, split)
            }
        }
        on("splitting by the letter e") {
            val splitBy = "e"
            val split = byteArray.split(splitBy.toByteArray()).map { it.toString(Charsets.UTF_8) }
            val expected = string.split(splitBy)
            it("should be split correctly") {
                assertEquals(expected, split)
            }
        }
        on("splitting by is") {
            val splitBy = "is"
            val split = byteArray.split(splitBy.toByteArray()).map { it.toString(Charsets.UTF_8) }
            val expected = string.split(splitBy)
            it("should be split correctly") {
                assertEquals(expected, split)
            }
        }
    }
    given("an empty ByteArray") {
        val empty = ByteArray(0)
        on("splitting by lines") {
            val split = empty.splitLines()
            it("should be empty") {
                assertEquals(0, split.size)
            }
        }
        on("splitting by anything") {
            val split = empty.split(ByteArray(0))
            it("should have one element") {
                assertEquals(1, split.size)
            }
            it("the element should be empty") {
                assertTrue(split[0].isEmpty())
            }
        }
    }
})
