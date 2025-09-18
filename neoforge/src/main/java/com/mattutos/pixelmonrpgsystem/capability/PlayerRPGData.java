package com.mattutos.pixelmonrpgsystem.capability;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress;
import com.pixelmonmod.pixelmon.api.pokemon.ExperienceGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

import java.util.HashMap;
import java.util.Map;

public class PlayerRPGData implements INBTSerializable<CompoundTag> {
    private static final ExperienceGroup experienceGroup = ExperienceGroup.FAST;
    private int level = 5;
    private int experience = PlayerRPGData.getTotalExperienceToThisLevel(level);
    private long lastDailyReward = 0;
    private Map<String, MasteryProgress> masteries = new HashMap<>();

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
        for (Map.Entry<String, MasteryProgress> entry : masteries.entrySet()) {
            masteriesTag.put(entry.getKey(), entry.getValue().serializeNBT(provider));
        }
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
                masteries.put(key, progress);
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

    public MasteryProgress getMastery(String type) {
        return masteries.computeIfAbsent(type, k -> new MasteryProgress());
    }

    public void addMasteryXp(String type, int xp) {
        if (level >= 30) {
            getMastery(type).addXp(xp);
        }
    }

    public Map<String, MasteryProgress> getAllMasteries() {
        return masteries;
    }
}
