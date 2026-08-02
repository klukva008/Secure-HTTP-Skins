package com.securehttpskins.forge.mixin;

import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.securehttpskins.forge.Config;
import com.securehttpskins.forge.SecureHttpSkins;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = YggdrasilMinecraftSessionService.class, remap = false)
public abstract class PropertyMixin {

    @Inject(method = "getSecurePropertyValue", at = @At("HEAD"), cancellable = true)
    private void securehttpskins$bypassValidation(Property property, CallbackInfoReturnable<String> cir) {
        if (!Config.isSignatureBypassEnabled()) return;
        if (Config.isDebugLogging()) {
            SecureHttpSkins.LOGGER.info("[PropertyMixin] Bypassed signature validation for property '{}'", property.getName());
        }
        cir.setReturnValue(property.getValue());
    }
}
