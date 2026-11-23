package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SeparationAnxiety extends ExtraHandicap {
    public static final double RANGE = 20.0;
    public static final double VILLAGER_RANGE = 15;

    private final Set<UUID> anxious = new HashSet<>();

    public SeparationAnxiety() {
        super("separation_anxiety");
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean alone = alone(p);
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, alone);

        if (alone && anxious.add(p.getUUID())) {
            overlay(p, "You feel anxious being alone...");
        } else if (!alone && anxious.remove(p.getUUID())) {
            overlay(p, "You feel calmer with company.");
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        anxious.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        anxious.remove(player);
    }

    public static boolean alone(Player p) {
        return !Nearby.any(p, RANGE, Player.class, o -> !o.isSpectator())
                && !Nearby.any(p, RANGE, TamableAnimal.class, pet -> pet.isOwnedBy(p))
                && !Nearby.any(p, VILLAGER_RANGE, Villager.class, v -> true);
    }
}
