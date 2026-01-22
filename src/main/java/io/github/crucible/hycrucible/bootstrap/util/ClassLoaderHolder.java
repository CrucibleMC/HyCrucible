package io.github.crucible.hycrucible.bootstrap.util;

import lombok.Getter;
import lombok.Setter;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ClassLoaderHolder {

    @Getter
    @Setter
    private static ClassLoader systemClassLoader;

    @Getter
    @Setter
    private static ClassLoader pluginClassLoader;

    @Getter
    @Setter
    private static ClassLoader runtimeClassLoader;

    public static ClassLoader getClassLoader(String clazz) throws FileNotFoundException {

        if (contains(systemClassLoader, clazz)) return systemClassLoader;
        if (contains(pluginClassLoader, clazz)) return pluginClassLoader;
        if (contains(runtimeClassLoader, clazz)) return pluginClassLoader;

        throw new NullPointerException("The ClassLoaderHolder cannot determine an classloader for this element");

    }

    public static ClassLoader getClassLoader_(String clazz) throws ClassNotFoundException {
        try {
            return getClassLoader(clazz);
        } catch (FileNotFoundException e) {
            throw new ClassNotFoundException(clazz);
        }
    }

    public static InputStream findResource(String resource) throws FileNotFoundException {
        return getClassLoader(resource).getResourceAsStream(resource);
    }

    private static boolean contains(ClassLoader classLoader, String clazz) {

//        String normalizedName = formatClassName(clazz);

        return classLoader != null && classLoader.getResource(clazz) != null;
    }

    public static String formatClassName(String className) {
       return className.replace(".", "/").concat(".class");
    }


}
