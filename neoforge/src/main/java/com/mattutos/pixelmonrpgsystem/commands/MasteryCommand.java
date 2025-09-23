package com.mattutos.pixelmonrpgsystem.commands;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryManager;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class MasteryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal("maestria")
                .then(commandMasteryType())
                .then(commandMasteryAdmin())
                .executes(context -> {
                    context.getSource().sendFailure(Component.literal("§cUso: /maestria {tipo} ou /maestria admin {player} {tipo} set to {nivel} ou /maestria admin {player} reset"));
                    return 0;
                });

        dispatcher.register(argumentBuilder);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, String> commandMasteryType() {
        return Commands.argument("tipo", StringArgumentType.string())
                .executes(context -> {
                    CommandSourceStack source = context.getSource();
                    
                    if (!(source.getEntity() instanceof ServerPlayer player)) {
                        source.sendFailure(Component.literal("§cApenas jogadores podem usar este comando"));
                        return 0;
                    }

                    String type = StringArgumentType.getString(context, "tipo").toLowerCase();
                    PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
                    
                    if (data == null) {
                        source.sendFailure(Component.literal("§cErro ao acessar dados do jogador"));
                        return 0;
                    }

                    MasteryProgress mastery = data.getMastery(type);
                    String displayName = MasteryManager.getTypeDisplayName(type);
                    
                    player.sendSystemMessage(Component.literal("§6[Maestria - " + displayName + "]"));
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
                        .then(Commands.argument("tipo", StringArgumentType.string())
                                .then(Commands.literal("set")
                                        .then(Commands.literal("to")
                                                .then(Commands.argument("nivel", StringArgumentType.string())
                                                        .executes(context -> {
                                                            CommandSourceStack source = context.getSource();
                                                            ServerPlayer targetPlayer = EntityArgument.getPlayer(context, "player");
                                                            String type = StringArgumentType.getString(context, "tipo").toLowerCase();
                                                            String masteryLevel = StringArgumentType.getString(context, "nivel").toLowerCase();

                                                            PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(targetPlayer);
                                                            if (data == null) {
                                                                source.sendFailure(Component.literal("§cErro ao acessar dados do jogador"));
                                                                return 0;
                                                            }

                                                            try {
                                                                data.setMastery(type, masteryLevel);
                                                                String displayName = MasteryManager.getTypeDisplayName(type);
                                                                String displayLevel = masteryLevel.substring(0, 1).toUpperCase() + masteryLevel.substring(1);
                                                                
                                                                targetPlayer.sendSystemMessage(Component.literal("§6Sua maestria de " + displayName + " foi definida para " + displayLevel + "!"));
                                                                source.sendSuccess(() -> Component.literal("§aMaestria de " + displayName + " de " + targetPlayer.getName().getString() + " definida para " + displayLevel), true);
                                                                return 1;
                                                            } catch (IllegalArgumentException e) {
                                                                source.sendFailure(Component.literal("§cNível de maestria inválido. Use: novato, aspirante, experiente, mestre"));
                                                                return 0;
                                                            }
                                                        })
                                                )
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
