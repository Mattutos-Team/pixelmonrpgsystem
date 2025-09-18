package com.mattutos.pixelmonrpgsystem.experience;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.events.PlayerGainRPGExperienceEvent;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.common.NeoForge;

public class RPGExperienceManager {

    public static void addExperience(ServerPlayer player, int amount) {
        addExperience(player, amount, ExperienceSource.UNKNOWN);
    }

    public static void addExperience(ServerPlayer player, int amount, ExperienceSource source) {
        if (amount <= 0) {
            return;
        }

        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null) {
            return;
        }

        PlayerGainRPGExperienceEvent.Pre preEvent = new PlayerGainRPGExperienceEvent.Pre(player, amount, source);
        if (NeoForge.EVENT_BUS.post(preEvent).isCanceled()) {
            return;
        }

        int oldLevel = data.getLevel();
        data.addExperience(amount);
        int newLevel = data.getLevel();

        PlayerGainRPGExperienceEvent.Post postEvent = new PlayerGainRPGExperienceEvent.Post(player, amount, source);
        NeoForge.EVENT_BUS.post(postEvent);

        if (newLevel > oldLevel) {
            player.sendSystemMessage(Component.translatable("pixelmonrpgsystem.message.levelup", newLevel)
                    .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
        }
    }
}
