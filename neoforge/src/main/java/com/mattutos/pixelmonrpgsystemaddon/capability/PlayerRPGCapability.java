package com.mattutos.pixelmonrpgsystemaddon.capability;

import com.mattutos.pixelmonrpgsystemaddon.PixelmonRPGSystemAddon;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class PlayerRPGCapability {
    public static final EntityCapability<PlayerRPGData, Void> INSTANCE = 
        EntityCapability.create(ResourceLocation.fromNamespaceAndPath(PixelmonRPGSystemAddon.MODID, "player_rpg_data"), PlayerRPGData.class, Void.class);
    
    public static void register(RegisterCapabilitiesEvent event) {
        event.registerEntity(INSTANCE, EntityType.PLAYER, (player, context) -> new PlayerRPGData());
    }
}
