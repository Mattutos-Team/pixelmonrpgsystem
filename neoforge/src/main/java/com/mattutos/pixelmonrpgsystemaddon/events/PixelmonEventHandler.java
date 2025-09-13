package com.mattutos.pixelmonrpgsystemaddon.events;

import com.mattutos.pixelmonrpgsystemaddon.Config;
import com.mattutos.pixelmonrpgsystemaddon.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystemaddon.capability.PlayerRPGData;
import com.mattutos.pixelmonrpgsystemaddon.network.NetworkHandler;
import com.mattutos.pixelmonrpgsystemaddon.network.PlayerRPGSyncPacket;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.PermanentStats;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PixelmonEventHandler {

    @SubscribeEvent
    public void onPokemonGainExperience(ExperienceGainEvent event) {
        if (event.pokemon.getOwnerPlayer() != null) {
            Player player = event.pokemon.getOwnerPlayer();
            PlayerRPGData data = player.getCapability(PlayerRPGCapability.INSTANCE);
            if (data != null) {
                double multiplier = Config.PLAYER_XP_MULTIPLIER.get();
                int playerXP = Math.max(1, (int) (event.getExperience() * multiplier));
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
    public void onPokemonCapture(CaptureEvent.StartCapture event) {
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
    public void onBattleEnd(BattleEndEvent event) {
        if (Config.ENABLE_BATTLE_RESTRICTIONS.get()) {
            event.getPlayers().forEach(battlePlayer -> {
                PlayerRPGData data = battlePlayer.getCapability(PlayerRPGCapability.INSTANCE);
                if (data != null) {
                    int playerLevel = data.getLevel();

                    battlePlayer.sendSystemMessage(Component.literal(
                            "§eBatalha finalizada! Seu nível atual: " + playerLevel
                    ));
                }
            });
        }
    }

    @SubscribeEvent
    public void onBattleStart(BattleStartedEvent.Post event) {
        handleTeam(event.getTeamOne());
        handleTeam(event.getTeamTwo());
    }

    private void handleTeam(com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant[] team) {
        for (var participant : team) {
            if (participant instanceof PlayerParticipant playerPart) {
                ServerPlayer player = (ServerPlayer) playerPart.getEntity();

                PlayerRPGData rpg = player.getCapability(PlayerRPGCapability.INSTANCE);

                if(rpg != null)  {
                    int level = rpg.getLevel();
                    double multiplier = 1.0 + (level / 10) * 0.05;
                    if (multiplier > 1.5) multiplier = 1.5;

                    for (Pokemon pokemon : playerPart.getStorage().getTeam()) {
                        if (pokemon == null) continue;
                        PermanentStats stats = pokemon.getStats();

                        int currentAttack = stats.getAttack();

                        player.sendSystemMessage(Component.literal(
                                "Multiplicador atual " + multiplier
                        ));

                        player.sendSystemMessage(Component.literal(
                                "Alterando o status defense de: " + currentAttack + " para: " + (currentAttack * multiplier + "no pokemon " + pokemon.getDisplayName())
                        ));


                        stats.setAttack((int)(stats.getAttack() * multiplier));
                        stats.setDefense((int)(stats.getDefense() * multiplier));
                        stats.setSpecialAttack((int)(stats.getSpecialAttack() * multiplier));
                        stats.setSpecialDefense((int)(stats.getSpecialDefense() * multiplier));
                        stats.setSpeed((int)(stats.getSpeed() * multiplier));
                        stats.setHP((int)(stats.getHP() * multiplier));
                    }
                }
            }
        }
    }
}
