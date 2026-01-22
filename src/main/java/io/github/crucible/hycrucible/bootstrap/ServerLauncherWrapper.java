package io.github.crucible.hycrucible.bootstrap;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.plugin.early.TransformingClassLoader;
import io.github.crucible.hycrucible.bootstrap.util.ClassLoaderHolder;
import io.github.crucible.hycrucible.bootstrap.util.ClassTransformerMapper;
import io.github.crucible.hycrucible.bootstrap.util.ReflectionAccessor;
import lombok.Getter;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;

public class ServerLauncherWrapper {

    @Getter
    private static HytaleLogger logger;

    @Getter
    private static TransformingClassLoader transformingClassLoader;

    static void main(String[] args) throws IOException {

        ClassLoaderHolder.setSystemClassLoader(ServerLauncherWrapper.class.getClassLoader());

        logger = HytaleLogger.get("HyCrucible");

        Locale.setDefault(Locale.ENGLISH);

        System.setProperty("java.awt.headless", "true");
        System.setProperty("file.encoding", "UTF-8");

        List<String> parsedArgs = EarlyPluginLoaderWrapper.init(args);

        ClassTransformerMapper.loadCandidates(ClassTransformerMapper.class.getClassLoader());

        bootServer(parsedArgs.toArray(new String[0]));

    }

    private static void bootServer(String[] args) {

        URL[] urls = ReflectionAccessor.<URL[]>method(
                ClassLoaderHolder.getSystemClassLoader(),
                "com.hypixel.hytale.Main",
                "getClasspathUrls"
        ).$();

        transformingClassLoader = new TransformingClassLoader(
                urls,
                EarlyPluginLoaderWrapper.getTransformers(),
                ClassLoaderHolder.getSystemClassLoader().getParent(),
                ClassLoaderHolder.getSystemClassLoader()
        );

        Thread.currentThread().setContextClassLoader(transformingClassLoader);

        ReflectionAccessor.loadAndGetMethod(
                ServerLauncherWrapper.getTransformingClassLoader(),
                "com.hypixel.hytale.LateMain",
                "lateMain",
                String[].class
        ).$((Object) args);

    }


}
