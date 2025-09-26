package com.mattutos.pixelmonrpgsystem.commands;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.enums.MasteryType;
import com.mattutos.pixelmonrpgsystem.enums.PixelmonType;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;

public class MasteryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal("mastery")
                .then(commandMasteryType())
                .then(commandMasteryAdmin())
                .executes(commandMasteryListAll());

        dispatcher.register(argumentBuilder);
    }

    private static @NotNull Command<CommandSourceStack> commandMasteryListAll() {
        return context -> {
            CommandSourceStack source = context.getSource();

            if (!(source.getEntity() instanceof ServerPlayer player)) {
                source.sendFailure(Component.translatable("command.mastery.only_players").withStyle(ChatFormatting.RED));
                return 0;
            }

            PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
            Map<PixelmonType, MasteryProgress> masteries = data.getAllMasteries();

            // Header
            player.sendSystemMessage(
                    Component.translatable("command.mastery.list_all.header")
                            .withStyle(ChatFormatting.GOLD)
                            .withStyle(ChatFormatting.BOLD)
            );
            player.sendSystemMessage(Component.empty());

            if (masteries.isEmpty()) {
                player.sendSystemMessage(Component.translatable("command.mastery.list_all.none_title").withStyle(ChatFormatting.GRAY));
                player.sendSystemMessage(Component.translatable("command.mastery.list_all.none_hint").withStyle(ChatFormatting.GRAY));
            } else {
                for (Map.Entry<PixelmonType, MasteryProgress> entry : masteries.entrySet()) {
                    PixelmonType pixelmonType = entry.getKey();
                    MasteryProgress mastery = entry.getValue();

                    MasteryType masteryType = mastery.getStageName();

                    // Line with pixelmon type + mastery level (keeping the color of masteryType)
                    player.sendSystemMessage(
                            pixelmonType.translatedComponent().withStyle(ChatFormatting.GOLD)
                                    .append(Component.translatable("command.mastery.list_all.entry_separator"))
                                    .append(masteryType.translatedComponent().withStyle(masteryType.getColor()))
                    );

                    // XP: yellow label, white values
                    player.sendSystemMessage(
                            Component.translatable("command.mastery.list_all.entry_xp",
                                    Component.literal(String.valueOf(mastery.getXp())).withStyle(ChatFormatting.WHITE),
                                    Component.literal(String.valueOf(mastery.getXpForNextStage())).withStyle(ChatFormatting.WHITE)
                            ).withStyle(ChatFormatting.YELLOW)
                    );

                    // Bonus (formatted with 1 decimal place)
                    double bonus = mastery.getBonusPercentage();
                    if (bonus > 0) {
                        String bonusStr = String.format(Locale.ROOT, "%.1f", bonus);
                        player.sendSystemMessage(
                                Component.translatable("command.mastery.list_all.entry_bonus",
                                        Component.literal(bonusStr).withStyle(ChatFormatting.GREEN)
                                ).withStyle(ChatFormatting.YELLOW)
                        );
                    } else {
                        player.sendSystemMessage(
                                Component.translatable("command.mastery.list_all.entry_bonus_none")
                                        .withStyle(ChatFormatting.YELLOW)
                        );
                    }

                    player.sendSystemMessage(Component.empty());
                }
            }

            // Footer with hint
            player.sendSystemMessage(Component.translatable("command.mastery.list_all.footer_hint").withStyle(ChatFormatting.GRAY));
            player.sendSystemMessage(
                    Component.translatable("command.mastery.list_all.footer_end")
                            .withStyle(ChatFormatting.GOLD)
                            .withStyle(ChatFormatting.BOLD)
            );

            return 0;
        };
    }

    private static RequiredArgumentBuilder<CommandSourceStack, PixelmonType> commandMasteryType() {
        return Commands.argument("type", EnumArgument.enumArgument(PixelmonType.class))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();

                    if (!(source.getEntity() instanceof ServerPlayer player)) {
                        source.sendFailure(Component.translatable("command.mastery.only_players").withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    PixelmonType pixelmonType = context.getArgument("type", PixelmonType.class);
                    PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);

                    if (data == null) {
                        source.sendFailure(Component.translatable("command.mastery.error.access_data").withStyle(ChatFormatting.RED));
                        return 0;
                    }

                    MasteryProgress mastery = data.getMastery(pixelmonType);

                    // Cabeçalho: toda a linha em dourado; o %s (pixelmonType) é passado como Component.
                    player.sendSystemMessage(
                            Component.translatable("command.mastery.header", pixelmonType.translatedComponent())
                                    .withStyle(ChatFormatting.GOLD)
                    );

                    // Nível: label em amarelo, valor em branco
                    player.sendSystemMessage(
                            Component.translatable("command.mastery.level", mastery.getStageName().translatedComponent().withStyle(ChatFormatting.WHITE))
                                    .withStyle(ChatFormatting.YELLOW)
                    );

                    // XP atual / próximo: label em amarelo, valores em branco
                    player.sendSystemMessage(
                            Component.translatable("command.mastery.xp",
                                    Component.literal(String.valueOf(mastery.getXp())).withStyle(ChatFormatting.WHITE),
                                    Component.literal(String.valueOf(mastery.getXpForNextStage())).withStyle(ChatFormatting.WHITE)
                            ).withStyle(ChatFormatting.YELLOW)
                    );

                    // Bônus: formatado com 1 casa decimal; label amarelo + valor verde (ou mensagem "Nenhum")
                    double bonus = mastery.getBonusPercentage();
                    if (bonus > 0) {
                        String bonusStr = String.format(Locale.ROOT, "%.1f", bonus);
                        player.sendSystemMessage(
                                Component.translatable("command.mastery.bonus", Component.literal(bonusStr).withStyle(ChatFormatting.GREEN))
                                        .withStyle(ChatFormatting.YELLOW)
                        );
                    } else {
                        player.sendSystemMessage(
                                Component.translatable("command.mastery.bonus_none")
                                        .withStyle(ChatFormatting.YELLOW)
                        );
                    }

                    return 1;
                });
    }

    private static LiteralArgumentBuilder<CommandSourceStack> commandMasteryAdmin() {
        return Commands.literal("admin")
                .requires(source -> source.hasPermission(4))
                .then(Commands.argument("player", EntityArgument.player())
                        .then(Commands.literal("set")
                                .then(Commands.argument("type", EnumArgument.enumArgument(PixelmonType.class))
                                        .then(Commands.argument("level", EnumArgument.enumArgument(MasteryType.class))
                                                .executes(context -> {
                                                    CommandSourceStack source = context.getSource();
                                                    ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
                                                    PixelmonType pixelmonType = context.getArgument("type", PixelmonType.class);
                                                    MasteryType masteryLevel = context.getArgument("level", MasteryType.class);

                                                    PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(targetPlayer);
                                                    if (data == null) {
                                                        source.sendFailure(Component.translatable("command.mastery.error.access_data").withStyle(ChatFormatting.RED));
                                                        return 0;
                                                    }

                                                    data.setMastery(pixelmonType, masteryLevel);

                                                    // Mensagem para o player alvo (dourado)
                                                    targetPlayer.sendSystemMessage(
                                                            Component.translatable("command.mastery.set_target", pixelmonType.translatedComponent(), masteryLevel.translatedComponent())
                                                                    .withStyle(ChatFormatting.GOLD)
                                                    );

                                                    // Mensagem de sucesso para o executor (verde)
                                                    source.sendSuccess(() ->
                                                                    Component.translatable("command.mastery.set_success",
                                                                            pixelmonType.translatedComponent(),
                                                                            targetPlayer.getName(), // componente do próprio jogador
                                                                            masteryLevel.translatedComponent()
                                                                    ).withStyle(ChatFormatting.GREEN)
                                                            , true);

                                                    return 1;
                                                })
                                        )
                                )
                        )
                        .then(Commands.literal("reset")
                                .executes(context -> {
                                    CommandSourceStack source = context.getSource();
                                    ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");

                                    PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(targetPlayer);
                                    if (data == null) {
                                        source.sendFailure(Component.translatable("command.mastery.error.access_data").withStyle(ChatFormatting.RED));
                                        return 0;
                                    }

                                    data.resetAllMasteries();

                                    // Aviso para o player alvo (dourado)
                                    targetPlayer.sendSystemMessage(
                                            Component.translatable("command.mastery.reset_target")
                                                    .withStyle(ChatFormatting.GOLD)
                                    );

                                    // Sucesso para o executor (verde), enviando o nome do jogador como Component
                                    source.sendSuccess(() ->
                                                    Component.translatable("command.mastery.reset_success", targetPlayer.getName())
                                                            .withStyle(ChatFormatting.GREEN)
                                            , true);

                                    return 1;
                                })
                        )
                );
    }

}