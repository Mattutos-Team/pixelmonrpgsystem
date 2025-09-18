package com.mattutos.pixelmonrpgsystem.network;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.experience.ExperienceSource;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record PacketSyncExperience(int totalExperience, int experienceGained, ExperienceSource source) implements CustomPacketPayload {

    public static final Type<PacketSyncExperience> TYPE = new Type<>(PixelmonRPGSystem.prefix("sync_experience"));

    public static final StreamCodec<RegistryFriendlyByteBuf, PacketSyncExperience> STREAM_CODEC = StreamCodec.composite(
            StreamCodec.of(RegistryFriendlyByteBuf::writeInt, RegistryFriendlyByteBuf::readInt), PacketSyncExperience::totalExperience,
            StreamCodec.of(RegistryFriendlyByteBuf::writeInt, RegistryFriendlyByteBuf::readInt), PacketSyncExperience::experienceGained,
            StreamCodec.of((buf, source) -> buf.writeEnum(source), buf -> buf.readEnum(ExperienceSource.class)), PacketSyncExperience::source,
            PacketSyncExperience::new
    );

    public static void handle(PacketSyncExperience packet, IPayloadContext context) {
        context.enqueueWork(() -> {
            try {
                Class<?> clientDataClass = Class.forName("com.mattutos.pixelmonrpgsystem.client.ClientPlayerRPGData");
                clientDataClass.getMethod("setPlayerData", int.class, int.class)
                    .invoke(null, packet.totalExperience(), 
                           (Integer) clientDataClass.getMethod("getLevel").invoke(null));
            } catch (Exception e) {
            }
        });
    }

    @Override
    public Type<PacketSyncExperience> type() {
        return TYPE;
    }
}
