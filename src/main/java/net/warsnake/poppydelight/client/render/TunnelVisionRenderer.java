package net.warsnake.poppydelight.client.render;

import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.ViewportEvent;
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

public class TunnelVisionRenderer {

    public static final ResourceLocation TUNNEL_TEXTURE =
            new ResourceLocation("poppydelight", "textures/overlay/tunnelvision.png");

    public boolean effectActiveLastTick = false;

    @SubscribeEvent
    public void onPlayerTick(PlayerTickEvent event) {
        if (event.player.level().isClientSide
                && event.player == Minecraft.getInstance().player) {

            onEffectTick(event);

        }

        if (event.player.level().isClientSide
                && event.player == Minecraft.getInstance().player) {
            this.onEffectTick(event);
        }

    }

    public void renderOverlay(PoseStack poseStack) {
        Minecraft minecraft = Minecraft.getInstance();
        Window window = minecraft.getWindow();

        RenderSystem.setShaderTexture(0, TUNNEL_TEXTURE);
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

    public int ticksActive = 0;

    public void onEffectTick(PlayerTickEvent event) {
        if (!ModEffects.TUNNELVISION.isPresent()) return;

        MobEffectInstance effect = event.player.getEffect((MobEffect) ModEffects.TUNNELVISION.get());
        int duration = effect == null ? 0 : effect.getDuration();

        if (duration > 1) {
            effectActiveLastTick = true;
            ticksActive = Math.min(ticksActive + 1, 40);
        } else {
            effectActiveLastTick = false;
            ticksActive = 0;
        }
    }

    @SubscribeEvent
    public void onFovModifier(ViewportEvent.ComputeFov event) {
        if (!effectActiveLastTick) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        float progress = ticksActive / 40f;
        double targetMultiplier = 0.6;
        double multiplier = 1.0 - (1.0 - targetMultiplier) * progress;

        event.setFOV(event.getFOV() * multiplier);
    }

}
