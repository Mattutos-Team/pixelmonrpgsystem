package com.mattutos.pixelmonrpgsystem.client;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGData;

public class ClientPlayerRPGData {
    private static int level = 5;
    private static int experience = PlayerRPGData.getTotalExperienceToThisLevel(level);

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
        return PlayerRPGData.getExperienceForThisLevel(level + 1);
    }

    public static int getCurrentLevelExperience() {
        int previousLevelXP = PlayerRPGData.getTotalExperienceToThisLevel(level);
        return experience - previousLevelXP;
    }

    public static int getExperienceNeededForNextLevel() {
        return getExperienceForNextLevel() - getCurrentLevelExperience();
    }
}
