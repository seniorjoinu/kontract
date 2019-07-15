package net.joinu

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.node.JsonNodeFactory
import com.fasterxml.jackson.databind.node.MissingNode
import com.fasterxml.jackson.databind.node.ObjectNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper


class JsonKeyValueStorage(val root: ObjectNode = JsonNodeFactory.instance.objectNode(), val separator: String = "/") {
    override fun toString() = root.toString()

    fun get(path: String): JsonNode {
        val parts = path.split(separator)

        var currentNode = root

        for (i in parts.indices) {
            val result = currentNode.findPath(parts[i])

            when {
                i == parts.lastIndex -> return result
                result !is ObjectNode -> throw RuntimeException("Error getting $path from $this: value of \"${parts[i]}\" is not an object!")
                else -> currentNode = result
            }
        }

        return MissingNode.getInstance()
    }

    fun put(path: String, node: JsonNode) {
        val parts = path.split(separator)

        var currentNode = root

        for (i in parts.indices) {
            val result = currentNode.findPath(parts[i])

            when {
                i != parts.lastIndex -> currentNode = when (result) {
                    is MissingNode -> currentNode.putObject(parts[i])
                    !is ObjectNode -> throw RuntimeException("Error putting $path to $this: value of \"${parts[i]}\" is not an object!")
                    else -> result
                }
            }
        }

        currentNode.set(parts.last(), node)
    }

    companion object {
        val mapper = jacksonObjectMapper()

        fun fromString(jsonStr: String, separator: String = "/"): JsonKeyValueStorage {
            val root = mapper.valueToTree<ObjectNode>(jsonStr)
            return JsonKeyValueStorage(root, separator)
        }
    }

    fun <T : Any> get(path: String, clazz: Class<T>): T? {
        val node = get(path)
        return mapper.treeToValue(node, clazz)
    }

    inline fun <reified T : Any> get(path: String) = get(path, T::class.java)

    fun put(path: String, obj: Any) {
        val node = mapper.valueToTree<JsonNode>(obj)
        put(path, node)
    }
}
