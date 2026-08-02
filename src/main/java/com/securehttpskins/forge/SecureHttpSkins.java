package com.securehttpskins.forge;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(SecureHttpSkins.MOD_ID)
public class SecureHttpSkins {

    public static final String MOD_ID = "securehttpskins";
    public static final Logger LOGGER = LogManager.getLogger(MOD_ID);

    public SecureHttpSkins() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        LOGGER.info("Secure HTTP Skins loaded.");
        LOGGER.info("  HTTPS redirect: {}", Config.isHttpsRedirectEnabled());
        LOGGER.info("  Signature bypass: {}", Config.isSignatureBypassEnabled());
        LOGGER.info("  Debug logging: {}", Config.isDebugLogging());
    }
}
