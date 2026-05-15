package net.warsnake.poppydelight.client.render;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.TickEvent;
import net.warsnake.poppydelight.effect.ModEffects;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.lwjgl.glfw.GLFW;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ShroomsRenderer {

    private static final ResourceLocation SHROOM_SHADER =
            new ResourceLocation("poppydelight", "shaders/post/psychedelic.json");

    public boolean effectActiveLastTick = false;

    private static final Random random = new Random();
    private int tickCounter = 0;
    private int x = random.nextInt(5) + 1;
    private int y = random.nextInt(5) + 1;
    private int w = 1;
    private int timer;
    private static boolean activeLastTick = false;
    private static float t = 0.0F;
    private static float intensity = 0.0F;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.player.level().isClientSide
                && event.player == Minecraft.getInstance().player) {
            tickCounter++;
            onEffectTick(event);

            timer++;
        }

        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            if (event.player.level().isClientSide) {
                if (event.player == mc.player) {
                    if (event.phase == TickEvent.Phase.END) {
                        MobEffectInstance eff = mc.player.getEffect((MobEffect) ModEffects.SHROOMHIGH.get());
                        int dur = eff == null ? 0 : eff.getDuration();
                        int amp = eff == null ? 0 : eff.getAmplifier();
                        boolean shouldBeActive = dur > 1;
                        float target = shouldBeActive ? 1.0F : 0.0F;
                        float lerpSpeed = shouldBeActive ? 0.12F : 0.18F;
                        intensity = Mth.lerp(lerpSpeed, intensity, target);
                        if (intensity > 0.001F) {
                            t += 0.05F;
                        }

                        if (shouldBeActive && !activeLastTick) {
                            activeLastTick = true;
                            mc.execute(() -> {
                                mc.gameRenderer.loadEffect(SHROOM_SHADER);
                            });
                        } else if (!shouldBeActive && activeLastTick && intensity < 0.01F) {
                            activeLastTick = false;
                            mc.execute(() -> {
                                mc.gameRenderer.shutdownEffect();
                            });
                        }

                        if (activeLastTick) {
                            PostChain chain = getPostChain(mc.gameRenderer);
                            if (chain != null) {
                                float ampBoost = 1.0F + (float) amp * 0.25F;
                                setUniformEveryPass(chain, "Time", t);
                                setUniformEveryPass(chain, "Intensity", intensity);
                                float breathe = 0.55F + 0.45F * Mth.sin(t * 0.2F);
                                float baseWarp = 0.0065F * intensity * ampBoost;
                                setUniformEveryPass(chain, "WarpStrength", baseWarp * breathe);
                                setUniformEveryPass(chain, "WarpScale", 2.2F);
                                setUniformEveryPass(chain, "BreathSpeed", 0.6F * ampBoost);
                                setUniformEveryPass(chain, "Aberration", 0.0015F + 0.002F * intensity);
                                setUniformEveryPass(chain, "AberrationSpeed", 0.9F * ampBoost);
                                setUniformEveryPass(chain, "HueSpeed", 0.1F + 0.35F * intensity);
                                setUniformEveryPass(chain, "SatBoost", 1.15F + 0.35F * intensity);
                                setUniformEveryPass(chain, "Threshold", 0.72F);
                                setUniformEveryPass(chain, "Knee", 0.2F);
                                setUniformEveryPass(chain, "BloomStrength", 0.55F * intensity);
                                setUniformEveryPass(chain, "Radius", 6.0F);
                                setUniformEveryPass(chain, "TrailStrength", 0.62F + 0.16F * intensity);
                            }
                        }
                    }
                }
            }
        }

    }

    private static boolean HUD_EFFECTS_ENABLED = true;

    public void renderOverlay(PoseStack poseStack) {

        if (!HUD_EFFECTS_ENABLED) return;

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

        if ((tickCounter >= 10) && ((y == 3) || (y == 5))) {
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

        if (!ModEffects.SHROOMHIGH.isPresent()) {
            return;
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

        private static PostChain getPostChain(GameRenderer renderer) {
            try {
                Field f = GameRenderer.class.getDeclaredField("postEffect");
                f.setAccessible(true);
                return (PostChain) f.get(renderer);
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }

        private static void setUniformEveryPass(PostChain chain, String name, float v) {
            try {
                Field passesField = PostChain.class.getDeclaredField("passes");
                passesField.setAccessible(true);
                List<?> passes = (List<?>) passesField.get(chain);

                for (Object passObj : passes) {
                    Field effectField = passObj.getClass().getDeclaredField("effect");
                    effectField.setAccessible(true);
                    Object effect = effectField.get(passObj);
                    Object uniform = effect.getClass().getMethod("getUniform", String.class).invoke(effect, name);
                    if (uniform != null) {
                        uniform.getClass().getMethod("set", Float.TYPE).invoke(uniform, v);
                    }
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    }
