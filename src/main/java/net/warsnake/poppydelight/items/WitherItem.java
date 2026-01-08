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

    public WitherItem(Properties p_41383_) {
        super(p_41383_);
    }

    // make sure to verify that reagent is correct term for this
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("A rare reagent used for strengthening Maritox.")
                .withStyle(style -> style.withColor(TextColor.fromRgb(0x656565))));
    }

}