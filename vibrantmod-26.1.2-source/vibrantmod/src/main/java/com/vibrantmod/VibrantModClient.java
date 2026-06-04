package com.vibrantmod;

import com.vibrantmod.config.VibrantConfig;
import com.vibrantmod.client.VibrantPostProcessRenderer;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VibrantModClient implements ClientModInitializer {

    public static final String MOD_ID = "vibrantmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitializeClient() {
        LOGGER.info("VibrantMod initialising (Minecraft 26.1.2)");

        // Load config from disk
        VibrantConfig.get();

        // Register the post-process renderer so it hooks into the frame pipeline
        VibrantPostProcessRenderer.register();

        // Tick listener: push updated uniforms if config was changed via the GUI
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            VibrantPostProcessRenderer.pushUniforms();
        });
    }
}
