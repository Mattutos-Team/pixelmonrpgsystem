package com.mattutos.pixelmonrpgsystem.network;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.client.ClientPlayerRPGData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerRPGSyncPacket(int experience, int level, long lastDailyReward) implements CustomPacketPayload {

    public static final Type<PlayerRPGSyncPacket> TYPE = new Type<>(PixelmonRPGSystem.prefix("player_rpg_sync"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PlayerRPGSyncPacket> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.of(RegistryFriendlyByteBuf::writeInt, RegistryFriendlyByteBuf::readInt), PlayerRPGSyncPacket::experience,
            StreamCodec.of(RegistryFriendlyByteBuf::writeInt, RegistryFriendlyByteBuf::readInt), PlayerRPGSyncPacket::level,
            StreamCodec.of(RegistryFriendlyByteBuf::writeLong, RegistryFriendlyByteBuf::readLong), PlayerRPGSyncPacket::lastDailyReward,
            PlayerRPGSyncPacket::new
    );

    public static void handle(PlayerRPGSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPlayerRPGData.setPlayerData(packet.experience(), packet.level());
            ClientPlayerRPGData.setLastDailyReward(packet.lastDailyReward());
        });
    }

    @Override
    public Type<PlayerRPGSyncPacket> type() {
        return TYPE;
    }
}
