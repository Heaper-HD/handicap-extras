package com.heaper.extra_handicaps;

import com.handicap.Handicap;
import com.handicap.HandicapManager;
import com.handicap.HandicapState;
import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import com.heaper.util.Symptoms;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ContagiousCold extends ExtraHandicap {
    private static final double RANGE = 4.0;
    private static final float CATCH_CHANCE = 0.08f;
    private static final float SNEEZE_MULTIPLIER = 3f;
    private static final int SNEEZE_ONE_IN = 12;
    private static final long SICK_TICKS = 20L * 60 * 3;
    private static final long IMMUNE_TICKS = 20L * 80 * 5;

    private final Map<UUID, Long> recoversAt = new HashMap<>();
    private final Map<UUID, Long> immuneUntil = new HashMap<>();

    public ContagiousCold() {
        super("contagious_cold");
    }

    private static long now(ServerPlayer p) {
        return ((ServerLevel) p.level()).getServer().overworld().getGameTime();
    }

    @Override
    public void onTick(ServerPlayer p) {
        long now = now(p);

        Long recovery = recoversAt.get(p.getUUID());
        if (recovery != null && now >= recovery) {
            recoversAt.remove(p.getUUID());
            immuneUntil.put(p.getUUID(), now + IMMUNE_TICKS);
            HandicapManager.remove(p, handicap(), false);
            overlay(p, "You feel better.");
            return;
        }

        setModifier(p, Attributes.MOVEMENT_SPEED, "sick", -0.1, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, true);

        boolean sneezed = p.getRandom().nextInt(SNEEZE_ONE_IN) == 0;
        if (sneezed) {
            Symptoms.sneeze(p);
        }

        HandicapState state = HandicapManager.state();
        if (state == null || state.sync()) {
            return;
        }

        float chance = sneezed ? CATCH_CHANCE * SNEEZE_MULTIPLIER : CATCH_CHANCE;
        for (Player nearby : Nearby.players(p, RANGE)) {
            if (nearby instanceof ServerPlayer other && !has(other) && !immune(other, now)
                    && other.getRandom().nextFloat() < chance) {
                infect(other, p, now);
            }
        }
    }

    private boolean immune(ServerPlayer p, long now) {
        Long until = immuneUntil.get(p.getUUID());
        if (until == null) {
            return false;
        }
        if (now >= until) {
            immuneUntil.remove(p.getUUID());
            return false;
        }
        return true;
    }

    private void infect(ServerPlayer victim, ServerPlayer source, long now) {
        if (HandicapManager.give(victim, handicap(), false)) {
            recoversAt.put(victim.getUUID(), now + SICK_TICKS);
            if (!has(victim, Handicap.ANOSOGNOSIA)) {
                overlay(victim, "You caught a cold from " + source.getName().getString() + "!");
            }
        }
    }

    @Override
    public void onRemove(ServerPlayer p) {
        setModifier(p, Attributes.MOVEMENT_SPEED, "sick", 0, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL, false);
        recoversAt.remove(p.getUUID());;
    }
}
