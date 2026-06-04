package com.vibrantmod.client;

import com.mojang.blaze3d.pipeline.RenderTarget;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.systems.RenderSystem;
import com.vibrantmod.VibrantModClient;
import com.vibrantmod.config.VibrantConfig;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

/**
 * Loads vibrantmod:post/vibrance.json as a post-process chain and
 * re-runs it at the end of every frame via HudRenderCallback.
 *
 * Uniform values are sourced directly from VibrantConfig so any
 * change made in the config screen takes effect on the next frame.
 */
public class VibrantPostProcessRenderer {

    private static PostChain postChain = null;
    private static boolean dirty = true;   // force uniform push on first frame
    private static boolean loadFailed = false;

    public static void register() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> render());

        // Re-load the shader if the window is resized
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents
                .CLIENT_STARTED.register(client -> reload());
    }

    /** Load (or reload) the PostChain from resources. */
    public static void reload() {
        loadFailed = false;
        Minecraft mc = Minecraft.getInstance();
        if (postChain != null) {
            postChain.close();
            postChain = null;
        }
        try {
            postChain = PostChain.load(
                    mc.getResourceManager(),
                    mc.getMainRenderTarget(),
                    ResourceLocation.fromNamespaceAndPath(VibrantModClient.MOD_ID, "post/vibrance.json")
            );
            postChain.resize(mc.getWindow().getWidth(), mc.getWindow().getHeight());
            dirty = true;
            VibrantModClient.LOGGER.info("VibrantMod shader loaded successfully.");
        } catch (IOException e) {
            loadFailed = true;
            VibrantModClient.LOGGER.error("VibrantMod could not load post-process shader: {}", e.getMessage());
        }
    }

    /** Push the current config values into the shader uniforms (only when needed). */
    public static void pushUniforms() {
        dirty = true; // always update – config may have changed
    }

    private static void render() {
        VibrantConfig cfg = VibrantConfig.get();
        if (!cfg.enabled || postChain == null || loadFailed) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) return; // only in-game

        if (dirty) {
            applyUniforms(cfg);
            dirty = false;
        }

        RenderTarget main = mc.getMainRenderTarget();
        main.unbindWrite();
        postChain.process(mc.getTimer().getGameTimeDeltaPartialTick(true));
        main.bindWrite(false);
    }

    private static void applyUniforms(VibrantConfig cfg) {
        if (postChain == null) return;
        try {
            setUniform("Saturation",  cfg.saturation);
            setUniform("Contrast",    cfg.contrast);
            setUniform("Brightness",  cfg.brightness);
            setUniform("Temperature", cfg.temperature);
            setUniform("Vibrance",    cfg.vibrance);
            setUniform("Gamma",       cfg.gamma);
        } catch (Exception e) {
            VibrantModClient.LOGGER.warn("VibrantMod could not set uniform: {}", e.getMessage());
        }
    }

    private static void setUniform(String name, float value) {
        if (postChain == null) return;
        // PostChain exposes passes; iterate to find the matching uniform
        postChain.getPasses().forEach(pass -> {
            Uniform u = pass.getEffect().getUniform(name);
            if (u != null) u.set(value);
        });
    }

    public static void close() {
        if (postChain != null) {
            postChain.close();
            postChain = null;
        }
    }
}
