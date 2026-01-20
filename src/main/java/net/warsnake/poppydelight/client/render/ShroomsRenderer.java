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

public class ShroomsRenderer {

    private static final ResourceLocation SHROOM_SHADER =
            new ResourceLocation("poppydelight", "shaders/post/shrooms.json");

    public boolean effectActiveLastTick = false;

    private static final Random random = new Random();
    private int tickCounter = 0;
    private int x = random.nextInt(5) + 1;
    private int y = random.nextInt(5) + 1;
    private int w = 1;
    private int timer;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.player.level().isClientSide
                && event.player == Minecraft.getInstance().player) {
            tickCounter++;
            onEffectTick(event);

            timer++;
        }
    }

    public void renderOverlay(PoseStack poseStack) {

        if (tickCounter >= 4000) {
            x = random.nextInt(5) + 1;
           y = random.nextInt(5) + 1;
            tickCounter = 0;
        }

        ResourceLocation textureX = new ResourceLocation("poppydelight", "textures/overlay/colour" + x + ".png");

        ResourceLocation textureY = null;
        ResourceLocation textureW = null;


        switch(y) {

            // keep this a switch so i can add more animated stuff later
            case 3 -> {animatedEffect(); textureY = new ResourceLocation("poppydelight", "textures/overlay/shrooms3/shrooms" + w + ".png");}
            case 5 -> {animatedEffect(); textureY = new ResourceLocation("poppydelight", "textures/overlay/shrooms5/shrooms" + w + ".png");
                textureW = new ResourceLocation("poppydelight", "textures/overlay/shrooms" + y + ".png");
            }

            default -> {textureY = new ResourceLocation("poppydelight", "textures/overlay/shrooms" + y + ".png");}
        }

        poseStack.pushPose();

        renderTextureWithEffect(textureX, poseStack);

        if (y == 5) {
            renderTextureWithEffect(textureW, poseStack);
        }

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

            }, 50, TimeUnit.MILLISECONDS);
        }
    }

    public void fuckforgearbitraryrules(PlayerTickEvent event) {

        MobEffectInstance effect = event.player.getEffect(ModEffects.SHROOMHIGH.get());
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

        MobEffectInstance effect = minecraft.player.getEffect(ModEffects.SHROOMHIGH.get());

        if (effect != null && effect.getDuration() > 1 && !effectActiveLastTick) {
            effectActiveLastTick = true;
            minecraft.execute(() -> minecraft.gameRenderer.loadEffect(SHROOM_SHADER));
        } else if ((effect == null || effect.getDuration() <= 1) && effectActiveLastTick) {
            effectActiveLastTick = false;
            minecraft.execute(() -> minecraft.gameRenderer.shutdownEffect());
        }

        if (effect == null) return;

        float w = window.getGuiScaledWidth();
        float h = window.getGuiScaledHeight();

        float fadeProgress = (timer % 100) / 100.0F;

        float minAlpha = 0.75F;
        float maxAlpha = 1.0F;
        float fadeFactor =
                minAlpha + (maxAlpha - minAlpha)
                        * (0.5F * (float) Math.sin(Math.PI * fadeProgress) + 0.5F);

        float redOffset   = (float) Math.sin(tickCounter * 0.1F) * 0.04F;
        float greenOffset = (float) Math.sin(tickCounter * 0.1F + Math.PI / 2) * 0.04F;
        float blueOffset  = (float) Math.sin(tickCounter * 0.1F + Math.PI) * 0.04F;

        float x0 = 0.0F;
        float y0 = 0.0F;
        float x1 = w;
        float y1 = h;

        float wave = (float) Math.sin(tickCounter * 0.05F) * 0.025F;

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
        poseStack.mulPose(Axis.ZP.rotation(tickCounter * 0.0015F));
        poseStack.translate(-w / 2.0F, -h / 2.0F, 0);

        Tesselator tesselator = Tesselator.getInstance();
        BufferBuilder buffer = tesselator.getBuilder();

        RenderSystem.setShaderColor(
                0.95F + redOffset,
                0.95F + greenOffset,
                0.95F + blueOffset,
                fadeFactor
        );

        buffer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
        buffer.vertex(x0, y1, -90).uv(0 + wave, 1).endVertex();
        buffer.vertex(x1, y1, -90).uv(1 + wave, 1).endVertex();
        buffer.vertex(x1, y0, -90).uv(1 - wave, 0).endVertex();
        buffer.vertex(x0, y0, -90).uv(0 - wave, 0).endVertex();
        tesselator.end();


        RenderSystem.setShaderColor(0, 0, 0, 0.35F);

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

    public void onEffectTick(PlayerTickEvent event) {

        if (forgebs == 1){
            fuckforgearbitraryrules(event);
            forgebs = 0;
        }

        MobEffectInstance effect = event.player.getEffect(ModEffects.SHROOMHIGH.get());
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