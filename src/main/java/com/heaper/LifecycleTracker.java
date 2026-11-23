package com.heaper;

import com.handicap.Handicap;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public class LifecycleTracker<K> {
    private final Map<K, Set<Handicap>> lastSeen = new HashMap<>();

    public void update(K key, Set<Handicap> current, Consumer<Handicap> onGive, Consumer<Handicap> onRemove) {
        Set<Handicap> previous = lastSeen.getOrDefault(key, Set.of());
        for (Handicap d : current) {
            if (!previous.contains(d)) {
                onGive.accept(d);
            }
        }
        for (Handicap d : previous) {
            if (!current.contains(d)) {
                onRemove.accept(d);
            }
        }
        lastSeen.put(key, current.isEmpty() ? EnumSet.noneOf(Handicap.class) : EnumSet.copyOf(current));
    }

    public Set<Handicap> forget(K key) {
        Set<Handicap> removed = lastSeen.remove(key);
        return removed == null ? Set.of() : removed;
    }

    public void clear() {
        lastSeen.clear();
    }
}
