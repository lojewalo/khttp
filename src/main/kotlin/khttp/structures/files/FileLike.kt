/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.structures.files

import java.io.File
import java.nio.file.Path

class FileLike(val fieldName: String, val fileName: String, val contents: ByteArray) {

    constructor(name: String, contents: String) : this(name, contents.toByteArray())

    constructor(name: String, file: File) : this(name, file.readBytes())

    constructor(name: String, path: Path) : this(name, path.toFile())

    constructor(file: File) : this(file.name, file)

    constructor(path: Path) : this(path.toFile())

    constructor(name: String, contents: ByteArray) : this(name, name, contents)
}
