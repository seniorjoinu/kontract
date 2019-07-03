package net.joinu


interface PathNode : Comparable<PathNode> {
    val parent: PathNode?
    val name: String

    val pathFromRoot: String
        get() = if (parent == null) name else parent!!.pathFromRoot + name

    override fun compareTo(other: PathNode) = this.name.compareTo(other.name)
}

class DirNode(override val name: String, override val parent: PathNode?) : PathNode {
    val children = sortedMapOf<String, PathNode>()

    fun find(keys: Collection<String>): PathNode? {
        // TODO: обход
    }
}

class FileNode(override val name: String, val value: String, override val parent: PathNode?) : PathNode {
    private var isActive = true

    fun remove() {
        isActive = false
    }

    fun restore() {
        isActive = true
    }
}

class TrieMap(val dir: DirNode, val separator: String = "/") : MutableMap<String, String> {
    constructor(rootDirName: String = "/", separator: String = "/") : this(DirNode(rootDirName, null), separator)

    override fun put(key: String, value: String): String? {
        if (!key.contains(separator)) {
            // add key to the root
            val leaf = FileNode(key, value, dir)
            return dir.children.put(leaf.name, leaf)?.name
        }

        val keys = key.split(separator).filter { it.isNotBlank() }


    }

    override val size: Int
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun containsKey(key: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun containsValue(value: String): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun get(key: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEmpty(): Boolean {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override val entries: MutableSet<MutableMap.MutableEntry<String, String>>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val keys: MutableSet<String>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.
    override val values: MutableCollection<String>
        get() = TODO("not implemented") //To change initializer of created properties use File | Settings | File Templates.

    override fun clear() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun putAll(from: Map<out String, String>) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun remove(key: String): String? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
