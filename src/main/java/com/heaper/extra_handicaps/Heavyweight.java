package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Heavyweight extends ExtraHandicap {
    public Heavyweight() {
        super("heavyweight");
    }

    @Override
    public void onTick(ServerPlayer p) {
        apply(p, true);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        apply(p, false);
    }

    private void apply(ServerPlayer p, boolean active) {
        setModifier(p, Attributes.GRAVITY, "gravity", 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
        setModifier(p, Attributes.JUMP_STRENGTH, "jump", -0.2, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
        setModifier(p, Attributes.FALL_DAMAGE_MULTIPLIER, "fall", 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, active);
    }
}
