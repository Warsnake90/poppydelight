package net.warsnake.poppydelight.items;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemNameBlockItem;
import net.minecraft.world.item.RecordItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;
import net.warsnake.poppydelight.blocks.ModBlocks;
import net.warsnake.poppydelight.sounds.ModSounds;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, PoppyDelight.MODID);

    public static final RegistryObject<Item> DEBUGITEM =
            ITEMS.register("debugitem",
                    () -> new Item(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TDXVIAL =
            ITEMS.register("tdxvial",
                    () -> new FinalTdxItem(new Item.Properties()));

    public static final RegistryObject<Item> TDXGLAND =
            ITEMS.register("tdxgland",
                    () -> new TdxItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> TDXAGENT =
            ITEMS.register("tdxagent",
                    () -> new TdxItem(new Item.Properties().stacksTo(1)));

    public static final RegistryObject<Item> DUST =
            ITEMS.register("dust",
                    () -> new Item(new Item.Properties()));

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

    public static final RegistryObject<Item> DRYHEMPLEAF =
            ITEMS.register("dryhempleaf",
                    () -> new Item(new Item.Properties()));

    public static final RegistryObject<Item> JOINT =
            ITEMS.register("joint",
                    () -> new JointItem(new Item.Properties().stacksTo(1)));

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

    public static final RegistryObject<Item> SHROOMS1 =
            ITEMS.register("void_psilocybe",
                    () -> new ShroomItem(new Item.Properties()));

    public static final RegistryObject<Item> SHROOMS2 =
            ITEMS.register("brown_psilocybe",
                    () -> new ShroomItem(new Item.Properties()));

    public static final RegistryObject<Item> PSILOCYBE_SPAWN =
            ITEMS.register("psilocybe_spawn",
                    () -> new ItemNameBlockItem(ModBlocks.SHROOM_CROP.get() ,new Item.Properties()));

    public static final RegistryObject<Item> WALTUH = ITEMS.register("waltuh_musicdisc",
                    () -> new RecordItem(1, ModSounds.WALTUH,new Item.Properties().stacksTo(1), 1500 ));
    public static final RegistryObject<Item> WAR = ITEMS.register("war_music_disc",
                    () -> new RecordItem(1, ModSounds.WAR,new Item.Properties().stacksTo(1), 8160 ));
    public static final RegistryObject<Item> LIGHT = ITEMS.register("light_music_disc",
                    () -> new RecordItem(1, ModSounds.LIGHT,new Item.Properties().stacksTo(1), 3600 ));
    public static final RegistryObject<Item> TOXIC = ITEMS.register("toxic_music_disc",
                    () -> new RecordItem(1, ModSounds.TOXIC,new Item.Properties().stacksTo(1), 2160 ));
    public static final RegistryObject<Item> STICKERBRUSH = ITEMS.register("stickerbrush_music_disc",
                    () -> new RecordItem(1, ModSounds.STICKERBRUSH,new Item.Properties().stacksTo(1), 5360 ));
    public static final RegistryObject<Item> ENDS = ITEMS.register("ends_music_disc",
                    () -> new RecordItem(1, ModSounds.ENDS,new Item.Properties().stacksTo(1), 2980 ));
    public static final RegistryObject<Item> STARS = ITEMS.register("stars_music_disc",
                    () -> new RecordItem(1, ModSounds.STARS,new Item.Properties().stacksTo(1), 1460 ));

    // disc crafting
    public static final RegistryObject<Item> DISC0 = ITEMS.register("unfinished_disc", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DISC1 = ITEMS.register("unfinished_disc2", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DISC2 = ITEMS.register("unfinished_disc3", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DISC3 = ITEMS.register("unfinished_disc4", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DISC4 = ITEMS.register("unfinished_disc5", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DISC5 = ITEMS.register("unfinished_disc6", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> DISC6 = ITEMS.register("unfinished_disc7", () -> new Item(new Item.Properties()));

    // NOT USED ANYMORE
    @Deprecated(forRemoval = true) public static final RegistryObject<Item> WETPOPPYSEED = ITEMS.register("wetseed", () -> new RemovedItem(new Item.Properties()));
    @Deprecated(forRemoval = true) public static final RegistryObject<Item> CRUSHEDPOPPYSEED = ITEMS.register("crushedseed", () -> new RemovedItem(new Item.Properties()));
    @Deprecated(forRemoval = true) public static final RegistryObject<Item> DRIEDPOPPYSEED = ITEMS.register("driedseed", () -> new RemovedItem(new Item.Properties()));
}