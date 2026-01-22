package io.github.crucible.hycrucible.transformers;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import io.github.crucible.hycrucible.bootstrap.annotations.Transformer;
import io.github.crucible.hycrucible.bootstrap.mixin.Service;
import io.github.crucible.hycrucible.bootstrap.util.ClassLoaderHolder;
import org.spongepowered.asm.launch.MixinBootstrap;
import org.spongepowered.asm.mixin.MixinEnvironment;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Transformer
public class MixinTransformerBridge implements ClassTransformer {

    {
        MixinBootstrap.init();
    }

    @Override
    public int priority() {
        return -1000;
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
