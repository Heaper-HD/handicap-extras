package com.heaper.extra_handicaps;

import com.heaper.ExtraHandicap;
import com.heaper.util.Nearby;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Hemophobia extends ExtraHandicap {
    private static final double RANGE = 12.0;
    private static final long COOLDOWN_TICKS = 200;

    private final Map<UUID, Long> lastFaint = new HashMap<>();

    public Hemophobia() {
        super("hemophobia");
    }

    @Override
    public void registerEvents() {
        ServerLivingEntityEvents.AFTER_DAMAGE.register(((entity, source, baseDamage, damageTaken, blocked) -> {
            if (blocked || damageTaken <= 0f || !(entity instanceof ServerPlayer victim)) {
                return;
            }
            for (Player nearby : Nearby.players(victim, RANGE)) {
                if (nearby instanceof ServerPlayer witness && has(witness) && witness.hasLineOfSight(victim)) {
                    faint(witness);
                }
            }
        }));
    }

    private void faint(ServerPlayer p) {
        long now = p.level().getGameTime();
        Long last = lastFaint.get(p.getUUID());
        if (last != null && now - last < COOLDOWN_TICKS) {
            return;
        }
        lastFaint.put(p.getUUID(), now);

        p.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 50, 0, false, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 80, 0, false, false, false));
        p.addEffect(new MobEffectInstance(MobEffects.NAUSEA, 120, 0, false, false, false));
        overlay(p, "Blood! You feel faint...");
    }

    @Override
    public void onRemove(ServerPlayer p) {
        lastFaint.remove(p.getUUID());
    }

    @Override
    public void forget(UUID player) {
        lastFaint.remove(player);
    }
}
