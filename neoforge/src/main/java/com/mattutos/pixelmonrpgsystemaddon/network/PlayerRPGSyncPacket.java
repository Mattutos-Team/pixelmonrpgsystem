package com.mattutos.pixelmonrpgsystemaddon.network;

import com.mattutos.pixelmonrpgsystemaddon.PixelmonRPGSystemAddon;
import com.mattutos.pixelmonrpgsystemaddon.client.ClientPlayerRPGData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PlayerRPGSyncPacket(int experience, int level) implements CustomPacketPayload {
    
    public static final Type<PlayerRPGSyncPacket> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(PixelmonRPGSystemAddon.MODID, "player_rpg_sync"));
    
    public static final StreamCodec<FriendlyByteBuf, PlayerRPGSyncPacket> STREAM_CODEC = StreamCodec.composite(
        StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt), PlayerRPGSyncPacket::experience,
        StreamCodec.of(FriendlyByteBuf::writeInt, FriendlyByteBuf::readInt), PlayerRPGSyncPacket::level,
        PlayerRPGSyncPacket::new
    );
    
    @Override
    public Type<PlayerRPGSyncPacket> type() {
        return TYPE;
    }
    
    public static void handle(PlayerRPGSyncPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            ClientPlayerRPGData.setPlayerData(packet.experience(), packet.level());
        });
    }
}
