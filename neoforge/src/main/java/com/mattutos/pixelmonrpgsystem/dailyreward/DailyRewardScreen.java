package com.mattutos.pixelmonrpgsystem.dailyreward;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

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
        super.render(graphics, mouseX, mouseY, partialTick);

        int totalItems = rewards.size();
        int itemsPerRow = 6; // número máximo de colunas
        int rows = (int) Math.ceil((double) totalItems / itemsPerRow);

        int slotSize = 18;
        int padding = 8;

        int boxWidth = itemsPerRow * slotSize + padding * 2;
        int boxHeight = rows * slotSize + padding * 2;

        int boxX = (this.width - boxWidth) / 2;
        int boxY = (this.height - boxHeight) / 2;

        // fundo semi-transparente
        graphics.fill(boxX, boxY, boxX + boxWidth, boxY + boxHeight, 0xAA000000);

        // cor da borda (branco levemente opaco)
        int borderColor = 0xFFFFFFFF;

        // borda superior
        graphics.hLine(boxX, boxX + boxWidth, boxY, borderColor);
        // borda inferior
        graphics.hLine(boxX, boxX + boxWidth, boxY + boxHeight, borderColor);
        // borda esquerda
        graphics.vLine(boxX, boxY, boxY + boxHeight, borderColor);
        // borda direita
        graphics.vLine(boxX + boxWidth, boxY, boxY + boxHeight, borderColor);

        Component title = Component.translatable("pixelmonrpgsystem.dailyreward.title");
        graphics.drawCenteredString(this.font, title, this.width / 2, 20, 0xFFFFFF);

        int startX = this.width / 2 - (itemsPerRow * 20) / 2;
        int startY = this.height / 2 - 40;

        // renderizar itens dentro da caixa
        for (int i = 0; i < totalItems; i++) {
            ItemStack item = rewards.get(i);
            int row = i / itemsPerRow;
            int col = i % itemsPerRow;

            int itemX = boxX + padding + col * slotSize;
            int itemY = boxY + padding + row * slotSize;

            graphics.renderItem(item, itemX, itemY);
            graphics.renderItemDecorations(this.font, item, itemX, itemY);

            if (mouseX >= itemX && mouseX < itemX + 16 && mouseY >= itemY && mouseY < itemY + 16) {
                graphics.renderTooltip(this.font, item, mouseX, mouseY);
            }
        }

//        for (int i = 0; i < rewards.size(); i++) {
//            ItemStack item = rewards.get(i);
//            int row = i / itemsPerRow;
//            int col = i % itemsPerRow;
//            int x = startX + (col * 25);
//            int y = startY + (row * 25);
//
//            graphics.renderItem(item, x, y);
//            graphics.renderItemDecorations(this.font, item, x, y);
//
//            if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
//                graphics.renderTooltip(this.font, item, mouseX, mouseY);
//            }
//        }

    }

    private void onConfirm() {
        this.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
