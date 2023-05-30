import kotlinx.cinterop.*
import zenohc.*

/**
 * Zenoh publisher
 */
class Publisher(session: CValue<z_owned_session_t>, private val keyExpr: String) {

    private val zenohPub: CValue<z_owned_publisher_t>

    init {
        val optionsDefault = z_publisher_options_default()
        this.zenohPub = z_declare_publisher(
            z_session_loan(session),
            z_keyexpr(keyExpr.cstr.getBytes().refTo(0)),
            optionsDefault
        )
    }

    fun put(input: ByteArray) {
        val options = z_publisher_put_options_default()
        val result = z_publisher_put(
            z_publisher_loan(zenohPub.getBytes().refTo(0) as CValuesRef<z_owned_publisher_t>),
            input.toUByteArray().refTo(0),
            input.size.toULong(),
            options
        )
        if (result < 0) {
            throw Exception("Put failed!")
        }
    }

    protected fun finalize() {
        z_undeclare_publisher(zenohPub)
    }
}