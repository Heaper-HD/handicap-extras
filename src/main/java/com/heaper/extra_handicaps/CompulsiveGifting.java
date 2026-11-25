package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Prediction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CompulsiveGifting extends ExtraHandicap {
    private static final double RANGE = 16.0;
    private static final int ONE_IN = 30;

    public CompulsiveGifting() {
        super("compulsive_gifting");
    }

    @Override
    public void onTick(ServerPlayer p) {
        if (p.isSpectator() || p.getRandom().nextInt(ONE_IN) != 0) {
            return;
        }

        List<Player> nearby = Nearby.players(p, RANGE);
        if (nearby.isEmpty()) {
            return;
        }
        Player target = nearby.stream().min(Comparator.comparingDouble(o -> o.distanceToSqr(p))).get();

        Inventory inv = p.getInventory();
        List<Integer> slots = new ArrayList<>();
        for (int i = 0; i < 36; i++) {
            if (!inv.getItem(i).isEmpty()) {
                slots.add(i);
            }
        }
        if (slots.isEmpty()) {
            return;
        }

        ItemStack gift = inv.removeItem(slots.get(p.getRandom().nextInt(slots.size())), 1);
        ItemEntity thrown = p.drop(gift, false, Prediction.PREDICTED);
        if (thrown != null) {
            Vec3 toTarget = target.position().subtract(p.position()).normalize();
            thrown.setDeltaMovement(toTarget.scale(0.4).add(0, 0.25, 0));
        }
        overlay(p, "You feel the urge to give " + target.getName().getString() + " your " + gift.getHoverName().getString() + "!");
    }
}
