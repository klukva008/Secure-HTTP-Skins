package com.securehttpskins.forge.mixin;

import com.securehttpskins.forge.Config;
import com.securehttpskins.forge.SecureHttpSkins;
import net.minecraft.client.renderer.texture.HttpTexture;
import net.minecraft.server.packs.resources.ResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.URI;

@Mixin(HttpTexture.class)
public abstract class HttpTextureMixin {

    @Shadow
    @Mutable
    private String urlString;

    @Inject(
            method = "load(Lnet/minecraft/server/packs/resources/ResourceManager;)V",
            at = @At("HEAD")
    )
    private void securehttpskins$forceHttps(ResourceManager resourceManager, CallbackInfo ci) {
        if (!Config.isHttpsRedirectEnabled()) return;

        String url = this.urlString;
        if (url == null) return;

        String secured = rewrite(url);
        if (secured != url) {
            if (Config.isDebugLogging()) {
                SecureHttpSkins.LOGGER.info("[HttpTexture] {} -> {}", url, secured);
            }
            this.urlString = secured;
        }
    }

    private static String rewrite(String uri) {
        try {
            if (uri == null) return null;
            URI u = URI.create(uri);
            if (!"http".equalsIgnoreCase(u.getScheme())) return uri;
            if (u.getHost() == null) return uri;
            if (!Config.shouldForceHttps(uri)) return uri;

            return new URI(
                    "https",
                    u.getUserInfo(),
                    u.getHost(),
                    -1,
                    u.getPath(),
                    u.getQuery(),
                    u.getFragment()
            ).toString();
        } catch (Exception e) {
            return uri;
        }
    }
}
