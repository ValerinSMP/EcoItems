package com.willfp.ecoitems.items

import org.bukkit.attribute.AttributeInstance
import org.bukkit.attribute.AttributeModifier
import org.bukkit.inventory.EquipmentSlotGroup

private val AttributeModifier.isFromEcoItems: Boolean
        get() {
            if (this.name.startsWith("EcoItems Damage", true)
                || this.name.startsWith("EcoItems Speed", true)
                || this.name.startsWith("EcoItems Range", true)
            ) {
                return true
            }

            return this.key.namespace == "ecoitems" && (this.key.key.startsWith("damage")
                    || this.key.key.startsWith("speed")
                    || this.key.key.startsWith("range"))
        }

internal fun reconcileItemAttributes(instance: AttributeInstance, desired: Map<String, Double>, keyFor: (String) -> org.bukkit.NamespacedKey) {
        with(instance) {
        val remaining = desired.toMutableMap()
        for (modifier in modifiers.filter { it.isFromEcoItems }) {
            val amount = remaining[modifier.key.key]
            if (modifier.key.namespace == "ecoitems" && amount != null && modifier.amount == amount
                && modifier.operation == AttributeModifier.Operation.ADD_NUMBER
                && modifier.slotGroup == EquipmentSlotGroup.MAINHAND) {
                remaining.remove(modifier.key.key)
            } else {
                removeModifier(modifier)
            }
        }
        for ((key, amount) in remaining) {
            addModifier(AttributeModifier(keyFor(key), amount,
                AttributeModifier.Operation.ADD_NUMBER, EquipmentSlotGroup.MAINHAND))
        }
    }
    }
