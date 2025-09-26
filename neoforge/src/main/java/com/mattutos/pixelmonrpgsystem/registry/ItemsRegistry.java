package com.mattutos.pixelmonrpgsystem.registry;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemsRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.ITEM, PixelmonRPGSystem.MODID);
}
