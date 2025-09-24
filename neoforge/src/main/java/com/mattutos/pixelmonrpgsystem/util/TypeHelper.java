package com.mattutos.pixelmonrpgsystem.util;

import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public record TypeHelper(String name, Type type) {

    private static final List<TypeHelper> ALL_TYPES = new ArrayList<>();

    public TypeHelper(Holder<Type> typeHolder) {
        this(typeHolder.unwrapKey().get().location().getPath(), typeHolder.value());
    }

    public static TypeHelper of(String typeName) {
        Registry<Type> registry = Minecraft.getInstance().level.registryAccess().registryOrThrow(PixelmonRegistry.TYPE_REGISTRY);
        ResourceKey<Type> typeResourceKey = Type.parseOrNull(typeName);
        Type type = registry.getOrThrow(typeResourceKey);

        return new TypeHelper(typeName, type);
    }

    public static List<TypeHelper> getAllTypes() {
        if (ALL_TYPES.isEmpty()) {
            Registry<Type> registry = Minecraft.getInstance().level.registryAccess().registryOrThrow(PixelmonRegistry.TYPE_REGISTRY);

            for (Type type : registry) {
                String nameType = type.icon().getTexture().getPath();
                if (nameType.equals(Type.MYSTERY.location().getPath())) continue;

                ALL_TYPES.add(new TypeHelper(nameType, type));
            }
        }
        return ALL_TYPES;
    }

    public static boolean isValidType(String nameType) {
        return getAllTypes().stream()
                .anyMatch(typeHelper -> typeHelper.name.equals(nameType));
    }

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
