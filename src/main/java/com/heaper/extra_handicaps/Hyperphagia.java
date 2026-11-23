package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;

public class Hyperphagia extends ExtraHandicap {
    private static final float EXHAUSTION_PER_SECOND = 0.25f;

    public Hyperphagia() {
        super("hyperphagia");
    }

    @Override
    public void onTick(ServerPlayer p) {
        p.causeFoodExhaustion(EXHAUSTION_PER_SECOND);
    }
}
