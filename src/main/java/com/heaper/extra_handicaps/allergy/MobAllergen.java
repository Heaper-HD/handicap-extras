package com.heaper.extra_handicaps.allergy;

import com.heaper.util.Nearby;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;

import java.util.Set;

public record MobAllergen(String key, String name, Set<EntityType<?>> types) implements Allergen {
    private static final double RANGE = 6.0;

    @Override
    public boolean isNear(ServerPlayer p) {
        return Nearby.any(p, RANGE, Entity.class, e -> types.contains(e.getType()));
    }
}
