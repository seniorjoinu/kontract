package net.joinu

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


data class SignatureAndPublicKey(val signature: ByteArray, val publicKey: ByteArray)

data class SignedTransaction(val body: Transaction, val signature: SignatureAndPublicKey)

data class TransactionPayload(val stateBefore: JsonKeyValueStorage, val stateAfter: JsonKeyValueStorage)

data class Transaction(val header: TransactionHeader, val payload: TransactionPayload)

data class TransactionHeader(
    val family: String,
    val type: String,
    val version: String,
    val attributes: Map<String, String>
)

class TransactionHeaderBuilder {
    var attributes = mutableMapOf<String, String>()
    var family = ""
    var type = ""
    var version = ""

    internal fun build(): TransactionHeader {
        require(family.isNotBlank()) { "Transaction family is not specified!" }
        require(type.isNotBlank()) { "Transaction type is not specified!" }
        require(version.isNotBlank()) { "Transaction version is not specified" }

        return TransactionHeader(family, type, version, attributes)
    }
}

class TransactionPayloadBuilder : Builder<TransactionPayload> {
    val stateBefore = JsonKeyValueStorage()
    val stateAfter = JsonKeyValueStorage()

    override fun build() = TransactionPayload(stateBefore, stateAfter)
}

class TransactionBuilder {
    private var signCallback: SignatureProvider? = null
    fun sign(callback: SignatureProvider) { signCallback = callback }

    companion object {
        val mapper = jacksonObjectMapper()
    }

    private var header: TransactionHeader? = null
    fun header(init: TransactionHeaderBuilder.() -> Unit) {
        val head = TransactionHeaderBuilder()
        head.init()

        header = head.build()
    }

    private var payload: TransactionPayload? = null
    fun payload(init: TransactionPayloadBuilder.() -> Unit) {
        val builder = TransactionPayloadBuilder()
        builder.init()

        payload = builder.build()
    }

    internal fun build(): SignedTransaction {
        requireNotNull(header) { "Header is not specified" }
        requireNotNull(payload) { "Payload is not specified" }
        requireNotNull(signCallback) { "Signature provider is not specified" }

        val txn = Transaction(header!!, payload!!)
        val signature = signCallback!!.invoke(mapper.writeValueAsBytes(txn))

        return SignedTransaction(txn, signature)
    }
}

fun txn(init: TransactionBuilder.() -> Unit): SignedTransaction {
    val builder = TransactionBuilder()
    builder.init()

    return builder.build()
}

typealias SignatureProvider = (data: ByteArray) -> SignatureAndPublicKey
