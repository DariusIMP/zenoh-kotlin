import kotlinx.cinterop.*
import platform.posix.*
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
    var session = z_open(config)


    if (!z_session_check(session)) {
        println("Unable to open session!")
        return
    }

    println("Declaring Publisher on $keyExpression...")
    val publisher = Publisher(session, keyExpression)

    var idx = 0
    var buff = ""
    while (true) {
        sleep(1)
        buff = "[$idx] $value"

        println("Putting Data ('$keyExpression': '$buff')...")
        publisher.put(buff.encodeToByteArray())

        idx += 1
    }

    z_close(session)
}

fun callbackSample(sample: Sample) {
    println(sample)
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

    val subscriber = Subscriber(z_session_loan(session), keyExpression, ::callbackSample)
    val pinsub = subscriber.pin()
    println("Enter 'q' to quit...")
    var c = '0'
    while (c != 'q') {
        c = readln()[0]
        if (c != 'q') {
            sleep(1)
        }
    }

    pinsub.unpin()
    z_close(session)
}

fun main(args: Array<String>) {
    publisher()
//    subscriber()
//    put()
}