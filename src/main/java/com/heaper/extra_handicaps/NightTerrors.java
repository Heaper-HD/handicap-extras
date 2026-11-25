package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NightTerrors extends ExtraHandicap {
    private static final int ONE_IN = 3;
    private static final int GRACE_SECONDS = 1;

    private final Map<UUID, Integer> sleptSeconds = new HashMap<>();

    public NightTerrors() {
        super("night_terrors");
    }

    @Override
    public void onTick(ServerPlayer p) {
        if (!p.isSleeping()) {
            sleptSeconds.remove(p.getUUID());
            return;
        }

        int seconds = sleptSeconds.merge(p.getUUID(), 1, Integer::sum);
        if (seconds > GRACE_SECONDS && p.getRandom().nextInt(ONE_IN) == 0) {
            nightmare(p);
        }
    }

    private void nightmare(ServerPlayer p) {
        sleptSeconds.remove(p.getUUID());
        p.stopSleepInBed(true, true);
        p.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 120, 0, false, false,false));
        p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 120, 0, false, false, false));
        p.level().playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.PHANTOM_AMBIENT, SoundSource.PLAYERS, 1.2f, 0.6f);
        overlay(p, "You wake up screaming from a nightmare!");
    }

    @Override
    public void onRemove(ServerPlayer p) {
        sleptSeconds.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        sleptSeconds.remove(player);
    }
}
