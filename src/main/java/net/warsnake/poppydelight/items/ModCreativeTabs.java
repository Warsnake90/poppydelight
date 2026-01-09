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

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, PoppyDelight.MODID);

    public static final ResourceKey<CreativeModeTab> POPPYDELIGHT_TAB =
            ResourceKey.create(
                    Registries.CREATIVE_MODE_TAB,
                    new ResourceLocation(PoppyDelight.MODID, "main")
            );

    public static final RegistryObject<CreativeModeTab> MAIN_TAB =
            TABS.register("main", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.poppydelight.main"))
                    .icon(() -> new ItemStack(Items.POPPY))
                    .displayItems((params, output) -> {

                        // I should just delete not used items huh...

                      //  output.accept(ModItems.DEBUGITEM.get());
                       // output.accept(ModItems.WETPOPPYSEED.get());
                        // output.accept(ModItems.CRUSHEDPOPPYSEED.get());
                       // output.accept(ModItems.DRIEDPOPPYSEED.get());
                        output.accept(ModItems.POPPYSEED.get());
                        output.accept(ModItems.CRYINGPOD.get());
                        output.accept(ModItems.RAWOPIUM.get());
                        output.accept(ModItems.LOWQUALITY.get());
                        output.accept(ModItems.OPIUM.get());
                        output.accept(ModItems.HIGHQUALITY.get());
                        output.accept(ModItems.TDXGLAND.get());
                        output.accept(ModItems.TDXAGENT.get());
                        output.accept(ModItems.TDXVIAL.get());
                        output.accept(ModItems.DUST.get());
                        output.accept(ModItems.WITHERDUST.get());
                        output.accept(ModItems.LOWPACKAGE.get());
                        output.accept(ModItems.PACKAGE.get());
                        output.accept(ModItems.HIGHPACKAGE.get());
                        output.accept(ModItems.HEMPPACKAGE.get());
                        output.accept(ModItems.WORMWOODLEAF.get());
                        output.accept(ModItems.ABSINTHE.get());
                        output.accept(ModItems.HEMPSEED.get());
                        output.accept(ModItems.HEMPLEAF.get());
                        output.accept(ModItems.WETROLLINGPAPER.get());
                        output.accept(ModItems.ROLLINGPAPER.get());
                        output.accept(ModItems.JOINT.get());
                        output.accept(ModItems.LITERALPOT.get());

                    })
                    .build()
            );
}
