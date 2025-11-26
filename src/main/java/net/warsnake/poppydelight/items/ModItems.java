package net.warsnake.poppydelight.items;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import net.minecraft.Util;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;

public class ModItems {

    public static final Item DEBUGITEM = new Item(new Item.Properties().stacksTo(1));
    public static final Item TDXVIAL = new TdxItem(new Item.Properties());
    public static final Item TDXGLAND = new TdxItem(new Item.Properties().stacksTo(1));
    public static final Item TDXAGENT = new TdxItem(new Item.Properties().stacksTo(1));
    public static final Item POPPYSEED = new Item(new Item.Properties().stacksTo(1));
    public static final Item WETPOPPYSEED = new Item(new Item.Properties().stacksTo(1));
    public static final Item CRUSHEDPOPPYSEED = new Item(new Item.Properties().stacksTo(1));
    public static final Item DRIEDPOPPYSEED = new Item(new Item.Properties().stacksTo(1));
    public static final Item RAWOPIOD = new TdxItem(new Item.Properties().stacksTo(1));
    public static final Item LOWQUALITY = new LowOpiumItem(new Item.Properties().stacksTo(1));
    public static final Item OPIUM = new MedOpiumItem(new Item.Properties().stacksTo(1));
    public static final Item HIGHQUALITY = new HighOpiumItem(new Item.Properties().stacksTo(1));

    public static void register(BiConsumer<String, Item> helper) {

        helper.accept("debugitem", DEBUGITEM);

        helper.accept("tdxvial", TDXVIAL);
        helper.accept("tdxgland", TDXGLAND);
        helper.accept("tdxagent", TDXAGENT);

        helper.accept("standardquality", OPIUM);
        helper.accept("poppyseed", POPPYSEED);
        helper.accept("wetseed", WETPOPPYSEED);
        helper.accept("crushedseed", CRUSHEDPOPPYSEED);
        helper.accept("driedseed", DRIEDPOPPYSEED);
        helper.accept("rawopium", RAWOPIOD);
        helper.accept("lowquality", LOWQUALITY);
        helper.accept("highquality", HIGHQUALITY);
    }

}