package net.warsnake.poppydelight.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.warsnake.poppydelight.PoppyDelight;

public class ModCreativeTabs {

    // Deferred register for creative tabs
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PoppyDelight.MODID);

    // Key for the tab
    public static final ResourceKey<CreativeModeTab> POPPYDELIGHT_TAB =
            ResourceKey.create(
                    Registries.CREATIVE_MODE_TAB,
                    new ResourceLocation(PoppyDelight.MODID, "main")
            );

    // The actual tab
    public static final RegistryObject<CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.poppydelight.main"))
                    .icon(() -> new ItemStack(Items.POPPY))
                    .displayItems((params, output) -> {

                        // Add all items using DeferredRegister references
                        output.accept(ModItems.DEBUGITEM.get());
                        output.accept(ModItems.POPPYSEED.get());
                        output.accept(ModItems.WETPOPPYSEED.get());
                        output.accept(ModItems.CRUSHEDPOPPYSEED.get());
                        output.accept(ModItems.DRIEDPOPPYSEED.get());
                        output.accept(ModItems.RAWOPIUM.get());
                        output.accept(ModItems.LOWQUALITY.get());
                        output.accept(ModItems.OPIUM.get());
                        output.accept(ModItems.HIGHQUALITY.get());
                        output.accept(ModItems.TDXGLAND.get());
                        output.accept(ModItems.TDXAGENT.get());
                        output.accept(ModItems.TDXVIAL.get());
                        output.accept(ModItems.DUST.get());

                    })
                    .build()
            );
}
