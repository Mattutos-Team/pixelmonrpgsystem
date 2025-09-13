package com.mattutos.pixelmonrpgsystemaddon.capability;

import com.mattutos.pixelmonrpgsystemaddon.registry.AttachmentsRegistry;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class PlayerRPGData implements INBTSerializable<CompoundTag> {
    private int experience = 0;
    private int level = 1;
    
    public void addExperience(int xp, Player player) {
        this.experience += xp;
        updateLevel();
        saveToEntity(player);
    }
    
    private void updateLevel() {
        int maxLevel = 100;
        while (level < maxLevel) {
            int requiredXP = level * level * 100;
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
    
    public int getExperienceForNextLevel() {
        return level * level * 100;
    }
    
    public int getCurrentLevelExperience() {
        int previousLevelXP = (level - 1) * (level - 1) * 100;
        return experience - previousLevelXP;
    }
    
    public int getExperienceNeededForNextLevel() {
        int nextLevelXP = level * level * 100;
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
    
    public void saveToEntity(Player player) {
        player.setData(AttachmentsRegistry.PLAYER_RPG_DATA.get(), this);
    }
    
    public static PlayerRPGData get(Player player) {
        return player.getData(AttachmentsRegistry.PLAYER_RPG_DATA.get());
    }
}
