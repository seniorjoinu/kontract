package net.joinu


data class Contract(val header: ContractHeader, val stateFunctions: List<StateFunction>)

data class ContractHeader(
    val family: String,
    val type: String,
    val version: String,
    val affectedInputNamespaces: List<ByteArray>,
    val affectedOutputNamespaces: List<ByteArray>
)

// TODO: we need to make something with this, we need to store some tokens instead of actual stuff
sealed class StateFunction {
    data class SET_CONST(val address: ByteArray, val payload: ByteArray) : StateFunction()
    data class GET_CONST(val address: ByteArray) : StateFunction()
}

sealed class StateVariable {
    data class ADDRESS(val name: String) : StateVariable()
}

sealed class Operation {
    data class ASSIGN(val variable: StateVariable, val result: StateFunction) : Operation()
}

class ContractBuilder : Builder<Contract> {
    private var header: ContractHeader? = null
    private var stateFunctions: List<StateFunction>? = null

    fun header(init: ContractHeaderBuilder.() -> Unit) {
        val headerBuilder = ContractHeaderBuilder()
        headerBuilder.init()
        header = headerBuilder.build()
    }

    fun body(init: ContractBodyBuilder.() -> Unit) {
        val opsBuilder = ContractBodyBuilder()
        opsBuilder.init()
        stateFunctions = opsBuilder.build()
    }

    override fun build(): Contract {
        require(header != null) { "Header should be specified!" }
        require(stateFunctions != null) { "Body should be specified!" }

        return Contract(header!!, stateFunctions!!)
    }
}

class ContractHeaderBuilder : Builder<ContractHeader> {
    private val affectedInputNamespaces = mutableListOf<ByteArray>()
    private val affectedOutputNamespaces = mutableListOf<ByteArray>()

    var family: String = ""
    var type: String = ""
    var version: String = ""

    fun affectInputNamespace(input: ByteArray) {
        affectedInputNamespaces.add(input)
    }

    fun affectInputNamespace(inputs: Collection<ByteArray>) {
        affectedInputNamespaces.addAll(inputs)
    }

    fun affectOutputNamespace(output: ByteArray) {
        affectedOutputNamespaces.add(output)
    }

    fun affectOutputNamespace(outputs: Collection<ByteArray>) {
        affectedInputNamespaces.addAll(outputs)
    }

    override fun build(): ContractHeader {
        require(family.isNotBlank()) { "Contract family should be specified!" }
        require(type.isNotBlank()) { "Contract type should be specified!" }
        require(version.isNotBlank()) { "Contract version should be specified!" }

        return ContractHeader(family, type, version, affectedInputNamespaces, affectedOutputNamespaces)
    }
}

class ContractBodyBuilder : Builder<List<StateFunction>> {
    private val operations = mutableListOf<StateFunction>()

    infix fun ByteArray.set(data: ByteArray) {
        operations.add(StateFunction.SET_CONST(this, data))
    }

    fun get(address: ByteArray):

            override

    fun build(): List<StateFunction> {
        require(operations.isNotEmpty()) { "You should specify at least one operation for contract" }

        return operations
    }
}


interface Builder<T : Any> {
    fun build(): T
}

