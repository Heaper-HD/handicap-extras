package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Pyrophobia extends ExtraHandicap {
    private static final int RADIUS = 4;
    private static final int HEIGHT = 2;

    private final Set<UUID> panicking = new HashSet<>();

    public Pyrophobia() {
        super("pyrophobia");
    }

    public static boolean nearFire(Player p) {
        if (p.isOnFire()) {
            return true;
        }
        Level level = p.level();
        BlockPos center = p.blockPosition();
        for (BlockPos pos : BlockPos.betweenClosed(center.offset(-RADIUS, -HEIGHT, -RADIUS), center.offset(RADIUS, HEIGHT, RADIUS))) {
            if (isFire(level.getBlockState(pos))) {
                return true;
            }
        }
        return false;
    }

    private static boolean isFire(BlockState state) {
        if (state.is(BlockTags.FIRE) || state.getFluidState().is(FluidTags.LAVA)) {
            return true;
        }
        if (state.is(BlockTags.CAMPFIRES)) {
            return state.getValue(CampfireBlock.LIT);
        }
        if (state.getBlock() instanceof AbstractFurnaceBlock) {
            return state.getValue(AbstractFurnaceBlock.LIT);
        }
        return false;
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean afraid = nearFire(p);
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, afraid);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, afraid);
        setModifier(p, Attributes.BLOCK_BREAK_SPEED, "mining", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, afraid);

        if (afraid && panicking.add(p.getUUID())) {
            overlay(p, "Fire! You panic...");
        } else if (!afraid) {
            panicking.remove(p.getUUID());
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.BLOCK_BREAK_SPEED, "mining", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        panicking.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        panicking.remove(player);
    }
}
