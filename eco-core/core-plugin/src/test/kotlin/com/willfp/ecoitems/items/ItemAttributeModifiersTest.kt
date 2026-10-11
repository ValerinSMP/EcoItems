package com.willfp.ecoitems.items

import org.bukkit.NamespacedKey
import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class ItemAttributeModifiersTest {
    private fun key(id: String) = NamespacedKey("ecoitems", id)
    private fun modifier(id: String, amount: Double) =
        AttributeModifier(key(id), amount, AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND)

    @Test fun `unchanged attributes are not rewritten`() {
        val instance = mock(AttributeInstance::class.java)
        val existing = modifier("speed_0", 0.0)
        `when`(instance.modifiers).thenReturn(listOf(existing))
        reconcileItemAttributes(instance, mapOf("speed_0" to 0.0), ::key)
        verify(instance, never()).removeModifier(any(AttributeModifier::class.java))
        verify(instance, never()).addModifier(any(AttributeModifier::class.java))
    }
    @Test fun `unequip removes old modifier without touching foreign effects`() {
        val instance = mock(AttributeInstance::class.java)
        val old = modifier("speed_0", -0.5)
        val foreign = AttributeModifier(NamespacedKey("libreforge", "speed"), 0.1,
            AttributeModifier.Operation.ADD_SCALAR, EquipmentSlotGroup.ANY)
        `when`(instance.modifiers).thenReturn(listOf(old, foreign))
        reconcileItemAttributes(instance, emptyMap(), ::key)
        verify(instance).removeModifier(old)
        verify(instance, never()).removeModifier(foreign)
        verify(instance, never()).addModifier(any(AttributeModifier::class.java))
    }
    @Test fun `weapon change replaces old value exactly once`() {
        val instance = mock(AttributeInstance::class.java)
        val old = modifier("speed_0", -0.1)
        `when`(instance.modifiers).thenReturn(listOf(old))
        reconcileItemAttributes(instance, mapOf("speed_0" to 0.0), ::key)
        verify(instance).removeModifier(old)
        verify(instance).addModifier(modifier("speed_0", 0.0))
    }
}
