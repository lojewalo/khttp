/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.files

import khttp.extensions.fileLike
import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

class FileLikeSpec : Spek({
    describe("a File") {
        val file = File(PATH)
        context("creating a FileLike without a custom name") {
            val fileLike = file.fileLike()
            it("should have the same name") {
                assertEquals(NAME, fileLike.fieldName)
            }
            it("should have the same contents") {
                assertEquals(file.readBytes().asList(), fileLike.contents.asList())
            }
        }
        context("creating a FileLike with a custom name") {
            val name = "not_rare_pepe.png"
            val fileLike = file.fileLike(name = name)
            it("should have the custom name") {
                assertEquals(name, fileLike.fieldName)
            }
            it("should have the same contents") {
                assertEquals(file.readBytes().asList(), fileLike.contents.asList())
            }
        }
    }
    describe("a Path") {
        val path = Paths.get(PATH)
        context("creating a FileLike without a custom name") {
            val fileLike = path.fileLike()
            it("should have the same name") {
                assertEquals(NAME, fileLike.fieldName)
            }
            it("should have the same contents") {
                assertEquals(path.toFile().readBytes().asList(), fileLike.contents.asList())
            }
        }
        context("creating a FileLike with a custom name") {
            val name = "not_rare_pepe.png"
            val fileLike = path.fileLike(name = name)
            it("should have the custom name") {
                assertEquals(name, fileLike.fieldName)
            }
            it("should have the same contents") {
                assertEquals(path.toFile().readBytes().asList(), fileLike.contents.asList())
            }
        }
    }
    describe("a String") {
        val string = "toppest of keks"
        context("creating a FileLike with a custom name") {
            val name = "not_rare_pepe.png"
            val fileLike = string.fileLike(name = name)
            it("should have the custom name") {
                assertEquals(name, fileLike.fieldName)
            }
            it("should have the same contents") {
                assertEquals(string.toByteArray().asList(), fileLike.contents.asList())
            }
        }
    }
}) {
    companion object {
        const val PATH = "src/test/resources/rarest_of_pepes.png"
        const val NAME = "rarest_of_pepes.png"
    }
}
