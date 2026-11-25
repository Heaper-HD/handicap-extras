package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.HandicapExtras;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Hemophilia extends ExtraHandicap {
    private static final int BLEED_SECONDS = 8;
    private static final float BLEED_DAMAGE = 1.0f;
    private static final DustParticleOptions BLOOD = new DustParticleOptions(0x8A0303, 1.2f);

    private final Map<UUID, Integer> bleeding = new HashMap<>();

    private boolean applyingBleed = false;

    public Hemophilia() {
        super("hemophilia");
    }

    private InteractionResult tryBandage(ServerPlayer patient, ServerPlayer healer, ItemStack stack) {
        if (!bleeding.containsKey(patient.getUUID()) || (!stack.is(Items.PAPER) && !stack.is(ItemTags.WOOL))) {
            return InteractionResult.PASS;
        }
        bleeding.remove(patient.getUUID());
        if (!healer.isCreative()) {
            stack.shrink(1);
        }
        patient.level().playSound(null, patient.blockPosition(), SoundEvents.WOOL_PLACE, SoundSource.PLAYERS, 1f, 1.2f);
        if (healer == patient) {
            overlay(patient, "You bandage the wound.");
        } else {
            overlay(patient, healer.getName().getString() + " bandaged your wound.");
            overlay(healer, "You bandage " + patient.getName().getString() + "'s wound.");
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    public void registerEvents() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (applyingBleed || blocked || damageTaken <= 0f) {
                return;
            }
            if (entity instanceof ServerPlayer p && has(p)) {
                if (bleeding.put(p.getUUID(), BLEED_SECONDS) == null) {
                    overlay(p, "You're bleeding! User paper or wool to bandage yourself.");
                }
            }
        });

        UseItemCallback.EVENT.register(((player, level, hand) ->
                player instanceof ServerPlayer p ? tryBandage(p, p, p.getItemInHand(hand)) : InteractionResult.PASS));

        UseEntityCallback.EVENT.register(((player, level, hand, entity, hit) -> {
            if (player instanceof ServerPlayer healer && entity instanceof ServerPlayer patient) {
                return tryBandage(patient, healer, healer.getItemInHand(hand));
            }
            return InteractionResult.PASS;
        }));
    }

    @Override
    public void onGive(ServerPlayer p) {
        bleeding.remove(p.getUUID());
    }

    @Override
    public void onTick(ServerPlayer p) {
        Integer left = bleeding.get(p.getUUID());
        if (left == null) {
            return;
        }

        ServerLevel level = (ServerLevel) p.level();
        applyingBleed = true;
        try {
            p.hurtServer(level, p.damageSources().generic(), BLEED_DAMAGE);
        } finally {
            applyingBleed = false;
        }
        level.sendParticles(BLOOD, p.getX(), p.getY() + 0.6, p.getZ(), 8, 0.25, 0.3, 0.25, 0.0);

        if (left <= 1) {
            bleeding.remove(p.getUUID());
            overlay(p, "The bleeding stops.");
        } else {
            bleeding.put(p.getUUID(), left - 1);
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        bleeding.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        bleeding.remove(player);
    }
}
