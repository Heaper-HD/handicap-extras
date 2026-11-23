package com.heaper.client.extra_handicaps;

import com.heaper.client.ExtraClientHandicap;
import com.heaper.client.effects.CameraShake;
import com.heaper.extra_handicaps.SeparationAnxiety;
import net.minecraft.client.Minecraft;

public class SeparationAnxietyClient extends ExtraClientHandicap {
    private static final float RAMP_UP = 0.015f;
    private static final float RAMP_DOWN = 0.05f;

    public static float intensity = 0f;

    public SeparationAnxietyClient() {
        super("separation_anxiety");
    }

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player == null) {
            return;
        }
        if (SeparationAnxiety.alone(mc.player)) {
            intensity = Math.min(1f, intensity + RAMP_UP);
        } else {
            intensity = Math.max(0f, intensity - RAMP_DOWN);
        }
        CameraShake.set(id(), intensity);
    }

    @Override
    public void onRemove(Minecraft mc) {
        intensity = 0f;
    }
}
