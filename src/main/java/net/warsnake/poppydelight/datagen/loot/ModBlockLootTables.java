package net.warsnake.poppydelight.datagen.loot;

import net.minecraft.advancements.critereon.StatePropertiesPredicate;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.blocks.HempCropBlock;
import net.warsnake.poppydelight.blocks.ModBlocks;
import net.warsnake.poppydelight.blocks.PoppyCropBlock;
import net.warsnake.poppydelight.blocks.ShroomCropBlock;
import net.warsnake.poppydelight.items.ModItems;

import java.util.Set;

public class ModBlockLootTables extends BlockLootSubProvider {

    public ModBlockLootTables() {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags());
    }

    @Override
    protected void generate() {

        //poppy

        LootItemCondition.Builder poppyFullyGrown =
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(ModBlocks.POPPY_CROP.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(PoppyCropBlock.AGE, 3));

        this.add(ModBlocks.POPPY_CROP.get(),
                block -> LootTable.lootTable()

                        .withPool(applyExplosionCondition(block,
                                LootPool.lootPool()
                                        .when(poppyFullyGrown)
                                        .setRolls(ConstantValue.exactly(1))
                                        .add(LootItem.lootTableItem(Items.POPPY)
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(4))))

                        ))
                        .withPool(applyExplosionCondition(block,
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1))
                                        .when(poppyFullyGrown.invert())
                                        .add(LootItem.lootTableItem(ModItems.POPPYSEED.get()))
                        ))
        );


        // hemp

        LootItemCondition.Builder hempFullyGrown =
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(ModBlocks.HEMP_CROP.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(HempCropBlock.AGE, 7));

        this.add(ModBlocks.HEMP_CROP.get(),
                block -> LootTable.lootTable()
                        .withPool(applyExplosionCondition(block,
                                LootPool.lootPool()
                                        .when(hempFullyGrown)
                                        .setRolls(ConstantValue.exactly(1))

                                        .add(LootItem.lootTableItem(ModItems.HEMPSEED.get()))

                                        .add(LootItem.lootTableItem(ModItems.HEMPSEED.get())
                                                .when(LootItemRandomChanceCondition.randomChance(0.5f)))
                        ))
                        .withPool(applyExplosionCondition(block,
                                LootPool.lootPool()
                                        .when(hempFullyGrown)
                                        .setRolls(ConstantValue.exactly(1))

                                        .add(LootItem.lootTableItem(ModItems.HEMPSEED.get()))

                                        .add(LootItem.lootTableItem(ModItems.HEMPSEED.get())
                                                .when(LootItemRandomChanceCondition.randomChance(0.5f)))
                        ))
                        .withPool(applyExplosionCondition(block,
                                LootPool.lootPool()
                                        .when(hempFullyGrown.invert())
                                        .add(LootItem.lootTableItem(ModItems.HEMPSEED.get()))
                        ))
        );

        LootItemCondition.Builder shroomFullyGrown =
                LootItemBlockStatePropertyCondition
                        .hasBlockStateProperties(ModBlocks.SHROOM_CROP.get())
                        .setProperties(StatePropertiesPredicate.Builder.properties()
                                .hasProperty(ShroomCropBlock.AGE, 7));

        this.add(ModBlocks.SHROOM_CROP.get(),
                block -> LootTable.lootTable()
                        .withPool(applyExplosionCondition(block,
                                LootPool.lootPool()
                                        .when(shroomFullyGrown)
                                        .setRolls(ConstantValue.exactly(1))

                                        .add(LootItem.lootTableItem(ModItems.SHROOMS1.get())
                                                .when(LootItemRandomChanceCondition.randomChance(0.1f)))

                                        .add(LootItem.lootTableItem(ModItems.SHROOMS2.get())
                                                .when(LootItemRandomChanceCondition.randomChance(0.25f))
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(2))))

                        ))

                        .withPool(applyExplosionCondition(block,
                                LootPool.lootPool()
                                        .when(shroomFullyGrown)
                                        .setRolls(ConstantValue.exactly(1))

                                        .add(LootItem.lootTableItem(Items.BROWN_MUSHROOM)
                                                .apply(SetItemCountFunction.setCount(ConstantValue.exactly(3))))

                        ))
        );
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream()
                .map(RegistryObject::get)::iterator;
    }
}