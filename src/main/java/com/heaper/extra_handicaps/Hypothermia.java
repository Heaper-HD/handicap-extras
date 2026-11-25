package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Hypothermia extends ExtraHandicap {
    private static final float COLD_TEMPERATURE = 0.15f;
    private static final double FRIEND_RANGE = 4.0f;
    private static final int DELAY_SECONDS = 5;

    private final Map<UUID, Integer> coldSeconds = new HashMap<>();

    public Hypothermia() {
        super("hypothermia");
    }

    private static boolean cold(ServerPlayer p) {
        return p.level().getBiome(p.blockPosition()).value().getBaseTemperature() < COLD_TEMPERATURE;
    }

    private static boolean warm(ServerPlayer p) {
        return Pyrophobia.nearFire(p) || !Nearby.players(p, FRIEND_RANGE).isEmpty();
    }

    @Override
    public void onTick(ServerPlayer p) {
        boolean freezing = cold(p) && !warm(p) && !p.isCreative() && !p.isSpectator();
        int seconds = freezing ? coldSeconds.merge(p.getUUID(), 1, Integer::sum) : 0;
        if (seconds == 0) {
            coldSeconds.remove(p.getUUID());
        }

        boolean frozen = seconds >= DELAY_SECONDS;
        if (seconds == DELAY_SECONDS) {
            overlay(p, "You're freezing! Find a fire or a friend");
        }
        if (frozen) {
            p.setTicksFrozen(p.getTicksRequiredToFreeze() + 40);
        }
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, frozen);
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "slow", -0.3, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        coldSeconds.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        coldSeconds.remove(player);
    }
}
