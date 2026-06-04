package com.vibrantmod.mixin;

import com.vibrantmod.client.VibrantPostProcessRenderer;
import net.minecraft.client.renderer.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Hooks into GameRenderer.reloadShaders() so that our post-process
 * shader is reloaded whenever the player presses F3+T or changes
 * resource packs.
 */
@Mixin(GameRenderer.class)
public class GameRendererMixin {

    @Inject(method = "reloadShaders", at = @At("TAIL"))
    private void vibrantmod$onReloadShaders(net.minecraft.server.packs.resources.ResourceProvider resourceProvider,
                                             CallbackInfo ci) {
        VibrantPostProcessRenderer.reload();
    }
}
