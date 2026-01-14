package net.warsnake.poppydelight.items;

import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.player.Player;

import org.jetbrains.annotations.Nullable;
import java.util.List;

public class TdxItem extends Item {

    public TdxItem(Properties properties) {
        super(new Item.Properties()
                .food(new FoodProperties.Builder().alwaysEat().build()));

        // what the fuck was the purpose of this... it did NOTHING
       // new Item.Properties().stacksTo(1);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {

        // probably should let this change based on the item but im too lazy to add that... note to add it later
        return UseAnim.DRINK;
    }

    private void applyToxicTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("Toxic")) {
            tag.putBoolean("Toxic", true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("You probably shouldn't taste test it...")
                .withStyle(style -> style.withColor(TextColor.fromRgb(0x600400))));
        applyToxicTag(stack);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        applyToxicTag(stack);
    }

}