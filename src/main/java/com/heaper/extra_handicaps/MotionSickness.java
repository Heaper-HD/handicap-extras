package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class MotionSickness extends ExtraHandicap {
    public MotionSickness() {
        super("motion_sickness");
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean riding = p.isPassenger();
        if (riding) {
            p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 140, 0, false, false, false));
        }
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, riding);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
    }
}
