package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Gigantism extends ExtraHandicap {
    public Gigantism() {
        super("gigantism");
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
        setModifier(p, Attributes.SCALE, "scale", 0.5, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, active);
        setModifier(p, Attributes.BLOCK_INTERACTION_RANGE, "block_reach", 1.0, AttributeModifier.Operation.ADD_VALUE, active);
        setModifier(p, Attributes.ENTITY_INTERACTION_RANGE, "entity_reach", 0.75, AttributeModifier.Operation.ADD_VALUE, active);
        setModifier(p, Attributes.STEP_HEIGHT, "step", 0.5, AttributeModifier.Operation.ADD_VALUE, active);
    }
}
