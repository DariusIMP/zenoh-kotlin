import kotlinx.cinterop.*
import platform.posix.*
import zenohc.*
import kotlin.native.concurrent.freeze

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


    val k = z_keyexpr(keyExpression.cstr.getBytes().refTo(0))
    println(k.getBytes().size)
    k.getBytes().forEach { byte -> print("$byte, ") }
    println("Putting Data ('$keyExpression': '$value'...)")
    var options = z_put_options_default()
    val result = z_put(
        z_session_loan(session),
        k,
        value.cstr.getBytes().toUByteArray().refTo(0),
        value.length.toULong(),
        options
    )

    if (result < 0) {
        println("Put failed...")
    }
    println("Bye $keyExpression")
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
    val pub = z_declare_publisher(z_session_loan(session), z_keyexpr(keyExpression.utf8), options_default)
    if (!z_publisher_check(pub)) {
        println("Unable to declare Publisher for key expression!")
        return
    }

    var idx = 0
    var buff = ""
    while (idx < 256) {
        sleep(1)
        buff = "[$idx] $value"
        var options = z_publisher_put_options_default()
//        val buffer2 = buff.utf8.getBytes().toUByteArray()
        setPublisherEncoding(options, Z_ENCODING_PREFIX_TEXT_PLAIN)
        println("Attempting to put data...")
        val result = z_publisher_put(
            z_publisher_loan(pub),
            castPointer(buff.utf8),
            (buff.length + 1).toULong(),
            options
        )
        println("Putting Data ('$keyExpression': '$buff'...")
        if (result < 0) {
            println("Put failed!")
        }

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
        z_keyexpr(keyExpression.utf8),
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
//    publisher()
//    subscriber()
    put()
}