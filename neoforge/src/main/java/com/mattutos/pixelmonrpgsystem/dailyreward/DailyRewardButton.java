package com.mattutos.pixelmonrpgsystem.dailyreward;

import com.mattutos.pixelmonrpgsystem.client.ClientPlayerRPGData;
import com.mattutos.pixelmonrpgsystem.network.DailyRewardRequestPacket;
import com.mattutos.pixelmonrpgsystem.network.NetworkHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;

public class DailyRewardButton extends Button {
    private final int margin = 50;
    private boolean canClaim;

    public DailyRewardButton(int x, int y) {
        super(x, y, 80, 20, Component.translatable("pixelmonrpgsystem.dailyreward.button"), Button::onPress, DEFAULT_NARRATION);

        String dailyRewardText = Component.translatable("pixelmonrpgsystem.dailyreward.button").getString();
        int textWidth = Minecraft.getInstance().font.width(dailyRewardText);

        this.width = textWidth + margin;

        updateClaimStatus();
    }

    public void updateClaimStatus() {
        Minecraft mc = Minecraft.getInstance();
        int screenWidth = mc.getWindow().getGuiScaledWidth();
        long currentTime = System.currentTimeMillis();
        long lastReward = ClientPlayerRPGData.getLastDailyReward();

        long currentDay = currentTime / (24 * 60 * 60 * 1000);
        long lastRewardDay = lastReward / (24 * 60 * 60 * 1000);

        this.canClaim = currentDay > lastRewardDay;
        this.active = this.canClaim;

        MutableComponent dailyRewardComponent = canClaim ?
                Component.translatable("pixelmonrpgsystem.dailyreward.button.available") :
                Component.translatable("pixelmonrpgsystem.dailyreward.button.claimed");

        int textWidth = mc.font.width(dailyRewardComponent);
        this.width = textWidth + margin;

        int centerPosWitdh = (screenWidth / 2) - (this.width / 2); // 10 pixels de margem da borda direita
        this.setX(centerPosWitdh);

        this.setMessage(dailyRewardComponent);
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
