package net.warsnake.poppydelight;

import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warsnake.poppydelight.items.ModItems;

@Mod.EventBusSubscriber(modid = "poppydelight")
public class SpecialSpikingEvent {

    private static final TagKey<Item> FOODS_TAG =
            TagKey.create(Registries.ITEM, new ResourceLocation("poppydelight", "foods"));

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickItem event) {
        var player = event.getEntity();
        Level level = player.level();

        if (level.isClientSide) return;
        if (!player.isShiftKeyDown()) return;
        if (event.getHand() != InteractionHand.MAIN_HAND) return;

        ItemStack main = player.getMainHandItem();
        ItemStack offhand = player.getOffhandItem();

        if (!offhand.is(ModItems.HEMPLEAF.get())) return;
        if (!main.is(FOODS_TAG)) return;

        ItemStack PotItem = main.copy();
        PotItem.setCount(1);

        int potcount = offhand.getCount();


        if (offhand.is(ModItems.HEMPLEAF.get()) && potcount > 2) {
            PotItem.getOrCreateTag().putBoolean("pot", true);
        }

        main.shrink(1);
        offhand.shrink(3);

        if (!player.addItem(PotItem)) {
            player.drop(PotItem, false);
        }

        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
    }
}
