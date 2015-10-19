/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package me.kyleclemens.khttp

import org.jetbrains.spek.api.Spek

abstract class MavenSpek : Spek() {

    abstract fun test()

    init {
        this.test()
    }

}
