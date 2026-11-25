package com.heaper.extra_handicaps.allergy;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Predicate;

public record BlockAllergen(String key, String name, Predicate<BlockState> matches) implements Allergen {
    private static final int RADIUS = 3;
    private static final int HEIGHT = 2;

    @Override
    public boolean isNear(ServerPlayer p) {
        Level level = p.level();
        BlockPos center = p.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -HEIGHT, -RADIUS), center.offset(RADIUS, HEIGHT, RADIUS))) {
            if (matches.test(level.getBlockState(pos))) {
                return true;
            }
        }
        return false;
    }
}
