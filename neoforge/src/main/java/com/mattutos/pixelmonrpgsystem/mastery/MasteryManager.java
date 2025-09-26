package com.mattutos.pixelmonrpgsystem.mastery;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.enums.PixelmonType;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.mattutos.pixelmonrpgsystem.util.PixelmonRPGHelper;
import com.pixelmonmod.pixelmon.api.pokemon.Pokemon;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class MasteryManager {

    public static void addCaptureXp(ServerPlayer player, Pokemon pokemon) {
        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null || data.getLevel() < 30) return;

        List<PixelmonType> types = PixelmonRPGHelper.getPokemonTypesHelper(pokemon);
        int xpAmount = Config.MASTERY_XP_CAPTURE.get();

        for (PixelmonType type : types) {
            // se não tem maestria registrada para esse tipo, pula
            if (data.getMastery(type) == null) continue;

            int currentMasteryExp = data.getCurrentMasteryStage(type);
            // evita ultrapassar o stage máximo (uso >= por segurança)
            if (currentMasteryExp >= 3) continue;

            // componentes estilizados para placeholders
            Component xpComp = Component.literal(String.valueOf(xpAmount)).withStyle(ChatFormatting.AQUA);
            Component typeComp = type.translatedComponent().withStyle(ChatFormatting.GOLD);
            Component pokemonComp = pokemon.getDisplayName().withStyle(ChatFormatting.AQUA);

            // mensagem translatable (base estilizada em verde; argumentos mantêm seus estilos)
            player.sendSystemMessage(
                    Component.translatable("pixelmonrpgsystem.mastery.capture_message", xpComp, typeComp, pokemonComp)
                            .withStyle(ChatFormatting.GREEN)
            );

            data.addMasteryXp(type, xpAmount);

            if (!Config.MASTERY_DUAL_TYPE_XP.get()) break;
        }
    }

    public static void addBattleVictoryXp(ServerPlayer player, Pokemon defeatedPokemon) {
        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null || data.getLevel() < 30) return;

        List<PixelmonType> types = PixelmonRPGHelper.getPokemonTypesHelper(defeatedPokemon);
        if (types.isEmpty()) return;

        int xpAmount = Config.MASTERY_XP_VICTORY.get();
        int splitXp = xpAmount / types.size();

        for (PixelmonType type : types) {
            if (data.getMastery(type) == null) continue;

            int currentMasteryStage = data.getCurrentMasteryStage(type);
            if (currentMasteryStage >= 3) continue;

            Component xpComp = Component.literal(String.valueOf(splitXp)).withStyle(ChatFormatting.AQUA);
            Component typeComp = type.translatedComponent().withStyle(ChatFormatting.GOLD);
            Component pokemonComp = defeatedPokemon.getDisplayName().withStyle(ChatFormatting.AQUA);

            player.sendSystemMessage(
                    Component.translatable("pixelmonrpgsystem.mastery.victory_message", xpComp, typeComp, pokemonComp)
                            .withStyle(ChatFormatting.GREEN)
            );

            data.addMasteryXp(type, splitXp);

            if (!Config.MASTERY_DUAL_TYPE_XP.get()) {
                break;
            }
        }
    }

    public static double getMasteryBonus(PlayerRPGCapability player, Pokemon pokemon) {
        List<PixelmonType> types = PixelmonRPGHelper.getPokemonTypesHelper(pokemon);
        double maxBonus = 0.0;

        for (PixelmonType type : types) {
            MasteryProgress mastery = player.getMastery(type);
            if (mastery == null) continue;
            double bonus = mastery.getBonusPercentage();
            if (Double.isFinite(bonus)) {
                maxBonus = Math.max(maxBonus, bonus);
            }
        }

        return maxBonus;
    }

}
