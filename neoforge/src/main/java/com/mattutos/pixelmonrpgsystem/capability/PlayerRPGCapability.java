package com.mattutos.pixelmonrpgsystem.capability;

import com.mattutos.pixelmonrpgsystem.registry.AttachmentsRegistry;
import net.minecraft.world.entity.LivingEntity;

public class PlayerRPGCapability {

    PlayerRPGData data;
    LivingEntity entity;

    public PlayerRPGCapability(LivingEntity entity) {
        this.entity = entity;
        this.data = entity.getData(AttachmentsRegistry.PLAYER_RPG_DATA.get());
    }

    public void addExperience(int xp) {
        data.addExperience(xp);
        saveToEntity(entity);
    }

    public int getExperience() {
        return data.getExperience();
    }

    public int getLevel() {
        return data.getLevel();
    }

    public int getExperienceForNextLevel() {
        return data.getExperienceForNextLevel();
    }

    public int getCurrentLevelExperience() {
        return data.getCurrentLevelExperience();
    }

    public int getExperienceNeededForNextLevel() {
        return data.getExperienceNeededForNextLevel();
    }

    public void saveToEntity(LivingEntity entity) {
        entity.setData(AttachmentsRegistry.PLAYER_RPG_DATA.get(), data);
    }

}
