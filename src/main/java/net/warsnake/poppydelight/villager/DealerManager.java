package net.warsnake.poppydelight.villager;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warsnake.poppydelight.PoppyDelight;
import net.warsnake.poppydelight.items.ModItems;

import java.util.*;

@Mod.EventBusSubscriber(modid = PoppyDelight.MODID)
public class DealerManager {

    private static final int VIP_THRESHOLD = 12;
    private static final double DECAY_RATE = 0.85;

    private static final String LAST_SHUFFLE = "DealerShuffleTime";


    // INTERACTION RULE
    @SubscribeEvent
    public static void onInteract(PlayerInteractEvent.EntityInteract event) {
        Player player = event.getEntity();

        if (!(event.getTarget() instanceof Villager villager)) return;

        if (!villager.getVillagerData().getProfession()
                .equals(ModVillagers.DEALER.get())) return;

        if (event.getLevel().isClientSide) return;

        ServerLevel level = (ServerLevel) event.getLevel();

        //  DAY = REFUSE TRADE
        if (level.isDay()) {
            event.setCanceled(true);
            player.sendSystemMessage(Component.literal("Come back at night..."));


            return;
        }

        ensureTrades(level, villager);
    }

    // NIGHT TICK SYSTEM
    @SubscribeEvent
    public static void onTick(TickEvent.LevelTickEvent event) {
        if (!(event.level instanceof ServerLevel level)) return;

        if (level.getDayTime() % 24000 == 13000) {
            BlackMarketSaveData data = BlackMarketSaveData.BlackMarketDataManager.get(level);

            data.decay(); //  demand decay each night
        }

        if (level.isDay()) return;

        level.getAllEntities().forEach(entity -> {
            if (!(entity instanceof Villager villager)) return;

            if (!villager.getVillagerData().getProfession()
                    .equals(ModVillagers.DEALER.get())) return;

            long time = level.getGameTime();
            long last = villager.getPersistentData().getLong(LAST_SHUFFLE);

            if (time - last > 6000) {
                shuffleTrades(level, villager);
                villager.getPersistentData().putLong(LAST_SHUFFLE, time);
            }
        });
    }

    // TRADE BUILDER
    private static void ensureTrades(ServerLevel level, Villager villager) {
        if (villager.getOffers().isEmpty()) {
            shuffleTrades(level, villager);
        }
    }

    private static void shuffleTrades(ServerLevel level, Villager villager) {

        BlackMarketSaveData data = BlackMarketSaveData.BlackMarketDataManager.get(level);
        Map<String, Integer> demand = data.getDemand();

        int villagerLevel = villager.getVillagerData().getLevel();

        List<TradeTemplate> pool = DealerTrades.getNightTrades(villagerLevel);

        //  VIP unlock
        pool.addAll(getVipTrades(demand));

        Collections.shuffle(pool);

        MerchantOffers offers = new MerchantOffers();

        for (TradeTemplate trade : pool) {

            int popularity = demand.getOrDefault(trade.id, 0);
            int cost = applyInflation(trade.baseGoldCost, popularity);

            offers.add(new MerchantOffer(
                    new net.minecraft.world.item.ItemStack(Items.GOLD_INGOT, cost),
                    new net.minecraft.world.item.ItemStack(trade.output.getItem(), trade.output.getCount()),
                    trade.maxUses,
                    trade.xp,
                    trade.priceMultiplier
            ));
        }

        villager.setOffers(offers);
    }

    private static int applyInflation(int base, int demand) {
        double multiplier = 1.0 + (demand * 0.15);
        return Math.max(1, (int) Math.round(base * multiplier));
    }


    private static List<TradeTemplate> getVipTrades(Map<String, Integer> demand) {

        int total = demand.values().stream().mapToInt(i -> i).sum();

        if (total < VIP_THRESHOLD) return List.of();

        return List.of(
                new TradeTemplate("vip_opium",
                        new net.minecraft.world.item.ItemStack(ModItems.OPIUM.get(), 2),
                        55, 1, 50, 0.6f),

                new TradeTemplate("vip_shrooms",
                        new net.minecraft.world.item.ItemStack(ModItems.SHROOMS1.get(), 3),
                        45, 1, 40, 0.5f)
        );
    }

    public static void registerTradeUse(ServerLevel level, String id) {
        BlackMarketSaveData data = BlackMarketSaveData.BlackMarketDataManager.get(level);
        data.increaseDemand(id);
    }
}