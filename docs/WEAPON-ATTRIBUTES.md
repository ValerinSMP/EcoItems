# Weapon attributes after equipment changes

Attribute corrections read the current slot and item material on the next tick, independently of LibreForge's cached provider answer. Slot validity and numeric-slot exclusion follow the existing native finder contract. Base damage, attack speed and interaction range retain their configured meanings and MAINHAND scope.

Slot changes, drops, EcoItem pickups, swaps, inventory clicks/drags/close, joins, respawns and item breaks signal an update. GUI-cancelled clicks are included because plugins can move items themselves. Signals are coalesced per player into one next-tick callback; they do not create polling or hit-time work. A cancelled session callback cannot remove a newer session's request. Native reload clears cancelled callbacks and refreshes online players once. Disable removes only EcoItems-owned attributes.

Reconciliation retains identical owned modifiers, replaces changed ones and removes obsolete ones. LibreForge and other plugins' keys remain untouched. No weapon balance, XP bridge, damage callback or EcoSkills statistic changes.

Nine JVM tests cover event coalescing with current equipment, cancellation/reconnection, reload cancellation, 100 independent players, scheduler failure, vanilla material arithmetic, unchanged modifiers, unequip removal preserving foreign effects, and replacement without accumulation. These are not a live performance benchmark or proof of either ticket's exact cause.

Gameplay pending after owner activation in Test: mace -> sword -> vanilla -> empty hand, repeated rapid switches, F swaps, inventory number keys/shift/drag, drop/pickup, reconnect, respawn and native reload. Compare effective attack speed and damage and confirm charge recovery. Production installation and lifecycle are not authorized by this delivery.

Build: canonical personal repository, existing Gradle Wrapper via GradleWrapperMain (original shell script is CRLF/non-executable), Kotlin 2.3.0, JDK 21 target/toolchain, limited executor launched with JDK25. Existing isolated Maven repository retained. Runtime is assembled over the exact Test baseline, replacing only five correction classes; loader/resources and all other classes remain byte-identical.

Test installed runtime: EcoItems-2026.27-all.jar
SHA256: e3636d3b6836b3630459dca3aabcf622a68a162c3cade41d7ecbc348aadd9fb1
Full compiled artifact SHA256: 1a10677c27b41e956a5475db405f92b82b9bd758bd51065813abdf9bd9c76a2a
Baseline SHA256: b0d603326e0754abfc6d87ddf7c84f260c6343c56ada46a2676471ff0c9e21b3
Backup: /mnt/storage/backups/ai/survival-custom/ecoitems-weapon-attributes-20261011T210540Z
Evidence: /home/mtyn/staging/survival-custom/ecoitems-weapon-attributes/
