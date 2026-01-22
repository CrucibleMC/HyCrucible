package io.github.crucible.hycrucible.bootstrap.mixin.impl;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.HytaleServer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.logging.Level;

@Mixin(value = HytaleServer.class, remap = false)
public class HytaleServerMixin {

    @Final
    @Shadow
    private static HytaleLogger LOGGER;

    @Inject(method = "boot", at = @At("RETURN"))
    private void boot(CallbackInfo ci) {
        LOGGER.at(Level.INFO).log("HyCrucible server has been booted! :P");
    }

}
