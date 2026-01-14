package net.warsnake.poppydelight.client.render;

import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraftforge.client.event.InputEvent;
import net.warsnake.poppydelight.effect.ModEffects;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class BadTripRenderer {

    private static final ResourceLocation SHROOM_SHADER =
            new ResourceLocation("poppydelight", "shaders/post/shrooms.json");

    public boolean effectActiveLastTick = false;

    private static final Random random = new Random();
    private int tickCounter = 0;
    private int x = random.nextInt(5) + 1;
    private int y = random.nextInt(6) + 1;
    private int w = 1;
    private int timer;
    ResourceLocation textureY;
    ResourceLocation textureX;

    private boolean invertColors = false;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.player.level().isClientSide
                && event.player == Minecraft.getInstance().player) {
            tickCounter++;
            onEffectTick(event);

            timer++;

            if (tickCounter % 20 == 0) {
                invertColors = random.nextInt(20) == 0;
            }
        }
    }

    public void renderOverlay(PoseStack poseStack) {

        if (tickCounter >= 400) {
            x = random.nextInt(5) + 1;
            y = random.nextInt(6) + 1;
            tickCounter = 0;
        }

        textureX = new ResourceLocation("poppydelight", "textures/overlay/colour" + x + ".png");

        ResourceLocation textureW = null;

        textureY = new ResourceLocation("poppydelight", "textures/overlay/badtrip/shroomsbad" + y + ".png");

        poseStack.pushPose();

        renderTextureWithEffect(textureX, poseStack);

        renderTextureWithEffect(textureY, poseStack);

        poseStack.popPose();
    }

    private void animatedEffect() {
        if ((w == 0)) {w = 1;}
        if ((w >= 11)) {w = 1;}

        if ((tickCounter >= 5) && ((y == 3) || (y == 5))) {
            w++;

            if (w >= 11) { w = 1;}
        }
    }

    private static int forgebs = 0;

    @SubscribeEvent
    public void onKeyPress(InputEvent.Key event) {
        if (event.getKey() == GLFW.GLFW_KEY_F5 || event.getKey() == GLFW.GLFW_KEY_F11) {
            Executors.newSingleThreadScheduledExecutor().schedule(() -> {

                forgebs = 1;

            }, 5, TimeUnit.MILLISECONDS);
        }
    }

    public void fuckforgearbitraryrules(PlayerTickEvent event) {

        MobEffectInstance effect = event.player.getEffect(ModEffects.BADSHROOMHIGH.get());
        int duration = effect == null ? 0 : effect.getDuration();
        if ((event.player.level().isClientSide) && (duration > 0)) {

            Minecraft minecraft = Minecraft.getInstance();
            GameRenderer renderer = minecraft.gameRenderer;
            renderer.loadEffect(SHROOM_SHADER);
        }
    }

    private void renderTextureWithEffect(ResourceLocation texture, PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        MobEffectInstance effect = minecraft.player.getEffect(ModEffects.BADSHROOMHIGH.get());

        if (effect != null && effect.getDuration() > 1 && !effectActiveLastTick) {
            effectActiveLastTick = true;
            minecraft.execute(() -> minecraft.gameRenderer.loadEffect(SHROOM_SHADER));
        } else if ((effect == null || effect.getDuration() <= 1) && effectActiveLastTick) {
            effectActiveLastTick = false;
            minecraft.execute(() -> minecraft.gameRenderer.shutdownEffect());
        }

        if (effect == null) return;

        float w = window.getWidth();
        float h = window.getHeight();

        float intensity = Math.min(1.0F, effect.getDuration() / 200.0F);
        float intensity1 = Math.min(1.0F, effect.getDuration() / 200.0F);

        float fadeProgress = (timer % 50) / 50.0F;
        float fadeFactor = (0.5F * (float) Math.sin(Math.PI * fadeProgress) + 0.5F) * intensity;

        float redOffset   = (float) Math.sin(tickCounter * 0.10F) * 0.06F * intensity;
        float greenOffset = (float) Math.sin(tickCounter * 0.10F + Math.PI / 2) * 0.06F * intensity;
        float blueOffset  = (float) Math.sin(tickCounter * 0.10F + Math.PI) * 0.06F * intensity;

        if (invertColors) {
            redOffset = -redOffset;
            greenOffset = -greenOffset;
            blueOffset = -blueOffset;
        }

        float flicker = (minecraft.level.random.nextFloat() - 0.5F) * 0.15F * intensity;

        float x0 = 0.0F;
        float y0 = 0.0F;
        float x1 = w;
        float y1 = h;

        float wave = (float) Math.sin(tickCounter * 0.05F) * 0.03F * intensity;

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.enableBlend();
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);

        RenderSystem.blendFunc(
                SourceFactor.SRC_ALPHA,
                DestFactor.ONE
        );

        poseStack.pushPose();

        poseStack.translate(w / 2.0F, h / 2.0F, 0);
        poseStack.mulPose(Axis.ZP.rotation(tickCounter * 0.002F * intensity));
        poseStack.translate(-w / 2.0F, -h / 2.0F, 0);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.setShaderColor(
                .5F + redOffset + flicker,
                .5F + greenOffset + flicker,
                .5F + blueOffset + flicker,
                fadeFactor
        );

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x0, y1, -90).uv(0 + wave, 1).endVertex();
        buffer.vertex(x1, y1, -90).uv(1 + wave, 1).endVertex();
        buffer.vertex(x1, y0, -90).uv(1 - wave, 0).endVertex();
        buffer.vertex(x0, y0, -90).uv(0 - wave, 0).endVertex();
        tesselator.end();

        RenderSystem.setShaderColor(1, 1, 1, fadeFactor * 0.35F);

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x0 + 8, y1 + 4, -90).uv(0, 1).endVertex();
        buffer.vertex(x1 + 8, y1 + 4, -90).uv(1, 1).endVertex();
        buffer.vertex(x1 + 8, y0 + 4, -90).uv(1, 0).endVertex();
        buffer.vertex(x0 + 8, y0 + 4, -90).uv(0, 0).endVertex();
        tesselator.end();

        float chroma = 4.0F * intensity;

        RenderSystem.setShaderColor(1.2F, 0.8F, 0.8F, 0.25F);
        drawOffsetQuad(buffer, tesselator, x0 + chroma, y0, x1 + chroma, y1);

        RenderSystem.setShaderColor(0.8F, 1.2F, 0.8F, 0.25F);
        drawOffsetQuad(buffer, tesselator, x0 - chroma, y0, x1 - chroma, y1);

        RenderSystem.setShaderColor(0.8F, 0.8F, 1.2F, 0.25F);
        drawOffsetQuad(buffer, tesselator, x0, y0 + chroma, x1, y1 + chroma);

        for (int i = 0; i < 6; i++) {
            float sliceY = minecraft.level.random.nextFloat() * h;
            float sliceH = 6 + minecraft.level.random.nextFloat() * 10;
            float offset = (minecraft.level.random.nextFloat() - 0.5F) * 40 * intensity;

            buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            buffer.vertex(offset, sliceY + sliceH, -89).uv(0, 1).endVertex();
            buffer.vertex(w + offset, sliceY + sliceH, -89).uv(1, 1).endVertex();
            buffer.vertex(w + offset, sliceY, -89).uv(1, 0).endVertex();
            buffer.vertex(offset, sliceY, -89).uv(0, 0).endVertex();
            tesselator.end();
        }

        RenderSystem.setShaderColor(0, 0, 0, 0.35F * intensity);

        float inset = 40;
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(inset, h - inset, -88).uv(0, 1).endVertex();
        buffer.vertex(w - inset, h - inset, -88).uv(1, 1).endVertex();
        buffer.vertex(w - inset, inset, -88).uv(1, 0).endVertex();
        buffer.vertex(inset, inset, -88).uv(0, 0).endVertex();
        tesselator.end();

        poseStack.popPose();

        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.disableBlend();
    }

    private void drawOffsetQuad(BufferBuilder buffer, Tesselator tesselator,
                                float x0, float y0, float x1, float y1) {
        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x0, y1, -90).uv(0, 1).endVertex();
        buffer.vertex(x1, y1, -90).uv(1, 1).endVertex();
        buffer.vertex(x1, y0, -90).uv(1, 0).endVertex();
        buffer.vertex(x0, y0, -90).uv(0, 0).endVertex();
        tesselator.end();
    }

    public void onEffectTick(PlayerTickEvent event) {

        if (forgebs == 1){
            fuckforgearbitraryrules(event);
            forgebs = 0;
        }

        MobEffectInstance effect = event.player.getEffect(ModEffects.BADSHROOMHIGH.get());
        int duration = effect == null ? 0 : effect.getDuration();

        if (duration > 1) {
            if (!effectActiveLastTick) {
                effectActiveLastTick = true;

                Minecraft.getInstance().execute(() -> {
                    GameRenderer renderer = Minecraft.getInstance().gameRenderer;
                    renderer.loadEffect(SHROOM_SHADER);
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