package com.willfp.ecoitems.items

import com.willfp.ecoitems.plugin
import com.willfp.libreforge.slot.SlotTypes
import com.willfp.libreforge.slot.impl.NumericSlotType
import org.bukkit.Bukkit
import org.bukkit.attribute.Attribute
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.EventPriority
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerSwapHandItemsEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.player.PlayerRespawnEvent
import org.bukkit.event.player.PlayerItemBreakEvent
import java.util.UUID
import org.bukkit.event.entity.EntityPickupItemEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerItemHeldEvent

private const val PLAYER_BASE_ATTACK_DAMAGE = 1.0
private const val PLAYER_BASE_ATTACK_SPEED = 4.0
private const val PLAYER_BASE_ENTITY_INTERACTION_RANGE = 3.0

object ItemAttributeListener : Listener {
    private val updates = AttributeUpdateQueue<UUID>()

    fun refreshAll() {
        updates.clear()
        Bukkit.getOnlinePlayers().forEach { request(it) }
    }

    fun stop() {
        updates.clear()
        for (player in Bukkit.getOnlinePlayers()) {
            for (attribute in listOf(Attribute.ATTACK_DAMAGE, Attribute.ATTACK_SPEED, Attribute.ENTITY_INTERACTION_RANGE)) {
                player.getAttribute(attribute)?.let { instance ->
                    reconcileItemAttributes(instance, emptyMap()) { plugin.createNamespacedKey(it) }
                }
            }
        }
    }

    private fun request(player: Player) {
        updates.request(player.uniqueId, { task -> plugin.scheduler.run { task() } }) {
            if (player.isOnline) apply(player)
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handle(event: PlayerItemHeldEvent) = request(event.player)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleDrop(event: PlayerDropItemEvent) = request(event.player)

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handlePickup(event: EntityPickupItemEvent) {
        val player = event.entity as? Player ?: return
        if (event.item.itemStack.ecoItem != null) request(player)
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun handleSwap(event: PlayerSwapHandItemsEvent) = request(event.player)

    // Cancelled GUI events may still move items through another plugin.
    @EventHandler(priority = EventPriority.MONITOR)
    fun handleClick(event: InventoryClickEvent) {
        (event.whoClicked as? Player)?.let { request(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleDrag(event: InventoryDragEvent) {
        (event.whoClicked as? Player)?.let { request(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleClose(event: InventoryCloseEvent) {
        (event.player as? Player)?.let { request(it) }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleJoin(event: PlayerJoinEvent) = request(event.player)

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleRespawn(event: PlayerRespawnEvent) = request(event.player)

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleBreak(event: PlayerItemBreakEvent) = request(event.player)

    @EventHandler(priority = EventPriority.MONITOR)
    fun handleQuit(event: PlayerQuitEvent) {
        updates.cancel(event.player.uniqueId)
    }

    private fun apply(player: Player) {
        // Read the real inventory after the event; the provider answer can still be from an earlier tick.
        val items = SlotTypes.baseTypes.asSequence()
            .filter { it != NumericSlotType(player.inventory.heldItemSlot) }
            .flatMap { slot ->
                slot.getItems(player).asSequence().mapNotNull { stack ->
                    stack.ecoItem?.takeIf { EcoItemFinder.isValidInSlot(it, slot) }?.let { it to stack.type }
                }
            }.toList()

        val damageInst = player.getAttribute(Attribute.ATTACK_DAMAGE) ?: return
        val speedInst = player.getAttribute(Attribute.ATTACK_SPEED) ?: return
        val rangeInst = player.getAttribute(Attribute.ENTITY_INTERACTION_RANGE) ?: return

        val damage = mutableMapOf<String, Double>()
        val speed = mutableMapOf<String, Double>()
        val range = mutableMapOf<String, Double>()
        for ((offset, entry) in items.withIndex()) {
            val (item, baselineMaterial) = entry
            if (item.baseDamage != null) {
                damage["damage_$offset"] = attributeOffset(
                    item.baseDamage, PLAYER_BASE_ATTACK_DAMAGE, baselineMaterial.attackDamageModifier
                )
            }

            if (item.baseAttackSpeed != null) {
                speed["speed_$offset"] = attributeOffset(
                    item.baseAttackSpeed, PLAYER_BASE_ATTACK_SPEED, baselineMaterial.attackSpeedModifier
                )
            }

            if (item.baseAttackRange != null) {
                range["range_$offset"] = attributeOffset(
                    item.baseAttackRange, PLAYER_BASE_ENTITY_INTERACTION_RANGE, baselineMaterial.entityInteractionRangeModifier
                )
            }
        }
        reconcileItemAttributes(damageInst, damage) { plugin.createNamespacedKey(it) }
        reconcileItemAttributes(speedInst, speed) { plugin.createNamespacedKey(it) }
        reconcileItemAttributes(rangeInst, range) { plugin.createNamespacedKey(it) }
    }

}
