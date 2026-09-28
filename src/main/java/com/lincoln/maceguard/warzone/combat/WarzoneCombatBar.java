package com.lincoln.maceguard.warzone.combat;

import com.lincoln.maceguard.warzone.config.WarzoneConfig;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/** Displays the CombatLogX timer only for players carrying a Warzone combat latch. */
public final class WarzoneCombatBar {
    private final CombatScopeService scopes;
    private final WarzoneConfig.WarzoneTag config;
    private final Map<UUID, Shown> shown = new HashMap<>();

    public WarzoneCombatBar(CombatScopeService scopes, WarzoneConfig.WarzoneTag config) {
        this.scopes = scopes;
        this.config = config;
    }

    public void reconcile(Iterable<? extends Player> players) {
        Set<UUID> seen = new HashSet<>();
        for (Player player : players) {
            UUID id = player.getUniqueId();
            seen.add(id);
            if (!config.enabled() || !config.bossBarEnabled() || !scopes.warzoneTagged(player)
                    || player.hasPermission("warzonerotator.bypass")) {
                hide(id);
                continue;
            }
            Duration remaining = scopes.combat().remaining(player);
            if (remaining == null || remaining.isZero() || remaining.isNegative()) {
                hide(id);
                continue;
            }
            int maximum = Math.max(1, scopes.combat().maximumSeconds(player));
            float progress = Math.max(0f, Math.min(1f,
                    remaining.toMillis() / (maximum * 1000f)));
            long seconds = Math.max(1, (remaining.toMillis() + 999) / 1000);
            Component title = Component.text(config.bossBarTitle() + " • " + seconds + "s")
                    .color(TextColor.fromHexString(config.bossBarTextColor()));
            Shown current = shown.get(id);
            if (current == null) {
                BossBar bar = BossBar.bossBar(title, progress,
                        BossBar.Color.valueOf(config.bossBarFillColor()),
                        BossBar.Overlay.PROGRESS);
                player.showBossBar(bar);
                shown.put(id, new Shown(player, bar));
            } else {
                current.bar().name(title);
                current.bar().progress(progress);
            }
        }
        for (UUID id : Set.copyOf(shown.keySet())) {
            if (!seen.contains(id)) hide(id);
        }
    }

    public void clear() {
        for (UUID id : Set.copyOf(shown.keySet())) hide(id);
    }

    private void hide(UUID id) {
        Shown current = shown.remove(id);
        if (current != null) current.player().hideBossBar(current.bar());
    }

    private record Shown(Player player, BossBar bar) { }
}
