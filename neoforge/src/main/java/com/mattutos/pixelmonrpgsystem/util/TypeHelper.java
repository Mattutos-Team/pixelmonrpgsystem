package com.mattutos.pixelmonrpgsystem.util;

import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public record TypeHelper(String name, Type type) {

    public String translationKey() {
        return "type." + name;
    }

    public MutableComponent translatedComponent() {
        return Component.translatable(translationKey());
    }

    public ResourceLocation location() {
        return type.icon().getTexture();
    }

}
