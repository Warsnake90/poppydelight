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

public class DaturaItem extends Item {

    public DaturaItem(Properties properties) {
        super(new Item.Properties()
                .food(new FoodProperties.Builder().alwaysEat().build()));

    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {

        return UseAnim.EAT;
    }

    private void applyToxicTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("datura")) {
            tag.putBoolean("datura", true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("I wouldn't touch it with a giant pole, unless I'm slipping it in the King's tea.")
                .withStyle(style -> style.withColor(TextColor.fromRgb(0x600400))));
        applyToxicTag(stack);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        applyToxicTag(stack);
    }

}