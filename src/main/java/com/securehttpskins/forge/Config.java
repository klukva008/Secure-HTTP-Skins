package com.securehttpskins.forge;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public final class Config {

    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.BooleanValue ENABLE_HTTPS_REDIRECT;
    private static final ForgeConfigSpec.BooleanValue ENABLE_SIGNATURE_BYPASS;
    private static final ForgeConfigSpec.BooleanValue DEBUG_LOGGING;
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_URL_PATTERNS;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        builder.push("securehttpskins");

        ENABLE_HTTPS_REDIRECT = builder
                .comment("Rewrite all http:// skin/cape texture URLs to https://.")
                .define("enableHttpsRedirect", true);

        ENABLE_SIGNATURE_BYPASS = builder
                .comment("Bypass Mojang texture signature validation so SkinRestorer and similar mods work.")
                .define("enableSignatureBypass", true);

        DEBUG_LOGGING = builder
                .comment("Log every HTTP->HTTPS URL rewrite to the console.")
                .define("debugLogging", false);

        EXCLUDED_URL_PATTERNS = builder
                .comment(
                        "Regex patterns (Java regex, find()) for URLs that should NOT be rewritten to HTTPS.",
                        "Example: \"^http://my-skin-server\\.example\\.com/.*\""
                )
                .defineList("excludedUrlPatterns", Collections.emptyList(), o -> o instanceof String);

        builder.pop();
        SPEC = builder.build();
    }

    private static volatile List<? extends String> lastRaw = null;
    private static volatile List<Pattern> compiled = Collections.emptyList();

    private Config() {
    }

    public static boolean isHttpsRedirectEnabled() {
        try {
            return ENABLE_HTTPS_REDIRECT.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }

    public static boolean isSignatureBypassEnabled() {
        try {
            return ENABLE_SIGNATURE_BYPASS.get();
        } catch (IllegalStateException e) {
            return true;
        }
    }

    public static boolean isDebugLogging() {
        try {
            return DEBUG_LOGGING.get();
        } catch (IllegalStateException e) {
            return false;
        }
    }

    public static boolean shouldForceHttps(String url) {
        List<? extends String> raw;
        try {
            raw = EXCLUDED_URL_PATTERNS.get();
        } catch (IllegalStateException e) {
            return true;
        }

        if (raw != lastRaw) {
            synchronized (Config.class) {
                if (raw != lastRaw) {
                    compiled = raw.stream()
                            .map(Config::safeCompile)
                            .filter(p -> p != null)
                            .collect(Collectors.toList());
                    lastRaw = raw;
                    SecureHttpSkins.LOGGER.info("Config reloaded: {} exclusion pattern(s) active.", compiled.size());
                }
            }
        }

        for (Pattern pattern : compiled) {
            if (pattern.matcher(url).find()) {
                return false;
            }
        }
        return true;
    }

    private static Pattern safeCompile(String regex) {
        try {
            return Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            SecureHttpSkins.LOGGER.warn("Invalid exclusion regex '{}': {}", regex, e.getMessage());
            return null;
        }
    }
}
