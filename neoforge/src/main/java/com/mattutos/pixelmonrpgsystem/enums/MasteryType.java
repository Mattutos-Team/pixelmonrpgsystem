package com.mattutos.pixelmonrpgsystem.enums;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum MasteryType {

    NOVICE("novice"),
    ASPIRANT("aspirant"),
    EXPERT("expert"),
    MASTER("master");

    private final String key;

    MasteryType(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public MutableComponent translatedComponent() {
        return Component.translatable(PixelmonRPGSystem.MODID + ".mastery.type." + this.key);
    }

}
