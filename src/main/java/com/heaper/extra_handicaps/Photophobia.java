package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Photophobia extends ExtraHandicap {
    private static final int BRIGHT_LEVEL = 13;

    private final Set<UUID> squinting = new HashSet<>();

    public Photophobia() {
        super("photophobia");
    }

    public static boolean dazzled(Player p) {
        return p.getItemBySlot(EquipmentSlot.HEAD).isEmpty()
                && p.level().getMaxLocalRawBrightness(BlockPos.containing(p.getEyePosition())) >= BRIGHT_LEVEL;
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean dazzled = dazzled(p);
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, dazzled);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, dazzled);

        if (dazzled && squinting.add(p.getUUID())) {
            overlay(p, "The light hurts your eyes...");
        } else if (!dazzled) {
            squinting.remove(p.getUUID());
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        squinting.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        squinting.remove(player);
    }
}
