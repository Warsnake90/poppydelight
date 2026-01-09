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
public class SpikeItem {

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

        if (!main.is(FOODS_TAG)) return;

        // avoid checking three or four tags every time anything eats... too bad the rest of this shitty code negates this save
        if (main.hasTag() && main.getOrCreateTag().getBoolean("opium")) return;

        ItemStack opiumItem = main.copy();
        opiumItem.setCount(1);

        opiumItem.getOrCreateTag().putBoolean("opium", true);

        if (offhand.is(ModItems.OPIUM.get())) {
            opiumItem.getOrCreateTag().putBoolean("medopium", true);
        } else if (offhand.is(ModItems.LOWQUALITY.get())) {
            opiumItem.getOrCreateTag().putBoolean("lowopium", true);
        } else if (offhand.is(ModItems.HIGHQUALITY.get())) {
            opiumItem.getOrCreateTag().putBoolean("highopium", true);
        }

        main.shrink(1);
        offhand.shrink(1);

        if (!player.addItem(opiumItem)) {
            player.drop(opiumItem, false);
        }

       // player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);
        event.setCanceled(true);
        event.setCancellationResult(net.minecraft.world.InteractionResult.SUCCESS);
    }
}
