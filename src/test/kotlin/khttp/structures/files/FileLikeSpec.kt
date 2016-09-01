/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.files

import khttp.extensions.fileLike
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.io.File
import java.nio.file.Paths
import kotlin.test.assertEquals

class FileLikeSpec : Spek({
    given("a File") {
        val file = File(PATH)
        on("creating a FileLike without a custom name") {
            val fileLike = file.fileLike()
            it("should have the same name") {
                assertEquals(NAME, fileLike.name)
            }
            it("should have the same contents") {
                assertEquals(file.readBytes().asList(), fileLike.contents.asList())
            }
        }
        on("creating a FileLike with a custom name") {
            val name = "not_rare_pepe.png"
            val fileLike = file.fileLike(name = name)
            it("should have the custom name") {
                assertEquals(name, fileLike.name)
            }
            it("should have the same contents") {
                assertEquals(file.readBytes().asList(), fileLike.contents.asList())
            }
        }
    }
    given("a Path") {
        val path = Paths.get(PATH)
        on("creating a FileLike without a custom name") {
            val fileLike = path.fileLike()
            it("should have the same name") {
                assertEquals(NAME, fileLike.name)
            }
            it("should have the same contents") {
                assertEquals(path.toFile().readBytes().asList(), fileLike.contents.asList())
            }
        }
        on("creating a FileLike with a custom name") {
            val name = "not_rare_pepe.png"
            val fileLike = path.fileLike(name = name)
            it("should have the custom name") {
                assertEquals(name, fileLike.name)
            }
            it("should have the same contents") {
                assertEquals(path.toFile().readBytes().asList(), fileLike.contents.asList())
            }
        }
    }
    given("a String") {
        val string = "toppest of keks"
        on("creating a FileLike with a custom name") {
            val name = "not_rare_pepe.png"
            val fileLike = string.fileLike(name = name)
            it("should have the custom name") {
                assertEquals(name, fileLike.name)
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
