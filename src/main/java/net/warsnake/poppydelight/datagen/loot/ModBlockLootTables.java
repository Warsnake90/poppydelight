package net.warsnake.poppydelight.datagen.loot;

import net.minecraft.world.level.storage.loot.LootTable;
import net.warsnake.poppydelight.blocks.HempCropBlock;
import net.warsnake.poppydelight.blocks.ModBlocks;
import net.warsnake.poppydelight.blocks.PoppyCropBlock;
import net.warsnake.poppydelight.items.ModItems;
import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        LootItemCondition.Builder fullyGrown = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.POPPY_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(PoppyCropBlock.AGE, 3));

        this.add(ModBlocks.POPPY_CROP.get(),
                block -> LootTable.lootTable()
                        .withPool(applyExplosionCondition(ModBlocks.POPPY_CROP.get(),
                                LootPool.lootPool()
                                        .when(fullyGrown)
                                        .add(LootItem.lootTableItem(Items.POPPY)
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))))
                        ))

                        .withPool(applyExplosionCondition(ModBlocks.POPPY_CROP.get(),
                                LootPool.lootPool()
                                        .when(fullyGrown)
                                        .add(LootItem.lootTableItem(ModItems.POPPYSEED.get())
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        ))

                        .withPool(applyExplosionCondition(ModBlocks.POPPY_CROP.get(),
                                LootPool.lootPool()
                                        .when(fullyGrown.invert())
                                        .add(LootItem.lootTableItem(ModItems.POPPYSEED.get()))
                        ))
        );

        LootItemCondition.Builder fullyGrown2 = LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.HEMP_CROP.get())
                .setProperties(StatePropertiesPredicate.Builder.properties()
                        .hasProperty(HempCropBlock.AGE, 7));

        this.add(ModBlocks.HEMP_CROP.get(),
                block -> LootTable.lootTable()
                        .withPool(applyExplosionCondition(ModBlocks.HEMP_CROP.get(),
                                LootPool.lootPool()
                                        .when(fullyGrown2)
                                        .add(LootItem.lootTableItem(ModItems.HEMPLEAF.get())
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))))
                        ))

                        .withPool(applyExplosionCondition(ModBlocks.HEMP_CROP.get(),
                                LootPool.lootPool()
                                        .when(fullyGrown2)
                                        .add(LootItem.lootTableItem(ModItems.HEMPSEED.get())
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(1))))
                        ))

                        .withPool(applyExplosionCondition(ModBlocks.HEMP_CROP.get(),
                                LootPool.lootPool()
                                        .when(fullyGrown.invert())
                                        .add(LootItem.lootTableItem(ModItems.HEMPSEED.get()))
                        ))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get)::iterator;
    }
}
