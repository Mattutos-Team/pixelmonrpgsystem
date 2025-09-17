package com.mattutos.pixelmonrpgsystem.network;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.capability.PlayerRPGCapability;
import com.mattutos.pixelmonrpgsystem.dailyreward.DailyRewardManager;
import com.mattutos.pixelmonrpgsystem.registry.CapabilitiesRegistry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.List;

public record DailyRewardRequestPacket() implements CustomPacketPayload {
    public static final Type<DailyRewardRequestPacket> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(PixelmonRPGSystem.MODID, "daily_reward_request"));

    public static final StreamCodec<RegistryFriendlyByteBuf, DailyRewardRequestPacket> STREAM_CODEC = StreamCodec.of(
            (buf, packet) -> {
            },
            buf -> new DailyRewardRequestPacket()
    );

    public static void handle(DailyRewardRequestPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer serverPlayer) {
                PlayerRPGCapability capability = CapabilitiesRegistry.getPlayerRPGCapability(serverPlayer);

                if (capability != null && capability.canClaimDailyReward()) {
                    capability.claimDailyReward();

                    List<ItemStack> rewards = DailyRewardManager.generateDailyRewards(serverPlayer);

                    DailyRewardManager.giveDailyRewards(serverPlayer, rewards);

                    NetworkHandler.sendToPlayer(new DailyRewardResponsePacket(rewards), serverPlayer);

                    NetworkHandler.sendToPlayer(new PlayerRPGSyncPacket(capability.getExperience(), capability.getLevel(), capability.getLastDailyReward()), serverPlayer);

                    serverPlayer.sendSystemMessage(Component.translatable("pixelmonrpgsystem.dailyreward.claimed_success"));
                } else {
                    serverPlayer.sendSystemMessage(Component.translatable("pixelmonrpgsystem.dailyreward.already_claimed"));
                }
            }
        });
    }

    @Override
    public Type<DailyRewardRequestPacket> type() {
        return TYPE;
    }
}
