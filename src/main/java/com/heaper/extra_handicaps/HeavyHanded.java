package com.heaper.extra_handicaps;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;

public class HeavyHanded extends ExtraHandicap {
    private static Handicap handicap;

    public HeavyHanded() {
        super("heavy_handed");
        handicap = handicap();
    }

    public static boolean applies(ServerPlayer p) {
        HandicapState state = HandicapManager.state();
        return handicap != null && state != null && state.has(p.getUUID(), handicap);
    }
}
