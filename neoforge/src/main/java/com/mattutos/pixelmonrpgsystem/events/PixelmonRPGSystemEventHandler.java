package com.mattutos.pixelmonrpgsystem.events;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.network.NetworkHandler;
import com.mattutos.pixelmonrpgsystem.network.PlayerRPGSyncPacket;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.PermanentStats;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PixelmonRPGSystemEventHandler {

    private static void notifyPlayerLevelUp(ServerPlayer serverPlayer, int newLevel) {
        serverPlayer.sendSystemMessage(Component.translatable("pixelmonrpgsystem.message.levelup", newLevel)
        .withStyle(ChatFormatting.GREEN, ChatFormatting.BOLD));
    }

    @SubscribeEvent
    public void onPokemonGainExperience(ExperienceGainEvent event) {
        if (event.pokemon.getOwnerPlayer() instanceof ServerPlayer serverPlayer) {
            PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(serverPlayer);
            if (data != null) {
                double multiplier = Config.PLAYER_XP_MULTIPLIER.get();
                int playerXP = Math.max(1, (int) (event.getExperience() * multiplier));
                int oldLevel = data.getLevel();
                data.addExperience(playerXP);
                int newLevel = data.getLevel();

                NetworkHandler.sendToPlayer(new PlayerRPGSyncPacket(data.getExperience(), data.getLevel()), serverPlayer);

                if (newLevel > oldLevel) {
                    notifyPlayerLevelUp(serverPlayer, newLevel);
                }
            }
        }
    }

    //TODO IMPLEMENTAR LOGICA DE LIMITAR LEVEL DO POKEMON PARA TRADE E GIFT

    @SubscribeEvent
    public void onSuccessCapture(CaptureEvent.SuccessfulCapture event) {
        if (Config.ENABLE_CAPTURE_RESTRICTIONS.get()) {

            ServerPlayer player = event.getPlayer();
            PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);

            int pokemonLevel = event.getPokemon().getPokemonLevel();

            gainXpFromCapturedPokemon(player, event.getPokemon(), data);

            int newPlayerLevel = data.getLevel();

            if (pokemonLevel > newPlayerLevel) {
                // SET THE POKEMON LEVEL TO THE PLAYER LEVEL
                player.sendSystemMessage(Component.translatable(
                    "pixelmonrpgsystem.message.capture.level.adjusted", pokemonLevel, newPlayerLevel
                ).withStyle(ChatFormatting.GOLD));
                event.getPokemon().setLevel(newPlayerLevel);
            }
        }
    }

    private void gainXpFromCapturedPokemon(ServerPlayer player, Pokemon pokemon, PlayerRPGCapability data) {
        int level = pokemon.getPokemonLevel();
        int catchRate = pokemon.getSpecies().getDefaultForm().getCatchRate();
        int oldPlayerLevel = data.getLevel();

        // Base XP value
        int base = 5;
        // Rarity factor, capped at a maximum value
        double rarityFactor = Math.min(10.0, 255.0 / catchRate);
        // Calculated XP gain
        int xpGain = (int) Math.round(level * base * rarityFactor);

        // Adds XP to the Player RPG system
        data.addExperience(xpGain);

        // New player level after gaining XP
        int newPlayerLevel = data.getLevel();

        NetworkHandler.sendToPlayer(new PlayerRPGSyncPacket(data.getExperience(), data.getLevel()), player);

        player.sendSystemMessage(Component.translatable(
                "pixelmonrpgsystem.message.capture.xp", xpGain, pokemon.getSpecies().getName(), level
        ));

        if (newPlayerLevel > oldPlayerLevel) notifyPlayerLevelUp(player, newPlayerLevel);
    }

    @SubscribeEvent
    public void onPokemonCapture(CaptureEvent.StartCapture event) {
        if (Config.ENABLE_CAPTURE_RESTRICTIONS.get()) {
            Player player = event.getPlayer();
            PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
            if (data != null) {
                int pokemonLevel = event.getPokemon().getPokemonLevel();
                int playerLevel = data.getLevel();
                boolean isMasterBall = event.getPokeBallEntity().getBallType().getName().equals("master_ball");

                if (!isMasterBall && pokemonLevel > playerLevel) {
                    int catchRate = event.getCaptureValues().getCatchRate();
                    int debufCacthRate = (int) (catchRate * 0.01);

                    // REDUCE THE PROBABILITY OF CAPTURING THE POKEMON
                    if (debufCacthRate == 0) {
                        player.sendSystemMessage(
                                Component.translatable("pixelmonrpgsystem.message.capture.impossible", pokemonLevel, playerLevel)
                                        .withStyle(ChatFormatting.RED)
                        );
                    } else {
                        player.sendSystemMessage(
                                Component.translatable("pixelmonrpgsystem.message.capture.harder", pokemonLevel, playerLevel)
                                        .withStyle(ChatFormatting.GOLD)
                        );
                    }

                    event.getCaptureValues().setCatchRate(debufCacthRate);
                }
            }
        }
    }

    @SubscribeEvent
    public void onBattleStart(BattleStartedEvent.Pre event) {
        recalculateStatsBasedOnPlayerLevel(event.getTeamOne());
        recalculateStatsBasedOnPlayerLevel(event.getTeamTwo());
    }

    private void recalculateStatsBasedOnPlayerLevel(BattleParticipant[] team) {
        for (var participant : team) {
            if (participant instanceof PlayerParticipant playerPart) {
                ServerPlayer player = (ServerPlayer) playerPart.getEntity();

                PlayerRPGCapability rpg = CapabilitiesRegistry.getPlayerRPGCapability(player);

                if (rpg != null) {
                    int level = rpg.getLevel();
                    double multiplier = 1.0 + (level / 10) * 0.05;
                    if (multiplier > 1.5) multiplier = 1.5;

                    for (Pokemon pokemon : playerPart.getStorage().getTeam()) {
                        if (pokemon == null) continue;
                        PermanentStats stats = pokemon.getStats();

                        stats.setAttack((int) (stats.getAttack() * multiplier));
                        stats.setDefense((int) (stats.getDefense() * multiplier));
                        stats.setSpecialAttack((int) (stats.getSpecialAttack() * multiplier));
                        stats.setSpecialDefense((int) (stats.getSpecialDefense() * multiplier));
                        stats.setSpeed((int) (stats.getSpeed() * multiplier));
                        stats.setHP((int) (stats.getHP() * multiplier));
                    }
                }
            }
        }
    }
}
