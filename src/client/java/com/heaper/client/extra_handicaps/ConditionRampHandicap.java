package com.heaper.client.extra_handicaps;

import com.heaper.client.ExtraClientHandicap;
import com.heaper.client.effects.EffectChannel;
import net.minecraft.client.Minecraft;

import java.util.List;

public abstract class ConditionRampHandicap extends ExtraClientHandicap {
    private final float rampUp;
    private final float rampDown;
    private final float maxIntensity;
    private final List<EffectChannel.Binding> effects;
    private float intensity = 0f;

    protected ConditionRampHandicap(String id, float rampUp, float rampDown, float maxIntensity, EffectChannel.Binding... effects) {
        super(id);
        this.rampUp = rampUp;
        this.rampDown = rampDown;
        this.maxIntensity = maxIntensity;
        this.effects = List.of(effects);
    }

    protected abstract boolean condition(Minecraft mc);

    @Override
    public void onTick(Minecraft mc) {
        if (mc.player == null) {
            return;
        }
        intensity = condition(mc)
                ? Math.min(maxIntensity, intensity + rampUp)
                : Math.max(0f, intensity - rampDown);
        for (EffectChannel.Binding effect : effects) {
            effect.set(id(), intensity);
        }
    }

    @Override
    public void onRemove(Minecraft mc) {
        intensity = 0f;
        for (EffectChannel.Binding effect : effects) {
            effect.clear(id());
        }
    }

    protected float intensity() {
        return intensity;
    }
}
