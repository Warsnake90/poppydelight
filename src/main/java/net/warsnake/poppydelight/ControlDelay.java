package net.warsnake.poppydelight;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.UUID;

//@Mod("poppydelight")
public class ControlDelay {

    private static final int DELAY_TICKS = 10;
    private UUID targetPlayerUUID;

    private boolean forward, backward, left, right, sneak;
    private boolean pendingForward, pendingBackward, pendingLeft, pendingRight, pendingSneak;
    private int tickCounter = 0;


    public void setTargetPlayer(UUID playerUUID) {
        this.targetPlayerUUID = playerUUID;
    }

   // @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || targetPlayerUUID == null) return;

        if (!player.getUUID().equals(targetPlayerUUID)) return;

        tickCounter++;

        pendingForward = mc.options.keyUp.isDown();
        pendingBackward = mc.options.keyDown.isDown();
        pendingLeft = mc.options.keyLeft.isDown();
        pendingRight = mc.options.keyRight.isDown();
        pendingSneak = mc.options.keyShift.isDown();

        if (tickCounter >= DELAY_TICKS) {
            forward = pendingForward;
            backward = pendingBackward;
            left = pendingLeft;
            right = pendingRight;
            sneak = pendingSneak;
            tickCounter = 0;
        }

        player.zza = (forward ? 1 : 0) - (backward ? 1 : 0);
        player.xxa = (right ? 1 : 0) - (left ? 1 : 0);
        player.setShiftKeyDown(sneak);


    }

    public void clearDelay(UUID playerUUID) {
        if (targetPlayerUUID != null && targetPlayerUUID.equals(playerUUID)) {
            targetPlayerUUID = null;

            forward = backward = left = right = sneak = false;
            pendingForward = pendingBackward = pendingLeft = pendingRight = pendingSneak = false;
            tickCounter = 0;
        }
    }
}