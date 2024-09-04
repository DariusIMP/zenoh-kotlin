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

use crate::{errors::Result, throw_exception, utils::decode_byte_array};
use jni::{
    objects::{JByteArray, JClass, JString},
    sys::jstring,
    JNIEnv,
};
use zenoh::config::ZenohId;
use zenoh_protocol::core::ZenohIdProto;

#[no_mangle]
#[allow(non_snake_case)]
pub extern "C" fn Java_io_zenoh_jni_JNIZenohID_getIdViaJNI(
    mut env: JNIEnv,
    _class: JClass,
    zenohid: JByteArray,
) -> jstring {
    || -> Result<JString> {
        let bytes = decode_byte_array(&env, zenohid)?;
        let zenohIdProto = ZenohIdProto::try_from(bytes.as_slice()).unwrap();
        let zenohid = ZenohId::from(zenohIdProto);
        Ok(env.new_string(zenohid.to_string()).unwrap())
    }()
    .unwrap_or_else(|err| {
        throw_exception!(env, err);
        JString::default()
    })
    .as_raw()
}
