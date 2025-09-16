package com.mattutos.pixelmonrpgsystem.events;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.cache.BattleLevelCache;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.network.NetworkHandler;
import com.mattutos.pixelmonrpgsystem.network.PlayerRPGSyncPacket;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleEndEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.stats.PermanentStats;
import com.pixelmonmod.pixelmon.api.storage.PlayerPartyStorage;
import com.pixelmonmod.pixelmon.api.storage.StorageProxy;
import com.pixelmonmod.pixelmon.battles.controller.BattleController;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PixelmonRPGSystemEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PixelmonRPGSystemEventHandler.class);

    @SubscribeEvent
    public void onPokemonGainExperience(ExperienceGainEvent event) {
        if (event.pokemon.getOwnerPlayer() != null) {
            Player player = event.pokemon.getOwnerPlayer();
            PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
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

    //TODO IMPLEMENTAR LOGICA DE LIMITAR LEVEL DO POKEMON PARA TRADE E GIFT

    @SubscribeEvent
    public  void onSuccessCapture(CaptureEvent.SuccessfulCapture event){
        if (Config.ENABLE_CAPTURE_RESTRICTIONS.get()) {
            Player player = event.getPlayer();
            PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);

            int pokemonLevel = event.getPokemon().getPokemonLevel();
            int playerLevel = data.getLevel();

            if(pokemonLevel > playerLevel) {
                // SET THE POKEMON LEVEL TO THE PLAYER LEVEL
                player.sendSystemMessage(Component.literal(
                        "§eVocê capturou um pokémon de nível " + pokemonLevel + ", mas seu nível é " + playerLevel + ". O nível do pokémon foi ajustado para o seu nível."
                ));
                event.getPokemon().setLevel(playerLevel);
            }
        }
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
//                    debufCacthRate = catchRate - (pokemonLevel - playerLevel) * debufCacthRate;
                    System.out.println("Catch Rate Original: " + catchRate + " | Debuf: " + debufCacthRate + " | Nível do Pokémon: " + pokemonLevel + " | Nível do Jogador: " + playerLevel);

                    String msgProbability = debufCacthRate == 0 ? "IMPOSSIVEL" : "mais difícil";

                    // REDUCE THE PROBABILITY OF CAPTURING THE POKEMON
                    player.sendSystemMessage(Component.literal(
                            "§cEste pokémon é de nível " + pokemonLevel + ", mas você é nível " + playerLevel + ". A captura será " + msgProbability + "!"
                    ));

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
