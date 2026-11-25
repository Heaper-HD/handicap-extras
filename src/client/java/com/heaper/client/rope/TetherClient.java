package com.heaper.client.rope;

import com.heaper.network.ImpulsePayload;
import com.heaper.network.StrainPayload;
import com.heaper.network.TetherPayload;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class TetherClient {
    private static final int STALE_TICKS = 5;
    private static final double CORRECTION = 0.3;
    private static final double MAX_CORRECTION = 0.5;
    private static final double MIN_STRAIN = 0.005;
    private static final double TOLERANCE = 0.25;

    private static Vec3 pivot = null;
    private static double allowed = 0;
    private static int ticksSinceUpdate = 0;

    private TetherClient() {}

    public static void register() {
        ClientPlayNetworking.registerGlobalReceiver(ImpulsePayload.TYPE, (payload, context) ->
                context.player().setDeltaMovement(context.player().getDeltaMovement().add(payload.x(), payload.y(), payload.z())));

        ClientPlayNetworking.registerGlobalReceiver(TetherPayload.TYPE, (payload, context) -> {
           pivot = new Vec3(payload.px(), payload.py(), payload.pz());
           allowed = payload.allowed();
           ticksSinceUpdate = 0;
        });

        ClientTickEvents.END_CLIENT_TICK.register(mc -> {
            if (pivot == null || ++ticksSinceUpdate > STALE_TICKS) {
                pivot = null;
                return;
            }
            LocalPlayer p = mc.player;
            if (p == null || p.isPassenger()) {
                return;
            }

            Vec3 anchor = p.position().add(0, p.getBbHeight() * 0.5, 0);
            Vec3 offset = anchor.subtract(pivot);
            double dist = offset.length();
            if (dist <= allowed || dist < 1e-6) {
                return;
            }

            Vec3 outward = offset.scale(1 / dist);
            Vec3 v = p.getDeltaMovement();

            double out = v.dot(outward);
            Vec3 removed = out > 0 ? outward.scale(out) : Vec3.ZERO;

            double overshoot = dist - allowed - TOLERANCE;
            Vec3 correction = overshoot > 0 ? outward.scale(Math.min(overshoot * CORRECTION, MAX_CORRECTION)) : Vec3.ZERO;

            p.setDeltaMovement(v.subtract(removed).subtract(correction));

            if (removed.lengthSqr() > MIN_STRAIN * MIN_STRAIN) {
                ClientPlayNetworking.send(new StrainPayload((float) removed.x, (float) removed.y, (float) removed.z));
            }
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, mc) -> mc.execute(() -> pivot = null));
    }
}
