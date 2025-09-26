package com.mattutos.pixelmonrpgsystem.enums;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public enum MasteryType {

    NOVICE("novice", ChatFormatting.GRAY),
    ASPIRANT("aspirant", ChatFormatting.YELLOW),
    EXPERT("expert", ChatFormatting.AQUA),
    MASTER("master", ChatFormatting.LIGHT_PURPLE);

    private final String key;
    private final ChatFormatting color;

    MasteryType(String key, ChatFormatting color) {
        this.key = key;
        this.color = color;
    }

    public ChatFormatting getColor() {
        return color;
    }

    public String getKey() {
        return key;
    }

    public MutableComponent translatedComponent() {
        return Component.translatable(PixelmonRPGSystem.MODID + ".mastery.type." + this.key);
    }

}
