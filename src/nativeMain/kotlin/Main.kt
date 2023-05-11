import kotlinx.cinterop.*
import platform.posix.sleep
import platform.posix.uint8_tVar
import zenohc.*

fun publisher() {
    val keyExpression = "demo/example/zenoh-k-pub"
    val value = "Pub from Kotlin!"

    var config = z_config_default()
    println(config.toString())

    println("Opening session...")
    var session = z_open(config)
    if (!z_session_check(session)) {
        println("Unable to open session!")
        return
    }

    println("Declaring Publisher on $keyExpression...")
    val pub = z_declare_publisher(z_session_loan(session), z_keyexpr(keyExpression), z_publisher_options_default())
    println("XXX")
    if (!z_publisher_check(pub)) {
        println("Unable to declare Publisher for key expression!")
        return
    }

    val idx = 0;
    var buff = ""
    while (idx < 256) {
        sleep(1)
        buff = "[$idx] $value"
        println("Putting Data ('$keyExpression': '$buff'...")
        val options = z_publisher_put_options_default()
//        options.encoding = z_encoding(Z_ENCODING_PREFIX_TEXT_PLAIN, null);
        val bufferBytes = buff.cstr.getBytes();
        z_publisher_put(
            z_publisher_loan(pub),
            bufferBytes.refTo(0) as CValuesRef<uint8_tVar /* = UByteVarOf<UByte> */>?,
            bufferBytes.size.toULong(),
            options
        )
        idx.inc()
    }

    z_undeclare_publisher(pub);

    z_close(session)
}

fun subscriber() {
    val keyExpression = "demo/example/**"

    var config = z_config_default()
    println(config.toString())

    println("Opening session...")
    var session = z_open(config)
    if (!z_session_check(session)) {
        println("Unable to open session!")
        return
    }

    println("Declaring Subscriber on $keyExpression...")
    val sub = z_declare_subscriber(z_session_loan(session), z_keyexpr(keyExpression), z_closure_sample_null(), z_subscriber_options_default())
    println("XXX")
    if (!z_subscriber_check(sub)) {
        println("Unable to declare Subscriber for key expression!")
        return
    }


    z_undeclare_subscriber(sub);

    z_close(session)
}
fun main(args: Array<String>) {

    subscriber()
}