package com.heaper.network;

import com.heaper.HandicapExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ImpulsePayload(float x, float y, float z) implements CustomPacketPayload {
    public static final Type<ImpulsePayload> TYPE = new Type<>(HandicapExtras.id("impulse"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ImpulsePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, ImpulsePayload::x,
            ByteBufCodecs.FLOAT, ImpulsePayload::y,
            ByteBufCodecs.FLOAT, ImpulsePayload::z,
            ImpulsePayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
