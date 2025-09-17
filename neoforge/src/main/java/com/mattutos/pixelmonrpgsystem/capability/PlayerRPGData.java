package com.mattutos.pixelmonrpgsystem.capability;

import com.mattutos.pixelmonrpgsystem.Config;
import com.pixelmonmod.pixelmon.api.pokemon.ExperienceGroup;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerRPGData implements INBTSerializable<CompoundTag> {
    private static final ExperienceGroup experienceGroup = ExperienceGroup.FAST;
    private int level = 5;
    private int experience = PlayerRPGData.getTotalExperienceToThisLevel(level);

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
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        experience = tag.getInt("experience");
        level = tag.getInt("level");
    }

}
