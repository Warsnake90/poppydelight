package net.warsnake.poppydelight;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import java.util.Random;
import java.util.UUID;

@Mod.EventBusSubscriber
public class ShroomEvent {

    private static final Random random = new Random();

    private static final Map<UUID, Long> shroomStartTimes = new HashMap<>();
    private static final Map<UUID, Integer> stageSent = new HashMap<>();
    private static final Map<UUID, Boolean> goodTrip = new HashMap<>();
    private static final Map<UUID, Integer> hallucinationTicks = new HashMap<>();

    private static final long TICKS_3_MIN  = 3 * 60 * 20;
    private static final long TICKS_5_MIN  = 5 * 60 * 20;
    private static final long TICKS_15_MIN = 15 * 60 * 20;
    private static final long TICKS_30_MIN = 30 * 60 * 20;
    private static final long TICKS_35_MIN = 35 * 60 * 20;

    // completly rewritten because old system is super inefficient... It probably(?) doesn't matter for the other 2

    @SubscribeEvent
    public static void onFoodEaten(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.level().isClientSide) return;

        UUID id = player.getUUID();
        if (shroomStartTimes.containsKey(id)) return;

        ItemStack stack = event.getItem();
        if (stack.hasTag() && stack.getTag().getBoolean("Shroom")) {
            shroomStartTimes.put(id, player.level().getGameTime());
            stageSent.put(id, 0);
            hallucinationTicks.put(id, 0);
            goodTrip.put(id, random.nextInt(10) != 0);

            player.sendSystemMessage(Component.literal("§dHere we go..."));
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Player player = event.player;
        UUID id = player.getUUID();

        if (!shroomStartTimes.containsKey(id)) return;

        long elapsed = player.level().getGameTime() - shroomStartTimes.get(id);
        boolean good = goodTrip.getOrDefault(id, true);

        if (elapsed >= TICKS_35_MIN) {
            cleanup(id);
            return;
        }

        if (elapsed >= TICKS_30_MIN && stageSent.get(id) < 4) {
            stageSent.put(id, 4);
            player.sendSystemMessage(Component.literal(
                    good ? "§dIt's starting to wind down..." : "§dThank god it's almost over..."
            ));
            if (good) apply30MinuteEffects(player);
            else applybad30MinuteEffects(player);
        }

        else if (elapsed >= TICKS_15_MIN && stageSent.get(id) < 3) {
            stageSent.put(id, 3);
            player.sendSystemMessage(Component.literal(
                    good ? "§dNow that feels good..." : "§dThe walls feel as if they are closing in..."
            ));
            if (good) apply15MinuteEffects(player);
            else applybad15MinuteEffects(player);
        }

        else if (elapsed >= TICKS_5_MIN && stageSent.get(id) < 2) {
            stageSent.put(id, 2);
            player.sendSystemMessage(Component.literal(
                    good ? "§dYou feel great..." : "§dWhat the hell is happening...?"
            ));
            if (good) apply5MinuteEffects(player);
            else applybad5MinuteEffects(player);
        }

        else if (elapsed >= TICKS_3_MIN && stageSent.get(id) < 1) {
            stageSent.put(id, 1);
            player.sendSystemMessage(Component.literal(
                    good ? "§dHere it comes..." : "§dWhat the hell is happening..."
            ));
            if (good) apply3MinuteEffects(player);
            else applybad3MinuteEffects(player);
        }

        vtick(player);
    }

    private static void apply3MinuteEffects(Player p) {
        apply(p, 2399,
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 2399, 0),
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 2399, 0),
                new MobEffectInstance(MobEffects.WEAKNESS, 2399, 0),
                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 2399, 0),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 2399, 1, false, true, true)
        );
    }

    private static void apply5MinuteEffects(Player p) {
        apply(p, 11999,
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 11999, 1),
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 11999, 1),
                new MobEffectInstance(MobEffects.WEAKNESS, 11999, 1),
                new MobEffectInstance(MobEffects.REGENERATION, 11999, 0),
                new MobEffectInstance(ModEffects.SHROOMHIGH.get(), 11999, 0),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 11999, 3, false, true, true)
        );
    }

    private static void apply15MinuteEffects(Player p) {
        apply(p, 17999,
                new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 17999, 1),
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 17999, 2),
                new MobEffectInstance(MobEffects.DAMAGE_RESISTANCE, 17999, 1),
                new MobEffectInstance(ModEffects.SHROOMHIGH.get(), 17999, 0),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 17999, 4, false, true, true)
        );
    }

    private static void apply30MinuteEffects(Player p) {
        apply(p, 5999,
                new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 5999, 1),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 5999, 3, false, true, true)
        );
    }

    private static void applybad3MinuteEffects(Player p) {
        apply3MinuteEffects(p);
    }

    private static void applybad5MinuteEffects(Player p) {
        apply(p, 11999,
                new MobEffectInstance(MobEffects.WEAKNESS, 11999, 2),
                new MobEffectInstance(ModEffects.BADSHROOMHIGH.get(), 11999, 0),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 11999, 3, false, true, true)
        );
    }

    private static void applybad15MinuteEffects(Player p) {
        applybad5MinuteEffects(p);
    }

    private static void applybad30MinuteEffects(Player p) {
        apply(p, 5999,
                new MobEffectInstance(MobEffects.WEAKNESS, 5999, 2),
                new MobEffectInstance((MobEffect) BnCEffects.TIPSY.get(), 5999, 3, false, true, true)
        );
    }

    private static void apply(Player p, int duration, MobEffectInstance... effects) {
        for (MobEffectInstance e : effects) {
            p.addEffect(e);
        }
    }

    private static void vtick(Player player) {
        UUID id = player.getUUID();
        int ticks = hallucinationTicks.getOrDefault(id, 0) + 1;

        if (ticks >= 200) {
            ticks = 0;
            playRandomHallucination(player);
        }

        hallucinationTicks.put(id, ticks);
    }

    private static void playRandomHallucination(Player player) {
        if (!player.level().isClientSide) return;

        SoundEvent[] sounds = {
                SoundEvents.CREEPER_PRIMED,
                SoundEvents.GHAST_SCREAM,
                SoundEvents.TNT_PRIMED,
                SoundEvents.ZOMBIE_AMBIENT
        };

        SoundEvent sound = sounds[random.nextInt(sounds.length)];
        float volume = 0.3F;
        float pitch = 0.6F + random.nextFloat();

        player.level().playLocalSound(
                player.getX() + random.nextGaussian() * 6,
                player.getY() + random.nextInt(3),
                player.getZ() + random.nextGaussian() * 6,
                sound,
                SoundSource.AMBIENT,
                volume,
                pitch,
                false
        );
    }


    private static void cleanup(UUID id) {
        shroomStartTimes.remove(id);
        stageSent.remove(id);
        goodTrip.remove(id);
        hallucinationTicks.remove(id);
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        if (event.getEntity() instanceof Player player) {
            cleanup(player.getUUID());
        }
    }
}