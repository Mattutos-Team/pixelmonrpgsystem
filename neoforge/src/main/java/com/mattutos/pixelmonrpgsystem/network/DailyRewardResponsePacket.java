package com.mattutos.pixelmonrpgsystem.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.dailyreward.DailyRewardScreen;
import net.minecraft.client.Minecraft;
import java.util.List;
import java.util.ArrayList;

public record DailyRewardResponsePacket(List<ItemStack> rewards) implements CustomPacketPayload {
    public static final Type<DailyRewardResponsePacket> TYPE = new Type<>(
        ResourceLocation.fromNamespaceAndPath(PixelmonRPGSystem.MODID, "daily_reward_response"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, DailyRewardResponsePacket> STREAM_CODEC = StreamCodec.of(
        (buf, packet) -> {
            buf.writeInt(packet.rewards.size());
            for (ItemStack stack : packet.rewards) {
                ItemStack.STREAM_CODEC.encode(buf, stack);
            }
        },
        buf -> {
            int size = buf.readInt();
            List<ItemStack> rewards = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                rewards.add(ItemStack.STREAM_CODEC.decode(buf));
            }
            return new DailyRewardResponsePacket(rewards);
        }
    );

    @Override
    public Type<DailyRewardResponsePacket> type() {
        return TYPE;
    }

    public static void handle(DailyRewardResponsePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new DailyRewardScreen(packet.rewards));
        });
    }
}
