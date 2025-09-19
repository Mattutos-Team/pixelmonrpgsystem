package com.mattutos.pixelmonrpgsystem.mastery;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.network.chat.Component;
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

        List<String> types = getPokemonTypes(pokemon);
        int xpAmount = Config.MASTERY_XP_CAPTURE.get();
        String typeDisplay = getTypeDisplayName(types.getFirst());


        for (String type : types) {
            int currentMasteryExp = data.getCurrentMasteryStage(type);
            if (data.getMastery(type) == null) continue;

            if (currentMasteryExp == 3) continue;

            player.sendSystemMessage(Component.literal(
                    "§aVocê ganhou §b" + xpAmount + " XP §ade Maestria do tipo §e"
                            + typeDisplay + " §apor capturar §b" + pokemon.getDisplayName().getString() + "§a!"
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

        List<String> types = getPokemonTypes(defeatedPokemon);
        int xpAmount = Config.MASTERY_XP_VICTORY.get();
        String typeDisplay = getTypeDisplayName(types.getFirst());

        for (String type : types) {
            int currentMasteryExp = data.getCurrentMasteryStage(type);
            if (data.getMastery(type) == null) continue;

            if (currentMasteryExp == 3) continue;

            player.sendSystemMessage(Component.literal(
                    "§aVocê ganhou §b" + xpAmount + " XP §ade Maestria do tipo §e"
                            + typeDisplay + " §apor vencer §b" + defeatedPokemon.getDisplayName().getString() + "§a!"
            ));
            data.addMasteryXp(type, xpAmount);
            if (!Config.MASTERY_DUAL_TYPE_XP.get()) {
                break;
            }
        }
    }

    public static double getMasteryBonus(ServerPlayer player, Pokemon pokemon) {
        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null) return 0.0;

        List<String> types = getPokemonTypes(pokemon);
        double maxBonus = 0.0;

        for (String type : types) {
            MasteryProgress mastery = data.getMastery(type);
            double bonus = mastery.getBonusPercentage();
            maxBonus = Math.max(maxBonus, bonus);
        }

        return maxBonus;
    }

    public static String getTypeDisplayName(String type) {
        return switch (type.toLowerCase()) {
            case "fire" -> "Fogo";
            case "water" -> "Agua";
            case "grass" -> "Grama";
            case "electric" -> "Eletrico";
            case "psychic" -> "Psiquico";
            case "ice" -> "Gelo";
            case "dragon" -> "Dragao";
            case "dark" -> "Sombrio";
            case "fairy" -> "Fada";
            case "fighting" -> "Lutador";
            case "poison" -> "Venenoso";
            case "ground" -> "Terra";
            case "flying" -> "Voador";
            case "bug" -> "Inseto";
            case "rock" -> "Pedra";
            case "ghost" -> "Fantasma";
            case "steel" -> "Aco";
            case "normal" -> "Normal";
            default -> type.substring(0, 1).toUpperCase() + type.substring(1);
        };
    }
}
