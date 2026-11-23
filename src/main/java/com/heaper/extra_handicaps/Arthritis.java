package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.HandicapCheck;
import net.fabricmc.fabric.api.event.player.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class Arthritis extends ExtraHandicap {
    public Arthritis() {
        super("arthritis");
    }

    public static boolean locked(Player p) {
        return !p.isCreative() && !p.isSpectator()
                && p.getHealth() < p.getMaxHealth() - 0.01f;
    }

    private boolean blocked(Player p) {
        return HandicapCheck.has(p, handicap()) && locked(p);
    }

    private static boolean consumable(ItemStack stack) {
        return stack.has(DataComponents.CONSUMABLE);
    }

    private static InteractionResult deny(Player p) {
        if (p.level().isClientSide()) {
            p.sendOverlayMessage(Component.literal("Your joints hurt too much..."));
        }
        return InteractionResult.FAIL;
    }

    @Override
    public void registerEvents() {
        AttackBlockCallback.EVENT.register((p, level, hand, pos, dir) ->
                blocked(p) ? deny(p) : InteractionResult.PASS);

        PlayerBlockBreakEvents.BEFORE.register(((level, p, pos, state, be) -> !blocked(p)));

        UseBlockCallback.EVENT.register((p, level, hand, hit) -> {
            if (!blocked(p)) {
                return InteractionResult.PASS;
            }

            if (consumable(p.getItemInHand(hand))
                    && level.getBlockState(hit.getBlockPos()).getMenuProvider(level, hit.getBlockPos()) == null) {
                return InteractionResult.PASS;
            }
            return deny(p);
        });

        UseEntityCallback.EVENT.register((p, level, hand, entity, hit) ->
                blocked(p) ? deny(p) : InteractionResult.PASS);

        UseItemCallback.EVENT.register((p, level, hand) -> {
            if (!blocked(p) || consumable(p.getItemInHand(hand))) {
                return InteractionResult.PASS;
            }
            return deny(p);
        });
    }

    @Override
    public void onTick(ServerPlayer p) {
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.6, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, locked(p));
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
    }
}
