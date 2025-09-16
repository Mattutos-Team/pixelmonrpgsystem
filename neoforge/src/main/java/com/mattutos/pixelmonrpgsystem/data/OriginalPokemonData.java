package com.mattutos.pixelmonrpgsystem.data;

import com.pixelmonmod.pixelmon.api.pokemon.stats.PermanentStats;

/**
 * Data structure to store original Pokémon data before level capping during battles.
 * This allows for complete restoration after the battle ends.
 * 
 * When a Pokémon's level exceeds the player's level during battle, we temporarily
 * reduce the Pokémon's level to match the player's level. This class stores all
 * the original data needed to restore the Pokémon to its pre-battle state.
 */
public class OriginalPokemonData {
    private final int originalLevel;
    private final int originalExperience;
    private final boolean originalDoesLevel;
    
    private final int originalHP;
    private final int originalAttack;
    private final int originalDefense;
    private final int originalSpecialAttack;
    private final int originalSpecialDefense;
    private final int originalSpeed;
    
    /**
     * Constructor to capture the current state of a Pokémon before level capping.
     * 
     * @param level The Pokémon's current level
     * @param experience The Pokémon's current experience points
     * @param doesLevel Whether the Pokémon can level up
     * @param stats The Pokémon's current permanent stats
     */
    public OriginalPokemonData(int level, int experience, boolean doesLevel, PermanentStats stats) {
        this.originalLevel = level;
        this.originalExperience = experience;
        this.originalDoesLevel = doesLevel;
        
        this.originalHP = stats.getHP();
        this.originalAttack = stats.getAttack();
        this.originalDefense = stats.getDefense();
        this.originalSpecialAttack = stats.getSpecialAttack();
        this.originalSpecialDefense = stats.getSpecialDefense();
        this.originalSpeed = stats.getSpeed();
    }
    
    public int getOriginalLevel() { return originalLevel; }
    public int getOriginalExperience() { return originalExperience; }
    public boolean getOriginalDoesLevel() { return originalDoesLevel; }
    public int getOriginalHP() { return originalHP; }
    public int getOriginalAttack() { return originalAttack; }
    public int getOriginalDefense() { return originalDefense; }
    public int getOriginalSpecialAttack() { return originalSpecialAttack; }
    public int getOriginalSpecialDefense() { return originalSpecialDefense; }
    public int getOriginalSpeed() { return originalSpeed; }
    
    @Override
    public String toString() {
        return String.format("OriginalPokemonData{level=%d, exp=%d, doesLevel=%b, HP=%d, ATK=%d, DEF=%d, SPA=%d, SPD=%d, SPE=%d}",
                originalLevel, originalExperience, originalDoesLevel, originalHP, originalAttack, originalDefense,
                originalSpecialAttack, originalSpecialDefense, originalSpeed);
    }
}
