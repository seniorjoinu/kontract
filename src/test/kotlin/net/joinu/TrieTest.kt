package net.joinu

import org.junit.jupiter.api.Test

class TrieTest {
    @Test
    fun `node split works as expected`() {
        val node = TrieNode("abcdefg", null, "123")
        val (base, suffix) = node.split(3)

        assert(base.prefix == null)
        assert(!base.isLeaf())
        assert(base.absolutePath == "abcd")
        assert(base.path == base.absolutePath)
        assert(base.isPrefixFor("abcdefg"))
        assert(base.value == null)

        assert(suffix.isLeaf())
        assert(suffix.absolutePath == "abcdefg")
        assert(suffix.path == "efg")
        assert(suffix.prefix == base)
        assert(suffix.value == "123")
    }

    @Test
    fun `node append remove work as expected`() {
        val node = TrieNode("test", null)

        assert(node.isLeaf())

        val child = node.append("123", "val")

        assert(!node.isLeaf())
        assert(child.isLeaf())
        assert(node.absolutePath == "test")
        assert(child.absolutePath == "test123")
        assert(node.value == null)
        assert(child.value == "val")

        assert(!node.remove("12"))
        assert(!node.isLeaf())

        assert(node.remove("123"))
        assert(node.isLeaf())
    }

    @Test
    fun `forking works as expected`() {
        val root = TrieNode("/base/value1", null, "abc")
        val (newRoot, branch) = root.split("/base")

        assert(newRoot.path == "/base")
        assert(branch.path == "/value1")

        val anotherBranch = newRoot.append("/value2", "def")
        assert(!newRoot.isLeaf())
        assert(branch.isLeaf())
        assert(anotherBranch.isLeaf())
        assert(branch.value == "abc")
        assert(anotherBranch.value == "def")
        assert(branch.absolutePath == "/base/value1")
        assert(anotherBranch.absolutePath == "/base/value2")
    }
}
