package com.mattutos.pixelmonrpgsystemaddon.client;

import com.mattutos.pixelmonrpgsystemaddon.PixelmonRPGSystemAddon;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ScreenEvent;

@EventBusSubscriber(modid = PixelmonRPGSystemAddon.MODID, value = Dist.CLIENT)
public class HUDRenderer {
    
    @SubscribeEvent
    public static void onScreenRender(ScreenEvent.Render.Post event) {
        if (event.getScreen() instanceof InventoryScreen) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                renderPlayerLevel(event.getGuiGraphics(), event.getScreen().width, event.getScreen().height);
            }
        }
    }
    
    private static void renderPlayerLevel(GuiGraphics guiGraphics, int screenWidth, int screenHeight) {
        int level = ClientPlayerRPGData.getLevel();
        int currentXP = ClientPlayerRPGData.getCurrentLevelExperience();
        int neededXP = ClientPlayerRPGData.getExperienceNeededForNextLevel();
        int totalXPForLevel = ClientPlayerRPGData.getExperienceForNextLevel() - ((level - 1) * (level - 1) * 100);
        
        String levelText = "Nível: " + level;
        String xpText = "XP: " + currentXP + "/" + totalXPForLevel;
        
        int x = screenWidth - 150;
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
