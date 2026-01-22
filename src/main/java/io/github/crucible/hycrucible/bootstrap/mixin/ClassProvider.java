package io.github.crucible.hycrucible.bootstrap.mixin;

import io.github.crucible.hycrucible.bootstrap.util.ClassLoaderHolder;
import org.spongepowered.asm.service.IClassProvider;

import java.net.URL;

public class ClassProvider implements IClassProvider {

    @Override
    public URL[] getClassPath() {
        return new URL[0];
    }

    @Override
    public Class<?> findClass(String name) throws ClassNotFoundException {
        return ClassLoaderHolder.getClassLoader_(name).loadClass(name);
    }

    @Override
    public Class<?> findClass(String name, boolean initialize) throws ClassNotFoundException {
        return Class.forName(name, initialize, ClassLoaderHolder.getClassLoader_(name));
    }

    @Override
    public Class<?> findAgentClass(String name, boolean initialize) throws ClassNotFoundException {
        return this.findClass(name, initialize);
    }

}
