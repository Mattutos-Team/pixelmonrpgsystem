package com.mattutos.pixelmonrpgsystem.events;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.cache.BattleLevelCache;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.data.OriginalPokemonData;
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

/**
 * Event handler for the Pixelmon RPG System that manages battle level capping mechanics.
 * 
 * This system ensures that during battles, Pokémon cannot exceed their trainer's level.
 * When a battle starts, any Pokémon with a level higher than the player's level is
 * temporarily capped to match the player's level. All stats are recalculated accordingly,
 * and the UI is updated to reflect the temporary changes.
 * 
 * During the battle, level-capped Pokémon are prevented from gaining experience to
 * maintain balance. When the battle ends, all Pokémon are restored to their original
 * levels, stats, and experience values.
 * 
 * Key Features:
 * - Temporary level capping during battles
 * - Stat recalculation for capped levels
 * - UI synchronization to show correct levels
 * - Experience gain prevention for capped Pokémon
 * - Complete restoration after battle ends
 */
public class PixelmonRPGSystemEventHandler {

    private static final Logger log = LoggerFactory.getLogger(PixelmonRPGSystemEventHandler.class);

    /**
     * Handles experience gain events to prevent XP gain for level-capped Pokémon.
     * 
     * This method is called whenever a Pokémon would gain experience. If the Pokémon
     * was level-capped during the current battle (indicated by its presence in the
     * BattleLevelCache), the experience gain is cancelled and the player is notified.
     * 
     * For non-capped Pokémon, normal experience processing continues, including
     * player XP gain with the configured multiplier.
     * 
     * @param event The ExperienceGainEvent containing the Pokémon and experience amount
     */
    @SubscribeEvent
    public void onPokemonGainExperience(ExperienceGainEvent event) {
        if (event.pokemon.getOwnerPlayer() == null) {
            return;
        }
        
        Player player = event.pokemon.getOwnerPlayer();
        UUID pokemonId = event.pokemon.getUUID();
        
        if (BattleLevelCache.wasLevelCapped(pokemonId)) {
            event.setCanceled(true);
            
            if (player instanceof ServerPlayer serverPlayer) {
                player.sendSystemMessage(Component.literal(
                    "§e" + event.pokemon.getDisplayName() + " não ganhou experiência porque estava limitado pelo seu nível de jogador durante a batalha."
                ));
            }
            
            log.debug("Blocked experience gain for level-capped Pokémon: {} (UUID: {})", 
                     event.pokemon.getDisplayName(), pokemonId);
            return;
        }
        
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

    /**
     * Handles Pokémon capture events to apply level-based capture restrictions.
     * 
     * When capture restrictions are enabled, this method reduces the capture rate
     * for Pokémon that are higher level than the player, making them more difficult
     * to catch and maintaining game balance.
     * 
     * @param event The CaptureEvent.StartCapture containing capture details
     */
    @SubscribeEvent
    public void onPokemonCapture(CaptureEvent.StartCapture event) {
        if (!Config.ENABLE_CAPTURE_RESTRICTIONS.get()) {
            return;
        }
        
        Player player = event.getPlayer();
        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        
        if (data != null) {
            int pokemonLevel = event.getPokemon().getPokemonLevel();
            int playerLevel = data.getLevel();

            if (pokemonLevel > playerLevel) {
                player.sendSystemMessage(Component.literal(
                        "§cEste pokémon é de nível " + pokemonLevel + ", mas você é nível " + playerLevel + ". A captura será mais difícil!"
                ));

                int catchRate = event.getCaptureValues().getCatchRate();
                event.getCaptureValues().setCatchRate((int) (catchRate * 0.01));
                
                log.debug("Applied capture restriction: Pokemon level {} vs Player level {}", pokemonLevel, playerLevel);
            }
        }
    }

    /**
     * Handles battle end events to restore Pokémon to their original state.
     * 
     * This method is called when any battle ends. It performs the following actions:
     * 1. Notifies players of their current level (if battle restrictions are enabled)
     * 2. Restores all level-capped Pokémon to their original levels, stats, and experience
     * 3. Updates the UI to reflect the restored data
     * 4. Cleans up the battle cache
     * 
     * This ensures that temporary level capping during battles doesn't permanently
     * affect Pokémon data.
     * 
     * @param event The BattleEndEvent containing battle and player information
     */
    @SubscribeEvent
    public void onBattleEnd(BattleEndEvent event) {
        if (Config.ENABLE_BATTLE_RESTRICTIONS.get()) {
            event.getPlayers().forEach(player -> {
                PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
                if (data != null) {
                    int playerLevel = data.getLevel();
                    player.sendSystemMessage(Component.literal(
                            "§eBatalha finalizada! Seu nível atual: " + playerLevel
                    ));
                }
            });
        }

        restorePokemonAfterBattle(event);
        
        log.info("Battle ended. Restored {} Pokémon to original levels.", BattleLevelCache.size());
    }

    /**
     * Handles battle start events to apply level capping and stat modifications.
     * 
     * This method is called when a battle begins (after it has been registered).
     * It performs the following operations for both teams:
     * 
     * 1. Applies RPG stat multipliers based on player level
     * 2. Caps Pokémon levels to match player levels (if restrictions enabled)
     * 3. Recalculates stats for capped Pokémon
     * 4. Updates the battle UI to show temporary changes
     * 5. Stores original data for restoration after battle
     * 
     * The level capping ensures fair battles where Pokémon cannot exceed their
     * trainer's level, maintaining game balance in the RPG system.
     * 
     * @param event The BattleStartedEvent.Post containing team information
     */
    @SubscribeEvent
    public void onBattleStart(BattleStartedEvent.Post event) {
        log.info("Battle started. Applying RPG system modifications...");
        
        applyRPGStatMultipliers(event.getTeamOne());
        applyRPGStatMultipliers(event.getTeamTwo());

        if (Config.ENABLE_BATTLE_RESTRICTIONS.get()) {
            int cappedPokemonTeamOne = applyLevelCapping(event.getTeamOne());
            int cappedPokemonTeamTwo = applyLevelCapping(event.getTeamTwo());
            
            log.info("Applied level capping: Team 1: {} Pokémon, Team 2: {} Pokémon", 
                    cappedPokemonTeamOne, cappedPokemonTeamTwo);
        }
    }

    /**
     * Applies RPG stat multipliers to Pokémon based on their trainer's level.
     * 
     * This method enhances Pokémon stats based on the player's RPG level, providing
     * a progression system where higher-level trainers have stronger Pokémon.
     * 
     * Multiplier calculation:
     * - Base multiplier: 1.0 (no change)
     * - Bonus: +5% per 10 player levels
     * - Maximum multiplier: 1.5 (50% stat increase)
     * 
     * All core stats are affected: HP, Attack, Defense, Special Attack, Special Defense, Speed
     * 
     * @param team Array of battle participants to process
     */
    private void applyRPGStatMultipliers(BattleParticipant[] team) {
        for (BattleParticipant participant : team) {
            if (!(participant instanceof PlayerParticipant playerParticipant)) {
                continue;
            }
            
            ServerPlayer player = (ServerPlayer) playerParticipant.getEntity();
            PlayerRPGCapability rpgData = CapabilitiesRegistry.getPlayerRPGCapability(player);
            
            if (rpgData == null) {
                continue;
            }
            
            int playerLevel = rpgData.getLevel();
            double multiplier = 1.0 + (playerLevel / 10.0) * 0.05;
            if (multiplier > 1.5) {
                multiplier = 1.5; // Cap at 50% bonus
            }
            
            log.debug("Applying RPG stat multiplier {} for player level {}", multiplier, playerLevel);
            
            for (Pokemon pokemon : playerParticipant.getStorage().getTeam()) {
                if (pokemon == null) {
                    continue;
                }
                
                PermanentStats stats = pokemon.getStats();
                
                stats.setAttack((int) (stats.getAttack() * multiplier));
                stats.setDefense((int) (stats.getDefense() * multiplier));
                stats.setSpecialAttack((int) (stats.getSpecialAttack() * multiplier));
                stats.setSpecialDefense((int) (stats.getSpecialDefense() * multiplier));
                stats.setSpeed((int) (stats.getSpeed() * multiplier));
                stats.setHP((int) (stats.getHP() * multiplier));
                
                log.debug("Applied RPG multiplier to {}: HP={}, ATK={}, DEF={}", 
                         pokemon.getDisplayName(), stats.getHP(), stats.getAttack(), stats.getDefense());
            }
        }
    }

    /**
     * Applies level capping to Pokémon that exceed their trainer's level.
     * 
     * This method processes each team and caps any Pokémon whose level is higher
     * than their trainer's level. The process involves:
     * 
     * 1. Storing original data (level, experience, stats, etc.) in the cache
     * 2. Setting the Pokémon's level to match the player's level
     * 3. Recalculating all stats based on the new level
     * 4. Updating the UI to show the temporary changes
     * 5. Synchronizing battle controller data
     * 
     * The original data is preserved for complete restoration after the battle ends.
     * 
     * @param team Array of battle participants to process
     * @return Number of Pokémon that were level-capped
     */
    private int applyLevelCapping(BattleParticipant[] team) {
        int cappedCount = 0;
        
        for (BattleParticipant participant : team) {
            if (!(participant instanceof PlayerParticipant playerParticipant)) {
                continue;
            }

            ServerPlayer player = (ServerPlayer) playerParticipant.getEntity();
            PlayerRPGCapability rpgData = CapabilitiesRegistry.getPlayerRPGCapability(player);
            
            if (rpgData == null) {
                continue;
            }
            
            int playerLevel = rpgData.getLevel();
            log.debug("Processing team for player {} (level {})", player.getName().getString(), playerLevel);

            for (Pokemon pokemon : playerParticipant.getStorage().getTeam()) {
                if (pokemon == null) {
                    continue;
                }

                int pokemonLevel = pokemon.getPokemonLevel();
                
                if (pokemonLevel > playerLevel) {
                    UUID pokemonId = pokemon.getUUID();
                    
                    OriginalPokemonData originalData = new OriginalPokemonData(
                        pokemonLevel,
                        pokemon.getExperience(),
                        pokemon.doesLevel(),
                        pokemon.getStats()
                    );
                    
                    BattleLevelCache.storeOriginalData(pokemonId, originalData);
                    
                    pokemon.setLevel(playerLevel);
                    pokemon.getStats().recalculateStats();
                    
                    log.info("Level-capped {} from level {} to level {} | New stats - HP: {}, ATK: {}", 
                            pokemon.getDisplayName(), pokemonLevel, playerLevel,
                            pokemon.getStats().getHP(), pokemon.getStats().getAttack());
                    
                    updatePokemonUI(pokemon, player);
                    updateBattleController(pokemon, pokemonId);
                    
                    cappedCount++;
                }
            }
        }
        
        return cappedCount;
    }
    
    /**
     * Updates the Pokémon UI to reflect level and stat changes.
     * 
     * This method ensures that the player's party UI shows the correct (temporary)
     * level and stats during battle. It marks the Pokémon as dirty to trigger
     * UI updates and sends the updated party data to the player.
     * 
     * @param pokemon The Pokémon whose UI needs updating
     * @param player The player who owns the Pokémon
     */
    private void updatePokemonUI(Pokemon pokemon, ServerPlayer player) {
        pokemon.markDirty(com.pixelmonmod.pixelmon.comm.EnumUpdateType.Stats);
        
        PlayerPartyStorage party = StorageProxy.getPartyNow(player);
        if (party != null) {
            party.sendCacheToPlayer(player);
        }
        
        log.debug("Updated UI for {} (player: {})", pokemon.getDisplayName(), player.getName().getString());
    }
    
    /**
     * Updates the battle controller with modified Pokémon data.
     * 
     * This method synchronizes the battle controller's data with the level-capped
     * Pokémon, ensuring that the battle mechanics use the correct (temporary) stats
     * and level during combat.
     * 
     * @param pokemon The Pokémon whose battle data needs updating
     * @param pokemonId The UUID of the Pokémon
     */
    private void updateBattleController(Pokemon pokemon, UUID pokemonId) {
        BattleController battleController = pokemon.getBattleController();
        if (battleController != null) {
            PixelmonWrapper pokemonWrapper = battleController.getPokemonFromUUID(pokemonId);
            if (pokemonWrapper != null) {
                battleController.modifyStats(pokemonWrapper);
                battleController.updateFormChange(pokemonWrapper);
                
                log.debug("Updated battle controller for {} (UUID: {})", pokemon.getDisplayName(), pokemonId);
            }
        }
    }

    /**
     * Restores all level-capped Pokémon to their original state after battle ends.
     * 
     * This method is responsible for completely restoring Pokémon that were
     * level-capped during the battle. The restoration process includes:
     * 
     * 1. Retrieving original data from the battle cache
     * 2. Restoring original level, experience, and leveling settings
     * 3. Recalculating stats based on the original level
     * 4. Updating the UI to show the restored data
     * 5. Synchronizing battle controller data
     * 6. Cleaning up the cache
     * 
     * This ensures that temporary battle modifications don't permanently affect
     * Pokémon data, maintaining the integrity of the player's collection.
     * 
     * @param event The BattleEndEvent containing battle and player information
     */
    private void restorePokemonAfterBattle(BattleEndEvent event) {
        BattleController battleController = event.getBattleController();
        int restoredCount = 0;
        
        for (Player player : event.getPlayers()) {
            if (!(player instanceof ServerPlayer serverPlayer)) {
                continue;
            }

            PlayerPartyStorage party = StorageProxy.getPartyNow(serverPlayer);
            if (party == null) {
                continue;
            }

            log.debug("Restoring Pokémon for player: {}", serverPlayer.getName().getString());

            for (Pokemon pokemon : party.getTeam()) {
                if (pokemon == null) {
                    continue;
                }
                
                UUID pokemonId = pokemon.getUUID();
                
                OriginalPokemonData originalData = BattleLevelCache.removeOriginalData(pokemonId);
                if (originalData == null) {
                    continue; // This Pokémon wasn't level-capped
                }
                
                int cappedLevel = pokemon.getPokemonLevel();
                
                pokemon.setLevel(originalData.getOriginalLevel());
                pokemon.setExperience(originalData.getOriginalExperience());
                pokemon.setDoesLevel(originalData.getOriginalDoesLevel());
                
                pokemon.getStats().recalculateStats();
                
                log.info("Restored {} from level {} back to level {} | Restored stats - HP: {}, ATK: {}", 
                        pokemon.getDisplayName(), cappedLevel, originalData.getOriginalLevel(),
                        pokemon.getStats().getHP(), pokemon.getStats().getAttack());
                
                pokemon.markDirty(
                    com.pixelmonmod.pixelmon.comm.EnumUpdateType.Stats,
                    com.pixelmonmod.pixelmon.comm.EnumUpdateType.Experience
                );
                
                party.sendCacheToPlayer(serverPlayer);
                
                if (battleController != null) {
                    PixelmonWrapper pokemonWrapper = battleController.getPokemonFromUUID(pokemonId);
                    if (pokemonWrapper != null) {
                        battleController.modifyStats(pokemonWrapper);
                        battleController.updateFormChange(pokemonWrapper);
                    }
                }
                
                restoredCount++;
            }
        }
        
        log.info("Battle restoration complete. Restored {} Pokémon to original levels.", restoredCount);
        
        if (BattleLevelCache.size() > 0) {
            log.warn("Battle cache not fully cleared. Remaining entries: {}", BattleLevelCache.size());
        }
    }
}
