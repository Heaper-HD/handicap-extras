package com.heaper.extra_handicaps;

import com.handicap.Handicap;
import com.heaper.ExtraHandicap;
import com.heaper.extra_handicaps.allergy.Allergen;
import com.heaper.extra_handicaps.allergy.AllergenPool;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Allergy extends ExtraHandicap {
    private static final float SNEEZE_CHANCE = 0.20f;
    private static final int POISON_AFTER = 15;

    private final Map<UUID, Allergen> allergens = new HashMap<>();
    private final Map<UUID, Integer> exposure = new HashMap<>();

    public Allergy() {
        super("allergy");
    }

    @Override
    public void onGive(ServerPlayer p) {
        if (!allergens.containsKey(p.getUUID())) {
            assign(p);
        }
    }

    private void assign(ServerPlayer p) {
        Allergen allergen = AllergenPool.pick(p);
        allergens.put(p.getUUID(), allergen);

        boolean hidden = has(p, Handicap.ANOSOGNOSIA);
        Component msg = Component.literal(p.getName().getString() + " is allergic to " + allergen.name() + "!")
                .withStyle(ChatFormatting.YELLOW);
        for (ServerPlayer other : p.level().getServer().getPlayerList().getPlayers()) {
            if (other != p || !hidden) {
                other.sendSystemMessage(msg);
            }
        }
    }

    @Override
    public void onTick(ServerPlayer p) {
        Allergen allergen = allergens.get(p.getUUID());
        if (allergen == null || !allergen.isNear(p)) {
            exposure.remove(p.getUUID());
            return;
        }

        int seconds = exposure.merge(p.getUUID(), 1, Integer::sum);
        if (p.getRandom().nextFloat() < SNEEZE_CHANCE) {
            sneeze(p);
        }
        if (seconds == POISON_AFTER) {
            overlay(p, "Your allergy to " + allergen.name() + " is flaring up!");
        }
        if (seconds >= POISON_AFTER) {
            p.addEffect(new MobEffectInstance(MobEffects.POISON, 60, 0));
        }
    }

    private void sneeze(ServerPlayer p) {
        ServerLevel level = p.level();
        level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.PANDA_SNEEZE, SoundSource.PLAYERS, 1f, 1.2f);
        level.sendParticles(ParticleTypes.SNEEZE, p.getX(), p.getEyeY(), p.getZ(), 6, 0.2, 0.1, 0.2, 0.2);
        p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 1, false, false, false));
        jolt(p, 12f, (p.getRandom().nextFloat() - 0.5f) * 8f);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        allergens.remove(p.getUUID());
        exposure.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        exposure.remove(player);
    }
}
