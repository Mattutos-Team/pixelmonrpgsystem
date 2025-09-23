package com.mattutos.pixelmonrpgsystem.events;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.experience.ExperienceSource;
import com.mattutos.pixelmonrpgsystem.experience.RPGExperienceManager;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryManager;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryStatusBase;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.pixelmonmod.pixelmon.api.events.CaptureEvent;
import com.pixelmonmod.pixelmon.api.events.ExperienceGainEvent;
import com.pixelmonmod.pixelmon.api.events.LevelUpEvent;
import com.pixelmonmod.pixelmon.api.events.battles.AttackEvent;
import com.pixelmonmod.pixelmon.api.events.battles.BattleStartedEvent;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.battles.controller.participants.BattleParticipant;
import com.pixelmonmod.pixelmon.battles.controller.participants.PixelmonWrapper;
import com.pixelmonmod.pixelmon.battles.controller.participants.PlayerParticipant;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PixelmonRPGSystemEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PixelmonRPGSystemEventHandler.class);

    @SubscribeEvent
    public void onPokemonGainExperience(ExperienceGainEvent event) {
        if (event.pokemon.getOwnerPlayer() instanceof ServerPlayer serverPlayer) {
            double multiplier = Config.PLAYER_XP_MULTIPLIER.get();
            int playerXP = Math.max(1, (int) (event.getExperience() * multiplier));
            RPGExperienceManager.addExperience(serverPlayer, playerXP, ExperienceSource.POKEMON_BATTLE);

            limitXpGainedFromPokemonByPlayerLevel(event);

            if (event.getExperience() > 0) {
                try {
                    Pokemon faintedPokemon = event.getFaintedPokemon();
                    if (faintedPokemon != null) {
                        MasteryManager.addBattleVictoryXp(serverPlayer, faintedPokemon);
                    }
                } catch (Exception e) {
                    System.err.println("Error adding mastery XP from battle victory: " + e.getMessage()); // (important-comment)
                    e.printStackTrace();
                }
            }
        }
    }

    private void limitXpGainedFromPokemonByPlayerLevel(ExperienceGainEvent event) {
        Pokemon pokemon = event.pokemon;
        if (!(pokemon.getOwnerPlayer() instanceof ServerPlayer serverPlayer)) return;

        if (CapabilitiesRegistry.getPlayerRPGCapability(serverPlayer) instanceof PlayerRPGCapability data) {
            int playerLevel = data.getLevel();
            if (pokemon.getPokemonLevel() > playerLevel) {
                event.setCanceled(true);
            } else if (pokemon.getPokemonLevel() == playerLevel) {
                // calcule abaixo quanto de xp falta de fato para ir para o proximo nivel
                int xpForNextLevel = Math.max(0, (pokemon.getExperienceToLevelUp() - pokemon.getExperience()));
                int calculateXpForNextLevel = Math.min(xpForNextLevel, event.getExperience());
                if (calculateXpForNextLevel == 0) {
                    event.setCanceled(true);
                }
                event.setExperience(calculateXpForNextLevel);
            }
        }
    }

    @SubscribeEvent
    public void onLevelUpEvent(LevelUpEvent.Pre event) {
        if (CapabilitiesRegistry.getPlayerRPGCapability(event.getPlayer()) instanceof PlayerRPGCapability data) {
            int playerLevel = data.getLevel();
            if (event.getAfterLevel() > playerLevel) {
                event.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void onSuccessCapture(CaptureEvent.SuccessfulCapture event) {
        if (Config.ENABLE_CAPTURE_RESTRICTIONS.get()) {
            ServerPlayer player = event.getPlayer();
            gainXpFromCapturedPokemon(player, event.getPokemon());
            try {
                MasteryManager.addCaptureXp(player, event.getPokemon());
            } catch (Exception e) {
                System.err.println("Error adding mastery XP from capture: " + e.getMessage()); // (important-comment)
                e.printStackTrace();
            }
        }
    }

    private void gainXpFromCapturedPokemon(ServerPlayer player, Pokemon pokemon) {
        int level = pokemon.getPokemonLevel();
        int catchRate = pokemon.getSpecies().getDefaultForm().getCatchRate();

        // Base XP value
        int base = 5;
        // Rarity factor, capped at a maximum value
        double rarityFactor = Math.min(10.0, 255.0 / catchRate);
        // Calculated XP gain
        int xpGain = (int) Math.round(level * base * rarityFactor);

        // Adds XP to the Player RPG system
        RPGExperienceManager.addExperience(player, xpGain, ExperienceSource.POKEMON_CAPTURE);
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
                } else {
                    try {
                        double masteryBonus = MasteryManager.getMasteryBonus(data, event.getPokemon());
                        if (masteryBonus > 0) {
                            int catchRate = event.getCaptureValues().getCatchRate();
                            int bonusCatchRate = (int) (catchRate * (1.0 + masteryBonus / 100.0));

                            Pokemon faintedPokemon = event.getPokemon();
                            String pokemonType = String.valueOf(MasteryManager.getPokemonTypes(faintedPokemon).getFirst());

                            player.sendSystemMessage(Component.literal(
                                    "§aBônus de §b" + (int) masteryBonus + "% §ana taxa de captura por Maestria do tipo §e"
                                            + MasteryManager.getTypeDisplayName(pokemonType) + "§a!"
                            ));

                            event.getCaptureValues().setCatchRate(bonusCatchRate);
                        }
                    } catch (Exception e) {
                        System.err.println("Error calculating mastery bonus for capture: " + e.getMessage()); // (important-comment)
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onBattleStart(BattleStartedEvent.Pre event) {
        limitTempPokemonLevel(event.getTeamOne());
        limitTempPokemonLevel(event.getTeamTwo());

        recalculateStatsBasedOnPlayerLevel(event.getTeamOne());
        recalculateStatsBasedOnPlayerLevel(event.getTeamTwo());
    }

    @SubscribeEvent
    public void onAttackEventUse(AttackEvent.Use event) {
        log.info(event.user.getStats().getAttack() + " AQUI PORRA " + event.user.getDisplayName().getString());
    }

    private void limitTempPokemonLevel(BattleParticipant[] team) {
        for (var participant : team) {
            if (participant instanceof PlayerParticipant playerPart) {
                ServerPlayer player = (ServerPlayer) playerPart.getEntity();

                PlayerRPGCapability rpg = CapabilitiesRegistry.getPlayerRPGCapability(player);

                if (rpg != null) {
                    int playerLevel = rpg.getLevel();
                    for (PixelmonWrapper pw : playerPart.allPokemon) {
                        if (pw == null) continue;
                        if (pw.getPokemonLevel() > playerLevel) {
                            pw.setTempLevel(playerLevel);
                        }
                    }
                }
            }
        }
    }

    public void recalculateStatsBasedOnPlayerLevel(BattleParticipant[] team) {
        for (var participant : team) {
            if (participant instanceof PlayerParticipant playerPart) {
                ServerPlayer player = (ServerPlayer) playerPart.getEntity();

                PlayerRPGCapability rpg = CapabilitiesRegistry.getPlayerRPGCapability(player);

                if (rpg != null) {
                    for (PixelmonWrapper pw : playerPart.allPokemon) {
                        double masteryBonus = MasteryManager.getMasteryBonus(rpg, pw) / 100;
                        double multiplier = 1.0 + masteryBonus;

                        log.info("Aplicando bonus de maestria de " + masteryBonus + "% para " + pw.getDisplayName().getString());

                        MasteryStatusBase masteryStatusBase = new MasteryStatusBase(multiplier);
                        pw.addStatus(masteryStatusBase, pw);
                    }
                }
            }
        }
    }

}
