package com.mattutos.pixelmonrpgsystem.registry;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;

public class CapabilitiesRegistry {
    public static final EntityCapability<PlayerRPGCapability, Void> PLAYER_RPG_CAPABILITY = EntityCapability.createVoid(
            PixelmonRPGSystem.prefix("player_rpg_capability"), PlayerRPGCapability.class);


    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        event.registerEntity(PLAYER_RPG_CAPABILITY, EntityType.PLAYER, (player, ctx) -> new PlayerRPGCapability(player));
    }

    public static PlayerRPGCapability getPlayerRPGCapability(final LivingEntity entity) {
        if (entity == null) return null;
        return entity.getCapability(PLAYER_RPG_CAPABILITY);
    }
}
