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
    }

    public static void sendToPlayer(PlayerRPGSyncPacket packet, ServerPlayer player) {
        player.connection.send(packet);
    }
}
