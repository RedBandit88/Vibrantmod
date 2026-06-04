#version 150

uniform sampler2D DiffuseSampler;

uniform float Saturation;   // 1.0 = vanilla, 1.6 = Vibrant Vanilla default
uniform float Contrast;     // 1.0 = unchanged
uniform float Brightness;   // 0.0 = unchanged (−0.5 … +0.5)
uniform float Temperature;  // 0.0 = neutral  (−1 = cold, +1 = warm)
uniform float Vibrance;     // 0.0 = off, 1.0 = full vibrance
uniform float Gamma;        // 1.0 = linear   (<1 = brighter)

in vec2 texCoord;
out vec4 fragColor;

// ── helpers ──────────────────────────────────────────────────────────────────

float luminance(vec3 c) {
    return dot(c, vec3(0.2126, 0.7152, 0.0722));
}

// Converts linear RGB → HSL-like saturation adjustment (keeps hue + luma)
vec3 adjustSaturation(vec3 color, float sat) {
    float lum = luminance(color);
    return mix(vec3(lum), color, sat);
}

// Vibrance: boosts less-saturated colours more than already vivid ones
vec3 adjustVibrance(vec3 color, float vib) {
    float maxC  = max(color.r, max(color.g, color.b));
    float minC  = min(color.r, min(color.g, color.b));
    float sat   = maxC - minC;                    // 0 = grey, 1 = fully saturated
    float boost = (1.0 - sat) * vib;              // boost more when less saturated
    float lum   = luminance(color);
    return mix(color, mix(vec3(lum), color, 1.0 + boost), 1.0);
}

// Colour temperature: shifts R/B channels slightly
vec3 adjustTemperature(vec3 color, float temp) {
    // Warm (positive): more red/green, less blue
    // Cool (negative): more blue, less red/green
    color.r += temp * 0.1;
    color.g += temp * 0.02;
    color.b -= temp * 0.1;
    return color;
}

// ── main ─────────────────────────────────────────────────────────────────────

void main() {
    vec4 tex = texture(DiffuseSampler, texCoord);
    vec3 color = tex.rgb;

    // 1. Gamma decode (assume sRGB input ≈ pow 2.2)
    color = pow(color, vec3(2.2));

    // 2. Brightness
    color += Brightness;

    // 3. Contrast:  out = (in − 0.5) × contrast + 0.5
    color = (color - 0.5) * Contrast + 0.5;

    // 4. Colour temperature
    color = adjustTemperature(color, Temperature);

    // 5. Saturation
    color = adjustSaturation(color, Saturation);

    // 6. Vibrance (applied after saturation for best results)
    color = adjustVibrance(color, Vibrance);

    // 7. Gamma re-encode with user gamma tweak
    color = clamp(color, 0.0, 1.0);
    color = pow(color, vec3(Gamma / 2.2));   // re-encode + apply gamma shift

    fragColor = vec4(color, tex.a);
}
