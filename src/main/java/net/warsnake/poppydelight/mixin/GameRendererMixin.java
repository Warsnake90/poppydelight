package com.example.psychshaders.mixin;

import net.warsnake.poppydelight.PsychedelicShaderHandler;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "resize(II)V", at = @At("TAIL"))
    private void onResize(int width, int height, CallbackInfo ci) {
        PsychedelicShaderHandler.resize(width, height);
    }
}
