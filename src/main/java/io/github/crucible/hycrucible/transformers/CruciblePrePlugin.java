package io.github.crucible.hycrucible.transformers;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import io.github.crucible.hycrucible.bootstrap.annotations.Transformer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

@Transformer
public class CruciblePrePlugin implements ClassTransformer {

    @Override
    public int priority() {
        return ClassTransformer.super.priority();
    }

    @Nullable
    @Override
    public byte[] transform(@Nonnull String s, @Nonnull String s1, @Nonnull byte[] bytes) {
        return bytes;
    }

}
