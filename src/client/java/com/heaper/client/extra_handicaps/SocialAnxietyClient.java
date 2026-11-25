package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.CameraShake;
import com.heaper.extra_handicaps.SocialAnxiety;
import net.minecraft.client.Minecraft;

public class SocialAnxietyClient extends ConditionRampHandicap {
    public SocialAnxietyClient() {
        super("social_anxiety", 0.02f, 0.05f, 0.9f, CameraShake.CHANNEL.bind());
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return SocialAnxiety.crowded(mc.player);
    }
}
