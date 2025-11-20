package net.warsnake.poppydelight.items;

import net.minecraft.world.item.*;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;

public class ModItems  {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PoppyDelight.MODID);

    // debug items go here

    public static final RegistryObject<Item> debug = ITEMS.register("debug",
            () -> new Item(new Item.Properties()));

    // poppy processing items

    // poison

    // random crafting junk

    // misc

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }

}