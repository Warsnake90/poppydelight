package net.warsnake.poppydelight;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.EffectInstance;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.client.renderer.PostPass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.warsnake.poppydelight.PoppyDelight;
import org.jetbrains.annotations.UnknownNullability;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = PoppyDelight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class PsychedelicShaderHandler {

    private static final ResourceLocation PSYCHEDELIC_EFFECT =
            new ResourceLocation("poppydelight", "post_effect/psychedelic.json");

    private static PostChain psychedelicChain = null;

    private static float elapsedSeconds = 0f;

   // call this
    public static void enable() {
        Minecraft mc = Minecraft.getInstance();
        if (psychedelicChain != null) return;

        try {
            psychedelicChain = new PostChain(
                    mc.getTextureManager(),
                    mc.getResourceManager(),
                    mc.getMainRenderTarget(),
                    PSYCHEDELIC_EFFECT
            );
            // resize
            psychedelicChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            elapsedSeconds = 0f;
            PoppyDelight.LOGGER.info("Shroom effects loaded &**");
        } catch (IOException e) {
            PoppyDelight.LOGGER.error("shroom effects NOT loaded!! &**", e);
        }
    }

    public static void disable() {
        if (psychedelicChain != null) {
            psychedelicChain.close();
            psychedelicChain = null;
            PoppyDelight.LOGGER.info("shroom effects unloaded &**");
        }
    }

    public static boolean isActive() {
        return psychedelicChain != null;
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (psychedelicChain == null) return;

        elapsedSeconds += 1f / 20f;
    }


    @SubscribeEvent
    public static void onRenderLevelLast(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;
        if (psychedelicChain == null) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return;

        updateUniforms(elapsedSeconds);
        psychedelicChain.process(event.getPartialTick());


        mc.getMainRenderTarget().bindWrite(true);
    }

    public static void resize(int width, int height) {
        if (psychedelicChain != null) {
            psychedelicChain.resize(width, height);
        }
    }


    private static void updateUniforms(float time) {
        if (psychedelicChain == null) return;

        try {
            java.lang.reflect.Field passesField =
                    PostChain.class.getDeclaredField("passes");
            passesField.setAccessible(true);

            @SuppressWarnings("unchecked")
            java.util.List<PostPass> passes =
                    (java.util.List<PostPass>) passesField.get(psychedelicChain);

            for (PostPass pass : passes) {
                var effect = pass.getEffect();
                if (effect == null) continue;

                safeSetFloat(effect, "Time",      time);
                safeSetFloat(effect, "Intensity", 1.0f);
                safeSetFloat(effect, "Strength",  1.5f);
                safeSetFloat(effect, "Segments",  6.0f);
            }

        } catch (NoSuchFieldException | IllegalAccessException e) {
            PoppyDelight.LOGGER.error("couldnt access postchain shit &**", e);
        }
    }

    private static void safeSetFloat(@UnknownNullability EffectInstance effect,
                                     String name, float value) {
        var uniform = effect.getUniform(name);
        if (uniform != null) {
            uniform.set(value);
        }
    }
}
