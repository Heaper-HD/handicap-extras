package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Agoraphobia extends ExtraHandicap {
    private static final int DELAY_SECONDS = 3;

    private final Map<UUID, Integer> exposedSeconds = new HashMap<>();

    public Agoraphobia() {
        super("agoraphobia");
    }

    public static boolean exposed(Player p) {
        return p.level().canSeeSky(BlockPos.containing(p.getEyePosition()));
    }

    @Override
    public void onTick(ServerPlayer p) {
        int seconds = exposed(p) ? exposedSeconds.merge(p.getUUID(), 1, Integer::sum) : 0;
        if (seconds == 0) {
            exposedSeconds.remove(p.getUUID());
        }

        boolean panicking = seconds >= DELAY_SECONDS;
        if (seconds == DELAY_SECONDS) {
            overlay(p, "The open sky makes you panic...");
        }
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.35, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, panicking);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        exposedSeconds.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        exposedSeconds.remove(player);
    }
}
