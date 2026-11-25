package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.level.ServerPlayer;

public class Amnesia extends ExtraHandicap {
    public Amnesia() {
        super("amnesia");
    }

    @Override
    public void registerEvents() {
        ServerLivingEntityEvents.AFTER_DEATH.register((entity, source) -> {
            if (entity instanceof ServerPlayer p && has(p)) {
                p.setRespawnPosition(null, false);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!alive && has(newPlayer)) {
                overlay(newPlayer, "Where... were do you live again?");
            }
        });
    }
}
