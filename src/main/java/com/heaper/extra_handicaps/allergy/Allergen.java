package com.heaper.extra_handicaps.allergy;

import net.minecraft.server.level.ServerPlayer;

public interface Allergen {
    String name();

    String key();

    boolean isNear(ServerPlayer p);
}
