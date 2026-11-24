package com.heaper;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class ExtraHandicapManager {
    private static LifecycleTracker<UUID> TRACKER = new LifecycleTracker<>();
    private static int ticks = 0;

    private ExtraHandicapManager() {}

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if (++ticks < 20) {
                return;
            }
            ticks = 0;

            HandicapState state = HandicapManager.state();
            if (state == null) {
                return;
            }

            for (ServerPlayer player : server.getPlayerList().getPlayers()) {
                tickPlayer(player, state);
            }
        });

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID id = handler.getPlayer().getUUID();
            TRACKER.forget(id);
            for (ExtraHandicap h : ExtraHandicapsRegistry.all()) {
                h.forget(id);
            }
        });

        ServerPlayerEvents.AFTER_RESPAWN.register(((oldPlayer, newPlayer, alive) ->
                TRACKER.forget(newPlayer.getUUID())));

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            TRACKER.clear();
            ticks = 0;
        });
    }

    private static void tickPlayer(ServerPlayer p, HandicapState state) {
        Set<Handicap> current = EnumSet.noneOf(Handicap.class);
        for (Handicap d : state.get(p.getUUID())) {
            if (ExtraHandicapsRegistry.get(d) != null) {
                current.add(d);
            }
        }

        TRACKER.update(p.getUUID(), current,
                d -> ExtraHandicapsRegistry.get(d).onGive(p),
                d -> ExtraHandicapsRegistry.get(d).onRemove(p));

        for (Handicap d : current) {
            ExtraHandicapsRegistry.get(d).onTick(p);
        }
    }

    public static void onConsume(ServerPlayer p, ItemStack item) {
        HandicapState state = HandicapManager.state();
        if (state == null) {
            return;
        }
        for (Handicap d : state.get(p.getUUID())) {
            ExtraHandicap h = ExtraHandicapsRegistry.get(d);
            if (h != null) {
                h.onConsume(p, item);
            }
        }
    }
}
