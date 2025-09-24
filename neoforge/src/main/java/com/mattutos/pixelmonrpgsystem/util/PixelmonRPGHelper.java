package com.mattutos.pixelmonrpgsystem.util;

import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

import java.util.List;

public class PixelmonRPGHelper {

    public static ItemStack generatePixelmonSpriteItemStack(Pokemon pokemon) {
        ItemStack item = new ItemStack(ItemRegistration.PIXELMON_SPRITE.get());
        CompoundTag tag = new CompoundTag();

        tag.putString("form", pokemon.getFormName());
        tag.putByte("gender", (byte) pokemon.getGender().ordinal());
        tag.putShort("ndex", (short) pokemon.getDex());
        tag.putString("palette", pokemon.getPaletteName());

        item.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));

        return item;
    }

    public static List<TypeHelper> getPokemonTypesHelper(Pokemon pokemon) {
        return pokemon.getForm().getTypes().stream()
                .map(TypeHelper::new)
                .toList();
    }
}
