package net.warsnake.poppydelight.items;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ShroomItem extends Item {

    public ShroomItem(Properties properties) {
        super(new Properties()
                .food(new FoodProperties.Builder().alwaysEat().build()));
        new Properties().stacksTo(1);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    private void applyShroomTag(ItemStack stack) {
        CompoundTag tag = stack.getOrCreateTag();
        if (!tag.getBoolean("Shroom")) {
            tag.putBoolean("Shroom", true);
        }
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("This will be fun at parties.")
                .withStyle(ChatFormatting.BLUE));
        applyShroomTag(stack);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            stack.getOrCreateTag().putBoolean("Shroom", true);
            applyShroomTag(stack);
        }

        return super.finishUsingItem(stack, level, entity);

    }

    @Override
    public void onInventoryTick(ItemStack stack, Level level, Player player, int slotIndex, int selectedIndex) {
        if (!level.isClientSide) {
            stack.getOrCreateTag().putBoolean("Shroom", true);
            applyShroomTag(stack);
        }

        super.onInventoryTick(stack, level, player, slotIndex, selectedIndex);
    }
}
