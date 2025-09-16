package com.mattutos.pixelmonrpgsystem.commands;

import com.mattutos.pixelmonrpgsystem.Config;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.network.NetworkHandler;
import com.mattutos.pixelmonrpgsystem.network.PlayerRPGSyncPacket;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class RPGSystemCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal("prpg")
                .requires(source -> source.hasPermission(4))
                .then(commandRpgPlayer()
                        .then(commandRpgPlayerLevel())
                )
                // Caso o jogador digite sem argumentos
                .executes(context -> {
                    context.getSource().sendFailure(Component.literal("§cUso incorreto do comando"));
                    return 0;
                });

        dispatcher.register(argumentBuilder);
    }

    private static RequiredArgumentBuilder<CommandSourceStack, Integer> commandRpgPlayerLevel() {
        return Commands.argument("level", IntegerArgumentType.integer(1, Config.MAX_PLAYER_LEVEL.get()))
                .executes(context -> {
                    CommandSourceStack source = context.getSource();

                    // Pega o jogador alvo
                    ServerPlayer playerTarget = EntityArgument.getPlayer(context, "player");
                    int level = IntegerArgumentType.getInteger(context, "level");

                    PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(playerTarget);

                    data.setLevel(level);

                    // avisa o player que o nivel dele foi alterado
                    playerTarget.sendSystemMessage(Component.literal("§6Seu nível foi alterado para " + level + "!"));
                    source.sendSuccess(() -> Component.literal("§aNível de " + playerTarget.getName().getString() + " alterado para " + level), true);

                    NetworkHandler.sendToPlayer(new PlayerRPGSyncPacket(data.getExperience(), data.getLevel()), playerTarget);

                    return 1;
                });
    }

    private static RequiredArgumentBuilder<CommandSourceStack, EntitySelector> commandRpgPlayer() {
        return Commands.argument("player", EntityArgument.player())
                .executes(context -> {
                    CommandSourceStack source = context.getSource();

                    // Pega o jogador alvo
                    ServerPlayer alvo = EntityArgument.getPlayer(context, "player");

                    // Ação do comando (aqui só manda mensagem)
                    alvo.sendSystemMessage(Component.literal("§6Você recebeu VIP!"));
                    source.sendSuccess(() -> Component.literal("§aVIP concedido para " + alvo.getName().getString()), true);

                    return 1;
                });
    }

}
