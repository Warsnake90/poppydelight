package net.warsnake.poppydelight.blocks.entity;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;
import net.warsnake.poppydelight.blocks.ModBlocks;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, PoppyDelight.MODID);

    public static final RegistryObject<BlockEntityType<DryingTableBlockEntity>> DRYINGTABLE_BE =
            BLOCK_ENTITIES.register("dryingtable_be", () ->
                    BlockEntityType.Builder.of(DryingTableBlockEntity::new,
                            ModBlocks.DRYING_TABLE.get()).build(null));


    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}
