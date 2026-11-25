package com.heaper.util;

import com.heaper.network.CameraJoltPayload;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;

public final class Symptoms {
    private Symptoms() {}

    public static void jolt(ServerPlayer p, float pitch, float yaw) {
        if (ServerPlayNetworking.canSend(p, CameraJoltPayload.TYPE)) {
            ServerPlayNetworking.send(p, new CameraJoltPayload(pitch, yaw));
        }
    }

    public static void sneeze(ServerPlayer p) {
        ServerLevel level = p.level();
        level.playSound(null, p.getX(), p.getY(), p.getZ(), SoundEvents.PANDA_SNEEZE, SoundSource.PLAYERS, 1f, 1.2f);
        level.sendParticles(ParticleTypes.SNEEZE, p.getX(), p.getEyeY(), p.getZ(), 6, 0.2, 0.1, 0.2, 0.02);
        p.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 20, 1, false, false, false));
        jolt(p, 12f, (p.getRandom().nextFloat() - 0.5f) * 8f);
    }
}
