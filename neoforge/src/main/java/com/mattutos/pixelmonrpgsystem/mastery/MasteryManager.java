package com.mattutos.pixelmonrpgsystem.mastery;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.mattutos.pixelmonrpgsystem.util.PixelmonRPGHelper;
import com.mattutos.pixelmonrpgsystem.util.TypeHelper;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

import java.util.ArrayList;
import java.util.List;

public class MasteryManager {

    public static List<String> getPokemonTypes(Pokemon pokemon) {
        List<String> types = new ArrayList<>();

        try {
            var pokemonTypes = pokemon.getTypes();
            if (pokemonTypes != null) {
                for (var typeHolder : pokemonTypes) {
                    if (typeHolder != null && typeHolder.unwrapKey().isPresent()) {
                        String typeName = typeHolder.unwrapKey().get().location().getPath();
                        types.add(typeName.toLowerCase());
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error extracting Pokemon types, using fallback: " + e.getMessage()); // (important-comment)
            types.add("normal");
        }

        if (types.isEmpty()) {
            types.add("normal");
        }

        return types;
    }

    public static void addCaptureXp(ServerPlayer player, Pokemon pokemon) {
        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null || data.getLevel() < 30) return;

        List<TypeHelper> types = PixelmonRPGHelper.getPokemonTypesHelper(pokemon);
        int xpAmount = Config.MASTERY_XP_CAPTURE.get();


        for (TypeHelper type : types) {
            int currentMasteryExp = data.getCurrentMasteryStage(type);
            if (data.getMastery(type) == null) continue;

            if (currentMasteryExp == 3) continue;

            player.sendSystemMessage(Component.literal(
                    "§aVocê ganhou §b" + xpAmount + " XP §ade Maestria do tipo §e"
                            + type.translatedComponent() + " §apor capturar §b" + pokemon.getDisplayName().getString() + "§a!"
            ));

            data.addMasteryXp(type, xpAmount);
            if (!Config.MASTERY_DUAL_TYPE_XP.get()) {
                break;
            }
        }
    }

    public static void addBattleVictoryXp(ServerPlayer player, Pokemon defeatedPokemon) {
        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null || data.getLevel() < 30) return;

        List<TypeHelper> types = PixelmonRPGHelper.getPokemonTypesHelper(defeatedPokemon);
        if (types.isEmpty()) return;

        int xpAmount = Config.MASTERY_XP_VICTORY.get();
        int splitXp = xpAmount / types.size();

        for (TypeHelper type : types) {
            if (data.getMastery(type) == null) continue;

            int currentMasteryStage = data.getCurrentMasteryStage(type);
            if (currentMasteryStage >= 3) continue;

            MutableComponent typeDisplay = type.translatedComponent();

            player.sendSystemMessage(Component.literal(
                    "§aVocê ganhou §b" + splitXp + " XP §ade Maestria do tipo §e"
                            + typeDisplay + " §apor vencer §b" + defeatedPokemon.getDisplayName().getString() + "§a!"
            ));

            data.addMasteryXp(type, splitXp);

            if (!Config.MASTERY_DUAL_TYPE_XP.get()) {
                break;
            }
        }
    }

    public static double getMasteryBonus(PlayerRPGCapability player, Pokemon pokemon) {
        if (player == null) return 0.0;

        List<TypeHelper> types = PixelmonRPGHelper.getPokemonTypesHelper(pokemon);
        double maxBonus = 0.0;

        for (TypeHelper type : types) {
            MasteryProgress mastery = player.getMastery(type);
            double bonus = mastery.getBonusPercentage();
            maxBonus = Math.max(maxBonus, bonus);
        }

        return maxBonus;
    }

}
