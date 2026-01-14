package net.warsnake.poppydelight.items;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LiteralPotItem extends Item {

    public LiteralPotItem(Properties p_41383_) {
        super(p_41383_);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level world, List<Component> tooltip, TooltipFlag flag) {
        int brown = 0x994500;
        int red = 0xFF0000;
        int black = 0xA9A9A9;

        tooltip.add(Component.literal("This is a pot.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("It does nothing special.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("Nothing is contained here.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("It is not an object of value or importance.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("This does nothing.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("And thus I must ask.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("Why are you here?").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("Is it for great wealth?").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("is it for this pot?").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("of course not.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("You want real pot.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("Now go my son...").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("hunt for the real pot we kept going for.").withStyle(style -> style.withColor(TextColor.fromRgb(brown))));
        tooltip.add(Component.literal("And maybe you will find the real size two pot.").withStyle(style -> style.withColor(TextColor.fromRgb(red))));
        tooltip.add(Component.literal("").withStyle(style -> style.withColor(TextColor.fromRgb(red))));
        tooltip.add(Component.literal("Size : 1").withStyle(style -> style.withColor(TextColor.fromRgb(black))));
    }

}