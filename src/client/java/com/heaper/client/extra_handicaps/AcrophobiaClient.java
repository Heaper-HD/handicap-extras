package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.CameraShake;
import com.heaper.extra_handicaps.Acrophobia;
import net.minecraft.client.Minecraft;

public class AcrophobiaClient extends ConditionRampHandicap {
    public AcrophobiaClient() {
        super("acrophobia", 0.04f, 0.06f, 0.8f, CameraShake.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return Acrophobia.nearDrop(mc.player);
    }
}
