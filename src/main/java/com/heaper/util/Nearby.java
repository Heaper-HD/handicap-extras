package com.heaper.util;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.List;
import java.util.function.Predicate;

public class Nearby {
    private Nearby() {}

    public static <T extends Entity> List<T> entities(Entity center, double range, Class<T> type, Predicate<? super T> filter) {
        double rangeSq = range * range;
        return center.level().getEntitiesOfClass(type, center.getBoundingBox().inflate(range),
                e -> e != center && e.isAlive() && e.distanceToSqr(center) <= rangeSq && filter.test(e));
    }

    public static <T extends Entity> boolean any(Entity center, double range, Class<T> type, Predicate<? super T> filter) {
        return !entities(center, range, type, filter).isEmpty();
    }

    public static List<Player> players(Entity center, double range) {
        return entities(center, range, Player.class, p -> !p.isSpectator());
    }
}
