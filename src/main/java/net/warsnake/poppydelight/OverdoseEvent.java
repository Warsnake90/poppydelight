package net.warsnake.poppydelight;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class OverdoseEvent {

    private static final Map<UUID, Long> OverdoseStartTimes = new HashMap<>();
    private static final Map<UUID, Integer> OverdoseStageSent = new HashMap<>();

    private static final long TICKS_5_MIN = 2 * 60 * 20;
    private static final long TICKS_10_MIN = 5 * 60 * 20;
    private static final long TICKS_15_MIN = 10 * 60 * 20;
    private static final long TICKS_20_MIN = 15 * 60 * 20;


    public static void startOverdoseForPlayer(Player player) {
        UUID playerId = player.getUUID();

        if (OverdoseStartTimes.containsKey(playerId)) {
            return;
        }

        OverdoseStartTimes.put(playerId, player.level().getGameTime());
        OverdoseStageSent.put(playerId, 0);

    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Player player = event.player;
        UUID id = player.getUUID();

        if (!OverdoseStartTimes.containsKey(id))
            return;

        long startTick = OverdoseStartTimes.get(id);
        long elapsed = player.level().getGameTime() - startTick;

        if (elapsed >= TICKS_20_MIN) {
            if (OverdoseStageSent.get(id) < 4) {
                OverdoseStageSent.put(id, 4);
                player.sendSystemMessage(Component.literal("§4§lYou feel as if your about to collapse"));
            }

            apply20MinuteEffects(player);

            if (player.level().getGameTime() % 20 == 0) {
                if (player.getRandom().nextInt(50) == 0) {
                    if (player instanceof ServerPlayer sp) {
                        player.hurt(ModDamageSources.overdose(sp.serverLevel()), Float.MAX_VALUE);
                        player.hurt(ModDamageSources.overdose(sp.serverLevel()), Float.MAX_VALUE);
                    }
                }
            }
            return;
        }

        if (elapsed >= TICKS_15_MIN) {
            if (OverdoseStageSent.get(id) < 3) {
                OverdoseStageSent.put(id, 3);
                player.sendSystemMessage(Component.literal("§4Your muscles ache, But you don't care..."));
            }
            apply15MinuteEffects(player);
            return;
        }

        if (elapsed >= TICKS_10_MIN) {
            if (OverdoseStageSent.get(id) < 2) {
                OverdoseStageSent.put(id, 2);
                player.sendSystemMessage(Component.literal("§cYou feel sick, But You don't care..."));
            }
            apply10MinuteEffects(player);
            return;
        }

        if (elapsed >= TICKS_5_MIN) {
            if (OverdoseStageSent.get(id) < 1) {
                OverdoseStageSent.put(id, 1);
                player.sendSystemMessage(Component.literal("§cYour heart feels as if its about to explode..."));
            }
            apply5MinuteEffects(player);
        }
    }

    private static void apply5MinuteEffects(Player player) {
        if (player.level().getGameTime() % 60 == 0) {
            if (player.getRandom().nextInt(20) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 80, 0));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 0));
            }
        }

        if (player.level().getGameTime() % 60 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0F,
                    1.0F
            );
        }
    }

    private static void apply10MinuteEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 0));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 0));

        if (player.level().getGameTime() % 40 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0F,
                    1.0F
            );
        }
    }

    private static void apply15MinuteEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 120, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 120, 1));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 80, 1));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 150, 1));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 80, 1));

        if (player.level().getGameTime() % 20 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0F,
                    1.0F
            );
        }
    }

    private static void apply20MinuteEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 100, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 100, 1));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 100, 1));

        if (player.level().getGameTime() % 10 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0F,
                    1.0F
            );
        }
    }


    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;

        UUID id = player.getUUID();
        OverdoseStartTimes.remove(id);
        OverdoseStageSent.remove(id);
    }
}

