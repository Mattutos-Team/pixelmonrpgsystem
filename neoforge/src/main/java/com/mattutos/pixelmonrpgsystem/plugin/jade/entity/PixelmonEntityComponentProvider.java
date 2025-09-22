package com.mattutos.pixelmonrpgsystem.plugin.jade.entity;

import com.mattutos.pixelmonrpgsystem.plugin.jade.PokedexPlugin;
import com.mattutos.pixelmonrpgsystem.plugin.jade.util.ElementHelperExt;
import com.mattutos.pixelmonrpgsystem.util.PixelmonRPGHelper;
import com.mattutos.pixelmonrpgsystem.util.TypeHelper;
import com.pixelmonmod.pixelmon.api.pokedex.PokeDexStorageProxy;
import com.pixelmonmod.pixelmon.api.pokedex.PokedexStorage;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import com.pixelmonmod.pixelmon.entities.pixelmon.PixelmonEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.IServerDataProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.IElement;

import java.util.List;

public enum PixelmonEntityComponentProvider implements IEntityComponentProvider, IServerDataProvider<EntityAccessor> {
    INSTANCE;
    final ElementHelperExt elementHelperExt = new ElementHelperExt();

    @Override
    public @Nullable IElement getIcon(EntityAccessor entityAccessor, IPluginConfig config, IElement currentIcon) {
        CompoundTag serverData = entityAccessor.getServerData();
        if (entityAccessor.getEntity() instanceof PixelmonEntity pixelmonEntity) {
            if (serverData.contains("seen") && serverData.getInt("seen") > 0) {
                return elementHelperExt.texture(pixelmonEntity.getPokemon().getSprite(), 32, 32).translate(new Vec2(0, -5));
            }
        }
        return IEntityComponentProvider.super.getIcon(entityAccessor, config, currentIcon);
    }

    @Override
    public void appendTooltip(ITooltip iTooltip, EntityAccessor entityAccessor, IPluginConfig iPluginConfig) {
        CompoundTag serverData = entityAccessor.getServerData();

        if (serverData.contains("seen") && serverData.getInt("seen") > 0 && entityAccessor.getEntity() instanceof PixelmonEntity) {
            appendInfoPokemon(iTooltip, entityAccessor);
        } else {
            iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.id_dex", "???"));
        }
    }

    private void appendInfoPokemon(ITooltip iTooltip, EntityAccessor entityAccessor) {
        Player player = entityAccessor.getPlayer();

        if (player.isCrouching()) {
            appendInfoPokemonAllInfos(iTooltip, entityAccessor);
        } else {
            appendInfoPokemonBasicInfos(iTooltip, entityAccessor);
        }
    }

    private void appendInfoPokemonBasicInfos(ITooltip iTooltip, EntityAccessor entityAccessor) {
        PixelmonEntity pixelmonEntity = (PixelmonEntity) entityAccessor.getEntity();
        List<TypeHelper> pokemonTypesHelper = PixelmonRPGHelper.getPokemonTypesHelper(pixelmonEntity.getPokemon());

        iTooltip.add(elementHelperExt.spacer(5, 0).align(IElement.Align.CENTER));
        pokemonTypesHelper.forEach(type -> {
            IElement iconType = elementHelperExt.texture(type.location(), 16, 16).align(IElement.Align.CENTER);
            iTooltip.append(iconType);
            iTooltip.append(elementHelperExt.spacer(5, 0).align(IElement.Align.CENTER));
        });
    }

    private void appendInfoPokemonAllInfos(ITooltip iTooltip, EntityAccessor entityAccessor) {
        PixelmonEntity pixelmonEntity = (PixelmonEntity) entityAccessor.getEntity();
        CompoundTag serverData = entityAccessor.getServerData();
        Pokemon pokemon = pixelmonEntity.getPokemon();
        List<TypeHelper> pokemonTypesHelper = PixelmonRPGHelper.getPokemonTypesHelper(pixelmonEntity.getPokemon());

        iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.id_dex", pokemon.getDex()));

        MutableComponent genderComponent = Component.translatable(pokemon.getGender().getTranslationKey());
        switch (pokemon.getGender()) {
            case MALE -> genderComponent.withStyle(ChatFormatting.AQUA);
            case FEMALE -> genderComponent.withStyle(ChatFormatting.LIGHT_PURPLE);
        }
        iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.gender", genderComponent));

        MutableComponent paletteComponent = pokemon.getPalette().getTranslatedName();
        if (pokemon.getPalette().isShiny()) paletteComponent.withStyle(ChatFormatting.YELLOW).append(" ⭐");
        iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.palette", paletteComponent));
        iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.form", pokemon.getForm().getTranslatedName()));
        iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.gen", pokemon.getSpecies().getGeneration()));

        iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.seen", serverData.getInt("seen")));
        iTooltip.add(Component.translatable("pixelmonrpgsystem.jade.pixelmon.caught", serverData.getInt("caught")));

        pokemonTypesHelper.forEach(type -> {
            IElement iconType = elementHelperExt.texture(type.location(), 10, 10);
            iTooltip.add(iconType.translate(new Vec2(0, -1)));
            iTooltip.append(Component.literal(" - "));
            iTooltip.append(type.translatedComponent());
        });
    }

    @Override
    public void appendServerData(CompoundTag compoundTag, EntityAccessor entityAccessor) {
        if (entityAccessor.getEntity() instanceof PixelmonEntity pixelmonEntity && entityAccessor.getPlayer() instanceof ServerPlayer player) {
            PokedexStorage pokedexStorage = PokeDexStorageProxy.getStorageNow(player);
            if (pokedexStorage != null) {
                Pokemon pokemon = pixelmonEntity.getPokemon();
                int seenCount = pokedexStorage.getSeenCount(pokemon);
                compoundTag.putInt("seen", seenCount);

                if (seenCount == 0) return;

                compoundTag.putShort("id_dex", (short) pokemon.getDex());
                compoundTag.putByte("gender", (byte) pokemon.getGender().ordinal());
                compoundTag.putString("form", pokemon.getFormName());
                compoundTag.putString("palette", pokemon.getPaletteName());
                compoundTag.putInt("caught", pokedexStorage.getCaughtCount(pokemon));
            }
        }
    }

    @Override
    public ResourceLocation getUid() {
        return PokedexPlugin.PIXELMON_ENTITY;
    }
}
