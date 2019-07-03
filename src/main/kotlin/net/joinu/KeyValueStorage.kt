package net.joinu

interface KeyValueStorage

data class StorageKey(val base: String, val suffixes: List<String> = emptyList()) {
    companion object {
        var maxBaseLength: Int = 64
    }
}

class StorageKeyBuilder : Builder<StorageKey> {
    private var base = ""
    private val suffixes = mutableListOf<String>()

    fun base(value: String): String {
        check(base.length <= StorageKey.maxBaseLength) { "Base should be less than ${StorageKey.maxBaseLength}! (StorageKey.maxBaseLength)" }

        base = value

        return value
    }

    operator fun String.unaryPlus() = base(this)

    fun suffix(value: String): String {
        check(value.isNotBlank()) { "Suffix should not be empty!" }

        suffixes.add(value)

        return value
    }

    operator fun String.div(other: String) = suffix(other)

    override fun build(): StorageKey {
        check(base.isNotBlank()) { "Base should not be empty!" }

        return StorageKey(base, suffixes)
    }
}

fun key(init: StorageKeyBuilder.() -> Unit): StorageKey {
    val builder = StorageKeyBuilder()
    builder.init()
    return builder.build()
}

fun main() {
    val k = key { +"startsWithIt" / "continuesWithIt" / "andAlsoIt" }
}
