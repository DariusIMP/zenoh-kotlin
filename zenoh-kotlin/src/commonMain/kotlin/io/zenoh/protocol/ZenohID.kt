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

package io.zenoh.protocol

import io.zenoh.jni.JNIZenohID

/**
 * The global unique id of a Zenoh peer.
 */
data class ZenohID internal constructor(private val bytes: ByteArray) {

    val id = JNIZenohID.getIdViaJNI(bytes)

    override fun toString(): String {
        return id
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ZenohID

        return true
//        return JNIZenohID.equalsViaJNI(bytes, other.bytes)
    }

    override fun hashCode(): Int {
        var result = bytes.contentHashCode()
        result = 31 * result + id.hashCode()
        return result
    }
}
