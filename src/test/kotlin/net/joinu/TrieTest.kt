package net.joinu

import org.junit.jupiter.api.Test


data class TestPojo(val kek: String, val lol: Long, val shrek: TestPojo1)
data class TestPojo1(val mem: Int, val shpek: TestPojo2)
data class TestPojo2(val aue: Array<Int>)


class KVTest {
    @Test
    fun `raw values are added easily`() {
        val kv = JsonKeyValueStorage()

        kv.put("works/fine/one", 1)
        kv.put("works/fine/two", 2)

        println(kv)

        val innerObj = kv.get("works/fine")

        println(innerObj)
    }

    @Test
    fun `objects are also added easily`() {
        val kv = JsonKeyValueStorage()
        val value1 = TestPojo("1", 1, TestPojo1(1, TestPojo2(arrayOf(1, 2, 3, 4))))
        val value2 = TestPojo("2", 2, TestPojo1(2, TestPojo2(arrayOf(1, 2, 3, 4))))

        kv.put("works/fine/one", value1)
        kv.put("works/fine/two", value2)

        println(kv)

        val values = kv.get<Map<String, TestPojo>>("works/fine")

        println(values)
    }
}
