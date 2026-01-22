package io.github.crucible.hycrucible.bootstrap;

import com.hypixel.hytale.Main;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.plugin.early.TransformingClassLoader;
import lombok.Getter;
import lombok.SneakyThrows;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class ServerLauncherWrapper {

    @Getter
    private static HytaleLogger logger;

    private static Method _getClasspathUrls;

    @SneakyThrows
    static void main(String[] args) {

        logger = HytaleLogger.get("HyCrucible");

        Locale.setDefault(Locale.ENGLISH);

        System.setProperty("java.awt.headless", "true");
        System.setProperty("file.encoding", "UTF-8");

        _getClasspathUrls = Main.class.getDeclaredMethod("getClasspathUrls");
        _getClasspathUrls.setAccessible(true);

        List<String> parsedArgs = EarlyPluginLoaderWrapper.init(args);

        ClassTransformerMapper.loadCandidates(ClassTransformerMapper.class.getClassLoader());

        bootServer(parsedArgs.toArray(new String[0]));

    }

    private static void bootServer(String[] args) {

        try {

            URL[] urls = (URL[]) _getClasspathUrls.invoke(null);

            ClassLoader appClassLoader = ServerLauncherWrapper.class.getClassLoader();

            TransformingClassLoader transformingClassLoader = new TransformingClassLoader(
                    urls, EarlyPluginLoaderWrapper.getTransformers(), appClassLoader.getParent(), appClassLoader
            );

            Thread.currentThread().setContextClassLoader(transformingClassLoader);

            Class<?> lateMainClass = transformingClassLoader.loadClass("com.hypixel.hytale.LateMain");

            Method mainMethod = lateMainClass.getMethod("lateMain", String[].class);

            mainMethod.setAccessible(true);
            mainMethod.invoke((Object) null, (Object) args);

        } catch (NoSuchMethodException | IllegalAccessException e) {

            throw new RuntimeException("Failed to launch with transforming classloader", e);

        } catch (InvocationTargetException e) {

            Throwable cause = e.getCause();

            if (cause instanceof RuntimeException re) {
                throw re;
            } else if (cause instanceof Error err) {
                throw err;
            } else {
                throw new RuntimeException("LateMain.lateMain() threw an exception", cause);
            }

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

    }


}
