package net.warsnake.poppydelight.villager;

import net.minecraft.world.item.ItemStack;
import net.warsnake.poppydelight.items.ModItems;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DealerTrades {

    // NORMAL NIGHT TRADES
    public static List<TradeTemplate> getNightTrades(int level) {

        List<TradeTemplate> list = new ArrayList<>();

        if (level >= 1) {
            list.add(new TradeTemplate(
                    "dry_hemp",
                    new ItemStack(ModItems.DRYHEMPLEAF.get(), 3),
                    15,
                    6,
                    5,
                    0.2f
            ));
        }

        if (level >= 2) {
            list.add(new TradeTemplate(
                    "datura_seed",
                    new ItemStack(ModItems.DATURASEED.get(), 1),
                    30,
                    4,
                    10,
                    0.25f
            ));
        }

        if (level >= 3) {
            list.add(new TradeTemplate(
                    "datura_flower",
                    new ItemStack(ModItems.DATURAFLOWER.get(), 1),
                    25,
                    3,
                    15,
                    0.25f
            ));
        }

        if (level >= 4) {
            list.add(new TradeTemplate(
                    "shroom_void",
                    new ItemStack(ModItems.SHROOMS1.get(), 2),
                    38,
                    1,
                    20,
                    0.3f
            ));

            list.add(new TradeTemplate(
                    "shroom_brown",
                    new ItemStack(ModItems.SHROOMS2.get(), 2),
                    32,
                    2,
                    20,
                    0.3f
            ));
        }

        if (level >= 5) {
            list.add(new TradeTemplate(
                    "opium",
                    new ItemStack(ModItems.OPIUM.get(), 1),
                    30,
                    2,
                    30,
                    0.35f
            ));
        }

        return list;
    }

    // =========================
    //  VIP TRADES (UNLOCKED VIA DEMAND)
    // =========================
    public static List<TradeTemplate> getVipTrades(int level, Map<String, Integer> demand) {

        List<TradeTemplate> vip = new ArrayList<>();

        int totalDemand = demand.values().stream().mapToInt(i -> i).sum();

        //  not unlocked yet
        if (totalDemand < 12) return vip;

        //  vip lv1
        vip.add(new TradeTemplate(
                "vip_opium_bundle",
                new ItemStack(ModItems.OPIUM.get(), 2),
                55,
                1,
                50,
                0.6f
        ));

        // vipshrooms
        vip.add(new TradeTemplate(
                "vip_shroom_crate",
                new ItemStack(ModItems.SHROOMS1.get(), 3),
                45,
                1,
                40,
                0.5f
        ));

        // yes
        vip.add(new TradeTemplate(
                "vip_mixed_psy",
                new ItemStack(ModItems.SHROOMS2.get(), 2),
                50,
                1,
                45,
                0.55f
        ));

        // vipt2
        if (totalDemand >= 25) {

            vip.add(new TradeTemplate(
                    "vip_black_box",
                    new ItemStack(ModItems.OPIUM.get(), 3),
                    75,
                    1,
                    60,
                    0.7f
            ));

            vip.add(new TradeTemplate(
                    "vip_fungal_reserve",
                    new ItemStack(ModItems.SHROOMS1.get(), 5),
                    70,
                    1,
                    60,
                    0.7f
            ));
        }

        return vip;
    }
}