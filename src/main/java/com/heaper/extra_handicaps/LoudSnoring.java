package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class LoudSnoring extends ExtraHandicap {
    private static final double RANGE = 20.0;

    public LoudSnoring() {
        super("loud_snoring");
    }

    @Override
    public void onTick(ServerPlayer p) {
        if (!p.isSleeping()) {
            return;
        }

        p.level().playSound(null, p.blockPosition(), SoundEvents.PIG_AMBIENT_BABY.value(), SoundSource.PLAYERS, 1.1f, 0.3f);

        for (Player nearby : Nearby.players(p, RANGE)) {
            if (nearby instanceof ServerPlayer other && other.isSleeping()) {
                other.stopSleepInBed(true, true);
                overlay(other, p.getName().getString() + "'s snoring woke you up!");
            }
        }
    }
}
