package com.mattutos.pixelmonrpgsystem.util;

import com.pixelmonmod.pixelmon.Pixelmon;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import com.pixelmonmod.pixelmon.init.registry.ItemRegistration;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PixelmonRPGHelper {

    private static final Logger log = LoggerFactory.getLogger(PixelmonRPGHelper.class);

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

    public static List<String> getPokemonTypes(PixelmonEntity pixelmonEntity) {
        List<Holder<Type>> holderTypes = pixelmonEntity.getPokemon().getTypes();
        List<String> types = new ArrayList<>();

        if (holderTypes != null) {
            for (Holder<Type> holderType : holderTypes) {
                if (holderType.isBound()) {
                    ResourceKey<Type> typeResourceKey = holderType.unwrapKey().get();
                    types.add(typeResourceKey.location().getPath());
                }
            }
        }

        return types;
    }

    public static List<TypeHelper> getPokemonTypesHelper(PixelmonEntity pixelmonEntity) {
        List<Holder<Type>> holderTypes = pixelmonEntity.getPokemon().getTypes();
        List<TypeHelper> types = new ArrayList<>();

        if (holderTypes != null) {
            for (Holder<Type> holderType : holderTypes) {
                if (holderType.isBound()) {
                    ResourceKey<Type> typeResourceKey = holderType.unwrapKey().get();
                    String typeName = typeResourceKey.location().getPath();
                    ResourceLocation resourceLocation = ResourceLocation.fromNamespaceAndPath(Pixelmon.MODID, "textures/gui/type/" + typeName + ".png");
                    types.add(new TypeHelper(typeName, resourceLocation));
                }
            }
        }

        return types;
    }

}
