package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.SquintOverlay;
import com.heaper.extra_handicaps.Photophobia;
import net.minecraft.client.Minecraft;

public class PhotophobiaClient extends ConditionRampHandicap {
    public PhotophobiaClient() {
        super("photophobia", 0.04f, 0.05f, 1f, SquintOverlay.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return Photophobia.dazzled(mc.player);
    }
}
