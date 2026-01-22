package io.github.crucible.hycrucible.bootstrap.mixin.providers;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import io.github.crucible.hycrucible.bootstrap.EarlyPluginLoaderWrapper;
import io.github.crucible.hycrucible.bootstrap.util.ClassLoaderHolder;
import lombok.SneakyThrows;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.IClassBytecodeProvider;

import java.io.InputStream;
import java.lang.reflect.Method;

public class BytecodeProvider implements IClassBytecodeProvider {

    private static final Method _readStream;

    static {
        try {
            _readStream = ClassReader.class.getDeclaredMethod("readStream", InputStream.class, Boolean.TYPE);
            _readStream.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClassNode getClassNode(String name) {
        return this.getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) {
        return this.getClassNode(name, runTransformers, 0);
    }

    @Override
    @SneakyThrows
    public ClassNode getClassNode(String name, boolean runTransformers, int readerFlags) {

        try (InputStream clazz = ClassLoaderHolder.findResource(ClassLoaderHolder.formatClassName(name))) {

            byte[] data = (byte[]) _readStream.invoke(null, clazz, false);

            if (runTransformers) {
                for (ClassTransformer classTransformer : EarlyPluginLoaderWrapper.getTransformers()) {

                    byte[] transformed = classTransformer.transform(name, name, data);

                    if (transformed != null)
                        data = transformed;

                }
            }

            ClassReader reader = new ClassReader(data);
            ClassNode node = new ClassNode();

            reader.accept(node, readerFlags);

            return node;
        }

    }


}
