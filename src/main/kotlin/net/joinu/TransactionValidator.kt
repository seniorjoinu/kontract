package net.joinu


class TransactionValidator {
    private val contracts = mutableListOf<Contract>()

    fun register(contract: Contract) = contracts.add(contract)
    fun register(contracts: Collection<Contract>) = this.contracts.addAll(contracts)
    fun register(vararg contracts: Contract) = this.contracts.addAll(contracts)

    fun validate(txn: SignedTransaction): Boolean {
        return contracts
            .filter {
                it.header.family == txn.body.header.family
                        && it.header.type == txn.body.header.type
                        && it.header.version == txn.body.header.version
            }
            .all {
                try {
                    it.body(txn)
                } catch (ex: Throwable) {
                    throw ContractException("Contract: $it thrown an exception", ex)
                }
            }
    }
}

class ContractException(message: String, throwable: Throwable? = null) : RuntimeException(message, throwable)
