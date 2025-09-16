package com.mattutos.pixelmonrpgsystem.cache;

import com.mattutos.pixelmonrpgsystem.data.OriginalPokemonData;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Cache system for storing original Pokémon data during battles.
 * 
 * When a battle starts and Pokémon levels are temporarily capped to match
 * the player's level, this cache stores all the original data needed to
 * restore the Pokémon to their pre-battle state when the battle ends.
 * 
 * Thread-safe implementation using ConcurrentHashMap to handle concurrent
 * battle scenarios safely.
 */
public final class BattleLevelCache {
    
    /**
     * Maps Pokémon UUID to their original data before level capping.
     * Key: Pokémon UUID
     * Value: OriginalPokemonData containing level, experience, stats, etc.
     */
    public static final Map<UUID, OriginalPokemonData> originalPokemonData = new ConcurrentHashMap<>();
    
    /**
     * Stores original data for a Pokémon that is about to be level-capped.
     * 
     * @param pokemonId The UUID of the Pokémon
     * @param data The original data to store
     */
    public static void storeOriginalData(UUID pokemonId, OriginalPokemonData data) {
        originalPokemonData.put(pokemonId, data);
    }
    
    /**
     * Retrieves and removes original data for a Pokémon after battle ends.
     * 
     * @param pokemonId The UUID of the Pokémon
     * @return The original data, or null if not found
     */
    public static OriginalPokemonData removeOriginalData(UUID pokemonId) {
        return originalPokemonData.remove(pokemonId);
    }
    
    /**
     * Checks if a Pokémon was level-capped during battle.
     * 
     * @param pokemonId The UUID of the Pokémon
     * @return true if the Pokémon was level-capped, false otherwise
     */
    public static boolean wasLevelCapped(UUID pokemonId) {
        return originalPokemonData.containsKey(pokemonId);
    }
    
    /**
     * Clears all cached data. Should be called if needed for cleanup.
     */
    public static void clearAll() {
        originalPokemonData.clear();
    }
    
    /**
     * Gets the current size of the cache for debugging purposes.
     * 
     * @return Number of Pokémon currently in the cache
     */
    public static int size() {
        return originalPokemonData.size();
    }
}
