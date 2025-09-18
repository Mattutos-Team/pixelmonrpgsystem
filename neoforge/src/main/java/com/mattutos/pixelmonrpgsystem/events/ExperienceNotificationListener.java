package com.mattutos.pixelmonrpgsystem.events;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.experience.ExperienceSource;
import com.mattutos.pixelmonrpgsystem.network.NetworkHandler;
import com.mattutos.pixelmonrpgsystem.network.PacketSyncExperience;
import com.mattutos.pixelmonrpgsystem.network.PlayerRPGSyncPacket;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber(modid = PixelmonRPGSystem.MODID)
public class ExperienceNotificationListener {

    private static void notifyPlayerLevelUp(ServerPlayer serverPlayer, int newLevel) {
        serverPlayer.sendSystemMessage(Component.translatable("pixelmonrpgsystem.message.levelup", newLevel)
                .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
    }

    @SubscribeEvent
    public static void onPlayerGainExperience(PlayerGainRPGExperienceEvent.Post event) {
        ServerPlayer player = event.getPlayer();
        int experienceGained = event.getExperienceAmount();
        ExperienceSource source = event.getSource();

        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null) {
            return;
        }

        int currentLevel = data.getLevel();

        NetworkHandler.sendToPlayer(new PlayerRPGSyncPacket(data.getExperience(), data.getLevel(), data.getLastDailyReward()), player);

        NetworkHandler.sendToPlayer(new PacketSyncExperience(data.getExperience(), experienceGained, source), player);

        String messageKey = switch (source) {
            case POKEMON_CAPTURE -> "pixelmonrpgsystem.message.xp.gained.pokemon_capture";
            case POKEMON_BATTLE -> "pixelmonrpgsystem.message.xp.gained.pokemon_battle";
            case UNKNOWN -> "pixelmonrpgsystem.message.xp.gained.unknown";
        };

        player.sendSystemMessage(Component.translatable(messageKey, experienceGained));
    }
}
