package com.heaper.network;

import com.heaper.HandicapExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record CameraJoltPayload(float pitch, float yaw) implements CustomPacketPayload {
    public static final Type<CameraJoltPayload> TYPE = new Type<>(HandicapExtras.id("camera_jolt"));

    public static final StreamCodec<RegistryFriendlyByteBuf, CameraJoltPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, CameraJoltPayload::pitch,
            ByteBufCodecs.FLOAT, CameraJoltPayload::yaw,
            CameraJoltPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
