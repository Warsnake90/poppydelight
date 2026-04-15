package net.warsnake.poppydelight.client.fakeDaturaStuff;

import net.warsnake.poppydelight.PoppyDelight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// cleanup during
@Mod.EventBusSubscriber(modid = PoppyDelight.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class DisconnectHandler {

    @SubscribeEvent
    public static void onPlayerLogout(ClientPlayerNetworkEvent.LoggingOut event) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            FakeEntityHandler.removeAllFakeEntities(level);
        }
    }
}