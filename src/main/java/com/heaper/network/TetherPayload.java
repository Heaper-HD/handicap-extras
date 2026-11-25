package com.heaper.network;

import com.heaper.HandicapExtras;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record TetherPayload(double px, double py, double pz, float allowed) implements CustomPacketPayload {
    public static final Type<TetherPayload> TYPE = new Type<>(HandicapExtras.id("tether"));

    public static final StreamCodec<RegistryFriendlyByteBuf, TetherPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.DOUBLE, TetherPayload::px,
            ByteBufCodecs.DOUBLE, TetherPayload::py,
            ByteBufCodecs.DOUBLE, TetherPayload::pz,
            ByteBufCodecs.FLOAT, TetherPayload::allowed,
            TetherPayload::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
