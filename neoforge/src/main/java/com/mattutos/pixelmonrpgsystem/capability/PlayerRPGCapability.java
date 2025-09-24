package com.mattutos.pixelmonrpgsystem.capability;

import com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress;
import com.mattutos.pixelmonrpgsystem.registry.AttachmentsRegistry;
import com.mattutos.pixelmonrpgsystem.util.TypeHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;

public class PlayerRPGCapability {

    PlayerRPGData data;
    LivingEntity entity;

    public PlayerRPGCapability(LivingEntity entity) {
        this.entity = entity;
        this.data = entity.getData(AttachmentsRegistry.PLAYER_RPG_DATA.get());
    }

    public void addExperience(int xp) {
        data.addExperience(xp);
        saveToEntity();
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
        saveToEntity();
    }

    public boolean canClaimDailyReward() {
        return data.canClaimDailyReward();
    }

    public void claimDailyReward() {
        data.claimDailyReward();
        saveToEntity();
    }

    public long getLastDailyReward() {
        return data.getLastDailyReward();
    }

    public MasteryProgress getMastery(TypeHelper type) {
        return data.getMastery(type);
    }

    public void addMasteryXp(TypeHelper type, int xp) {
        data.addMasteryXp(type, xp);
        saveToEntity();
    }

    public int getCurrentMasteryStage(TypeHelper type) {
        return data.getMastery(type).getStage();
    }

    public Map<TypeHelper, MasteryProgress> getAllMasteries() {
        return data.getAllMasteries();
    }

    public void setMastery(TypeHelper type, String masteryLevel) {
        data.setMastery(type, masteryLevel);
        saveToEntity();
    }

    public void resetAllMasteries() {
        data.resetAllMasteries();
        saveToEntity();
    }

    public void saveToEntity() {
        entity.setData(AttachmentsRegistry.PLAYER_RPG_DATA.get(), data);
    }

    public void copyTo(ServerPlayer other) {
        PlayerRPGData clone = this.data.clone();

        PlayerRPGCapability playerRPGCapability = new PlayerRPGCapability(other);
        playerRPGCapability.data = clone;
        playerRPGCapability.saveToEntity();
    }

}
