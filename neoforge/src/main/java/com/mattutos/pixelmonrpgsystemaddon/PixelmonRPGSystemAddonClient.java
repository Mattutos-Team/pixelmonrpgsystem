package com.mattutos.pixelmonrpgsystemaddon;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import com.mattutos.pixelmonrpgsystemaddon.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystemaddon.capability.PlayerRPGData;
import com.mattutos.pixelmonrpgsystemaddon.client.ClientPlayerRPGData;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = PixelmonRPGSystemAddon.MODID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = PixelmonRPGSystemAddon.MODID, value = Dist.CLIENT)
public class PixelmonRPGSystemAddonClient {
    public PixelmonRPGSystemAddonClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        PixelmonRPGSystemAddon.LOGGER.info("HELLO FROM CLIENT SETUP");
        PixelmonRPGSystemAddon.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
    
    @SubscribeEvent
    static void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity().level().isClientSide) {
            PlayerRPGData data = event.getEntity().getCapability(PlayerRPGCapability.INSTANCE);
            if (data != null) {
                ClientPlayerRPGData.setPlayerData(data.getExperience(), data.getLevel());
            }
        }
    }
}
