package com.willfp.ecoitems.items

/** Main-thread only. Cancelled callbacks cannot consume a newer session's update. */
internal class AttributeUpdateQueue<K> {
    private val pending = mutableMapOf<K, Any>()

    fun request(key: K, schedule: (() -> Unit) -> Unit, apply: () -> Unit) {
        if (pending.containsKey(key)) return
        val token = Any()
        pending[key] = token
        try {
            schedule {
                if (pending[key] === token) {
                    pending.remove(key)
                    apply()
                }
            }
        } catch (exception: RuntimeException) {
            if (pending[key] === token) pending.remove(key)
            throw exception
        }
    }

    fun clear() { pending.clear() }

    fun cancel(key: K) { pending.remove(key) }
}

internal fun attributeOffset(target: Double, playerBase: Double, materialOffset: Double): Double =
    target - playerBase - materialOffset
