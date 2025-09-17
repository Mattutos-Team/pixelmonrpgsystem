package com.mattutos.pixelmonrpgsystem.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public class ItemNbtCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("itemnbt")
                        .requires(source -> source.hasPermission(2)) // Apenas OP/moderador (pode trocar para 0 se quiser liberar)
                        .executes(context -> {
                            CommandSourceStack source = context.getSource();

                            // Garante que só jogador pode usar
                            if (!(source.getEntity() instanceof ServerPlayer player)) {
                                source.sendFailure(Component.literal("§cApenas jogadores podem usar este comando."));
                                return 0;
                            }

                            // Pega item da mão
                            ItemStack item = player.getMainHandItem();
                            if (item.isEmpty()) {
                                source.sendFailure(Component.literal("§cVocê não está segurando nenhum item na mão."));
                                return 0;
                            }

                            // Pega NBT
                            var nbt = item.save(player.level().registryAccess());
                            String nbtString = nbt.toString();

                            source.sendSuccess(() -> Component.literal("§eNBT do item: §f" + nbtString), false);

                            // Cria componente clicável que copia para clipboard
                            Component clickableMsg = Component.literal("§aClique aqui para copiar o NBT do item")
                                    .setStyle(
                                            Style.EMPTY.withClickEvent(
                                                    new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, nbtString)
                                            )
                                    );

                            // Envia mensagem para o jogador
                            player.sendSystemMessage(clickableMsg);


                            return 1;
                        })
        );
    }
}
