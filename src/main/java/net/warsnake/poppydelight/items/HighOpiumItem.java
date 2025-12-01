package net.warsnake.poppydelight.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class HighOpiumItem extends Item {

    public HighOpiumItem(Properties properties) {
        super(new Item.Properties()
                .food(new FoodProperties.Builder().alwaysEat().build()));
        new Item.Properties().stacksTo(1);
    }

/*
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
    return UseAnim.DRINK;
     }
 */
    private void applygeneralTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("opium")) {
            tag.putBoolean("opium", true);
        }
    }

    private void applyOpiumTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("highopium")) {
            tag.putBoolean("highopium", true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Maybe 96.2%?").withStyle(ChatFormatting.BLUE));
        applyOpiumTag(stack);
        applygeneralTag(stack);
    }

}
