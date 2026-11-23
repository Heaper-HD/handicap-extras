package com.heaper;

import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.UUID;

public abstract class ExtraHandicap extends HandicapBehaviour {
    protected ExtraHandicap(String id) {
        super(id);
    }

    public void registerEvents() {}
    public void onGive(ServerPlayer p) {}
    public void onRemove(ServerPlayer p) {}
    public void onTick(ServerPlayer p) {}
    public void forget(UUID player) {}

    protected boolean has(ServerPlayer p) {
        HandicapState state = HandicapManager.state();
        if (state == null) {
            return false;
        }
        return state.has(p.getUUID(), handicap());
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
}
