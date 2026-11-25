package com.heaper.extra_handicaps;

import com.handicap.Handicap;
import com.handicap.HandicapMod;
import com.heaper.ExtraHandicap;
import com.heaper.mixin.HandicapManagerAccessor;
import com.heaper.mixin.NarcolepsyRuntimeAccessor;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;

public class Narcolepsy extends ExtraHandicap {
    public Narcolepsy() {
        super("narcolepsy");
    }

    @Override
    public void registerEvents() {
        AttackEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            if (level.isClientSide()
                    || !(player instanceof ServerPlayer slapper)
                    || !(entity instanceof ServerPlayer sleeper)
                    || !has(sleeper)) {
                return InteractionResult.PASS;
            }

            NarcolepsyRuntimeAccessor rt = runtime(sleeper);
            if (rt == null || rt.handicap_extras$getNarcoSeconds() <= 0) {
                return InteractionResult.PASS;
            }

            wake(sleeper);
            level.playSound(null, sleeper.getX(), sleeper.getY(), sleeper.getZ(),
                    SoundEvents.PLAYER_ATTACK_NODAMAGE, SoundSource.PLAYERS, 1f, 1.2f);
            overlay(sleeper, slapper.getName().getString() + " slapped you awake!");
            overlay(slapper, "You woke " + sleeper.getName().getString() + " up!");
            return InteractionResult.SUCCESS;
        });
    }

    private static NarcolepsyRuntimeAccessor runtime(ServerPlayer p) {
        Object rt = HandicapManagerAccessor.handicap_extras$runtime().get(p.getUUID());
        return rt instanceof NarcolepsyRuntimeAccessor accessor ? accessor : null;
    }

    private void wake(ServerPlayer p) {
        NarcolepsyRuntimeAccessor rt = runtime(p);
        if (rt != null) {
            rt.handicap_extras$setNarcoSeconds(0);
        }

        AttributeInstance speed = p.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speed != null) {
            speed.removeModifier(HandicapMod.id("narcolepsy_move"));
        }
        AttributeInstance jump = p.getAttribute(Attributes.JUMP_STRENGTH);
        if(jump != null) {
            jump.removeModifier(HandicapMod.id("narcolepsy_jump"));
        }

        if (!has(p, Handicap.BLIND)) {
            p.removeEffect(MobEffects.BLINDNESS);
        }
    }
}
