package net.warsnake.poppydelight.items;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ModCreativeTabs {

    public static final ResourceKey<CreativeModeTab> POPPYDELIGHT_TAB =
            ResourceKey.create(
                    Registries.CREATIVE_MODE_TAB,
                    new ResourceLocation("poppydelight", "main")
            );

    public static CreativeModeTab TAB_INSTANCE;

    public static void register(BiConsumer<ResourceKey<CreativeModeTab>, CreativeModeTab> helper) {

        TAB_INSTANCE = CreativeModeTab.builder()
                .title(Component.translatable("creativetab.poppydelight.main"))
                .icon(() -> new ItemStack(Items.POPPY))
                .displayItems((params, output) -> {
                    output.accept(ModItems.DEBUGITEM);
                })
                .build();

        helper.accept(POPPYDELIGHT_TAB, TAB_INSTANCE);
    }

    public static void addToCreativeTab(ResourceKey<CreativeModeTab> tab, Consumer<Item> helper) {
        if (tab == POPPYDELIGHT_TAB) {
            helper.accept(ModItems.DEBUGITEM);
            helper.accept(ModItems.TDXVIAL);
            helper.accept(ModItems.TDXGLAND);
            helper.accept(ModItems.TDXAGENT);
            helper.accept(ModItems.OPIUM);
            helper.accept(ModItems.POPPYSEED);
            helper.accept(ModItems.CRUSHEDPOPPYSEED);
            helper.accept(ModItems.DRIEDPOPPYSEED);
            helper.accept(ModItems.WETPOPPYSEED);
            helper.accept(ModItems.LOWQUALITY);
            helper.accept(ModItems.HIGHQUALITY);
            helper.accept(ModItems.RAWOPIOD);

        }
    }



}