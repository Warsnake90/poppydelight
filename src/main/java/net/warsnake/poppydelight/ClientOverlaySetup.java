package net.warsnake.poppydelight;

import net.warsnake.poppydelight.PoppyDelight;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@EventBusSubscriber(
        modid = "poppydelight",
        value = {Dist.CLIENT},
        bus = Bus.MOD
)
public class ClientOverlaySetup {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.register(PoppyDelight.OPIUM_EFFECT_RENDERER);
        MinecraftForge.EVENT_BUS.register(PoppyDelight.SHROOM_EFFECT_RENDERER);
    }

    @SubscribeEvent
    public static void onRegisterGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("opium", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
            if (PoppyDelight.OPIUM_EFFECT_RENDERER.effectActiveLastTick) {
                gui.setupOverlayRenderState(true, false);
                PoppyDelight.OPIUM_EFFECT_RENDERER.renderOverlay(guiGraphics.pose());
            }
        });
        event.registerAboveAll("shrooms", (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
            if (PoppyDelight.SHROOM_EFFECT_RENDERER.effectActiveLastTick) {
                gui.setupOverlayRenderState(true, false);
                PoppyDelight.SHROOM_EFFECT_RENDERER.renderOverlay(guiGraphics.pose());
            }
        });

    }
}