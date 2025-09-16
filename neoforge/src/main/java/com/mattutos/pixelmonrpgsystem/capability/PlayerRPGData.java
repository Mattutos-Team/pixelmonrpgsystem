package com.mattutos.pixelmonrpgsystem.capability;

import com.mattutos.pixelmonrpgsystem.Config;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerRPGData implements INBTSerializable<CompoundTag> {
    private int level = 5;
    private int experience = PlayerRPGData.getTotalExperienceForThisLevel(level);

    public static int getTotalExperienceForThisLevel(int level) {
        return (level * level * 100) + 100;
    }

    public void addExperience(int xp) {
        this.experience += xp;
        updateLevel();
    }

    private void updateLevel() {
        int maxLevel = Config.MAX_PLAYER_LEVEL.getAsInt();
        while (level < maxLevel) {
            int requiredXP = getTotalExperienceForThisLevel(level);
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
        this.experience = getTotalExperienceForThisLevel(level - 1);
    }

    public int getExperienceForNextLevel() {
        return getTotalExperienceForThisLevel(level);
    }

    public int getCurrentLevelExperience() {
        int previousLevelXP = getTotalExperienceForThisLevel(level - 1);
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
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        experience = tag.getInt("experience");
        level = tag.getInt("level");
    }

}
