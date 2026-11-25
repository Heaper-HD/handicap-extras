package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class BadBack extends ExtraHandicap {
    private static final double FREE_LOAD = 0.25;
    private static final double MAX_SLOW = 0.5;
    private static final double HEAVY = 0.75;

    private final Set<UUID> struggling = new HashSet<>();

    public BadBack() {
        super("bad_back");
    }

    private static double load(ServerPlayer p) {
        double total = 0;
        for (int i = 0; i < 36; i++) {
            ItemStack stack = p.getInventory().getItem(i);
            if (!stack.isEmpty()) {
                total += (double) stack.getCount() / stack.getMaxStackSize();
            }
        }
        return total / 36.0;
    }

    @Override
    public void onTick(ServerPlayer p) {
        double load = load(p);
        double over = Math.max(0, (load - FREE_LOAD) / (1 - FREE_LOAD));
        double slow = Math.round(-MAX_SLOW * over * 20) / 20.0;

        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", slow, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, slow < 0);
        setModifier(p, Attributes.JUMP_STRENGTH, "jump", -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, load > HEAVY);

        if (load > HEAVY && struggling.add(p.getUUID())) {
            overlay(p, "Your back can't take all this weight...");
        } else if (load <= HEAVY) {
            struggling.remove(p.getUUID());
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.JUMP_STRENGTH, "jump", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        struggling.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        struggling.remove(player);
    }
}
