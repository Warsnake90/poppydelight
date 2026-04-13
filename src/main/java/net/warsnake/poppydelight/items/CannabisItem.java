package net.warsnake.poppydelight.items;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CannabisItem extends Item {
    public CannabisItem(Properties pProperties) {
        super(pProperties);
    }

    private void applyJointTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("pot")) {
            tag.putBoolean("pot", true);
        }
    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (!level.isClientSide) {
            stack.getOrCreateTag().putBoolean("pot", true);
            applyJointTag(stack);
        }

        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("This will be good.")
                .withStyle(ChatFormatting.BLUE));
        applyJointTag(stack);
    }

    @Override
    public void onCraftedBy(ItemStack stack, Level world, Player player) {
        applyJointTag(stack);
    }

}
