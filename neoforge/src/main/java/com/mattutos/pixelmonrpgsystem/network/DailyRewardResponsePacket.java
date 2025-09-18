package com.mattutos.pixelmonrpgsystem.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
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
            try {
                Class<?> minecraftClass = Class.forName("net.minecraft.client.Minecraft");
                Object minecraftInstance = minecraftClass.getMethod("getInstance").invoke(null);
                
                Class<?> screenClass = Class.forName("com.mattutos.pixelmonrpgsystem.dailyreward.DailyRewardScreen");
                Object screen = screenClass.getConstructor(List.class).newInstance(packet.rewards);
                
                minecraftClass.getMethod("setScreen", Class.forName("net.minecraft.client.gui.screens.Screen"))
                    .invoke(minecraftInstance, screen);
            } catch (Exception e) {
            }
        });
    }
}
