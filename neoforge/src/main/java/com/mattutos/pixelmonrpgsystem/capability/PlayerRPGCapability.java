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

    public void setLevel(int level) {
        data.setLevel(level);
        saveToEntity(entity);
    }

    public boolean canClaimDailyReward() {
        return data.canClaimDailyReward();
    }

    public void claimDailyReward() {
        data.claimDailyReward();
        saveToEntity(entity);
    }

    public long getLastDailyReward() {
        return data.getLastDailyReward();
    }

    public com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress getMastery(String type) {
        return data.getMastery(type);
    }

    public void addMasteryXp(String type, int xp) {
        data.addMasteryXp(type, xp);
        saveToEntity(entity);
    }

    public int getCurrentMasteryStage(String type) {
        return data.getMastery(type).getStage();
    }

    public java.util.Map<String, com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress> getAllMasteries() {
        return data.getAllMasteries();
    }

    public void setMastery(String type, String masteryLevel) {
        data.setMastery(type, masteryLevel);
        saveToEntity(entity);
    }

    public void resetAllMasteries() {
        data.resetAllMasteries();
        saveToEntity(entity);
    }

    public void saveToEntity(LivingEntity entity) {
        entity.setData(AttachmentsRegistry.PLAYER_RPG_DATA.get(), data);
    }


}
