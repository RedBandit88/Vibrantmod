package com.vibrantmod.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Stores all tweakable colour-grading settings.
 * Values are saved as JSON in .minecraft/config/vibrantmod.json
 */
public class VibrantConfig {

    // ── singleton ────────────────────────────────────────────────────────────
    private static VibrantConfig INSTANCE;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH =
            FabricLoader.getInstance().getConfigDir().resolve("vibrantmod.json");

    public static VibrantConfig get() {
        if (INSTANCE == null) INSTANCE = load();
        return INSTANCE;
    }

    // ── settings ─────────────────────────────────────────────────────────────

    /** Master toggle – disables the shader entirely when false */
    public boolean enabled = true;

    /**
     * Saturation multiplier.
     * 0.0 = greyscale, 1.0 = vanilla, 2.0 = doubled (Vibrant Vanilla default ≈ 1.6)
     */
    public float saturation = 1.6f;

    /**
     * Contrast: 1.0 = vanilla, >1 = more contrast.
     * Applied as  out = (in - 0.5) * contrast + 0.5
     */
    public float contrast = 1.1f;

    /**
     * Brightness offset in linear light (-0.2 … +0.2).
     * 0 = unchanged.
     */
    public float brightness = 0.0f;

    /**
     * Colour temperature shift.
     * Negative = cooler (more blue), positive = warmer (more orange).
     * Range: -1.0 … +1.0
     */
    public float temperature = 0.0f;

    /**
     * Vibrance: selectively boosts desaturated colours more than already-vivid ones.
     * 0 = off, 1 = strong vibrance boost.
     */
    public float vibrance = 0.4f;

    /**
     * Gamma correction exponent applied after all other effects.
     * 1.0 = linear (no correction), 0.8 = slightly brighter shadows.
     */
    public float gamma = 0.95f;

    // ── persistence ──────────────────────────────────────────────────────────

    public static VibrantConfig load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                return GSON.fromJson(json, VibrantConfig.class);
            } catch (IOException | com.google.gson.JsonParseException e) {
                System.err.println("[VibrantMod] Failed to load config, using defaults: " + e.getMessage());
            }
        }
        return new VibrantConfig();
    }

    public void save() {
        try {
            Files.writeString(CONFIG_PATH, GSON.toJson(this));
        } catch (IOException e) {
            System.err.println("[VibrantMod] Failed to save config: " + e.getMessage());
        }
    }

    /** Called when shader uniforms need updating after a settings change. */
    public void markDirty() {
        save();
    }
}
