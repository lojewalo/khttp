/*
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package khttp.helpers

import khttp.responses.Response

/**
 * Utility to aid in executing asynchronous tests in Spek
 */
class AsyncUtil {
    companion object {
        /**
         * The current response object
         */
        var response: Response? = null

        /**
         * The current error object
         */
        var error: Throwable? = null

        /**
         * Mutex for the request
         */
        val requestMutex = java.lang.Object()

        /**
         * Default error callback
         */
        val errorCallback: Throwable.() -> Unit = { set(err = this) }

        /**
         * Default response callback
         */
        val responseCallback: Response.() -> Unit = { set(this) }

        /**
         * Executes a khttp asynchronous request synchronously
         * @param request a block of code that performs the khttp async request
         */
        fun execute(request: () -> Unit): Unit {
            response = null
            error = null
            request()
            synchronized(requestMutex) { while (response == null && error == null) requestMutex.wait() }
        }

        /**
         * Sets the response and/or error values of the companion object
         * @param resp the value of the response
         * @param err the value of the error
         */
        fun set(resp: Response? = null, err: Throwable? = null): Unit {
            synchronized(requestMutex) {
                response = resp
                error = err
                requestMutex.notifyAll()
            }
        }
    }
}
