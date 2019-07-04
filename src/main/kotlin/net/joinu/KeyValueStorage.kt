package net.joinu

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


interface KeyValueStorage : MutableMap<String, String> {
    fun subset(prefix: String): KeyValueStorage
    fun <T : Any> subset(prefix: String, clazz: Class<T>): T?
    fun merge(other: Map<String, String>, prefix: String = "")
    fun merge(obj: Any, prefix: String = "")
}

inline fun <reified T : Any> KeyValueStorage.subset(prefix: String) = subset(prefix, T::class.java)

class InMemoryKeyValueStorage : KeyValueStorage {
    private val trie = CaseSensitiveMapTrie<String>()
    private val mapper = jacksonObjectMapper()

    override fun merge(obj: Any, prefix: String) {
        val node = mapper.valueToTree<JsonNode>(obj)
        val leaves = node.flatLeaves(prefix)

        leaves.forEach { (key, value) -> put(key, value) }
    }

    override fun merge(other: Map<String, String>, prefix: String) {
        other.entries.forEach { (key, value) -> put(prefix + key, value) }
    }

    override val size: Int
        get() = trie.size()

    override fun containsKey(key: String) = trie.contains(key)

    override fun containsValue(value: String) = trie.values().contains(value)

    override fun get(key: String): String? = trie.get(key)

    override fun isEmpty() = trie.size() == 0

    override val entries: MutableSet<MutableMap.MutableEntry<String, String>>
        get() = trie.keys().associateWith { trie.get(it) }.toMutableMap().entries

    override val keys: MutableSet<String>
        get() = trie.keys().map { it }.toMutableSet()

    override val values: MutableCollection<String>
        get() = trie.values()

    override fun clear() = trie.fastClear()

    override fun put(key: String, value: String): String? {
        val prevValue = trie.get(key)
        trie.insert(key, value)

        return prevValue
    }

    override fun putAll(from: Map<out String, String>) = from.forEach { put(it.key, it.value) }

    override fun remove(key: String): String? {
        val prevValue = trie.get(key)
        trie.deleteKey(key)

        return prevValue
    }

    override fun subset(prefix: String): KeyValueStorage {
        val subsetKeys = trie.getKeySuggestions(prefix)
        val subset = InMemoryKeyValueStorage()

        subsetKeys.forEach { k -> subset[k] = trie[k] }

        return subset
    }

    override fun <T : Any> subset(prefix: String, clazz: Class<T>): T? {
        // TODO: unflatLeaves
    }
}

fun JsonNode.flatLeaves(prefix: String = "", separator: String = "/"): Collection<Pair<String, String>> {
    val result = mutableListOf<Pair<String, String>>()

    for ((key, value) in fields()) {
        if (value.isObject)
            result.addAll(value.flatLeaves(prefix + key + separator))
        else {
            val strValue = if (value.isTextual) value.asText() else value.toString()
            result.add(prefix + key to strValue)
        }
    }

    return result
}
