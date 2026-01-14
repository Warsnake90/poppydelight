package net.warsnake.poppydelight.blocks;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;

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
}
