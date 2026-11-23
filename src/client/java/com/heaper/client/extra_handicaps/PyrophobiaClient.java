package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.CameraShake;
import com.heaper.extra_handicaps.Pyrophobia;
import net.minecraft.client.Minecraft;

public class PyrophobiaClient extends ConditionRampHandicap {
    public PyrophobiaClient() {
        super("pyrophobia", 0.05f, 0.04f, 1f, CameraShake.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return Pyrophobia.nearFire(mc.player);
    }
}
