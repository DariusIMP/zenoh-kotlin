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

package io.zenoh.liveliness

import io.zenoh.handlers.Callback
import io.zenoh.handlers.ChannelHandler
import io.zenoh.handlers.Handler
import io.zenoh.jni.JNILiveliness
import io.zenoh.keyexpr.KeyExpr
import io.zenoh.pubsub.Subscriber
import io.zenoh.query.Reply
import io.zenoh.sample.Sample
import kotlinx.coroutines.channels.Channel
import java.time.Duration

class Liveliness {

    /**
     * Create a LivelinessToken for the given key expression.
     */
    fun declareToken(keyExpr: KeyExpr): LivelinessToken {
        return JNILiveliness.declareToken(keyExpr)
    }

    /**
     * Query liveliness tokens with matching key expressions.
     */
    fun get(keyExpr: KeyExpr, callback: Callback<Reply>, timeout: Duration = Duration.ofMillis(10000)): Result<Unit> {
        return JNILiveliness.get(keyExpr, callback, Unit, timeout)
    }

    /**
     * Query liveliness tokens with matching key expressions.
     */
    fun <R> get(keyExpr: KeyExpr, handler: Handler<Reply, R>, timeout: Duration = Duration.ofMillis(10000)): Result<R> {
        val callback = handler::handle
        return JNILiveliness.get(keyExpr, callback, handler.receiver(), timeout)
    }

    /**
     * Query liveliness tokens with matching key expressions.
     */
    fun get(keyExpr: KeyExpr, channel: Channel<Reply>, timeout: Duration = Duration.ofMillis(10000)): Result<Channel<Reply>> {
        val handler = ChannelHandler(channel)
        return JNILiveliness.get(keyExpr, handler::handle, handler.receiver(), timeout)
    }

    /**
     * Create a Subscriber for liveliness changes matching the given key expression.
     */
    fun declareSubscriber(keyExpr: KeyExpr, callback: Callback<Sample>, history: Boolean = false): Result<Subscriber<Unit>> {
        return JNILiveliness.declareSubscriber(keyExpr, callback, history)
    }

    /**
     * Create a Subscriber for liveliness changes matching the given key expression.
     */
    fun <R> declareSubscriber(keyExpr: KeyExpr, handler: Handler<Sample, R>, history: Boolean = false): Result<Subscriber<R>> {
        return JNILiveliness.declareSubscriber(keyExpr, handler::handle, history)
    }

    /**
     * Create a Subscriber for liveliness changes matching the given key expression.
     */
    fun declareSubscriber(keyExpr: KeyExpr, channel: Channel<Sample>, history: Boolean = false): Result<Subscriber<Channel<Sample>>> {
        val handler = ChannelHandler(channel)
        return JNILiveliness.declareSubscriber(keyExpr, handler::handle, history)
    }
}
