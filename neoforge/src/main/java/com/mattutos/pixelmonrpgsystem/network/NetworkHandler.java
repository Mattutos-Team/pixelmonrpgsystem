package com.mattutos.pixelmonrpgsystem.network;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PixelmonRPGSystem.MODID);
        registrar.playToClient(
                PlayerRPGSyncPacket.TYPE,
                PlayerRPGSyncPacket.STREAM_CODEC,
                PlayerRPGSyncPacket::handle
        );
        registrar.playToServer(DailyRewardRequestPacket.TYPE, DailyRewardRequestPacket.STREAM_CODEC, DailyRewardRequestPacket::handle);
        registrar.playToClient(DailyRewardResponsePacket.TYPE, DailyRewardResponsePacket.STREAM_CODEC, DailyRewardResponsePacket::handle);
    }

    public static void sendToPlayer(PlayerRPGSyncPacket packet, ServerPlayer player) {
        player.connection.send(packet);
    }

    public static void sendToPlayer(DailyRewardResponsePacket packet, ServerPlayer player) {
        player.connection.send(packet);
    }

    public static void sendToServer(DailyRewardRequestPacket packet) {
        net.neoforged.neoforge.network.PacketDistributor.sendToServer(packet);
    }
}
