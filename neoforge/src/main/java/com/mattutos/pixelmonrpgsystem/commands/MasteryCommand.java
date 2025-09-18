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
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class MasteryCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        LiteralArgumentBuilder<CommandSourceStack> argumentBuilder = Commands.literal("maestria")
                .then(commandMasteryType())
                .executes(context -> {
                    context.getSource().sendFailure(Component.literal("§cUso: /maestria {tipo}"));
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
}
