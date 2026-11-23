package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Nyctophobia extends ExtraHandicap {
    private static final int DARK_LEVEL = 5;
    private static final int DELAY_SECONDS = 2;

    private final Map<UUID, Integer> darkSeconds = new HashMap<>();

    public Nyctophobia() {
        super("nyctophobia");
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean dark = p.level().getMaxLocalRawBrightness(p.blockPosition()) < DARK_LEVEL
                && !isLight(p.getMainHandItem()) && !isLight(p.getOffhandItem());

        int seconds = dark ? darkSeconds.merge(p.getUUID(), 1, Integer::sum) : 0;
        if (seconds == 0) {
            darkSeconds.remove(p.getUUID());
        }

        boolean afraid = seconds >= DELAY_SECONDS;
        if (seconds == DELAY_SECONDS) {
            overlay(p, "The darkness terrifies you...");
        }
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, afraid);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, afraid);
    }

    private static boolean isLight(ItemStack stack) {
        return stack.getItem() instanceof BlockItem block
                && block.getBlock().defaultBlockState().getLightEmission() > 0;
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        darkSeconds.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        darkSeconds.remove(player);
    }
}
