package net.warsnake.poppydelight;

import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warsnake.poppydelight.effect.ModEffects;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class CannabisEvent {

    private static final long TICKS_3_MIN = 3 * 60 * 20;
    private static final long TICKS_5_MIN = 5 * 60 * 20;
    private static final long TICKS_10_MIN = 10 * 60 * 20;
    private static final long TICKS_20_MIN = 20 * 60 * 20;
    private static final long TICKS_25_MIN = 25 * 60 * 20;

    private static final Map<UUID, Long> shroomStartTimes = new HashMap<>();
    private static final Map<UUID, Integer> stageSent = new HashMap<>();
    private static final Map<UUID, Integer> consumptionCount = new HashMap<>();
    private static final Map<UUID, Long> lastEffectDecayTime = new HashMap<>();

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        UUID id = player.getUUID();

        if (consumptionCount.getOrDefault(id, 0) >= 3) {
            player.sendSystemMessage(Component.literal("§cYou are already high."));
            return;
        }

        if (shroomStartTimes.containsKey(id)) return;

        ItemStack stack = event.getItem();
        if (stack.hasTag() && stack.getTag().getBoolean("pot")) {
            shroomStartTimes.put(id, player.level().getGameTime());
            stageSent.put(id, 0);
            consumptionCount.put(id, consumptionCount.getOrDefault(id, 0) + 1);
            player.sendSystemMessage(Component.literal("§aThat will feel better..."));
            lastEffectDecayTime.put(id, player.level().getGameTime());
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        UUID id = player.getUUID();

        if (!shroomStartTimes.containsKey(id)) return;

        long elapsed = player.level().getGameTime() - shroomStartTimes.get(id);


        long lastDecayTime = lastEffectDecayTime.getOrDefault(id, 0L);
        if (elapsed - lastDecayTime >= TICKS_10_MIN) {

            decayEffect(id);
            lastEffectDecayTime.put(id, player.level().getGameTime());
        }


        if (elapsed >= TICKS_25_MIN) {
            cleanup(id);
            return;
        }


        if (elapsed >= TICKS_20_MIN && stageSent.get(id) < 4) {
            stageSent.put(id, 4);
            apply20MinuteEffects(player);
        } else if (elapsed >= TICKS_10_MIN && stageSent.get(id) < 3) {
            stageSent.put(id, 3);
            apply10MinuteEffects(player);
        } else if (elapsed >= TICKS_5_MIN && stageSent.get(id) < 2) {
            stageSent.put(id, 2);
            apply5MinuteEffects(player);
        } else if (elapsed >= TICKS_3_MIN && stageSent.get(id) < 1) {
            stageSent.put(id, 1);
            apply3MinuteEffects(player);
        }
    }

    private static void apply3MinuteEffects(Player p) {
        int effectLevel = getEffectLevel(p);
        apply(p, 2399,
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2399, effectLevel),
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2399, effectLevel),
                new MobEffectInstance(MobEffects.WEAKNESS, 2399, effectLevel),
                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2399, effectLevel),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 2399, effectLevel, false, true, true)
        );
    }

    private static void apply5MinuteEffects(Player p) {
        int effectLevel = getEffectLevel(p);
        apply(p, 5999,
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 5999, effectLevel),
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5999, effectLevel - 1),
                new MobEffectInstance(MobEffects.WEAKNESS, 5999, effectLevel),
                new MobEffectInstance(MobEffects.REGENERATION, 5999, effectLevel),
                new MobEffectInstance(ModEffects.POTHIGH.get(), 5999, 0),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 5999, effectLevel + 1, false, true, true)
        );
    }

    private static void apply10MinuteEffects(Player p) {
        int effectLevel = getEffectLevel(p);
        apply(p, 5999,
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 11999, effectLevel),
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 11999, effectLevel),
                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 11999, 1),
                new MobEffectInstance(ModEffects.POTHIGH.get(), 11999, 0),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 11999, effectLevel + 3, false, true, true)
        );
    }

    private static void apply20MinuteEffects(Player p) {
        int effectLevel = getEffectLevel(p);
        apply(p, 11999,
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5999, effectLevel + 1),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 5999, effectLevel + 2, false, true, true)
        );
    }

    private static void apply(Player p, int duration, MobEffectInstance... effects) {
        for (MobEffectInstance e : effects) {
            p.addEffect(e);
        }
    }

    private static void decayEffect(UUID id) {
        int currentLevel = getEffectLevel(id);
        if (currentLevel > 0) {
            consumptionCount.put(id, currentLevel - 1);
        }
    }

    private static void cleanup(UUID id) {
        shroomStartTimes.remove(id);
        stageSent.remove(id);
        consumptionCount.remove(id);
        lastEffectDecayTime.remove(id);
    }

    private static int getEffectLevel(Player p) {
        int consumption = consumptionCount.getOrDefault(p.getUUID(), 0);
        if (consumption >= 3) {
            return 0;
        }
        return consumption;
    }

    private static int getEffectLevel(UUID id) {
        return consumptionCount.getOrDefault(id, 0);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            cleanup(player.getUUID());
        }
    }
}