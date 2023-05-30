import kotlinx.cinterop.*
import zenohc.z_keyexpr_ptr_to_string
import zenohc.z_sample_t
import zenohc.z_str_loan

/**
 * Zenoh Sample
 */
class Sample(private val sample: CPointer<z_sample_t>) {

    val keyExpr: String
    val payload: ByteArray
    val kind: Kind

    enum class Kind {
        PUT,
        DELETE,
    }

    init {
        val sample = this.sample.pointed
        this.keyExpr = z_str_loan(z_keyexpr_ptr_to_string(sample.keyexpr.ptr))!!.toKString()
        this.payload = sample.payload.start!!.readBytes(sample.payload.len.toInt())
        this.kind = if (sample.kind.toInt() == 0) Kind.PUT else Kind.DELETE // TODO: add unknown
    }

    override fun toString(): String {
        return "KeyExpr: $keyExpr - Kind: $kind - Payload: ${payload.toKString()}"
    }

}