package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;

public class Germaphobia extends ExtraHandicap {
    public Germaphobia() {
        super("germaphobia");
    }

    @Override
    public void onPickup(ServerPlayer p, ItemEntity item) {
        Entity owner = item.getOwner();
        if (owner instanceof Player && owner != p && !p.hasEffect(MobEffects.POISON)) {
            p.addEffect(new MobEffectInstance(MobEffects.POISON, 100, 0));
            p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 120, 0));
            overlay(p, "Ew, " + owner.getName().getString() + " touched that...");
        }
    }
}
