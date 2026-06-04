package com.vibrantmod.client;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.vibrantmod.config.VibrantConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.Component;

/**
 * Provides a Cloth Config screen accessible via the Mod Menu "Config" button.
 * If Cloth Config or Mod Menu is absent this class simply won't be called.
 */
public class VibrantModMenuIntegration implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            VibrantConfig cfg = VibrantConfig.get();

            ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Component.literal("Vibrant World Settings"))
                    .setSavingRunnable(() -> {
                        cfg.save();
                        VibrantPostProcessRenderer.pushUniforms();
                    });

            ConfigEntryBuilder eb = builder.entryBuilder();
            ConfigCategory cat = builder.getOrCreateCategory(Component.literal("Colour Grading"));

            // ── Enable toggle ────────────────────────────────────────────────
            cat.addEntry(eb.startBooleanToggle(
                            Component.literal("Enable Vibrant World"), cfg.enabled)
                    .setDefaultValue(true)
                    .setTooltip(Component.literal("Master toggle for the colour-grading shader"))
                    .setSaveConsumer(v -> cfg.enabled = v)
                    .build());

            // ── Saturation ───────────────────────────────────────────────────
            cat.addEntry(eb.startFloatField(
                            Component.literal("Saturation"), cfg.saturation)
                    .setDefaultValue(1.6f)
                    .setMin(0.0f).setMax(4.0f)
                    .setTooltip(
                            Component.literal("1.0 = vanilla, 1.6 = Vibrant Vanilla default"),
                            Component.literal("0.0 = greyscale, 4.0 = very vivid"))
                    .setSaveConsumer(v -> cfg.saturation = v)
                    .build());

            // ── Vibrance ─────────────────────────────────────────────────────
            cat.addEntry(eb.startFloatField(
                            Component.literal("Vibrance"), cfg.vibrance)
                    .setDefaultValue(0.4f)
                    .setMin(0.0f).setMax(1.0f)
                    .setTooltip(Component.literal("Selectively boosts less-saturated colours more."),
                            Component.literal("0 = off, 1 = full vibrance boost"))
                    .setSaveConsumer(v -> cfg.vibrance = v)
                    .build());

            // ── Contrast ─────────────────────────────────────────────────────
            cat.addEntry(eb.startFloatField(
                            Component.literal("Contrast"), cfg.contrast)
                    .setDefaultValue(1.1f)
                    .setMin(0.5f).setMax(2.0f)
                    .setTooltip(Component.literal("1.0 = unchanged, >1 increases contrast"))
                    .setSaveConsumer(v -> cfg.contrast = v)
                    .build());

            // ── Brightness ───────────────────────────────────────────────────
            cat.addEntry(eb.startFloatField(
                            Component.literal("Brightness"), cfg.brightness)
                    .setDefaultValue(0.0f)
                    .setMin(-0.5f).setMax(0.5f)
                    .setTooltip(Component.literal("Offset added to pixel brightness (-0.5 … +0.5)"))
                    .setSaveConsumer(v -> cfg.brightness = v)
                    .build());

            // ── Temperature ──────────────────────────────────────────────────
            cat.addEntry(eb.startFloatField(
                            Component.literal("Colour Temperature"), cfg.temperature)
                    .setDefaultValue(0.0f)
                    .setMin(-1.0f).setMax(1.0f)
                    .setTooltip(Component.literal("Negative = cooler/blue, Positive = warmer/orange"))
                    .setSaveConsumer(v -> cfg.temperature = v)
                    .build());

            // ── Gamma ────────────────────────────────────────────────────────
            cat.addEntry(eb.startFloatField(
                            Component.literal("Gamma"), cfg.gamma)
                    .setDefaultValue(0.95f)
                    .setMin(0.5f).setMax(2.0f)
                    .setTooltip(Component.literal("1.0 = linear, <1 = brighter shadows, >1 = darker"))
                    .setSaveConsumer(v -> cfg.gamma = v)
                    .build());

            return builder.build();
        };
    }
}
