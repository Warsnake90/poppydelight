package net.warsnake.poppydelight.items;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WitherItem extends Item {

    public WitherItem(Properties properties) {
        super(new Properties()
                .food(new FoodProperties.Builder().alwaysEat().build()));
        new Properties().stacksTo(1);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Catalyst for Making Maritox")
                .withStyle(style -> style.withColor(TextColor.fromRgb(0x909090))));
    }

}