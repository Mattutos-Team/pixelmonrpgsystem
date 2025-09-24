package com.mattutos.pixelmonrpgsystem.capability;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress;
import com.mattutos.pixelmonrpgsystem.util.TypeHelper;
import com.pixelmonmod.pixelmon.api.pokemon.ExperienceGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PlayerRPGData implements INBTSerializable<CompoundTag>, Cloneable {
    private static final ExperienceGroup experienceGroup = ExperienceGroup.FAST;
    private int level = 5;
    private int experience = PlayerRPGData.getTotalExperienceToThisLevel(level);
    private long lastDailyReward = 0;
    private final Map<TypeHelper, MasteryProgress> masteries = new HashMap<>();

    public static int getExperienceForThisLevel(int level) {
//        return (level * level * 100) + 100;
        return experienceGroup.getExpForLevel(level);
    }

    public static int getTotalExperienceToThisLevel(int level) {
//        return (level * level * 100) + 100;
        return experienceGroup.getExpToLevel(level);
    }

    public int getTotalExperienceToTheNextLevel() {
        return getTotalExperienceToThisLevel(level + 1);
    }

    public void addExperience(int xp) {
        this.experience += xp;
        updateLevel();
    }

    private void updateLevel() {
        int maxLevel = Config.MAX_PLAYER_LEVEL.getAsInt();
        while (level < maxLevel) {
            int requiredXP = getTotalExperienceToTheNextLevel();
            if (experience >= requiredXP) {
                level++;
            } else {
                break;
            }
        }
    }

    public int getExperience() {
        return experience;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
        this.experience = getTotalExperienceToThisLevel(level);
    }

    public int getExperienceForNextLevel() {
        return getExperienceForThisLevel(level);
    }

    public int getCurrentLevelExperience() {
        int previousLevelXP = getTotalExperienceToThisLevel(level);
        return experience - previousLevelXP;
    }

    public int getExperienceNeededForNextLevel() {
        int nextLevelXP = this.getExperienceForNextLevel();
        return nextLevelXP - experience;
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("experience", experience);
        tag.putInt("level", level);
        tag.putLong("lastDailyReward", lastDailyReward);

        CompoundTag masteriesTag = new CompoundTag();
        masteries.forEach((key, value) -> {
            masteriesTag.put(key.name(), value.serializeNBT(provider));
        });
        tag.put("masteries", masteriesTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        experience = tag.getInt("experience");
        level = tag.getInt("level");
        lastDailyReward = tag.getLong("lastDailyReward");

        masteries.clear();
        if (tag.contains("masteries")) {
            CompoundTag masteriesTag = tag.getCompound("masteries");
            for (String key : masteriesTag.getAllKeys()) {
                MasteryProgress progress = new MasteryProgress();
                progress.deserializeNBT(provider, masteriesTag.getCompound(key));
                masteries.put(TypeHelper.of(key), progress);
            }
        }
    }

    public boolean canClaimDailyReward() {
        long currentTime = System.currentTimeMillis();
        long currentDay = currentTime / (24 * 60 * 60 * 1000);
        long lastRewardDay = lastDailyReward / (24 * 60 * 60 * 1000);
        return currentDay > lastRewardDay;
    }

    public void claimDailyReward() {
        this.lastDailyReward = System.currentTimeMillis();
    }

    public long getLastDailyReward() {
        return lastDailyReward;
    }

    public MasteryProgress getMastery(TypeHelper type) {
        return masteries.computeIfAbsent(type, k -> new MasteryProgress());
    }

    public void addMasteryXp(TypeHelper type, int xp) {
        int currentXp = getMastery(type).getXp();
        int currentLevel = getMastery(type).getStage();

        if (level >= 30 && (currentLevel < 3 || (currentLevel == 3 && currentXp < 4000))) {
            getMastery(type).addXp(xp);
        }
    }

    public Map<TypeHelper, MasteryProgress> getAllMasteries() {
        return masteries;
    }

    public void setMastery(TypeHelper type, String masteryLevel) {
        MasteryProgress progress = getMastery(type);
        switch (masteryLevel.toLowerCase()) {
            case "novato" -> progress.setStageAndXp(0, 0);
            case "aspirante" -> progress.setStageAndXp(1, 1000);
            case "experiente" -> progress.setStageAndXp(2, 1800);
            case "mestre" -> progress.setStageAndXp(3, 4000);
            default -> throw new IllegalArgumentException("Invalid mastery level: " + masteryLevel);
        }
        masteries.put(type, progress);
    }

    public void resetAllMasteries() {
        masteries.clear();
    }

    @Override
    public PlayerRPGData clone() {
        PlayerRPGData clone = new PlayerRPGData();

        clone.experience = this.experience;
        clone.level = this.level;
        clone.lastDailyReward = this.lastDailyReward;

        return clone;
    }
}
