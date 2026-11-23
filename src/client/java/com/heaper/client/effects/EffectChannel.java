package com.heaper.client.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EffectChannel {
    private static final List<EffectChannel> ALL = new ArrayList<>();

    private final Map<String, Float> requests = new HashMap<>();
    private float current = 0f;

    public EffectChannel() {
        ALL.add(this);
    }

    public void set(String source, float intensity) {
        if (intensity <= 0f) {
            requests.remove(source);
        } else {
            requests.put(source, Math.min(1f, intensity));
        }
        recompute();
    }

    public void clear(String source) {
        requests.remove(source);
        recompute();
    }

    public void reset() {
        requests.clear();
        current = 0f;
    }

    public float current() {
        return current;
    }

    private void recompute() {
        float max = 0f;
        for (float v : requests.values()) {
            max = Math.max(max, v);
        }
        current = max;
    }

    public record Binding(EffectChannel channel, float scale) {
        public void set(String source, float intensity) {
            channel.set(source, intensity * scale);
        }

        public void clear(String source) {

            channel.clear(source);
        }
    }

    public Binding bind() {
        return new Binding(this, 1f);
    }

    public Binding bind(float scale) {
        return new Binding(this, scale);
    }

    public static void resetAll() {
        for (EffectChannel channel : ALL) {
            channel.reset();
        }
    }
}
