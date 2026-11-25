package com.heaper.extra_handicaps.allergy;

import net.minecraft.server.level.ServerPlayer;

import java.util.UUID;

public record PlayerAllergen(UUID target, String name) implements Allergen {
    private static final double RANGE = 6.0;

    @Override
    public String key() {
        return "player:" + target;
    }

    @Override
    public boolean isNear(ServerPlayer p) {
        ServerPlayer other = p.level().getServer().getPlayerList().getPlayer(target);
        return other != null && other.level() == p.level()
                && !other.isSpectator() && other.distanceToSqr(p) < RANGE * RANGE;
    }
}
