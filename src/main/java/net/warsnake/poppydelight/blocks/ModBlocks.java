package net.warsnake.poppydelight.blocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;
import net.warsnake.poppydelight.items.ModItems;

import java.util.function.Supplier;

import static net.minecraft.world.item.Items.registerBlock;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, PoppyDelight.MODID);

    public static final RegistryObject<PoppyCropBlock> POPPY_CROP = BLOCKS.register("poppy_crop", () ->
            new PoppyCropBlock(
                    Block.Properties.copy(Blocks.WHEAT)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
            )
    );

    public static final RegistryObject<HempCropBlock> HEMP_CROP = BLOCKS.register("hemp_crop", () ->
            new HempCropBlock(
                    Block.Properties.copy(Blocks.WHEAT)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
            )
    );

    public static final RegistryObject<ShroomCropBlock> SHROOM_CROP = BLOCKS.register("shroom_crop", () ->
            new ShroomCropBlock(
                    Block.Properties.copy(Blocks.WHEAT)
                            .noCollission()
                            .randomTicks()
                            .instabreak()
                            .sound(SoundType.CROP)
            )
    );

    public static final RegistryObject<Block> DRYING_TABLE = registerBlock("dryingtable",
            () -> new DryingTableBlock(BlockBehaviour.Properties.copy(Blocks.OAK_PLANKS).noOcclusion()));

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block) {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block) {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }

}
