package net.warsnake.poppydelight.sounds;

import net.warsnake.poppydelight.PoppyDelight;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, PoppyDelight.MODID);

    public static final RegistryObject<SoundEvent> WALTUH = registerSoundEvents("waltuh");
    public static final RegistryObject<SoundEvent> WAR = registerSoundEvents("war");
    public static final RegistryObject<SoundEvent> TOXIC = registerSoundEvents("toxic");
    public static final RegistryObject<SoundEvent> LIGHT = registerSoundEvents("light");
    public static final RegistryObject<SoundEvent> STICKERBRUSH = registerSoundEvents("stickerbrush");
    public static final RegistryObject<SoundEvent> ENDS = registerSoundEvents("ends");
    public static final RegistryObject<SoundEvent> STARS = registerSoundEvents("stars");


    private static RegistryObject<SoundEvent> registerSoundEvents(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(new ResourceLocation(PoppyDelight.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}