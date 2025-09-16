package com.mattutos.pixelmonrpgsystem.client;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGData;

public class ClientPlayerRPGData {
    private static int level = 5;
    private static int experience = PlayerRPGData.getTotalExperienceForThisLevel(level);

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
        return PlayerRPGData.getTotalExperienceForThisLevel(level);
    }

    public static int getCurrentLevelExperience() {
        int previousLevelXP = PlayerRPGData.getTotalExperienceForThisLevel(level - 1);
        return experience - previousLevelXP;
    }

    public static int getExperienceNeededForNextLevel() {
        int nextLevelXP = PlayerRPGData.getTotalExperienceForThisLevel(level);
        return nextLevelXP - experience;
    }
}
