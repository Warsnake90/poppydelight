package net.warsnake.poppydelight.villager;

import net.minecraft.world.item.ItemStack;

public class TradeTemplate {

    public final String id;
    public final ItemStack output;
    public final int baseGoldCost;
    public final int maxUses;
    public final int xp;
    public final float priceMultiplier;

    public TradeTemplate(String id, ItemStack output, int baseGoldCost, int maxUses, int xp, float priceMultiplier) {
        this.id = id;
        this.output = output;
        this.baseGoldCost = baseGoldCost;
        this.maxUses = maxUses;
        this.xp = xp;
        this.priceMultiplier = priceMultiplier;
    }
}