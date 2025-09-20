package com.mattutos.pixelmonrpgsystem.plugin.jade.util;

import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.ui.IElement;
import snownee.jade.impl.ui.ElementHelper;

public class ElementHelperExt extends ElementHelper {
    public IElement texture(ResourceLocation sprite, int width, int height) {
        return new TextureElement(sprite, width, height);
    }
}
