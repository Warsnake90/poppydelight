package net.warsnake.poppydelight.client.render;

import com.mojang.math.Axis;
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
    private static int tickCounter = 0;
    private int x = random.nextInt(5) + 1;
    private int y = random.nextInt(5) + 1;
  // private int y = 5;
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
            minecraft.execute(() -> {
                GameRenderer renderer = minecraft.gameRenderer;
                renderer.loadEffect(SHROOM_SHADER);
            });
        } else if (effect == null || effect.getDuration() <= 1) {
            if (effectActiveLastTick) {
                effectActiveLastTick = false;
                minecraft.execute(() -> {
                    GameRenderer renderer = minecraft.gameRenderer;
                    renderer.shutdownEffect();
                });
            }
        }

        float fadeProgress = (timer % 100) / 100.0F;
        float fadeFactor = 0.5F * (float) Math.sin(Math.PI * fadeProgress) + 0.5F;

        float redOffset = (float) Math.sin(tickCounter * 0.1F) * 0.05F;
        float greenOffset = (float) Math.sin(tickCounter * 0.1F + Math.PI / 2) * 0.05F;
        float blueOffset = (float) Math.sin(tickCounter * 0.1F + Math.PI) * 0.05F;

        RenderSystem.setShaderColor(1.0F + redOffset, 1.0F + greenOffset, 1.0F + blueOffset, fadeFactor);

        RenderSystem.setShaderTexture(0, texture);
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