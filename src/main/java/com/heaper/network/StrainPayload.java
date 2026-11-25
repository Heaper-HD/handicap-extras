package com.heaper.network;

import com.heaper.HandicapExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record StrainPayload(float x, float y, float z) implements CustomPacketPayload {
    public static final Type<StrainPayload> TYPE = new Type<>(HandicapExtras.id("strain"));

    public static final StreamCodec<RegistryFriendlyByteBuf, StrainPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.FLOAT, StrainPayload::x,
            ByteBufCodecs.FLOAT, StrainPayload::y,
            ByteBufCodecs.FLOAT, StrainPayload::z,
            StrainPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
