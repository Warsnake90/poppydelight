package net.warsnake.poppydelight;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ToxicFoodEvent {

    private static final Map<UUID, Long> toxicStartTimes = new HashMap<>();
    private static final Map<UUID, Integer> stageSent = new HashMap<>();

    private static final long TICKS_5_MIN = 5 * 60 * 20;
    private static final long TICKS_10_MIN = 10 * 60 * 20;
    private static final long TICKS_15_MIN = 15 * 60 * 20;
    private static final long TICKS_20_MIN = 20 * 60 * 20;

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack stack = event.getItem();

        if (stack.hasTag() && stack.getTag().getBoolean("Toxic")) {
            toxicStartTimes.put(player.getUUID(), player.level().getGameTime());
            stageSent.put(player.getUUID(), 0);

            player.sendSystemMessage(Component.literal("§eit tastes awfully bitter..."));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END)
            return;

        Player player = event.player;
        UUID id = player.getUUID();

        if (!toxicStartTimes.containsKey(id))
            return;

        long startTick = toxicStartTimes.get(id);
        long elapsed = player.level().getGameTime() - startTick;

        if (elapsed >= TICKS_20_MIN) {
            if (stageSent.get(id) < 4) {
                stageSent.put(id, 4);
                player.sendSystemMessage(Component.literal("§4§lYou feel as if your about to die..."));
            }

            apply20MinuteEffects(player);

            if (player.level().getGameTime() % 20 == 0) {
                if (player.getRandom().nextInt(50) == 0) {
                    if (player instanceof ServerPlayer sp) {
                        player.hurt(ModDamageSources.toxicFood(sp.serverLevel()), Float.MAX_VALUE);
                        player.hurt(ModDamageSources.toxicFood(sp.serverLevel()), Float.MAX_VALUE);
                    }
                }
            }
            return;
        }

        if (elapsed >= TICKS_15_MIN) {
            if (stageSent.get(id) < 3) {
                stageSent.put(id, 3);
                player.sendSystemMessage(Component.literal("§4Your muscles ache, and your vision starts to blur..."));
            }
            apply15MinuteEffects(player);
            return;
        }

        if (elapsed >= TICKS_10_MIN) {
            if (stageSent.get(id) < 2) {
                stageSent.put(id, 2);
                player.sendSystemMessage(Component.literal("§cYou feel especially weak..."));
            }
            apply10MinuteEffects(player);
            return;
        }

        if (elapsed >= TICKS_5_MIN) {
            if (stageSent.get(id) < 1) {
                stageSent.put(id, 1);
                player.sendSystemMessage(Component.literal("§cYour heart begins to beat awfully fast..."));
            }
            apply5MinuteEffects(player);
        }
    }

    private static void apply5MinuteEffects(Player player) {
        if (player.level().getGameTime() % 60 == 0) {
            if (player.getRandom().nextInt(20) == 0) {
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 0));
                player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));
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
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 0));

        if (player.level().getGameTime() % 40 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0F,
                    1.0F
            );
        }
    }

    private static void apply15MinuteEffects(Player player) {
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 60, 0));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 60, 1));
        player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 60, 1));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, 60, 1));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, 60, 1));

        if (player.level().getGameTime() % 20 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT,
                    1.0F,
                    1.0F
            );
        }

        ResourceLocation SHROOM_SHADER =
                new ResourceLocation("poppydelight", "shaders/post/shrooms.json");

        if ((player.level().isClientSide)) {

            Minecraft minecraft = Minecraft.getInstance();
            GameRenderer renderer = minecraft.gameRenderer;
            renderer.loadEffect(SHROOM_SHADER);
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
        toxicStartTimes.remove(id);
        stageSent.remove(id);
    }
}

