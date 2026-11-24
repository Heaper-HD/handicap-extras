package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PickyEater extends ExtraHandicap {
    private final Map<UUID, Item> lastFood = new HashMap<>();

    public PickyEater() {
        super("picky_eater");
    }

    @Override
    public void onConsume(ServerPlayer p, ItemStack item) {
        FoodProperties food = item.get(DataComponents.FOOD);
        if (food == null) {
            return;
        }

        Item previous = lastFood.put(p.getUUID(), item.getItem());
        if (previous != item.getItem()) {
            return;
        }

        FoodData data = p.getFoodData();
        data.setFoodLevel(Math.max(0, data.getFoodLevel() - food.nutrition() / 2));
        data.setSaturation(Math.max(0f, data.getSaturationLevel() - food.saturation() / 2f));
        p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 200, 0));
        overlay(p, "Ugh, not this again...");
    }

    @Override
    public void onRemove(ServerPlayer p) {
        lastFood.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        lastFood.remove(player);
    }
}
