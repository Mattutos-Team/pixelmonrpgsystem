package com.mattutos.pixelmonrpgsystem.dailyreward;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.client.Minecraft;
import com.mattutos.pixelmonrpgsystem.client.ClientPlayerRPGData;
import com.mattutos.pixelmonrpgsystem.network.NetworkHandler;
import com.mattutos.pixelmonrpgsystem.network.DailyRewardRequestPacket;

public class DailyRewardButton extends Button {
    private boolean canClaim;

    public DailyRewardButton(int x, int y) {
        super(x, y, 80, 20, Component.translatable("pixelmonrpgsystem.dailyreward.button"), 
              button -> ((DailyRewardButton) button).onPress(), DEFAULT_NARRATION);
        updateClaimStatus();
    }

    public void updateClaimStatus() {
        long currentTime = System.currentTimeMillis();
        long lastReward = ClientPlayerRPGData.getLastDailyReward();
        
        long currentDay = currentTime / (24 * 60 * 60 * 1000);
        long lastRewardDay = lastReward / (24 * 60 * 60 * 1000);
        
        this.canClaim = currentDay > lastRewardDay;
        this.active = this.canClaim;
        
        if (canClaim) {
            this.setMessage(Component.translatable("pixelmonrpgsystem.dailyreward.button.available"));
        } else {
            this.setMessage(Component.translatable("pixelmonrpgsystem.dailyreward.button.claimed"));
        }
    }

    @Override
    public void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        int color = canClaim ? 0xFF00AA00 : 0xFF666666;
        
        graphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, color);
        
        graphics.drawCenteredString(Minecraft.getInstance().font, this.getMessage(), 
            this.getX() + this.width / 2, this.getY() + (this.height - 8) / 2, 0xFFFFFF);
    }

    @Override
    public void onPress() {
        if (canClaim) {
            NetworkHandler.sendToServer(new DailyRewardRequestPacket());
        } else {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.sendSystemMessage(
                    Component.translatable("pixelmonrpgsystem.dailyreward.already_claimed")
                );
            }
        }
    }
}
