package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class SympatheticPain extends ExtraHandicap {
    private static final double RANGE = 12.0;
    private static final float SHARE = 0.2f;

    private boolean applying = false;

    public SympatheticPain() {
        super("sympathetic_pain");
    }

    @Override
    public void registerEvents() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register((entity, source, baseDamage, damageTaken, blocked) -> {
            if (applying || blocked || damageTaken <= 0f || !(entity instanceof ServerPlayer victim)) {
                return;
            }
            applying = true;
            try {
                for (Player nearby : Nearby.players(victim, RANGE)) {
                    if (nearby instanceof ServerPlayer witness && has(witness) && witness.hasLineOfSight(victim)) {
                        witness.hurtServer((ServerLevel) witness.level(), witness.damageSources().magic(),
                                Math.max(0.5f, damageTaken * SHARE));
                        overlay(witness, "You feel " + victim.getName().getString() + "'s pain...");
                    }
                }
            } finally {
                applying = false;
            }
        });
    }
}
