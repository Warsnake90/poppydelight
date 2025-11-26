package net.warsnake.poppydelight.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MedOpiumItem extends Item {

    public MedOpiumItem(Properties p_41383_) {
        super(p_41383_);
    }

    private void applyOpiumTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("medopium")) {
            tag.putBoolean("medopium", true);
        }
    }
    private void applygeneralTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("opium")) {
            tag.putBoolean("opium", true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("idk if its good or not but...").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("Damn it feels good").withStyle(ChatFormatting.BLUE));
        applyOpiumTag(stack);
        applygeneralTag(stack);
    }


}
