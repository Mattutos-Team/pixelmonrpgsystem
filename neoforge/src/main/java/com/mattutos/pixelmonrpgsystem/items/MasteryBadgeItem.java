package com.mattutos.pixelmonrpgsystem.items;

import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.mastery.MasteryProgress;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import com.mattutos.pixelmonrpgsystem.util.TypeHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.Map;

public class MasteryBadgeItem extends Item {

    public MasteryBadgeItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer) {
            showMasteryGUI(serverPlayer);
        }
        return InteractionResultHolder.success(player.getItemInHand(hand));
    }

    private void showMasteryGUI(ServerPlayer player) {
        PlayerRPGCapability data = CapabilitiesRegistry.getPlayerRPGCapability(player);
        if (data == null) {
            player.sendSystemMessage(Component.literal("§cErro ao acessar dados do jogador"));
            return;
        }

        player.sendSystemMessage(Component.literal("§6§l=== MAESTRIAS POKÉMON ==="));
        player.sendSystemMessage(Component.literal(""));

        Map<TypeHelper, MasteryProgress> masteries = data.getAllMasteries();

        if (masteries.isEmpty()) {
            player.sendSystemMessage(Component.literal("§7Nenhuma maestria desenvolvida ainda."));
            player.sendSystemMessage(Component.literal("§7Capture Pokémon e vença batalhas para ganhar XP!"));
        } else {
            for (Map.Entry<TypeHelper, MasteryProgress> entry : masteries.entrySet()) {
                TypeHelper type = entry.getKey();
                MasteryProgress mastery = entry.getValue();

                String stageColor = switch (mastery.getStage()) {
                    case 0 -> "§7"; // Novato - cinza
                    case 1 -> "§e"; // Aspirante - amarelo
                    case 2 -> "§b"; // Experiente - azul claro
                    case 3 -> "§d"; // Mestre - roxo
                    default -> "§f";
                };

                player.sendSystemMessage(Component.literal("§6" + type.translatedComponent() + ": " + stageColor + mastery.getStageName()));
                player.sendSystemMessage(Component.literal("  §eXP: §f" + mastery.getXp() + " / " + mastery.getXpForNextStage()));

                double bonus = mastery.getBonusPercentage();
                if (bonus > 0) {
                    player.sendSystemMessage(Component.literal("  §eBônus: §a+" + bonus + "%"));
                } else {
                    player.sendSystemMessage(Component.literal("  §eBônus: §7Nenhum"));
                }
                player.sendSystemMessage(Component.literal(""));
            }
        }

        player.sendSystemMessage(Component.literal("§7Use /maestria {tipo} para detalhes específicos"));
        player.sendSystemMessage(Component.literal("§6§l========================"));
    }
}
