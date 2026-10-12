package com.lincoln.maceguard.website;

import com.lincoln.maceguard.warzone.rotation.RotationManager;
import com.lincoln.maceguard.warzone.rotation.OverrideDurationMode;
import com.lincoln.maceguard.warzone.runtime.WarzoneRuntime;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class PublicWarzoneSnapshot {
    private PublicWarzoneSnapshot() { }
    public static Map<String, Object> capture(WarzoneRuntime runtime, long now) {
        RotationManager rotation = runtime.rotations();
        var selection = rotation.activeSelection();
        var active = selection.activeSet();
        Map<String, Object> kit = new LinkedHashMap<>();
        kit.put("id", "current-meta"); kit.put("type", "kit");
        kit.put("name", bounded(runtime.messages().plain(active.displayName()), 80));
        kit.put("description", bounded(runtime.messages().plain(active.description()), 2000));
        kit.put("manualOverride", selection.manualOverride());
        boolean indefinite = rotation.state().overrideActive()
                && rotation.state().overrideDurationMode() == OverrideDurationMode.UNTIL_CLEARED;
        if (!indefinite && (rotation.scheduleEnabled()
                || rotation.state().overrideActive() && rotation.state().overrideExpiresAtMillis() > 0)) {
            kit.put("endsAt", Instant.ofEpochMilli(rotation.nextEffectiveTransitionMillis()).toString());
        }
        var next = Map.<String, Object>of("id", "next-meta", "type", "activity",
                "name", bounded(runtime.messages().plain(nextName(rotation, indefinite)), 80), "description", "");
        return Map.of("kind", "warzone", "schemaVersion", 1,
                "generatedAt", Instant.ofEpochMilli(now).toString(),
                "items", List.of(kit, next));
    }
    private static String nextName(RotationManager rotation, boolean indefinite) {
        if (indefinite) return "After override is cleared";
        if (rotation.state().overrideActive()
                && (!rotation.scheduleEnabled()
                    || rotation.nextEffectiveTransitionMillis() < rotation.state().automaticSlotEndMillis()))
            return rotation.automaticSelection().activeSet().displayName();
        if (!rotation.scheduleEnabled()) return "Automatic schedule paused";
        return rotation.entryName(rotation.nextSlot().entry());
    }
    private static String bounded(String value, int limit) {
        return value == null ? "" : value.substring(0, Math.min(value.length(), limit));
    }
    public static long weekStart(long now) {
        return Instant.ofEpochMilli(now).atZone(ZoneOffset.UTC).toLocalDate()
                .with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli();
    }
    public static Map<String, Object> withLeaders(Map<String, Object> rotation, Object raw) {
        List<Map<String, Object>> rows = new ArrayList<>();
        for (Object item : (List<?>) rotation.get("items")) {
            if (!(item instanceof Map<?, ?> map)) throw new IllegalArgumentException("Invalid rotation");
            Map<String, Object> copy = new LinkedHashMap<>();
            map.forEach((key, value) -> copy.put(key.toString(), value)); rows.add(copy);
        }
        List<Map<String, Object>> leaders = validateLeaders(raw);
        if (leaders != null) rows.addAll(leaders);
        String status = leaders == null ? "Weekly WarzoneDuels results are unavailable."
                : leaders.isEmpty() ? "No counted match wins this week." : "Counted WarzoneDuels match wins this week.";
        rows.add(Map.of("id", "duels-status", "type", "activity", "name", "Weekly duel wins", "description", status));
        Map<String, Object> result = new LinkedHashMap<>(rotation); result.put("items", rows); return result;
    }
    private static List<Map<String, Object>> validateLeaders(Object raw) {
        if (!(raw instanceof List<?> values) || values.size() > 3) return null;
        List<Map<String, Object>> leaders = new ArrayList<>();
        var ids = new java.util.HashSet<String>();
        for (Object value : values) {
            if (!(value instanceof Map<?, ?> row) || !(row.get("id") instanceof String id)
                    || !id.matches("[a-fA-F0-9]{8}(-[a-fA-F0-9]{4}){3}-[a-fA-F0-9]{12}") || !ids.add(id)
                    || !(row.get("name") instanceof String name) || !name.matches("[A-Za-z0-9_. -]{1,40}")
                    || !(row.get("wins") instanceof Number wins) || !Double.isFinite(wins.doubleValue())
                    || wins.doubleValue() != Math.rint(wins.doubleValue()) || wins.doubleValue() < 0 || wins.doubleValue() > 1e9)
                return null;
            leaders.add(Map.of("id", "duels-" + id.toLowerCase(java.util.Locale.ROOT), "type", "leader",
                    "name", name, "wins", wins.longValue(), "description", ""));
        }
        return leaders;
    }
}
