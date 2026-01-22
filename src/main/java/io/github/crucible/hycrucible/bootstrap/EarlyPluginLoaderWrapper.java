package io.github.crucible.hycrucible.bootstrap;

import com.hypixel.hytale.plugin.early.ClassTransformer;
import com.hypixel.hytale.plugin.early.EarlyPluginLoader;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Path;
import java.util.*;

public class EarlyPluginLoaderWrapper {

    public static final Path EARLY_PLUGINS_PATH = Path.of("earlyplugins");

    private static final String ENABLE = "--accept-early-plugins";
    private static final String PATHS = "--early-plugins";

    private static final Method _collectPluginJars;
    private static final Field _pluginClassLoader;
    private static final Field _transformers;

    @Getter
    private static URLClassLoader pluginClassLoader;

    private EarlyPluginLoaderWrapper() {

    }

    static {

        try {

            if (!EARLY_PLUGINS_PATH.toFile().exists())
                EARLY_PLUGINS_PATH.toFile().mkdirs();

            _collectPluginJars = EarlyPluginLoader.class.getDeclaredMethod("collectPluginJars", Path.class, List.class);
            _pluginClassLoader = EarlyPluginLoader.class.getDeclaredField("pluginClassLoader");

            _transformers = EarlyPluginLoader.class.getDeclaredField("transformers");

            _collectPluginJars.setAccessible(true);
            _pluginClassLoader.setAccessible(true);

            _transformers.setAccessible(true);

        } catch (NoSuchMethodException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }

    }

    @SneakyThrows
    public static List<String> init(String[] args) {

        List<String> parsedArgs = parseArgs(args);
        List<Path> parsedPaths = readPaths(args);

        var plugins = collectPluginJars(parsedPaths);

        loadTransformers(plugins.toArray(new URL[0]));

        return parsedArgs;

    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    private static void loadTransformers(URL[] urls) {

        pluginClassLoader = new URLClassLoader(urls, EarlyPluginLoaderWrapper.class.getClassLoader());

        List<ClassTransformer> transformers = new ArrayList<>();

        for (ClassTransformer transformer : ServiceLoader.load(ClassTransformer.class, pluginClassLoader)) {
            ServerLauncherWrapper.getLogger().atInfo().log("[EarlyPlugin] Loading transformer: " + transformer.getClass().getName() + " (priority=" + transformer.priority() + ")");
            transformers.add(transformer);
        }

        transformers.sort(Comparator.comparingInt(ClassTransformer::priority).reversed());

        ((List<ClassTransformer>) _transformers.get(null)).clear();
        ((List<ClassTransformer>) _transformers.get(null)).addAll(transformers);

        _pluginClassLoader.set(null, pluginClassLoader);

    }

    private static ObjectArrayList<URL> collectPluginJars(List<Path> paths) throws InvocationTargetException, IllegalAccessException {

        ObjectArrayList<URL> urls = new ObjectArrayList<>();

        _collectPluginJars.invoke(null, EARLY_PLUGINS_PATH, urls);

        for (Path path : paths)
            _collectPluginJars.invoke(null, path, urls);

        return urls;
    }

    private static List<String> parseArgs(String[] args) {

        ArrayList<String> argsArray = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {

            if (args[i].startsWith(PATHS)) {
                if (!args[i].contains("=")) {
                    i++;
                }
                continue;
            }

            argsArray.add(args[i]);
        }

        if (!argsArray.contains(ENABLE)) {
            argsArray.add(ENABLE);
        }

        return argsArray;
    }

    private static List<Path> readPaths(String[] args) {

        ArrayList<Path> arrayList = new ArrayList<>();

        for (int i = 0; i < args.length; i++) {

            int index;
            String paths = "";

            if ((index = args[i].indexOf(PATHS)) != -1) {
                if (args[i].charAt(index + 1) == '=') {
                    paths = args[i].substring(index + 2);
                } else if (i + 1 < args.length) {
                    paths = args[i + 1];
                }
            }

            if (!paths.isEmpty())
                for (String path : paths.split(","))
                    arrayList.add(Path.of(path.trim()));

        }

        return arrayList;

    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static void addTransformer(ClassTransformer transformer) {
        ((List<ClassTransformer>) _transformers.get(null)).add(transformer);
    }

    @SneakyThrows
    @SuppressWarnings("unchecked")
    public static List<ClassTransformer> getTransformers() {
        return Collections.unmodifiableList(((List<ClassTransformer>) _transformers.get(null)));
    }

}
