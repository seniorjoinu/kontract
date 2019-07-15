package net.joinu


data class Contract(val header: ContractHeader, val body: ContractBody)

data class ContractHeader(
    val family: String,
    val type: String,
    val version: String
)

class ContractBuilder : Builder<Contract> {
    private var header: ContractHeader? = null
    private var body: ContractBody? = null

    fun header(init: ContractHeaderBuilder.() -> Unit) {
        val headerBuilder = ContractHeaderBuilder()
        headerBuilder.init()
        header = headerBuilder.build()
    }

    fun body(body: ContractBody) {
        this.body = body
    }

    override fun build(): Contract {
        require(header != null) { "Header should be specified!" }
        require(body != null) { "Body should be specified!" }

        return Contract(header!!, body!!)
    }
}

class ContractHeaderBuilder : Builder<ContractHeader> {
    var family: String = ""
    var type: String = ""
    var version: String = ""

    override fun build(): ContractHeader {
        require(family.isNotBlank()) { "Contract family should be specified!" }
        require(type.isNotBlank()) { "Contract type should be specified!" }
        require(version.isNotBlank()) { "Contract version should be specified!" }

        return ContractHeader(family, type, version)
    }
}


interface Builder<T : Any> {
    fun build(): T
}

typealias ContractBody = (txn: SignedTransaction) -> Boolean

fun kontract(init: ContractBuilder.() -> Unit): Contract {
    val contractBuilder = ContractBuilder()
    contractBuilder.init()
    return contractBuilder.build()
}
