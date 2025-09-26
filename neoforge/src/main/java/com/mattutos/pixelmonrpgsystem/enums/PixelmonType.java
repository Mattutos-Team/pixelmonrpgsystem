package com.mattutos.pixelmonrpgsystem.enums;

import com.pixelmonmod.pixelmon.api.pokemon.type.Type;
import com.pixelmonmod.pixelmon.api.util.helpers.ResourceLocationHelper;
import com.pixelmonmod.pixelmon.init.registry.PixelmonRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public enum PixelmonType {

    NORMAL("normal", true),
    FIRE("fire", true),
    WATER("water", true),
    ELECTRIC("electric", true),
    GRASS("grass", true),
    ICE("ice", true),
    FIGHTING("fighting", true),
    POISON("poison", true),
    GROUND("ground", true),
    FLYING("flying", true),
    PSYCHIC("psychic", true),
    BUG("bug", true),
    ROCK("rock", true),
    GHOST("ghost", true),
    DRAGON("dragon", true),
    DARK("dark", true),
    STEEL("steel", true),
    //    MYSTERY("mystery", false),   // exclusivo do Pixelmon
    FAIRY("fairy", true),
    STELLAR("stellar", true);    // Gen 9 (oficial)

    public static final Set<PixelmonType> ALL_TYPES = Set.of(values());
    public static final Set<PixelmonType> ALL_OFFICIAL_TYPES = ALL_TYPES.stream().filter(PixelmonType::isOfficial).collect(Collectors.toSet());

    private final String key;
    private final ResourceKey<?> resourceKey;
    private final boolean official;

    PixelmonType(String key, boolean official) {
        this.key = key.toLowerCase(Locale.ROOT);
        this.resourceKey = ResourceKey.create(PixelmonRegistry.TYPE_REGISTRY, ResourceLocationHelper.pixelmon(key));
        this.official = official;
    }

    /**
     * Converte uma String em PixelmonType (case-insensitive).
     */
    public static PixelmonType of(String name) {
        String normalized = name.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(t -> t.getKey().equals(normalized))
                .findFirst()
                .orElseThrow();
    }

    public static PixelmonType of(Holder<Type> typeHolder) {
        return of(typeHolder.unwrapKey().get().location().getPath());
    }

    /**
     * Converts a Pixelmon Type to PixelmonType enum.
     *
     * @param type The Pixelmon Type to convert.
     * @return The corresponding PixelmonType enum.
     * @throws NullPointerException if type is null.
     */
    public static PixelmonType of(Type type) {
        return of(type.icon().getTexture().getPath());
    }

    /**
     * Checks if the given name corresponds to a valid PixelmonType.
     * The check is case-insensitive.
     *
     * @param name The name to check.
     * @return true if the name corresponds to a valid PixelmonType, false otherwise.
     * @throws NullPointerException if name is null.
     */
    public static boolean isValid(String name) {
        return Arrays.stream(values()).anyMatch(pt -> pt.key.equals(name));
    }

    public String getKey() {
        return this.key;
    }

    public ResourceKey<?> getResourceKey() {
        return resourceKey;
    }

    /**
     * Checks if this PixelmonType is an official type.
     *
     * @return true if this PixelmonType is official, false otherwise.
     */
    public boolean isOfficial() {
        return official;
    }

    /**
     * Gets the Pixelmon Type associated with this PixelmonType enum.
     *
     * @return The Pixelmon Type associated with this PixelmonType enum.
     * @throws IllegalStateException if the type is not found in the registry.
     * @throws NullPointerException  if the Minecraft instance or level is null.
     */
    public Type getType() {
        Registry<Type> registry = Minecraft.getInstance().level.registryAccess()
                .registryOrThrow(PixelmonRegistry.TYPE_REGISTRY);
        ResourceKey<Type> typeResourceKey = Type.parseOrNull(this.key);
        return registry.getOrThrow(typeResourceKey);
    }

    public String translationKey() {
        return "type." + key;
    }

    public MutableComponent translatedComponent() {
        return Component.translatable(translationKey());
    }

    public ResourceLocation location() {
        return this.getType().icon().getTexture();
    }

}
