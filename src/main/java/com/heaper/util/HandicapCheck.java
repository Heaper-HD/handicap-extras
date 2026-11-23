package com.heaper.util;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.handicap.client.ClientState;
import net.minecraft.world.entity.player.Player;

public class HandicapCheck {
    private HandicapCheck() {
    }

    public static boolean has(Player p, Handicap h) {
        if (p.level().isClientSide()) {
            return ClientState.has(h);
        }
        HandicapState state = HandicapManager.state();
        return state != null && state.has(p.getUUID(), h);
    }
}
