package net.joinu

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets


data class SignatureAndPublicKey(val signature: ByteArray, val publicKey: ByteArray)

data class SignedTransaction(val body: Transaction, val signature: SignatureAndPublicKey)

data class Transaction(val header: TransactionHeader, val payload: ByteArray) {
    fun toByteArray(): ByteArray {
        val headerBytes = header.toByteArray()

        val dataSize = headerBytes.size + payload.size
        val markerSize = Int.SIZE_BYTES + Int.SIZE_BYTES

        val buffer = ByteBuffer.allocateDirect(dataSize + markerSize)

        buffer.putInt(headerBytes.size)
        buffer.put(headerBytes)

        buffer.putInt(payload.size)
        buffer.put(payload)

        val resultBytes = ByteArray(dataSize + markerSize)
        buffer.flip()

        buffer.get(resultBytes)

        return resultBytes
    }
}

data class TransactionHeader(
    val inputs: List<ByteArray>,
    val outputs: List<ByteArray>,
    val family: String,
    val type: String,
    val version: String
) {
    fun toByteArray(): ByteArray {
        val familyBytes = family.toByteArray(StandardCharsets.UTF_8)
        val typeBytes = type.toByteArray(StandardCharsets.UTF_8)

        val dataSize = (inputs + outputs)
            .fold(0) { acc, bytes -> acc + bytes.size } + familyBytes.size + typeBytes.size
        val markerSize = Int.SIZE_BYTES * 4

        val buffer = ByteBuffer.allocate(markerSize + dataSize)

        buffer.putInt(inputs.size)
        inputs.forEach { buffer.put(it) }

        buffer.putInt(outputs.size)
        outputs.forEach { buffer.put(it) }

        buffer.putInt(familyBytes.size)
        buffer.put(familyBytes)

        buffer.putInt(familyBytes.size)
        buffer.put(typeBytes)

        buffer.flip()

        val resultBytes = ByteArray(markerSize + dataSize)
        buffer.get(resultBytes)

        return resultBytes
    }
}

class TransactionHeaderBuilder {
    private val inputs = mutableListOf<ByteArray>()
    private val outputs = mutableListOf<ByteArray>()

    var family = ""
    var type = ""
    var version = ""

    fun input(input: ByteArray) {
        inputs.add(input)
    }

    fun input(inputs: Collection<ByteArray>) {
        this.inputs.addAll(inputs)
    }

    fun output(output: ByteArray) {
        outputs.add(output)
    }

    fun output(outputs: Collection<ByteArray>) {
        this.outputs.addAll(outputs)
    }

    internal fun build(): TransactionHeader {
        require(inputs.isNotEmpty()) { "Inputs are not defined!" }
        require(outputs.isNotEmpty()) { "Outputs are not defined!" }
        require(family.isNotBlank()) { "Transaction family is not specified!" }
        require(type.isNotBlank()) { "Transaction type is not specified!" }
        require(version.isNotBlank()) { "Transaction version is not specified" }
        require((inputs + outputs).all { it.size == inputs.first().size }) { "Inputs and outputs should all be of the same size" }

        return TransactionHeader(inputs, outputs, family, type, version)
    }
}

class TransactionBuilder {
    var payload: ByteArray? = null

    private var signCallback: SignatureProvider? = null
    fun sign(callback: SignatureProvider) { signCallback = callback }

    private var header: TransactionHeader? = null
    fun header(init: TransactionHeaderBuilder.() -> Unit) {
        val head = TransactionHeaderBuilder()
        head.init()

        header = head.build()
    }

    internal fun build(): SignedTransaction {
        requireNotNull(header) { "Header is not specified" }
        requireNotNull(payload) { "Payload is not specified" }
        require(payload!!.isNotEmpty()) { "Payload is not specified" }
        requireNotNull(signCallback) { "Signature provider is not specified" }

        val txn = Transaction(header!!, payload!!)
        val signature = signCallback!!.invoke(txn.toByteArray())

        return SignedTransaction(txn, signature)
    }
}

fun txn(init: TransactionBuilder.() -> Unit): SignedTransaction {
    val builder = TransactionBuilder()
    builder.init()

    return builder.build()
}

typealias SignatureProvider = (data: ByteArray) -> SignatureAndPublicKey

fun main() {
    val myAddr = ByteArray(100)
    val myPayload = ByteArray(10)
    val myEmptyKey = ByteArray(1)

    val t = txn {
        header {
            input(myAddr)
            output(myAddr)

            family = "settings"
            type = "test"
            version = "1.0"
        }

        payload = myPayload

        sign { SignatureAndPublicKey(it.sliceArray(0 until 10), myEmptyKey) }
    }

    t
}
