package net.warsnake.poppydelight.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.List;

public class AbsintheDrinkItem extends Item {

    public AbsintheDrinkItem() {
        super(new Item.Properties()
                .stacksTo(1)
                .food(new FoodProperties.Builder()
                        .nutrition(6)
                        .saturationMod(0.8F)
                        .alwaysEat()
                        .build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!level.isClientSide) {
            entity.removeEffect(MobEffects.HUNGER);
            entity.removeEffect(MobEffects.CONFUSION);
            entity.addEffect(new MobEffectInstance(
                    MobEffects.REGENERATION,
                    300,
                    0
            ));

            entity.addEffect(new MobEffectInstance(
                    (MobEffect) BnCEffects.TIPSY.get(),
                    12000, 1, false, true, true));
        }

        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.DRINK;
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 32;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.literal("a green alcoholic beverage known for its healing properties.")
                .withStyle(style -> style.withColor(TextColor.fromRgb(0x4599685))));
    }
}