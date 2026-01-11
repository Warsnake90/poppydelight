package net.warsnake.poppydelight.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS;
    public static final RegistryObject<MobEffect> OPIUMHIGH;
    public static final RegistryObject<MobEffect> SHROOMHIGH;


    public static void register(IEventBus eventBus) {
       MOB_EFFECTS.register(eventBus);
    }

    static {
        MOB_EFFECTS = DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, "poppydelight");
        OPIUMHIGH = MOB_EFFECTS.register("opium", () -> {
            return new OpiumEffect(MobEffectCategory.NEUTRAL, 0x00000000);
        });

        SHROOMHIGH = MOB_EFFECTS.register("shrooms", () -> {
            return new ShroomsEffect(MobEffectCategory.NEUTRAL, 0x00000000);
        });
    }
}