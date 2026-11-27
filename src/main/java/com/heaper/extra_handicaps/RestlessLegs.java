package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RestlessLegs extends ExtraHandicap {
    private static final int MIN_STILL = 4;
    private static final int MAX_STILL = 7;
    private static final double MOVE_EPSILON = 0.01;

    private static final double STAND_PUSH = 0.35;
    private static final double STAND_HOP = 0.6;
    private static final double SNEAK_PUSH = 0.15;
    private static final double SNEAK_HOP = 0.25;

    private final Map<UUID, Legs> legs = new HashMap<>();

    public RestlessLegs() {
        super("restless_legs");
    }

    private static final class Legs {
        double lastX, lastZ;
        int still;
        int threshold;
    }

    private static int rollThreshold(RandomSource random) {
        return MIN_STILL + random.nextInt(MAX_STILL - MIN_STILL + 1);
    }

    private static boolean exempt(ServerPlayer p) {
        return p.isSleeping() || p.isPassenger() || p.isInWater() || p.isFallFlying()
                || p.getAbilities().flying || p.isDeadOrDying() || p.isSpectator();
    }

    @Override
    public void onTick(ServerPlayer p) {
        Legs l = legs.computeIfAbsent(p.getUUID(), id -> {
            Legs fresh = new Legs();
            fresh.lastX = p.getX();
            fresh.lastZ = p.getZ();
            fresh.threshold = rollThreshold(p.getRandom());
            return fresh;
        });

        double dx = p.getX() - l.lastX;
        double dz = p.getZ() - l.lastZ;
        l.lastX = p.getX();
        l.lastZ = p.getZ();

        if (exempt(p) || dx * dx + dz * dz > MOVE_EPSILON * MOVE_EPSILON) {
            l.still = 0;
            return;
        }

        if (++l.still < l.threshold || !p.onGround()) {
            return;
        }

        boolean sneaking = p.isShiftKeyDown();
        double push = sneaking ? SNEAK_PUSH : STAND_PUSH;
        double hop = sneaking ? SNEAK_HOP : STAND_HOP;
        double angle = p.getRandom().nextDouble() * Math.PI * 2;

        impulse(p, Math.cos(angle) * push, hop, Math.sin(angle) * push);

        l.still = 0;
        l.threshold = rollThreshold(p.getRandom());
    }

    @Override
    public void onRemove(ServerPlayer p) {
        legs.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        legs.remove(player);
    }
}
