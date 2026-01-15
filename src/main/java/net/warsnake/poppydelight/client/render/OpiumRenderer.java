package net.warsnake.poppydelight.client.render;

import net.minecraftforge.client.event.InputEvent;
import net.warsnake.poppydelight.effect.ModEffects;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.warsnake.poppydelight.items.ModItems;
import org.lwjgl.glfw.GLFW;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class OpiumRenderer {


    public static final ResourceLocation OPIUM_SHADER =
            new ResourceLocation("poppydelight", "shaders/post/opium.json");
    public static final ResourceLocation OPIUM_TEXTURE =
            new ResourceLocation("poppydelight", "textures/overlay/opium.png");

    public boolean effectActiveLastTick = false;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.player.level().isClientSide
                && event.player == Minecraft.getInstance().player) {

                onEffectTick(event);

        }
    }

    public void renderOverlay(PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        RenderSystem.setShaderTexture(0, OPIUM_TEXTURE);
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

        buffer.begin(Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(0.0D, window.getHeight(), -90.0D).uv(0.0F, 1.0F).endVertex();
        buffer.vertex(window.getWidth(), window.getHeight(), -90.0D).uv(1.0F, 1.0F).endVertex();
        buffer.vertex(window.getWidth(), 0.0D, -90.0D).uv(1.0F, 0.0F).endVertex();
        buffer.vertex(0.0D, 0.0D, -90.0D).uv(0.0F, 0.0F).endVertex();

        tesselator.end();

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private static int forgebs = 0;

    @SubscribeEvent
    public void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_F5 || event.getKey() == GLFW.GLFW_KEY_F11) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {

                forgebs = 1;

            }, 50, TimeUnit.MILLISECONDS);
        }
    }

    public void fuckforgearbitraryrules(PlayerTickEvent event) {

        MobEffectInstance effect = event.player.getEffect(ModEffects.OPIUMHIGH.get());
        int duration = effect == null ? 0 : effect.getDuration();
        if ((event.player.level().isClientSide) && (duration > 0)) {

            Minecraft minecraft = Minecraft.getInstance();
            GameRenderer renderer = minecraft.gameRenderer;
            renderer.loadEffect(OPIUM_SHADER);
        }
    }

    public void onEffectTick(PlayerTickEvent event) {

        if (forgebs == 1){
            fuckforgearbitraryrules(event);
            forgebs = 0;
        }

        MobEffectInstance effect;
            effect = event.player.getEffect((MobEffect) ModEffects.OPIUMHIGH.get());

        int duration = effect == null ? 0 : effect.getDuration();

        if (duration > 1) {
            if (!effectActiveLastTick) {
                effectActiveLastTick = true;

                Minecraft.getInstance().execute(() -> {
                    GameRenderer renderer = Minecraft.getInstance().gameRenderer;
                    renderer.loadEffect(OPIUM_SHADER);
                });
            }
        } else if (effectActiveLastTick) {
            effectActiveLastTick = false;

            Minecraft.getInstance().execute(() -> {
                GameRenderer renderer = Minecraft.getInstance().gameRenderer;
                renderer.shutdownEffect();
            });
        }
    }
}