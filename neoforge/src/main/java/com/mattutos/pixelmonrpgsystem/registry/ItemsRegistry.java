package com.mattutos.pixelmonrpgsystem.registry;

import com.mattutos.pixelmonrpgsystem.PixelmonRPGSystem;
import com.mattutos.pixelmonrpgsystem.items.MasteryBadgeItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ItemsRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(net.minecraft.core.registries.BuiltInRegistries.ITEM, PixelmonRPGSystem.MODID);

    public static final Supplier<Item> MASTERY_BADGE = ITEMS.register("mastery_badge", 
        () -> new MasteryBadgeItem(new Item.Properties()));
}
