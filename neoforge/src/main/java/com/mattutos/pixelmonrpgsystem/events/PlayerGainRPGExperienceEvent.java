package com.mattutos.pixelmonrpgsystem.events;

import com.mattutos.pixelmonrpgsystem.experience.ExperienceSource;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;

public abstract class PlayerGainRPGExperienceEvent extends Event {
    private final ServerPlayer player;
    private final int experienceAmount;
    private final ExperienceSource source;

    public PlayerGainRPGExperienceEvent(ServerPlayer player, int experienceAmount, ExperienceSource source) {
        this.player = player;
        this.experienceAmount = experienceAmount;
        this.source = source;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public int getExperienceAmount() {
        return experienceAmount;
    }

    public ExperienceSource getSource() {
        return source;
    }

    public static class Pre extends PlayerGainRPGExperienceEvent implements ICancellableEvent {
        public Pre(ServerPlayer player, int experienceAmount, ExperienceSource source) {
            super(player, experienceAmount, source);
        }
    }

    public static class Post extends PlayerGainRPGExperienceEvent {
        public Post(ServerPlayer player, int experienceAmount, ExperienceSource source) {
            super(player, experienceAmount, source);
        }
    }
}
