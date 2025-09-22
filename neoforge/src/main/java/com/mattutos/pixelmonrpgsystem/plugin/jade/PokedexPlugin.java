package com.mattutos.pixelmonrpgsystem.plugin.jade;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.plugin.jade.entity.PixelmonEntityComponentProvider;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class PokedexPlugin implements IWailaPlugin {

    public static final ResourceLocation PIXELMON_ENTITY = ResourceLocation.fromNamespaceAndPath(PixelmonRPGSystem.MODID, "pixelmon_entity");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerEntityDataProvider(PixelmonEntityComponentProvider.INSTANCE, PixelmonEntity.class);
    }

    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerEntityComponent(PixelmonEntityComponentProvider.INSTANCE, PixelmonEntity.class);
        registration.registerEntityIcon(PixelmonEntityComponentProvider.INSTANCE, PixelmonEntity.class);
    }

}
