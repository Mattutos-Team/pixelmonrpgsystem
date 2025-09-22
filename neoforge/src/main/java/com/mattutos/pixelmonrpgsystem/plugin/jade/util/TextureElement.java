package com.mattutos.pixelmonrpgsystem.plugin.jade.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import snownee.jade.api.ui.Element;
import snownee.jade.overlay.OverlayRenderer;

public class TextureElement extends Element {
    private final ResourceLocation resourceLocation;
    private final int width;
    private final int height;

    public TextureElement(ResourceLocation resourceLocation, int width, int height) {
        this.resourceLocation = resourceLocation;
        this.width = width;
        this.height = height;
    }

    @Override
    public Vec2 getSize() {
        return new Vec2((float) this.width, (float) this.height);
    }

    @Override
    public void render(GuiGraphics guiGraphics, float x, float y, float maxX, float maxY) {
        RenderSystem.enableBlend();
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, OverlayRenderer.alpha);
        guiGraphics.blit(resourceLocation, Math.round(x), Math.round(y), 0, 0, this.width, this.height, Math.round(this.getCachedSize().x), Math.round(this.getCachedSize().y));
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
