package com.mattutos.pixelmonrpgsystem.commands;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.enums.MasteryType;
import com.mattutos.pixelmonrpgsystem.enums.PixelmonType;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.server.command.EnumArgument;

public class MasteryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal("mastery")
                .then(commandMasteryType())
                .then(commandMasteryAdmin())
                .executes(context -> {
                    context.getSource().sendFailure(Component.literal("§cUso: /mastery {type} ou /mastery admin {player} set {type} {level} ou /mastery admin {player} reset"));
                    return 0;
                });

        dispatcher.register(argumentBuilder);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, PixelmonType> commandMasteryType() {
        return Commands.argument("type", EnumArgument.enumArgument(PixelmonType.class))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();

                    if (!(source.getEntity() instanceof ServerPlayer player)) {
                        source.sendFailure(Component.literal("§cApenas jogadores podem usar este comando"));
                        return 0;
                    }

                    PixelmonType pixelmonType = context.getArgument("type", PixelmonType.class);
                    PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);

                    if (data == null) {
                        source.sendFailure(Component.literal("§cErro ao acessar dados do jogador"));
                        return 0;
                    }

                    MasteryProgress mastery = data.getMastery(pixelmonType);

                    player.sendSystemMessage(Component.literal("§6[Maestria - ").append(pixelmonType.translatedComponent()).append("]"));
                    player.sendSystemMessage(Component.literal("§eNível: §f" + mastery.getStageName()));
                    player.sendSystemMessage(Component.literal("§eXP: §f" + mastery.getXp() + " / " + mastery.getXpForNextStage()));

                    double bonus = mastery.getBonusPercentage();
                    if (bonus > 0) {
                        player.sendSystemMessage(Component.literal("§eBônus atual: §a+" + bonus + "% em batalha e captura"));
                    } else {
                        player.sendSystemMessage(Component.literal("§eBônus atual: §7Nenhum"));
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
                                                        source.sendFailure(Component.literal("§cErro ao acessar dados do jogador"));
                                                        return 0;
                                                    }

                                                    data.setMastery(pixelmonType, masteryLevel);

                                                    targetPlayer.sendSystemMessage(Component.literal("§6Sua maestria de " + pixelmonType.translatedComponent() + " foi definida para " + masteryLevel.translatedComponent() + "!"));
                                                    source.sendSuccess(() -> Component.literal("§aMaestria de " + pixelmonType.translatedComponent() + " de " + targetPlayer.getName().getString() + " definida para " + masteryLevel.translatedComponent()), true);

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
                                        source.sendFailure(Component.literal("§cErro ao acessar dados do jogador"));
                                        return 0;
                                    }

                                    data.resetAllMasteries();
                                    targetPlayer.sendSystemMessage(Component.literal("§6Todas as suas maestrias foram resetadas!"));
                                    source.sendSuccess(() -> Component.literal("§aTodas as maestrias de " + targetPlayer.getName().getString() + " foram resetadas"), true);
                                    return 1;
                                })
                        )
                );
    }
}
