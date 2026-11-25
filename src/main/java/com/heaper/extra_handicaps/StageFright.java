package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class StageFright extends ExtraHandicap {
    private static final double PLAYER_RANGE = 16.0;
    private static final double VILLAGER_RANGE = 6.0;
    private static final double LOOK_CONE = 0.7;

    private final Set<UUID> nervous = new HashSet<>();

    public StageFright() {
        super("stage_fright");
    }

    public static boolean watched(Player p) {
        Vec3 target = p.getEyePosition();
        for (Player other : Nearby.players(p, PLAYER_RANGE)) {
            Vec3 toMe = target.subtract(other.getEyePosition()).normalize();
            if (other.getViewVector(1f).dot(toMe) > LOOK_CONE && other.hasLineOfSight(p)) {
                return true;
            }
        }
        return Nearby.any(p, VILLAGER_RANGE, Villager.class, v -> p.hasLineOfSight(p));
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean watched = watched(p);
        setModifier(p, Attributes.BLOCK_BREAK_SPEED, "mining", -0.4, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, watched);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, watched);
        setModifier(p, Attributes.ATTACK_SPEED, "clumsy", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, watched);

        if (watched && nervous.add(p.getUUID())) {
            overlay(p, "Someone's watching... you start fumbling");
        } else if (!watched) {
            nervous.remove(p.getUUID());
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.BLOCK_BREAK_SPEED, "mining", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.ATTACK_DAMAGE, "weak", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        setModifier(p, Attributes.ATTACK_SPEED, "clumsy", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        nervous.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        nervous.remove(player);
    }
}
