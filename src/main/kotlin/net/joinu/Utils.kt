package net.joinu

import com.arun.trie.MapTrie
import com.arun.trie.base.TrieNode
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets
import java.util.*


object SafeBase64 {
    private val decoder = Base64.getUrlDecoder()
    private val encoder = Base64.getUrlEncoder()

    var charset: Charset = StandardCharsets.UTF_8

    // from base64
    fun decode(text: String) = decoder.decode(text).toString(charset)

    // to base64
    fun encode(text: String) = encoder.encodeToString(text.toByteArray(charset))
}

class CaseSensitiveMapTrie<T : Any> : MapTrie<T>() {
    override fun trimLowercaseString(key: String?): String {
        return key!!.trim()
    }

    override fun getKeySuggestions(key: String?): List<String> {
        if (key == null) return mutableListOf()

        val trimmedKey = trimLowercaseString(key)

        val prefix = StringBuilder()

        var crawler = root
        for (i in 0 until trimmedKey.length) {
            val c = trimmedKey[i]
            if (crawler.containsChild(c)) {
                prefix.append(c)
                crawler = crawler.getChild(c)
            } else {
                return emptyList()
            }
        }

        val strings = LinkedList<String>()
        findKeySuggestions(crawler, prefix, strings)
        return strings
    }

    private fun findKeySuggestions(trieNode: TrieNode<T>?, prefix: StringBuilder, words: MutableList<String>) {
        if (trieNode == null) return
        if (trieNode.isKey) words.add(prefix.toString())
        if (trieNode.isEnd) return

        for (child in trieNode.children)
            findKeySuggestions(child, StringBuilder(prefix).append(child.char), words)
    }
}
