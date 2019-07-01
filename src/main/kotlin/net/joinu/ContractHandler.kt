package net.joinu

class ContractHandler

interface StateProvider {
    fun get(address: ByteArray): ByteArray?
    fun set(address: ByteArray, payload: ByteArray)
}

class InMemotyStateProvider : StateProvider {
    private val state = mutableMapOf<ByteArray, ByteArray>()

    override fun get(address: ByteArray) = state[address]

    override fun set(address: ByteArray, payload: ByteArray) {
        state[address] = payload
    }
}
