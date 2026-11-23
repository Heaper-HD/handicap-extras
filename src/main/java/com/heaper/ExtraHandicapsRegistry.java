package com.heaper;

import com.handicap.Handicap;
import com.heaper.extra_handicaps.SeparationAnxiety;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtraHandicapsRegistry {
    private static final Map<Handicap, ExtraHandicap> HANDICAPS = new LinkedHashMap<>();
    private static boolean frozen = false;

    public static void register(ExtraHandicap h) {
        if (frozen) {
            throw new IllegalStateException("Handicap registry is frozen");
        }

        if (HANDICAPS.containsKey(h.handicap())) {
            throw new IllegalStateException("Handicap '" + h.id() + "' was already registered.");
        }

        HANDICAPS.put(h.handicap(), h);
    }

    public static ExtraHandicap get(Handicap h) {
        return HANDICAPS.get(h);
    }

    public static Collection<ExtraHandicap> all() {
        return Collections.unmodifiableCollection(HANDICAPS.values());
    }

    public static void init() {
        register(new SeparationAnxiety());

        frozen = true;
    }
}
