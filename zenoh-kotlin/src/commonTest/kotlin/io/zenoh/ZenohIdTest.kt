//
// Copyright (c) 2023 ZettaScale Technology
//
// This program and the accompanying materials are made available under the
// terms of the Eclipse Public License 2.0 which is available at
// http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
// which is available at https://www.apache.org/licenses/LICENSE-2.0.
//
// SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
//
// Contributors:
//   ZettaScale Zenoh Team, <zenoh@zettascale.tech>
//

package io.zenoh

import io.zenoh.protocol.ZenohID
import kotlin.test.Test

class ZenohIdTest {

    @Test
    fun `zenoh id test`() {
        val inputString = "1234123412341234"
        val stringBytes = inputString.toByteArray(Charsets.UTF_8)
        val byteArray = ByteArray(16)
        System.arraycopy(stringBytes, 0, byteArray, 0, stringBytes.size)

        val zenohid = ZenohID(byteArray)
        println(zenohid)
    }
}