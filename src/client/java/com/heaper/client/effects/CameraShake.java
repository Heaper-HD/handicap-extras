package com.heaper.client.effects;

public final class CameraShake {
    public static final EffectChannel CHANNEL = new EffectChannel();

    private CameraShake() {}

    public static float current() {
        return CHANNEL.current();
    }
}
