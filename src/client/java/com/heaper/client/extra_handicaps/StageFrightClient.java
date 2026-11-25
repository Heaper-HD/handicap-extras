package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.CameraShake;
import com.heaper.extra_handicaps.StageFright;
import net.minecraft.client.Minecraft;

public class StageFrightClient extends ConditionRampHandicap {
    public StageFrightClient() {
        super("stage_fright", 0.03f, 0.05f, 0.4f, CameraShake.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return StageFright.watched(mc.player);
    }
}
