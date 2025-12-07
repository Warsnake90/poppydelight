package net.warsnake.poppydelight;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warsnake.poppydelight.OverdoseEvent;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Random;

@Mod.EventBusSubscriber
public class OpiumEvents {

    private static final Map<UUID, Integer> opiumLevel = new HashMap<>();
    private static final Map<UUID, Long> lastDecreaseTime = new HashMap<>();
    private static final Random rand = new Random();
    private static final long REAL_TIME_15_MINUTES = 15 * 1200;

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack stack = event.getItem();
        UUID uuid = player.getUUID();

        if (!stack.hasTag() || !stack.getTag().getBoolean("opium"))
            return;

        int amount = 0;

        if (stack.getTag().getBoolean("lowopium")) {
            amount = 1 + (rand.nextFloat() < 0.5f ? 1 : 0);
        }

        else if (stack.getTag().getBoolean("medopium")) {
            float r = rand.nextFloat();
            if (r < 0.10f) amount = 5;
            else if (r < 0.60f) amount = 4;
            else amount = 3;
        }

        else if (stack.getTag().getBoolean("highopium")) {
            float r = rand.nextFloat();
            if (r < 0.25f) amount = 7;
            else if (r < 0.75f) amount = 6;
            else amount = 5;
        }

        int current = opiumLevel.getOrDefault(uuid, 0);
        current += amount;
        opiumLevel.put(uuid, current);

        lastDecreaseTime.putIfAbsent(uuid, player.level().getGameTime());

        applyStageEffects(player, current);

    }

    // ughhhhhh ts is so overly complex
    @SubscribeEvent
    public static void onWorldTick(TickEvent.PlayerTickEvent event) {
        Player player = event.player;
        UUID uuid = player.getUUID();

        if (!opiumLevel.containsKey(uuid))
            return;

        long last = lastDecreaseTime.getOrDefault(uuid, 0L);
        long now = player.level().getGameTime();

        if (now - last >= REAL_TIME_15_MINUTES) {
            int lvl = opiumLevel.get(uuid);

            if (lvl > 0) {
                lvl--;
                opiumLevel.put(uuid, lvl);
                applyStageEffects(player, lvl);
            }
            lastDecreaseTime.put(uuid, now);
        }
    }

    @SubscribeEvent
    public static void onMilkDrink(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player))
            return;

        ItemStack item = event.getItem();
        if (item.getItem() == Items.MILK_BUCKET) {
            UUID uuid = player.getUUID();

            int currentOpiumLevel = opiumLevel.getOrDefault(uuid, 0);
            applyStageEffects(player, currentOpiumLevel);

        }
    }

    private static void applyStageEffects(Player player, int level) {

        if (level <= 0) {
            return;
        }

        switch (level) {
            case 0 -> { return; } // if this somehow happens ill be very confused, but its good as a fallback ig
            case 1,2,3,4,5,6,7,8,9,10 -> apply1Effects(player);
            case 11,12,13,14,15,16,17,18,19,20 -> apply2Effects(player);
             case 21,22,23,24,25 -> apply3Effects(player);
           case 26,27,28,29 -> apply4Effects(player);
            default -> apply5Effects(player);
        }
    }

    private static void apply1Effects(Player player) {
        int duration = 12000;
        int potency = 2;

        player.sendSystemMessage(Component.literal("§eMan you feel good..."));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 0));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER, duration, 1));
        player.addEffect(new MobEffectInstance(
                (MobEffect) BnCEffects.TIPSY.get(),
                duration, potency, false, true, true));
    }
    private static void apply2Effects(Player player) {
        int duration = 12000;
        int potency = 3;

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 1));
        player.sendSystemMessage(Component.literal("§cYou feel Really good..."));
        player.addEffect(new MobEffectInstance(
                (MobEffect) BnCEffects.TIPSY.get(),
                duration, potency, false, true, true));

    }
    private static void apply3Effects(Player player) {
        int duration = 12000;
        int potency = 4;

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 2));
        player.sendSystemMessage(Component.literal("§cYou feel like you could fly..."));
        player.addEffect(new MobEffectInstance(
                (MobEffect) BnCEffects.TIPSY.get(),
                duration, potency, false, true, true));

        if (player.level().getGameTime() % 25 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT, 1.0F, 1.0F
            );
        }
    }
    private static void apply4Effects(Player player) {
        int duration = 12000;
        int potency = 6;

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, 3));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, duration, 1));
        player.sendSystemMessage(Component.literal("§cHow much further does this ride go?"));
        player.addEffect(new MobEffectInstance(
                (MobEffect) BnCEffects.TIPSY.get(),
                duration, potency, false, true, true));

        if (player.level().getGameTime() % 20 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT, 1.0F, 1.0F
            );
        }
    }

    private static void apply5Effects(Player player) {
        int duration = 12000;
        int potency = 4;

        player.sendSystemMessage(Component.literal("§c§lYour body aches... but who cares when you feel this good"));

        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, duration, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, duration, 3));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION, duration, 1));

        OverdoseEvent.startOverdoseForPlayer(player);

        if (player.level().getGameTime() % 20 == 0) {
            player.playSound(
                    SoundEvents.WARDEN_HEARTBEAT, 1.0F, 1.0F
            );
        }
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        // steal ts from pufferfish
        if (!(event.getEntity() instanceof Player player)) return;
        UUID id = player.getUUID();
        opiumLevel.remove(id);
        lastDecreaseTime.remove(id);
    }

    public static void sendOpiumLevelToPlayer(Player player) {
        if (player.level().isClientSide) return;

        UUID uuid = player.getUUID();
        int level = opiumLevel.getOrDefault(uuid, 0);

        player.sendSystemMessage(
                Component.literal("§eYour current opium level is: §6" + level)
        );
    }

    @Mod.EventBusSubscriber
    public class OpiumCommand {

        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {

            event.getDispatcher().register(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("opium")
                            .requires(source -> source.hasPermission(1))
                            .then(Commands.argument("player", EntityArgument.player())
                                    .executes(ctx -> {
                                        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                        OpiumEvents.sendOpiumLevelToPlayer(target);
                                        ctx.getSource().sendSuccess(() -> net.minecraft.network.chat.Component.literal("Poppydelight: Opium level checked for " + target.getName().getString()), false
                                        );
                                        return 1;
                                    }))
            );
        }
    }

}


