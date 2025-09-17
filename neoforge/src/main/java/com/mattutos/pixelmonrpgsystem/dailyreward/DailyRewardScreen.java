package com.mattutos.pixelmonrpgsystem.dailyreward;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import java.util.List;
import java.util.ArrayList;

public class DailyRewardScreen extends Screen {
    private final List<ItemStack> rewards;
    private Button confirmButton;

    public DailyRewardScreen(List<ItemStack> rewards) {
        super(Component.translatable("pixelmonrpgsystem.dailyreward.title"));
        this.rewards = new ArrayList<>(rewards);
    }

    @Override
    protected void init() {
        super.init();
        
        this.confirmButton = Button.builder(
            Component.translatable("pixelmonrpgsystem.dailyreward.confirm"),
            button -> this.onConfirm()
        ).bounds(this.width / 2 - 50, this.height - 40, 100, 20).build();
        
        this.addRenderableWidget(confirmButton);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.fill(0, 0, this.width, this.height, 0x80000000);
        
        Component title = Component.translatable("pixelmonrpgsystem.dailyreward.title");
        graphics.drawCenteredString(this.font, title, this.width / 2, 20, 0xFFFFFF);
        
        int itemsPerRow = 6;
        int startX = this.width / 2 - (itemsPerRow * 20) / 2;
        int startY = this.height / 2 - 40;
        
        for (int i = 0; i < rewards.size(); i++) {
            ItemStack item = rewards.get(i);
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;
            int x = startX + (col * 25);
            int y = startY + (row * 25);
            
            graphics.renderItem(item, x, y);
            graphics.renderItemDecorations(this.font, item, x, y);
            
            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
                graphics.renderTooltip(this.font, item, mouseX, mouseY);
            }
        }
        
        super.render(graphics, mouseX, mouseY, partialTick);
    }

    private void onConfirm() {
        this.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
