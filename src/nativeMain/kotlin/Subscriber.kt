import kotlinx.cinterop.*
import zenohc.*

/**
 * Zenoh Subscriber
 */
class Subscriber(private val session: CValue<z_session_t>, private val keyExpr: String, val callback: (input: Sample) -> Unit) {

    private val zenoh_sub: CValue<z_owned_subscriber_t>
    private val stableRef: StableRef<Subscriber> = StableRef.create(this)

    init {
        val closure = cValue<z_owned_closure_sample_t>() {
            call = staticCFunction(::callbackWrapper)
            context = stableRef.asCPointer()
        }
        zenoh_sub = z_declare_subscriber(
            session,
            z_keyexpr(keyExpr.cstr.getBytes().refTo(0)),
            closure,
            z_subscriber_options_default()
        )
        if (!z_subscriber_check(zenoh_sub)) {
            throw Exception("Unable to declare Subscriber for key expression!")
        }
    }

    protected fun finalize() { // called upon destruction
        z_undeclare_subscriber(zenoh_sub)
        stableRef.dispose()
    }
}

private fun callbackWrapper(sample: CPointer<z_sample_t>?, args: COpaquePointer?) {
    if (args != null && sample != null) {
        args.asStableRef<Subscriber>().get().callback(Sample(sample))
    }
}

