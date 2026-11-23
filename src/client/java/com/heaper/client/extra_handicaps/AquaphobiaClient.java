package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.CameraShake;
import net.minecraft.client.Minecraft;

public class AquaphobiaClient extends ConditionRampHandicap {
    private static final double DRAG = 0.85;

    public AquaphobiaClient() {
        super("aquaphobia", 0.03f, 0.05f, 0.8f, CameraShake.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return mc.player.isInWater();
    }

    @Override
    public void onTick(Minecraft mc) {
        super.onTick(mc);
        if (mc.player != null && mc.player.isInWater()) {
            mc.player.setDeltaMovement(mc.player.getDeltaMovement().multiply(DRAG, 1.0, DRAG));
        }
    }
}
