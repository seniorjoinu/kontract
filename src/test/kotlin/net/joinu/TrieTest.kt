package net.joinu

import org.junit.jupiter.api.Test


data class Test(val kek: String, val lol: Long, val shrek: Test1)
data class Test1(val mem: Int, val shpek: Test2)
data class Test2(val aue: Array<Int>)


class KVTest {
    @Test
    fun kek() {
        val kv = InMemoryKeyValueStorage()

        kv["/base/suffix1/value1"] = "1"
        kv["/base/suffix1/value2"] = "2"
        kv["/base/suffix2/value1"] = "3"
        kv["/base/suffix2/value2"] = "4"

        println(kv.keys)
        println(kv.values)
        println(kv.entries)
        println(kv.subset("/base/suffix1").entries)

        val k = Test("123", 123, Test1(12, Test2(arrayOf(1, 2, 3, 4))))

        kv.merge(k, "/base/suffix1/")
        println(kv.entries)
    }
}
