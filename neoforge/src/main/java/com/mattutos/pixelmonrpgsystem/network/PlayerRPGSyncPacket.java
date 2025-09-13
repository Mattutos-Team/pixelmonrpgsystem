package com.mattutos.pixelmonrpgsystem.network;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.client.ClientPlayerRPGData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerRPGSyncPacket(int experience, int level) implements CustomPacketPayload {

    public static final Type<PlayerRPGSyncPacket> TYPE = new Type<>(PixelmonRPGSystem.prefix("player_rpg_sync"));

    public static final StreamCodec<FriendlyByteBuf, PlayerRPGSyncPacket> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt), PlayerRPGSyncPacket::experience,
            StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt), PlayerRPGSyncPacket::level,
            PlayerRPGSyncPacket::new
    );

    public static void handle(PlayerRPGSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPlayerRPGData.setPlayerData(packet.experience(), packet.level()));
    }

    @Override
    public Type<PlayerRPGSyncPacket> type() {
        return TYPE;
    }
}
