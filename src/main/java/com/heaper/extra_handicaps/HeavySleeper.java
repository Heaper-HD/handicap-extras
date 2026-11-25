package com.heaper.extra_handicaps;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class HeavySleeper extends ExtraHandicap {
    private static final long WAKE_DELAY_TICKS = 200;

    private static HeavySleeper instance;
    private final Map<UUID, Long> wakeAt = new HashMap<>();
    private boolean allowing = false;

    public HeavySleeper() {
        super("heavy_sleeper");
        instance = this;
    }

    public static boolean blockWake(ServerPlayer p) {
        if (instance == null || instance.allowing || !p.isAlive() || p.isRemoved()) {
            return false;
        }
        HandicapState state = HandicapManager.state();
        Handicap h = instance.handicap();
        if (state == null || !state.has(p.getUUID(), h) || !p.isSleeping()) {
            return false;
        }
        if (instance.wakeAt.putIfAbsent(p.getUUID(), p.level().getGameTime() + WAKE_DELAY_TICKS) == null) {
            instance.overlay(p, "You're too sleepy to get up...");
        }
        return true;
    }

    @Override
    public void onTick(ServerPlayer p) {
        if (!p.isSleeping()) {
            wakeAt.remove(p.getUUID());
            return;
        }
        Long at = wakeAt.get(p.getUUID());
        if (at != null && p.level().getGameTime() >= at) {
            wakeAt.remove(p.getUUID());
            allowing = true;
            try {
                p.stopSleepInBed(true, true);
            } finally {
                allowing = false;
            }
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        wakeAt.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        wakeAt.remove(player);
    }
}
