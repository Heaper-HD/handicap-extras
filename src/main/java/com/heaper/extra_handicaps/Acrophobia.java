package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Acrophobia extends ExtraHandicap {
    private static final int RADIUS = 2;
    private static final int DEPTH = 5;

    public Acrophobia() {
        super("acrophobia");
    }

    public static boolean nearDrop(Player p) {
        Level level = p.level();
        BlockPos feet = p.blockPosition();
        for (int dx = -RADIUS; dx <= RADIUS; dx++) {
            for (int dz = -RADIUS; dz <= RADIUS; dz++) {
                if (isDrop(level, feet.offset(dx, -1, dz))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isDrop(Level level, BlockPos top) {
        for (int i = 0; i < DEPTH; i++) {
            BlockPos pos = top.below(i);
            if (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void onTick(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, nearDrop(p));
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
    }
}
