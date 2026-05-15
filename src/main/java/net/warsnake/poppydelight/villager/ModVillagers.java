package net.warsnake.poppydelight.villager;

import com.google.common.collect.ImmutableSet;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;
import vectorwing.farmersdelight.common.registry.ModBlocks;

import java.util.Set;

public class ModVillagers {

    public static final DeferredRegister<PoiType> POI_TYPES =
            DeferredRegister.create(ForgeRegistries.POI_TYPES, PoppyDelight.MODID);
    public static final DeferredRegister<VillagerProfession> PROFESSIONS =
            DeferredRegister.create(ForgeRegistries.VILLAGER_PROFESSIONS, PoppyDelight.MODID);

    public static final RegistryObject<PoiType> DRYING_POI = POI_TYPES.register("drying_table_poi",
            () -> new PoiType(ImmutableSet.copyOf(ModBlocks.TATAMI.get().getStateDefinition().getPossibleStates()),
                    1, 3));

    public static final RegistryObject<VillagerProfession> DEALER =
            PROFESSIONS.register("dealer", () -> new VillagerProfession("dealer",
                    holder -> holder.get() == DRYING_POI.get(), holder -> holder.get() == DRYING_POI.get(),
                    ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_CLERIC));


    public static void register(IEventBus eventBus) {
        POI_TYPES.register(eventBus);
        PROFESSIONS.register(eventBus);
    }
}