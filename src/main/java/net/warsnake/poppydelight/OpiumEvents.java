package net.warsnake.poppydelight;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warsnake.poppydelight.effect.ModEffects;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import umpaz.brewinandchewin.common.registry.BnCEffects;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public class OpiumEvents {

    private static final Random rand = new Random();

    private static final Map<UUID, Integer> opiumLevel       = new HashMap<>();
    private static final Map<UUID, Long>    lastDecreaseTick = new HashMap<>();

    private static final long DECREASE_INTERVAL = 5 * 60 * 20L; // 5 minutes


    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        ItemStack stack = event.getItem();
        if (!stack.hasTag() || !stack.getTag().getBoolean("opium")) return;

        int amount = rollAmount(stack);
        if (amount == 0) return;

        UUID uuid = player.getUUID();
        int newLevel = opiumLevel.getOrDefault(uuid, 0) + amount;
        opiumLevel.put(uuid, newLevel);

        // Only set the decrease timer if this is their first dose
        lastDecreaseTick.putIfAbsent(uuid, player.level().getGameTime());

        applyStageEffects(player, newLevel);
    }

    private static int rollAmount(ItemStack stack) {
        if (stack.getTag().getBoolean("lowopium")) {
            return rollDose(2, 1, 4);   // centered ~2 range 1-4
        } else if (stack.getTag().getBoolean("medopium")) {
            return rollDose(5, 3, 9);   // centered ~5 range 3-9
        } else if (stack.getTag().getBoolean("highopium")) {
            return rollDose(10, 6, 18); // centered ~10 range 6-18, fat tail upward
        }
        return 0;
    }

    private static int rollDose(int center, int min, int max) {
        float r1 = rand.nextFloat();
        float r2 = rand.nextFloat();
        float avg = (r1 + r2) / 2f;

        if (rand.nextFloat() < 0.06f) {
            avg = rand.nextFloat();
        }

        int range = max - min;
        int rolled = min + Math.round(avg * range);
        return Math.max(min, Math.min(max, rolled));
    }


    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        UUID uuid = player.getUUID();

        if (player.level().isClientSide) return;
        if (!opiumLevel.containsKey(uuid)) return;

        long now  = player.level().getGameTime();
        long last = lastDecreaseTick.getOrDefault(uuid, now);

        if (now - last < DECREASE_INTERVAL) return;

        lastDecreaseTick.put(uuid, now);

        int level = opiumLevel.get(uuid) - 1;

        if (level <= 0) {
            cleanup(uuid);
            return;
        }

        opiumLevel.put(uuid, level);
        applyStageEffects(player, level);
    }


    private static void applyStageEffects(Player player, int level) {
        if      (level <= 10) apply1Effects(player);
        else if (level <= 20) apply2Effects(player);
        else if (level <= 25) apply3Effects(player);
        else if (level <= 29) apply4Effects(player);
        else                  apply5Effects(player);
    }

    private static void apply1Effects(Player player) {
        int d = 12000;
        player.sendSystemMessage(Component.literal("§eMan you feel good..."));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, d, 1));
        player.addEffect(new MobEffectInstance(MobEffects.HUNGER,            d, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, d, 1));
        player.addEffect(new MobEffectInstance(ModEffects.OPIUMHIGH.get(),   d * 2, 0));
        player.addEffect(new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), d, 2, false, true, true));
    }

    private static void apply2Effects(Player player) {
        int d = 12000;
        player.sendSystemMessage(Component.literal("§cYou feel really good..."));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, d, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, d, 2));
        player.addEffect(new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), d, 3, false, true, true));
    }

    private static void apply3Effects(Player player) {
        int d = 12000;
        player.sendSystemMessage(Component.literal("§cYou feel like you could fly..."));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, d, 2));
        player.addEffect(new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), d, 4, false, true, true));
        if (player.level().getGameTime() % 25 == 0)
            player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0F, 1.0F);
    }

    private static void apply4Effects(Player player) {
        int d = 12000;
        player.sendSystemMessage(Component.literal("§cHow much further does this ride go?"));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, d, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,      d, 3));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,         d, 1));
        player.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST,      d, 2));
        player.addEffect(new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), d, 7, false, true, true));
        if (player.level().getGameTime() % 20 == 0)
            player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0F, 1.0F);
    }

    private static void apply5Effects(Player player) {
        int d = 12000;
        player.sendSystemMessage(Component.literal("§c§lYour body aches... but who cares when you feel this good"));
        player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, d, 2));
        player.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN,      d, 3));
        player.addEffect(new MobEffectInstance(MobEffects.CONFUSION,         d, 1));
        player.addEffect(new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), d, 10, false, true, true));
        OverdoseEvent.startOverdoseForPlayer(player);
        if (player.level().getGameTime() % 20 == 0)
            player.playSound(SoundEvents.WARDEN_HEARTBEAT, 1.0F, 1.0F);
    }


    private static void cleanup(UUID uuid) {
        opiumLevel.remove(uuid);
        lastDecreaseTick.remove(uuid);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player)
            cleanup(player.getUUID());
    }


    public static void sendOpiumLevelToPlayer(Player player) {
        if (player.level().isClientSide) return;
        int level = opiumLevel.getOrDefault(player.getUUID(), 0);
        player.sendSystemMessage(Component.literal("§eYour current opium level is: " + level));
    }

    @Mod.EventBusSubscriber
    public class OpiumCommand {
        @SubscribeEvent
        public static void onRegisterCommands(RegisterCommandsEvent event) {
            event.getDispatcher().register(
                    LiteralArgumentBuilder.<CommandSourceStack>literal("opium")
                            .requires(src -> src.hasPermission(1))
                            .then(Commands.argument("player", EntityArgument.player())
                                    .executes(ctx -> {
                                        ServerPlayer target = EntityArgument.getPlayer(ctx, "player");
                                        sendOpiumLevelToPlayer(target);
                                        ctx.getSource().sendSuccess(
                                                () -> Component.literal("Poppydelight: Opium level checked for " + target.getName().getString()),
                                                false
                                        );
                                        return 1;
                                    }))
            );
        }
    }
}