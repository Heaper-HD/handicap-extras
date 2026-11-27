package com.heaper.client.extra_handicaps;

import com.heaper.client.effects.DeepVignette;
import com.heaper.client.effects.SilhouetteOverlay;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.vehicle.boat.AbstractBoat;

public class ThalassophobiaClient extends ConditionRampHandicap {
    private static final int SAFE_DEPTH = 6;
    private static final int MAX_DEPTH = 24;
    private static final float BOAT_FACTOR = 0.5f;
    private static final double MAX_DRAG = 0.15;

    private int heartbeat = 0;

    public ThalassophobiaClient() {
        super("thalassophobia", 0.02f, 0.04f, 1f,
                DeepVignette.CHANNEL.bind(),
                SilhouetteOverlay.CHANNEL.bind());
    }

    private static boolean inBoat(LocalPlayer p) {
        return p.getVehicle() instanceof AbstractBoat;
    }

    private static int waterBelow(LocalPlayer p) {
        BlockPos.MutableBlockPos pos = p.blockPosition().mutable();
        if (!p.level().getFluidState(pos).is(FluidTags.WATER)) {
            pos.move(Direction.DOWN);
        }
        int depth = 0;
        while (depth < MAX_DEPTH && p.level().getFluidState(pos).is(FluidTags.WATER)) {
            depth++;
            pos.move(Direction.DOWN);
        }
        return depth;
    }

    @Override
    protected boolean condition(Minecraft mc) {
        return target(mc) > 0f;
    }

    @Override
    protected float target(Minecraft mc) {
        LocalPlayer p = mc.player;
        boolean boat = inBoat(p);
        if (!p.isInWater() && !boat) {
            return 0f;
        }
        float depth = (waterBelow(p) - SAFE_DEPTH) / (float) (MAX_DEPTH - SAFE_DEPTH);
        return Mth.clamp(depth, 0f, 1f) * (boat ? BOAT_FACTOR : 1f);
    }

    @Override
    public void onTick(Minecraft mc) {
        super.onTick(mc);
        LocalPlayer p = mc.player;
        if (p == null) {
            return;
        }
        float i = intensity();

        if (p.isInWater() && i > 0f) {
            double drag = 1.0 - MAX_DRAG * i;
            p.setDeltaMovement(p.getDeltaMovement().multiply(drag, 1.0, drag));
        }

        if (i > 0.05f) {
            int interval = Math.round(Mth.lerp(i, 30f, 12f));
            if (++heartbeat >= interval) {
                heartbeat = 0;
                mc.level.playLocalSound(p.getX(), p.getY(), p.getZ(), SoundEvents.WARDEN_HEARTBEAT,
                        SoundSource.AMBIENT, 0.4f + 0.6f * i, 1f, false);
            }
        } else {
            heartbeat = 0;
        }

        SilhouetteOverlay.tick(i);
    }

    @Override
    public void onRemove(Minecraft mc) {
        super.onRemove(mc);
        heartbeat = 0;
        SilhouetteOverlay.clear();
    }
}
