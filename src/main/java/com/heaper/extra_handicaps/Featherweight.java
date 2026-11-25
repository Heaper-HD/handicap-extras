package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Featherweight extends ExtraHandicap {
    public Featherweight() {
        super("featherweight");
    }

    @Override
    public void onTick(ServerPlayer p) {
        setModifier(p, Attributes.GRAVITY, "gravity", -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, true);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.GRAVITY, "gravity", -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
    }
}
