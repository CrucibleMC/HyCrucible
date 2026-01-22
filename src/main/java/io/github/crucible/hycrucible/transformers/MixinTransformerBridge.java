package io.github.crucible.hycrucible.transformers;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import io.github.crucible.hycrucible.bootstrap.annotations.Transformer;
import io.github.crucible.hycrucible.bootstrap.mixin.Service;
import io.github.crucible.hycrucible.bootstrap.mixin.ServiceBootstrap;
import io.github.crucible.hycrucible.bootstrap.util.ClassLoaderHolder;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;
import org.spongepowered.asm.mixin.Mixins;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Transformer
public class MixinTransformerBridge implements ClassTransformer {

    public MixinTransformerBridge() {

        // Hytale requires us to use their log manager. Setting it now prevents
        // Mixin from trying to initialize their own.
        System.setProperty("java.util.logging.manager", "com.hypixel.hytale.logger.backend.HytaleLogManager");

        // Set the bootstrap and mixin service manually. This will avoid the
        // scan Mixin usually performs, and prevents invalid platforms like
        // LaunchWrapper or ModLauncher from loading.
        System.setProperty("mixin.bootstrapService", ServiceBootstrap.class.getName());
        System.setProperty("mixin.service", Service.class.getName());

        MixinBootstrap.init();

        Mixins.addConfiguration("hycrucible.mixins.json");

    }

    @Override
    public int priority() {
        return -100;
    }

    @Nullable
    @Override
    public byte[] transform(@Nonnull String name, @Nonnull String path, @Nonnull byte[] bytes) {

        if (ClassLoaderHolder.getRuntimeClassLoader() == null) {

            ClassLoaderHolder.setRuntimeClassLoader(Thread.currentThread().getContextClassLoader());

            Service.phaseConsumer.accept(MixinEnvironment.Phase.INIT);
            Service.phaseConsumer.accept(MixinEnvironment.Phase.DEFAULT);

        }

        return Service.transformer.transformClassBytes(name, name, bytes);

    }

}
