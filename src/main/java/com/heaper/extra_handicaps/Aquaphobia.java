package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Aquaphobia extends ExtraHandicap {
    public Aquaphobia() {
        super("aquaphobia");
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean wet = p.isInWater();
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, wet);
        setModifier(p, Attributes.SUBMERGED_MINING_SPEED, "mining", -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, wet);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.SUBMERGED_MINING_SPEED, "mining", -0.5, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
    }
}
