package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SocialAnxiety extends ExtraHandicap {
    public static final double RANGE = 10.0;
    public static final int CROWD = 3;

    private final Set<UUID> overwhelmed = new HashSet<>();

    public SocialAnxiety() {
        super("social_anxiety");
    }

    public static boolean crowded(Player p) {
        return Nearby.players(p, RANGE).size() >= CROWD;
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean crowded = crowded(p);
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, crowded);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, crowded);

        if (crowded && overwhelmed.add(p.getUUID())) {
            overlay(p, "Too many people... you feel overwhelmed");
        } else if (!crowded && overwhelmed.remove(p.getUUID())) {
            overlay(p, "You can breath again.");
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        overwhelmed.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        overwhelmed.remove(player);
    }
}
