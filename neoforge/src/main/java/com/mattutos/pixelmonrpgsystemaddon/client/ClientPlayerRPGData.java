package com.mattutos.pixelmonrpgsystemaddon.client;

public class ClientPlayerRPGData {
    private static int experience = 0;
    private static int level = 1;
    
    public static void setPlayerData(int exp, int lvl) {
        experience = exp;
        level = lvl;
    }
    
    public static int getExperience() {
        return experience;
    }
    
    public static int getLevel() {
        return level;
    }
    
    public static int getExperienceForNextLevel() {
        return level * level * 100;
    }
    
    public static int getCurrentLevelExperience() {
        int previousLevelXP = (level - 1) * (level - 1) * 100;
        return experience - previousLevelXP;
    }
    
    public static int getExperienceNeededForNextLevel() {
        int nextLevelXP = level * level * 100;
        return nextLevelXP - experience;
    }
}
