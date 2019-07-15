package net.joinu

import org.junit.jupiter.api.Test

class CompleteTest {
    @Test
    fun `everything together work fine`() {
        val kv = JsonKeyValueStorage()
        val validator = TransactionValidator()

        validator.register(kontract {
            header {
                family = "test"
                type = "pay"
                version = "1.0"
            }

            body {
                val senderAddress = it.body.header.attributes["sender"]
                val receiverAddress = it.body.header.attributes["receiver"]

                val senderBefore = it.body.payload.stateBefore.get<Int>("$senderAddress/value")!!
                val senderAfter = it.body.payload.stateAfter.get<Int>("$senderAddress/value")!!

                val receiverBefore = it.body.payload.stateBefore.get<Int>("$receiverAddress/value")!!
                val receiverAfter = it.body.payload.stateAfter.get<Int>("$receiverAddress/value")!!

                val amountSender = senderBefore - senderAfter
                val amountReceiver = receiverAfter - receiverBefore

                if (amountSender <= 0) return@body false
                if (amountReceiver <= 0) return@body false
                if (amountSender != amountReceiver) return@body false
                if (senderBefore <= 0) return@body false
                if (senderAfter < 0) return@body false
                if (receiverAfter <= 0) return@body false
                if (receiverBefore < 0) return@body false

                return@body true
            }
        })

        val senderAddress = "sender123"
        val receiverAddress = "receiver321"

        val txn1 = txn {
            header {
                family = "test"
                type = "pay"
                version = "1.0"
                attributes["sender"] = senderAddress
                attributes["receiver"] = receiverAddress
            }

            payload {
                stateBefore.put("$senderAddress/value", 10)
                stateBefore.put("$receiverAddress/value", 0)

                stateAfter.put("$senderAddress/value", 5)
                stateAfter.put("$receiverAddress/value", 5)
            }

            sign { data -> SignatureAndPublicKey(ByteArray(0), ByteArray(0)) }
        }

        val valid1 = validator.validate(txn1)

        assert(valid1)

        val txn2 = txn {
            header {
                family = "test"
                type = "pay"
                version = "1.0"
                attributes["sender"] = senderAddress
                attributes["receiver"] = receiverAddress
            }

            payload {
                stateBefore.put("$senderAddress/value", 5)
                stateBefore.put("$receiverAddress/value", 5)

                stateAfter.put("$senderAddress/value", 7)
                stateAfter.put("$receiverAddress/value", 3)
            }

            sign { data -> SignatureAndPublicKey(ByteArray(0), ByteArray(0)) }
        }

        val valid2 = validator.validate(txn2)

        assert(!valid2)
    }
}
