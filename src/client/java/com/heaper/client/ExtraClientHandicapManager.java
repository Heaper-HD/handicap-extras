package com.heaper.client;

import com.handicap.Handicap;
import com.handicap.client.ClientState;
import com.heaper.LifecycleTracker;
import com.heaper.client.effects.CameraShake;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.EnumSet;
import java.util.Set;

public class ExtraClientHandicapManager {
    private static final Object LOCAL = new Object();
    private static final LifecycleTracker<Object> TRACKER = new LifecycleTracker<>();

    private ExtraClientHandicapManager() {}

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (mc.player == null) {
                return;
            }

            Set<Handicap> current = EnumSet.noneOf(Handicap.class);
            for (ExtraClientHandicap h : ExtraClientHandicapsRegistry.all()) {
                if (ClientState.has(h.handicap())) {
                    current.add(h.handicap());
                }
            }

            TRACKER.update(LOCAL, current,
                    d -> ExtraClientHandicapsRegistry.get(d).onGive(mc),
                    d -> ExtraClientHandicapsRegistry.get(d).onRemove(mc));

            for (Handicap d : current) {
                ExtraClientHandicapsRegistry.get(d).onTick(mc);
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> mc.execute(() -> {
            for (Handicap d : TRACKER.forget(LOCAL)) {
                ExtraClientHandicapsRegistry.get(d).onRemove(mc);
            }

            CameraShake.reset();
        }));
    }
}
