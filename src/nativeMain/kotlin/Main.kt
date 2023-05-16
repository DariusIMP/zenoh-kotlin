import kotlinx.cinterop.*
import platform.posix.NULL
import platform.posix.getchar
import platform.posix.sleep
import platform.posix.uint8_tVar
import zenohc.*

@OptIn(ExperimentalUnsignedTypes::class)
fun put() {
    val keyExpression = "demo/example/zenoh-k-put"
    val value = "Put from Kotlin!"

    val config = z_config_default()

    println("Opening session...")
    val session = z_open(config)
    if (!z_session_check(session)) {
        println("Unable to open session!")
        return
    }

    println("Putting Data ('$keyExpression': '$value'...")

    var options = z_put_options_default()
    val result = z_put(
        z_session_loan(session),
        z_keyexpr(keyExpression),
        value.cstr.getBytes().toUByteArray().refTo(0),
        value.length.toULong(),
        options
    )
    if (result < 0) {
        println("Put failed...")
    }

    z_close(session)
}

@OptIn(ExperimentalUnsignedTypes::class)
fun publisher() {
    val keyExpression = "demo/example/zenoh-k-pub"
    val value = "Pub from Kotlin!"

    val config = z_config_default()

    println("Opening session...")
    val session = z_open(config)
    if (!z_session_check(session)) {
        println("Unable to open session!")
        return
    }

    println("Declaring Publisher on $keyExpression...")
    val options_default = z_publisher_options_default()
    val pub = z_declare_publisher(z_session_loan(session), z_keyexpr(keyExpression), options_default)
    if (!z_publisher_check(pub)) {
        println("Unable to declare Publisher for key expression!")
        return
    }

    val idx = 0
    var buff = ""
    while (idx < 256) {
        sleep(1)
        buff = "[$idx] $value"
        println("Putting Data ('$keyExpression': '$buff'...")
        var options = z_publisher_put_options_default()
        // CValuesRef<uint8_tVar /* = UByteVarOf<uint8_t /* = UByte */> */>?
        val buffer2 = buff.cstr.getBytes().toUByteArray()
        z_publisher_put(
            z_publisher_loan(pub),
            buffer2.refTo(0),
            buff.length.toULong(),
            options
        )

        println("SSS")
        idx.inc()
    }

    z_undeclare_publisher(pub)
    z_close(session)
}

fun subDataHandler(sample: CPointer<z_sample_t>?, args: COpaquePointer?) {
    println("SUBDATAHANDLER")
    val keystr = sample?.pointed?.keyexpr
    println(">> [Subscriber] Received ${sample?.pointed?.kind} ('$keystr'): ${sample?.pointed?.payload} ")
}

fun subscriber() {
    val keyExpression = "demo/example/**"

    val config = z_config_default()

    println("Opening session...")
    val session = z_open(config)
    if (!z_session_check(session)) {
        println("Unable to open session!")
        return
    }

    println("Declaring Subscriber on $keyExpression...")
    val callback = cValue<z_owned_closure_sample_t> {
        call = staticCFunction(::subDataHandler)
    }

    val sub = z_declare_subscriber(
        z_session_loan(session),
        z_keyexpr(keyExpression),
        callback,
        z_subscriber_options_default()
    )

    if (!z_subscriber_check(sub)) {
        println("Unable to declare Subscriber for key expression!")
        return
    }

    println("Enter 'q' to quit...")
    var c = '0'
    while (c != 'q') {
        c = readln()[0]
        if (c != 'q') {
            sleep(1)
        }
    }

    z_undeclare_subscriber(sub);
    z_close(session)
}

fun main(args: Array<String>) {
    publisher()
//    subscriber()
//    put()
}