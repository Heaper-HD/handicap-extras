package com.heaper;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.heaper.chat.ChatMessage;
import com.heaper.network.CameraJoltPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public abstract class ExtraHandicap extends HandicapBehaviour {
    protected ExtraHandicap(String id) {
        super(id);
    }

    public void registerEvents() {}
    public void onGive(ServerPlayer p) {}
    public void onConsume(ServerPlayer p, ItemStack item) {}
    public void onChat(ChatMessage msg) {}
    public void observerChat(ServerPlayer sender, String text) {}
    public void onPickup(ServerPlayer p, ItemEntity item) {}
    public void onEffectAdded(ServerPlayer p, MobEffectInstance effect) {}
    public void onRemove(ServerPlayer p) {}
    public void onTick(ServerPlayer p) {}
    public void forget(UUID player) {}

    protected boolean has(ServerPlayer p) {
        HandicapState state = HandicapManager.state();
        return state != null && state.has(p.getUUID(), handicap());
    }

    protected boolean has(ServerPlayer p, Handicap other) {
        HandicapState state = HandicapManager.state();
        return state != null && state.has(p.getUUID(), other);
    }

    protected void overlay(ServerPlayer p, String msg) {
        p.sendOverlayMessage(Component.literal(msg));
    }

    protected void setModifier(ServerPlayer p, Holder<Attribute> attr, String suffix, double amount,
                               AttributeModifier.Operation op, boolean active) {
        AttributeInstance attrInstance = p.getAttribute(attr);
        if (attrInstance == null) {
            return;
        }

        Identifier modId = HandicapExtras.id(id() + "_" + suffix);
        AttributeModifier existing = attrInstance.getModifier(modId);

        if (active) {
            if (existing != null) {
                if (existing.amount() == amount) {
                    return;
                }
                attrInstance.removeModifier(modId);
            }
            attrInstance.addTransientModifier(new AttributeModifier(modId, amount, op));
        } else if (existing != null) {
            attrInstance.removeModifier(modId);
        }
    }

    protected void jolt(ServerPlayer p, float pitch, float yaw) {
        if (ServerPlayNetworking.canSend(p, CameraJoltPayload.TYPE)) {
            ServerPlayNetworking.send(p, new CameraJoltPayload(pitch, yaw));
        }
    }
}
