package com.mattutos.pixelmonrpgsystem.dailyreward;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import java.util.List;

public class DailyRewardManager {
    private static final ResourceKey<LootTable> DAILY_REWARD_LOOT_TABLE = 
        ResourceKey.create(Registries.LOOT_TABLE, ResourceLocation.fromNamespaceAndPath(PixelmonRPGSystem.MODID, "daily_reward"));

    public static List<ItemStack> generateDailyRewards(ServerPlayer player) {
        ServerLevel level = player.serverLevel();
        
        LootParams.Builder builder = new LootParams.Builder(level)
            .withParameter(LootContextParams.ORIGIN, player.position())
            .withParameter(LootContextParams.THIS_ENTITY, player)
            .withLuck(player.getLuck());
        
        LootParams params = builder.create(LootContextParamSets.CHEST);
        
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(DAILY_REWARD_LOOT_TABLE);
        return lootTable.getRandomItems(params);
    }
    
    public static void giveDailyRewards(ServerPlayer player, List<ItemStack> rewards) {
        for (ItemStack reward : rewards) {
            if (!player.getInventory().add(reward.copy())) {
                player.drop(reward, false);
            }
        }
    }
}
