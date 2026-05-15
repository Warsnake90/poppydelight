package net.warsnake.poppydelight.client.render;

import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.warsnake.poppydelight.effect.ModEffects;
import org.lwjgl.glfw.GLFW;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@OnlyIn(Dist.CLIENT)
public class DaturaRenderer {

    // This took forever to rework, I'll prob not fix the others since its just alot of work and potentially will break alot of stuff
    // just use this one as a frame from now on

    private static final ResourceLocation DATURA_SHADER =
            new ResourceLocation("poppydelight", "shaders/post/datura.json");
    public static final ResourceLocation DATURA_TEXTURE =
            new ResourceLocation("poppydelight", "textures/overlay/tunnelvision.png");

    public static boolean isEffectActive = false;
    private static int forgebs = 0;

    private final Random random = new Random();
    private int tickCounter = 0;
    private int timer = 0;

    public boolean effectActiveLastTick = false;



    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (!event.player.level().isClientSide) return;
        if (event.player != Minecraft.getInstance().player) return;
        if (event.phase != TickEvent.Phase.END) return;

        tickCounter++;
        timer++;

        if (forgebs == 1) {
            fuckforgearbitraryrules(event);
            forgebs = 0;
        }

        MobEffectInstance effect = event.player.getEffect((MobEffect) ModEffects.DATURA.get());
        boolean shouldBeActive = effect != null && effect.getDuration() > 1;

        if (shouldBeActive && !isEffectActive) {
            isEffectActive = true;
            effectActiveLastTick = true;
            onEffectStart();
        } else if (!shouldBeActive && isEffectActive) {
            isEffectActive = false;
            effectActiveLastTick = false;
            onEffectEnd();
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_F5 || event.getKey() == GLFW.GLFW_KEY_F11) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> forgebs = 1, 5, TimeUnit.MILLISECONDS);
        }
    }

    private void onEffectStart() {
        Minecraft.getInstance().execute(() ->
                Minecraft.getInstance().gameRenderer.loadEffect(DATURA_SHADER));
    }

    private void onEffectEnd() {
        Minecraft.getInstance().execute(() ->
                Minecraft.getInstance().gameRenderer.shutdownEffect());
    }

    private void fuckforgearbitraryrules(PlayerTickEvent event) {
        MobEffectInstance effect = event.player.getEffect(ModEffects.DATURA.get());
        int duration = effect == null ? 0 : effect.getDuration();
        if (event.player.level().isClientSide && duration > 0) {
            Minecraft.getInstance().gameRenderer.loadEffect(DATURA_SHADER);
        }
    }


    public void renderOverlay(PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        RenderSystem.setShaderTexture(0, DATURA_TEXTURE);
        RenderSystem.enableBlend();
        RenderSystem.blendFuncSeparate(
                SourceFactor.SRC_ALPHA,
                DestFactor.ONE_MINUS_SRC_ALPHA,
                SourceFactor.ONE,
                DestFactor.ZERO
        );

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        poseStack.pushPose();

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0.0D, window.getGuiScaledHeight(), -90.0D).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(window.getGuiScaledWidth(), window.getGuiScaledHeight(), -90.0D).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(window.getGuiScaledWidth(), 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();

        tesselator.end();

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static float currentFogDistance = 256.0F;

    @SubscribeEvent
    public void onFogRender(ViewportEvent.RenderFog event) {
        if (!isEffectActive) {
            currentFogDistance = 256.0F;
            return;
        }
        currentFogDistance = Mth.lerp(0.05F, currentFogDistance, 64.0F);
        event.setNearPlaneDistance(0.0F);
        event.setFarPlaneDistance(currentFogDistance);
        event.setCanceled(true);
    }

}