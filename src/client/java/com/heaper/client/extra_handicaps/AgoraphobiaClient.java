package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.CameraShake;
import com.heaper.extra_handicaps.Agoraphobia;
import net.minecraft.client.Minecraft;

public class AgoraphobiaClient extends ConditionRampHandicap {
    public AgoraphobiaClient() {
        super("agoraphobia", 0.01f, 0.04f, 0.7f, CameraShake.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return Agoraphobia.exposed(mc.player);
    }
}
