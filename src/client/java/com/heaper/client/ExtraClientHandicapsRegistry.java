package com.heaper.client;

import com.handicap.Handicap;
import com.heaper.client.extra_handicaps.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class ExtraClientHandicapsRegistry {
    private static final Map<Handicap, ExtraClientHandicap> HANDICAPS = new LinkedHashMap<>();
    private static boolean frozen = false;

    private ExtraClientHandicapsRegistry() {}

    public static void register(ExtraClientHandicap h) {
        if (frozen) {
            throw new IllegalStateException("Client handicap registry is frozen; register in init().");
        }
        if (HANDICAPS.containsKey(h.handicap())) {
            throw new IllegalStateException("Client handicap '" + h.id() + "' was already registered");
        }
        HANDICAPS.put(h.handicap(), h);
    }

    public static ExtraClientHandicap get(Handicap h) {
        return HANDICAPS.get(h);
    }

    public static Collection<ExtraClientHandicap> all() {
        return Collections.unmodifiableCollection(HANDICAPS.values());
    }

    public static void init() {
        register(new ArthritisClient());

        register(new SeparationAnxietyClient());
        register(new AgoraphobiaClient());
        register(new AcrophobiaClient());
        register(new AquaphobiaClient());
        register(new PhotophobiaClient());
        register(new PyrophobiaClient());
        register(new SocialAnxietyClient());

        frozen = true;
    }
}
