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

public class LowOpiumItem extends Item {

    public LowOpiumItem(Properties p_41383_) {
        super(p_41383_);
    }

    private void applygeneralTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("opium")) {
            tag.putBoolean("opium", true);
        }
    }

    private void applyOpiumTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("lowopium")) {
            tag.putBoolean("lowopium", true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("Could be refined further...")
                .withStyle(ChatFormatting.BLUE));
        applyOpiumTag(stack);
        applygeneralTag(stack);
    }

}
