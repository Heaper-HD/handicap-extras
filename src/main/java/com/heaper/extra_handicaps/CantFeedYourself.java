package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.ExtraHandicapManager;
import com.heaper.util.HandicapCheck;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;

public class CantFeedYourself extends ExtraHandicap {
    public CantFeedYourself() {
        super("cant_feed_yourself");
    }

    private static boolean isFood(ItemStack stack) {
        return stack.has(DataComponents.FOOD);
    }

    @Override
    public void registerEvents() {
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (isFood(player.getItemInHand(hand)) && HandicapCheck.has(player, handicap()) && !player.isCreative()) {
                player.sendOverlayMessage(Component.literal("You can't feed yourself... ask someone for help."));
                return InteractionResult.FAIL;
            }
            return InteractionResult.PASS;
        });

        UseEntityCallback.EVENT.register((player, level, hand, entity, hit) -> {
            ItemStack stack = player.getItemInHand(hand);
            if (!isFood(stack) || !(entity instanceof Player)) {
                return InteractionResult.PASS;
            }
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (!(player instanceof ServerPlayer feeder) || !(entity instanceof ServerPlayer patient) || !has(patient)) {
                return InteractionResult.SUCCESS;
            }

            FoodProperties food = stack.get(DataComponents.FOOD);
            if (!patient.canEat(food.canAlwaysEat())) {
                overlay(feeder, patient.getName().getString() + " isn't hungry.");
                return InteractionResult.FAIL;
            }

            ItemStack bite = stack.copyWithCount(1);
            bite.finishUsingItem(level, patient);
            ExtraHandicapManager.onConsume(patient, bite);
            if (!feeder.isCreative()) {
                stack.shrink(1);
            }
            level.playSound(null, patient.getX(), patient.getY(), patient.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1f, 1f);
            overlay(patient, feeder.getName().getString() + " fed you.");
            overlay(feeder, "You fed " + patient.getName().getString() + ".");
            return InteractionResult.SUCCESS;
        });
    }
}
