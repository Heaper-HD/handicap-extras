package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.CameraShake;
import com.heaper.extra_handicaps.SeparationAnxiety;
import net.minecraft.client.Minecraft;

public class SeparationAnxietyClient extends ConditionRampHandicap {
    public SeparationAnxietyClient() {
        super("separation_anxiety", 0.015f, 0.05f, 1f, CameraShake.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return SeparationAnxiety.alone(mc.player);
    }
}
