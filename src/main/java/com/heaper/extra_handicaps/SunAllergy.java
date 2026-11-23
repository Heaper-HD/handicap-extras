package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SunAllergy extends ExtraHandicap {
    private final Set<UUID> burning = new HashSet<>();

    public SunAllergy() {
        super("sun_allergy");
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean burn = !p.isCreative() && !p.isSpectator()
                && p.level().isBrightOutside()
                && p.level().canSeeSky(BlockPos.containing(p.getEyePosition()))
                && !p.isInWaterOrRain()
                && p.getItemBySlot(EquipmentSlot.HEAD).isEmpty();

        if (burn) {
            p.igniteForSeconds(3);
            if (burning.add(p.getUUID())) {
                overlay(p, "The sun burn your skin!");
            }
        } else {
            burning.remove(p.getUUID());
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        burning.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        burning.remove(player);
    }
}
