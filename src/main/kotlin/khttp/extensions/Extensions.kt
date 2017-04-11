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

fun ByteArray.splitLines(): List<ByteArray> {
    if (this.isEmpty()) return listOf()
    val lines = arrayListOf<ByteArray>()
    var lastSplit = 0
    var skip = 0
    for ((i, byte) in this.withIndex()) {
        if (skip > 0) {
            skip--
            continue
        }
        if (byte == '\n'.toByte()) {
            lines.add(this.sliceArray(lastSplit..i - 1))
            lastSplit = i + 1
        } else if (byte == '\r'.toByte() && i + 1 < this.size && this[i + 1] == '\n'.toByte()) {
            skip = 1
            lines.add(this.sliceArray(lastSplit..i - 1))
            lastSplit = i + 2
        } else if (byte == '\r'.toByte()) {
            lines.add(this.sliceArray(lastSplit..i - 1))
            lastSplit = i + 1
        }
    }
    lines += this.sliceArray(lastSplit..this.size - 1)
    return lines
}

fun ByteArray.split(delimiter: ByteArray): List<ByteArray> {
    val lines = arrayListOf<ByteArray>()
    var lastSplit = 0
    var skip = 0
    for (i in 0..this.size - 1) {
        if (skip > 0) {
            skip--
            continue
        }
        if (this.sliceArray(i..i + delimiter.size - 1).toList() == delimiter.toList()) {
            skip = delimiter.size
            lines += this.sliceArray(lastSplit..i - 1)
            lastSplit = i + delimiter.size
        }
    }
    lines += this.sliceArray(lastSplit..this.size - 1)
    return lines
}

internal fun <T> Class<T>.getSuperclasses(): List<Class<in T>> {
    val list = arrayListOf<Class<in T>>()
    var superclass = this.superclass
    while (superclass != null) {
        list.add(superclass)
        superclass = superclass.superclass
    }
    return list
}

fun <K, V> MutableMap<K, V>.putIfAbsentWithNull(key: K, value: V) {
    if (key !in this) {
        this[key] = value
    }
}

fun <K, V> MutableMap<K, V>.putAllIfAbsentWithNull(other: Map<K, V>) {
    for ((key, value) in other) {
        this.putIfAbsentWithNull(key, value)
    }
}
