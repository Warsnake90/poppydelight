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

public class JointItem extends Item {

    public JointItem(Properties properties) {
        super(new Properties()
                .food(new FoodProperties.Builder().alwaysEat().build()));
        new Properties().stacksTo(1);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player) {
            spawnMouthSmoke(player, level);
            applyJointTag(stack);
        }

        return super.finishUsingItem(stack, level, entity);
    }

    public static void spawnMouthSmoke(Player player, Level level) {
        if (!level.isClientSide) return;

        Vec3 eyePos = player.getEyePosition(1.0F);

        Vec3 look = player.getLookAngle();

        Vec3 mouthPos = eyePos
                .add(look.scale(0.25D))
                .subtract(0, 0.15D, 0);

        for (int i = 0; i < 6; i++) {
            double spread = 0.05D;

            double xSpeed = look.x * 0.02D + (level.random.nextGaussian() * spread);
            double ySpeed = look.y * 0.02D + (level.random.nextGaussian() * spread);
            double zSpeed = look.z * 0.02D + (level.random.nextGaussian() * spread);

            level.addParticle(
                    ParticleTypes.CAMPFIRE_COSY_SMOKE,
                    mouthPos.x,
                    mouthPos.y,
                    mouthPos.z,
                    xSpeed,
                    ySpeed,
                    zSpeed
            );
        }
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
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
