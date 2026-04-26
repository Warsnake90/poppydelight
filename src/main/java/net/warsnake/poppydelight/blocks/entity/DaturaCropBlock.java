package net.warsnake.poppydelight.blocks.entity;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.warsnake.poppydelight.items.ModItems;

public class DaturaCropBlock extends CropBlock {

    public static final int max_age = 5;
    public static final IntegerProperty AGE = BlockStateProperties.AGE_5;

    public DaturaCropBlock(Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(AGE, 0));
    }

    @Override
    public IntegerProperty getAgeProperty() {
        return AGE;
    }

    @Override
    public int getMaxAge() {
        return 5;
    }

    @Override
    protected Item getBaseSeedId() {
        return ModItems.DATURASEED.get();
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

}
