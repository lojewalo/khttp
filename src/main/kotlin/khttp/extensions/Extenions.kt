/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.extensions

import khttp.structures.files.FileLike
import java.io.File
import java.io.Writer
import java.nio.file.Path

/**
 * Creates a [FileLike] from this File and [name]. If [name] is not specified, the filename will be used.
 */
fun File.fileLike(name: String = this.name) = FileLike(name, this)

/**
 * Creates a [FileLike] from the Path.
 */
fun Path.fileLike() = FileLike(this)

/**
 * Creates a [FileLike] from this Path and [name]. If [name] is not specified, the filename will be used.
 */
fun Path.fileLike(name: String) = FileLike(name, this)

/**
 * Creates a [FileLike] from this String and [name].
 */
fun String.fileLike(name: String) = FileLike(name, this)

/**
 * Writes [string] to this writer and then calls [Writer#flush()][java.io.Writer#flush].
 */
internal fun Writer.writeAndFlush(string: String) {
    this.write(string)
    this.flush()
}
