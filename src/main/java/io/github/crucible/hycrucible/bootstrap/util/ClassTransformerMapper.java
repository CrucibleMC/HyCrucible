package io.github.crucible.hycrucible.bootstrap.util;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import io.github.crucible.hycrucible.bootstrap.EarlyPluginLoaderWrapper;
import io.github.crucible.hycrucible.bootstrap.annotations.Transformer;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassTransformerMapper {

    private static final String TARGET_PACKAGE =
            "io.github.crucible.hycrucible";

    private static final String TARGET_PATH =
            TARGET_PACKAGE.replace('.', '/') + "/";

    public static Set<String> searchCandidates(ClassLoader classLoader) throws IOException {

        Set<String> classes = new HashSet<>();

        if (classLoader instanceof URLClassLoader urlClassLoader) {
            for (URL url : urlClassLoader.getURLs()) {
                scanUrl(url, classes);
            }
        }

        Enumeration<URL> roots = classLoader.getResources(TARGET_PATH);

        while (roots.hasMoreElements()) {
            scanUrl(roots.nextElement(), classes);
        }

        return classes;
    }

    public static void loadCandidates() throws IOException {
        loadCandidates(ClassTransformerMapper.class.getClassLoader());
    }

    public static void loadCandidates(ClassLoader classLoader) throws IOException {

        Set<String> classNames = searchCandidates(classLoader);
        List<Class<?>> classes = new ArrayList<>();

        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className, false, classLoader);
                classes.add(clazz);
            } catch (ClassNotFoundException ignored) {
            }
        }

        classes.stream().filter(el ->
                el.getDeclaredAnnotation(Transformer.class) != null
        ).filter(
                ClassTransformer.class::isAssignableFrom
        ).forEach(el -> {
            try {
                ClassTransformer classTransformer = (ClassTransformer) el.getDeclaredConstructor().newInstance();
                EarlyPluginLoaderWrapper.addTransformer(classTransformer);
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException |
                     NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        });

    }

    private static void scanUrl(URL url, Set<String> classes) throws IOException {
        String protocol = url.getProtocol();

        if ("file".equals(protocol)) {
            File file = new File(url.getPath());
            if (file.isDirectory()) {
                scanDirectory(file, TARGET_PACKAGE + ".", classes);
            } else if (file.getName().endsWith(".jar")) {
                scanJar(file, classes);
            }
        } else if ("jar".equals(protocol)) {
            JarURLConnection connection = (JarURLConnection) url.openConnection();
            scanJar(connection.getJarFile(), classes);
        }
    }

    private static void scanDirectory(File root, String pkg, Set<String> classes) {
        File[] files = root.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, pkg + file.getName() + ".", classes);
            } else if (file.getName().endsWith(".class")) {
                String className = pkg + file.getName().replace(".class", "");
                classes.add(className);
            }
        }
    }

    private static void scanJar(File jarFile, Set<String> classes) throws IOException {
        try (JarFile jar = new JarFile(jarFile)) {
            scanJar(jar, classes);
        }
    }

    private static void scanJar(JarFile jar, Set<String> classes) {
        Enumeration<JarEntry> entries = jar.entries();
        while (entries.hasMoreElements()) {
            JarEntry entry = entries.nextElement();
            String name = entry.getName();

            if (name.startsWith(TARGET_PATH) && name.endsWith(".class")) {
                String className = name
                        .replace('/', '.')
                        .replace(".class", "");
                classes.add(className);
            }
        }
    }
}
