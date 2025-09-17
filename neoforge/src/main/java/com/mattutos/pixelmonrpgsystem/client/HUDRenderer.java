package com.mattutos.pixelmonrpgsystem.client;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

@EventBusSubscriber(modid = PixelmonRPGSystem.MODID, value = Dist.CLIENT)
public class HUDRenderer {

    @SubscribeEvent
    public static void onScreenRender(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null && mc.level != null && mc.screen == null) {
            renderPlayerLevel(event.getGuiGraphics());
        }
    }

    private static void renderPlayerLevel(GuiGraphics guiGraphics) {
        int level = ClientPlayerRPGData.getLevel();
        int currentXP = ClientPlayerRPGData.getCurrentLevelExperience();
        int totalXPForLevel = ClientPlayerRPGData.getExperienceForNextLevel();

        String levelText = "Nível: " + level;
        String xpText = "XP: " + currentXP + "/" + totalXPForLevel;

        int x = 10;
        int y = 10;

        guiGraphics.fill(x - 5, y - 2, x + 140, y + 25, 0x80000000);

        guiGraphics.drawString(Minecraft.getInstance().font, levelText, x, y, 0xFFFFFF);
        guiGraphics.drawString(Minecraft.getInstance().font, xpText, x, y + 10, 0xFFFFFF);

        int barWidth = 130;
        int barHeight = 4;
        int barX = x;
        int barY = y + 20;

        guiGraphics.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF333333);

        if (totalXPForLevel > 0) {
            int filledWidth = (currentXP * barWidth) / totalXPForLevel;
            guiGraphics.fill(barX, barY, barX + filledWidth, barY + barHeight, 0xFF00AA00);
        }
    }
}
