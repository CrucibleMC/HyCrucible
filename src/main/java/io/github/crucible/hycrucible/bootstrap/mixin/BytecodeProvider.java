package io.github.crucible.hycrucible.bootstrap.mixin;

import io.github.crucible.hycrucible.bootstrap.util.ClassLoaderHolder;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.service.IClassBytecodeProvider;

import java.io.IOException;
import java.io.InputStream;

public class BytecodeProvider implements IClassBytecodeProvider {

    @Override
    public ClassNode getClassNode(String name) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, true);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers) throws ClassNotFoundException, IOException {
        return this.getClassNode(name, runTransformers, 0);
    }

    @Override
    public ClassNode getClassNode(String name, boolean runTransformers, int readerFlags) throws ClassNotFoundException, IOException {

        ClassReader reader = this.getReader(name);
        ClassNode node = new ClassNode();

        reader.accept(node, readerFlags);

        return node;

    }

    private ClassReader getReader(String name) throws IOException {
        try (InputStream in = ClassLoaderHolder.findResource(ClassLoaderHolder.formatClassName(name))) {
            if (in != null)
                return new ClassReader(in);
        }
        throw new IOException("Class not found: " + name);
    }

}
