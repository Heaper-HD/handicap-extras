package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.*;

public class Pica extends ExtraHandicap {
    private static final List<Item> CRAVINGS = List.of(Items.DIRT, Items.GRAVEL, Items.SAND, Items.CLAY_BALL, Items.STICK, Items.COBBLESTONE);
    private static final int URGE_ONE_IN = 180;
    private static final int DEADLINE_SECONDS = 30;

    private record Urge(Item item, int secondsLeft) {}

    private final Map<UUID, Urge> urges = new HashMap<>();

    public Pica() {
        super("pica");
    }

    @Override
    public void registerEvents() {
        UseItemCallback.EVENT.register((player, level, hand) -> {
            if (!(player instanceof ServerPlayer p)) {
                return InteractionResult.PASS;
            }
            Urge urge = urges.get(p.getUUID());
            ItemStack stack = p.getItemInHand(hand);
            if (urge == null || !stack.is(urge.item())) {
                return InteractionResult.PASS;
            }
            urges.remove(p.getUUID());
            if (!p.isCreative()) {
                stack.shrink(1);
            }
            level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1f, 0.7f);
            p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 100, 0));
            overlay(p, "...that hit the spot. Gross.");
            return InteractionResult.SUCCESS;
        });
    }

    @Override
    public void onTick(ServerPlayer p) {
        Urge urge = urges.get(p.getUUID());
        if (urge == null) {
            if (p.getRandom().nextInt(URGE_ONE_IN) == 0) {
                Item craving = CRAVINGS.get(p.getRandom().nextInt(CRAVINGS.size()));
                urges.put(p.getUUID(), new Urge(craving, DEADLINE_SECONDS));
                overlay(p, "You really want to eat some " + craving.getDefaultInstance().getHoverName().getString().toLowerCase(Locale.ROOT) + "...");
            }
            return;
        }

        if (urge.secondsLeft() <= 0) {
            urges.remove(p.getUUID());
            p.addEffect(new MobEffectInstance(MobEffects.HUNGER, 600, 2));
            overlay(p, "The craving gnaws at your stomach...");
        } else {
            urges.put(p.getUUID(), new Urge(urge.item, urge.secondsLeft() - 1));
            if (urge.secondsLeft() % 10 == 0) {
                overlay(p, "Still craving " + urge.item().getDefaultInstance().getHoverName().getString().toLowerCase(Locale.ROOT) + "...");
            }
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        urges.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        urges.remove(player);
    }
}
