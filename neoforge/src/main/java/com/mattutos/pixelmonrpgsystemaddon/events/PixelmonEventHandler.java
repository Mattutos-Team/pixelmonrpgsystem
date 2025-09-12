package com.mattutos.pixelmonrpgsystemaddon.events;

import com.mattutos.pixelmonrpgsystemaddon.PixelmonRPGSystemAddon;
import com.mattutos.pixelmonrpgsystemaddon.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystemaddon.capability.PlayerRPGData;
import com.mattutos.pixelmonrpgsystemaddon.Config;
import com.mattutos.pixelmonrpgsystemaddon.network.PlayerRPGSyncPacket;
import com.mattutos.pixelmonrpgsystemaddon.network.NetworkHandler;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = PixelmonRPGSystemAddon.MODID)
public class PixelmonEventHandler {
    
    @SubscribeEvent
    public static void onPokemonGainExperience(ExperienceGainEvent event) {
        if (event.pokemon.getOwnerPlayer() != null) {
            Player player = event.pokemon.getOwnerPlayer();
            PlayerRPGData data = player.getCapability(PlayerRPGCapability.INSTANCE);
            if (data != null) {
                double multiplier = Config.PLAYER_XP_MULTIPLIER.get();
                int playerXP = Math.max(1, (int)(event.getExperience() * multiplier));
                int oldLevel = data.getLevel();
                data.addExperience(playerXP);
                int newLevel = data.getLevel();
                
                if (player instanceof ServerPlayer serverPlayer) {
                    NetworkHandler.sendToPlayer(new PlayerRPGSyncPacket(data.getExperience(), data.getLevel()), serverPlayer);
                    
                    if (newLevel > oldLevel) {
                        player.sendSystemMessage(Component.literal("§6Parabéns! Você subiu para o nível " + newLevel + "!"));
                    }
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onPokemonCapture(CaptureEvent.StartCapture event) {
        if (Config.ENABLE_CAPTURE_RESTRICTIONS.get()) {
            Player player = event.getPlayer();
            PlayerRPGData data = player.getCapability(PlayerRPGCapability.INSTANCE);
            if (data != null) {
                int pokemonLevel = event.getPokemon().getPokemonLevel();
                int playerLevel = data.getLevel();
                
                if (pokemonLevel > playerLevel) {
                    event.setCanceled(true);
                    player.sendSystemMessage(Component.literal(
                        "§cVocê precisa ser nível " + pokemonLevel + " para capturar este Pokémon! (Seu nível atual: " + playerLevel + ")"
                    ));
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onBattleEnd(BattleEndEvent event) {
        if (Config.ENABLE_BATTLE_RESTRICTIONS.get()) {
            event.getPlayers().forEach(battlePlayer -> {
                Player player = battlePlayer;
                PlayerRPGData data = player.getCapability(PlayerRPGCapability.INSTANCE);
                if (data != null) {
                    int playerLevel = data.getLevel();
                    
                    player.sendSystemMessage(Component.literal(
                        "§eBatalha finalizada! Seu nível atual: " + playerLevel
                    ));
                }
            });
        }
    }
}
