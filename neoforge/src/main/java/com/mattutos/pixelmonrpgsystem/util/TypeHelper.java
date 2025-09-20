package com.mattutos.pixelmonrpgsystem.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record TypeHelper(String name, ResourceLocation texture) {

    public String translationKey() {
        return "type." + name;
    }

    public MutableComponent translatedComponent() {
        return Component.translatable(translationKey());
    }

}
