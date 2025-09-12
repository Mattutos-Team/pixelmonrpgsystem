package com.mattutos.pixelmonrpgsystemaddon.network;

import com.mattutos.pixelmonrpgsystemaddon.PixelmonRPGSystemAddon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class NetworkHandler {
    public static void registerPackets(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar(PixelmonRPGSystemAddon.MODID);
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
