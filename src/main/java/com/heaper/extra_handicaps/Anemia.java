package com.heaper.extra_handicaps;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.heaper.ExtraHandicap;
import net.minecraft.world.entity.player.Player;

public class Anemia extends ExtraHandicap {
    private static final float REGEN_FACTOR = 0.25f;
    private static Handicap handicap;

    public Anemia() {
        super("anemia");
        handicap = handicap();
    }

    public static float scale(Player p, float amount) {
        HandicapState state = HandicapManager.state();
        if (handicap != null && state != null && state.has(p.getUUID(), handicap)) {
            return amount * REGEN_FACTOR;
        }
        return amount;
    }
}
