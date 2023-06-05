# zenoh-kotlin

Experimental kotlin bindings for Zenoh. This code is developed on top of [Zenoh C](https://github.com/eclipse/](https://github.com/eclipse-zenoh/zenoh-c) using Kotlin's [Cinterop](https://kotlinlang.org/docs/native-c-interop.html) tool which simplifies the interoprability task between Kotlin and C.

## Installation

ZenohC needs to be built and the library (dylib or so file) properly deployed to the path specified on the file `nativeInterop/cinterop/zenohc.def`, tipically `/usr/local/include`, although you may want to change it at your convenience.

On the `build.gradle.kts` file you can specify the build target, for instance Arm64 or X64. 

## Bindings

So far we have a have a Publisher and a Subscriber. The publisher requires a byte array to be passed when performing a put operation, and the subscriber requires a callback. Examples on how to use it can be seen on the Main.kt file.
