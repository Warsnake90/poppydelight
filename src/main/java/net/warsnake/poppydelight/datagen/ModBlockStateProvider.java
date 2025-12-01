package net.warsnake.poppydelight.datagen;

import net.warsnake.poppydelight.PoppyDelight;
import net.warsnake.poppydelight.blocks.ModBlocks;
import net.warsnake.poppydelight.blocks.PoppyCropBlock;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Function;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(PackOutput output, ExistingFileHelper exFileHelper) {
        super(output, PoppyDelight.MODID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {

        makePoppyCrop((CropBlock) ModBlocks.POPPY_CROP.get(), "poppy_crop_stage", "poppy_crop_stage");
    }


    public void makePoppyCrop(CropBlock block, String modelName, String textureName) {
        Function<BlockState, ConfiguredModel[]> function = state -> poppyStates(state, block, modelName, textureName);

        getVariantBuilder(block).forAllStates(function);
    }

    private ConfiguredModel[] poppyStates(BlockState state, CropBlock block, String modelName, String textureName) {
        ConfiguredModel[] models = new ConfiguredModel[1];
        models[0] = new ConfiguredModel(models().crop(modelName + state.getValue(((PoppyCropBlock) block).getAgeProperty()),
                new ResourceLocation(PoppyDelight.MODID, "block/" + textureName + state.getValue(((PoppyCropBlock) block).getAgeProperty()))).renderType("cutout"));

        return models;
    }

    private void blockWithItem(RegistryObject<Block> blockRegistryObject) {
        simpleBlockWithItem(blockRegistryObject.get(), cubeAll(blockRegistryObject.get()));
    }
}