package com.willfp.ecoitems.items

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class AttributeUpdateQueueTest {
    @Test fun `multiple events use latest equipment once`() {
        val queue = AttributeUpdateQueue<String>()
        val tasks = mutableListOf<() -> Unit>()
        var held = "mace"
        val seen = mutableListOf<String>()
        repeat(100) { queue.request("p", { tasks.add(it) }) { seen.add(held) } }
        held = "sword"
        assertEquals(1, tasks.size)
        tasks.removeAt(0)()
        assertEquals(listOf("sword"), seen)
        queue.request("p", { tasks.add(it) }) { seen.add("air") }
        tasks.removeAt(0)()
        assertEquals(listOf("sword", "air"), seen)
    }
    @Test fun `quit and reconnect ignore old callback`() {
        val queue = AttributeUpdateQueue<String>()
        val tasks = mutableListOf<() -> Unit>()
        var calls = 0
        queue.request("p", { tasks.add(it) }) { fail<Unit>("old session") }
        queue.cancel("p")
        queue.request("p", { tasks.add(it) }) { calls++ }
        tasks.forEach { it() }
        assertEquals(1, calls)
    }
    @Test fun `reload clears cancelled tasks and admits new updates`() {
        val queue = AttributeUpdateQueue<String>()
        val tasks = mutableListOf<() -> Unit>()
        queue.request("p", { tasks.add(it) }) { fail<Unit>("before reload") }
        queue.clear()
        var calls = 0
        queue.request("p", { tasks.add(it) }) { calls++ }
        tasks.forEach { it() }
        assertEquals(1, calls)
    }
    @Test fun `different players do not block each other`() {
        val queue = AttributeUpdateQueue<String>()
        val tasks = mutableListOf<() -> Unit>()
        var calls = 0
        for (i in 1..100) repeat(4) { queue.request("p$i", { tasks.add(it) }) { calls++ } }
        assertEquals(100, tasks.size)
        tasks.forEach { it() }
        assertEquals(100, calls)
    }
    @Test fun `failed scheduling permits retry`() {
        val queue = AttributeUpdateQueue<String>()
        assertThrows(IllegalStateException::class.java) {
            queue.request("p", { throw IllegalStateException() }) {}
        }
        var calls = 0
        queue.request("p", { it() }) { calls++ }
        assertEquals(1, calls)
    }
    @Test fun `correct material offsets preserve configured weapon values`() {
        for ((target, material) in listOf(0.9 to -3.4, 1.6 to -2.4, 1.0 to -3.0)) {
            assertEquals(target, 4.0 + material + attributeOffset(target, 4.0, material), 1e-12)
        }
    }
}
