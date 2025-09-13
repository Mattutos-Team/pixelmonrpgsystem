package com.mattutos.pixelmonrpgsystemaddon.registry;

import com.mattutos.pixelmonrpgsystemaddon.PixelmonRPGSystemAddon;
import com.mattutos.pixelmonrpgsystemaddon.capability.PlayerRPGData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class AttachmentsRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, PixelmonRPGSystemAddon.MODID);
    
    public static final Supplier<AttachmentType<PlayerRPGData>> PLAYER_RPG_DATA = ATTACHMENTS.register("player_rpg_data", 
        () -> AttachmentType.serializable(PlayerRPGData::new).build());
}
