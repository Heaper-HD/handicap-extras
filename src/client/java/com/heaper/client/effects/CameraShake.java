package com.heaper.client.effects;

import java.util.HashMap;
import java.util.Map;

public final class CameraShake {
    private static final Map<String, Float> REQUESTS = new HashMap<>();
    private static float current = 0f;

    private CameraShake() {}

    public static void set(String source, float intensity) {
        if (intensity <= 0f) {
            REQUESTS.remove(source);
        } else {
            REQUESTS.put(source, Math.min(1f, intensity));
        }
        recompute();
    }

    public static void clear(String source) {
        REQUESTS.remove(source);
        recompute();
    }

    public static void reset() {
        REQUESTS.clear();
        current = 0f;
    }

    public static float current() {
        return current;
    }

    private static void recompute() {
        float max = 0f;
        for (float v : REQUESTS.values()) {
            max = Math.max(max, v);
        }
        current = max;
    }
}
