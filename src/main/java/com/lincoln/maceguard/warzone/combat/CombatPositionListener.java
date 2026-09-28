package com.lincoln.maceguard.warzone.combat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

/** Acquires a latch when a tagged player crosses into an effective combat-zone flag. */
public final class CombatPositionListener implements Listener {
    private final CombatScopeService scopes;
    private final CombatIntegrationListener lifecycle;

    public CombatPositionListener(CombatScopeService scopes, CombatIntegrationListener lifecycle) {
        this.scopes = scopes;
        this.lifecycle = lifecycle;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onMove(PlayerMoveEvent event) {
        Location destination = event.getTo();
        if (destination != null && changedBlock(event.getFrom(), destination))
            reconcilePosition(event.getPlayer(), destination);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleportComplete(PlayerTeleportEvent event) {
        reconcilePosition(event.getPlayer(), event.getTo());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onDeath(PlayerDeathEvent event) {
        lifecycle.clear(event.getEntity());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        lifecycle.clear(event.getPlayer());
    }

    private void reconcilePosition(Player player, Location destination) {
        lifecycle.positionChanged(player, destination);
    }

    private boolean changedBlock(Location from, Location to) {
        return from.getWorld() == null || to.getWorld() == null
                || !from.getWorld().getUID().equals(to.getWorld().getUID())
                || from.getBlockX() != to.getBlockX()
                || from.getBlockY() != to.getBlockY()
                || from.getBlockZ() != to.getBlockZ();
    }
}
