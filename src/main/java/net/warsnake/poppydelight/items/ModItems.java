package net.warsnake.poppydelight.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;
import net.warsnake.poppydelight.blocks.ModBlocks;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PoppyDelight.MODID);

    public static final RegistryObject<Item> DEBUGITEM =
            ITEMS.register("debugitem",
                    () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TDXVIAL =
            ITEMS.register("tdxvial",
                    () -> new TdxItem(new Item.Properties()));

    public static final RegistryObject<Item> TDXGLAND =
            ITEMS.register("tdxgland",
                    () -> new TdxItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TDXAGENT =
            ITEMS.register("tdxagent",
                    () -> new TdxItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DUST =
            ITEMS.register("dust",
                    () -> new Item(new Item.Properties()));

    // NOT USED ANYMORE
    @Deprecated(forRemoval = true)
    public static final RegistryObject<Item> WETPOPPYSEED =
            ITEMS.register("wetseed",
                    () -> new RemovedItem(new Item.Properties()));

    // NOT USED ANYMORE
    @Deprecated(forRemoval = true)
    public static final RegistryObject<Item> CRUSHEDPOPPYSEED =
            ITEMS.register("crushedseed",
                    () -> new RemovedItem(new Item.Properties()));

    // To be removed
    @Deprecated(forRemoval = true)
    public static final RegistryObject<Item> DRIEDPOPPYSEED =
            ITEMS.register("driedseed",
                    () -> new RemovedItem(new Item.Properties()));

    public static final RegistryObject<Item> RAWOPIUM =
            ITEMS.register("rawopium",
                    () -> new Item(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> LOWQUALITY =
            ITEMS.register("lowquality",
                    () -> new LowOpiumItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> OPIUM =
            ITEMS.register("opium",
                    () -> new MedOpiumItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> HIGHQUALITY =
            ITEMS.register("highquality",
                    () -> new HighOpiumItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> POPPYSEED =
            ITEMS.register("poppyseed",
                    () -> new ItemNameBlockItem(ModBlocks.POPPY_CROP.get(), new Item.Properties()));

    public static final RegistryObject<Item> WITHERDUST =
            ITEMS.register("wither_dust",
                    () -> new WitherItem(new Item.Properties().stacksTo(64)));

    public static final RegistryObject<Item> CRYINGPOD =
            ITEMS.register("cryingpod",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HIGHPACKAGE =
            ITEMS.register("highpackage",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> PACKAGE =
            ITEMS.register("package",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LOWPACKAGE =
            ITEMS.register("lowpackage",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> ABSINTHE =
            ITEMS.register("absinthe",
                    () -> new AbsintheDrinkItem());

    public static final RegistryObject<Item> WORMWOODLEAF =
            ITEMS.register("wormwoodleaf",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HEMPLEAF =
            ITEMS.register("hempleaf",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> JOINT =
            ITEMS.register("joint",
                    () -> new JointItem(new Item.Properties()));

    public static final RegistryObject<Item> HEMPPACKAGE =
            ITEMS.register("hemppackage",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> HEMPSEED =
            ITEMS.register("hempseed",
                    () -> new ItemNameBlockItem(ModBlocks.HEMP_CROP.get(), new Item.Properties()));

    public static final RegistryObject<Item> ROLLINGPAPER =
            ITEMS.register("rollingpaper",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> WETROLLINGPAPER =
            ITEMS.register("wetrollingpaper",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> LITERALPOT =
            ITEMS.register("literalpot",
                    () -> new LiteralPotItem(new Item.Properties()));


}