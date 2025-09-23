package com.mattutos.pixelmonrpgsystem.mastery;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.common.util.INBTSerializable;

public class MasteryProgress implements INBTSerializable<CompoundTag> {
    int stage = 0; // 0 = Novato, 1 = Aspirante, 2 = Experiente, 3 = Mestre
    int xp = 0;

    public MasteryProgress() {}

    public MasteryProgress(int stage, int xp) {
        this.stage = stage;
        this.xp = xp;
    }

    public int getStage() {
        return stage;
    }

    public int getXp() {
        return xp;
    }

    public void addXp(int amount) {
        this.xp += amount;
        updateStage();
    }

    private void updateStage() {
        if (xp >= 4000 && stage < 3) {
            stage = 3; // Mestre
        } else if (xp >= 1800 && stage < 2) {
            stage = 2; // Experiente
        } else if (xp >= 1000 && stage < 1) {
            stage = 1; // Aspirante
        }
    }

    public String getStageName() {
        return switch (stage) {
            case 0 -> "Novato";
            case 1 -> "Aspirante";
            case 2 -> "Experiente";
            case 3 -> "Mestre";
            default -> "Desconhecido";
        };
    }

    public void setStageAndXp(int newStage, int newXp) {
        this.stage = newStage;
        this.xp = newXp;
    }

    public double getBonusPercentage() {
        return switch (stage) {
            case 0 -> 0.0; // Novato - sem bônus
            case 1 -> 5.0; // Aspirante - +5%
            case 2 -> 7.5; // Experiente - +7.5%
            case 3 -> xp >= 4000 ? 10.0 : 7.5; // Mestre - +10% apenas com 4000 XP exatos
            default -> 0.0;
        };
    }

    public int getXpForNextStage() {
        return switch (stage) {
            case 0 -> 1000;
            case 1 -> 1800;
            case 2 -> 4000;
            case 3 -> 4000; // Máximo
            default -> 0;
        };
    }

    @Override
    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("stage", stage);
        tag.putInt("xp", xp);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag tag) {
        stage = tag.getInt("stage");
        xp = tag.getInt("xp");
    }
}
