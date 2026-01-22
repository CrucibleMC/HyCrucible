package io.github.crucible.hycrucible.bootstrap.mixin;

import com.hypixel.hytale.logger.HytaleLogger;
import io.netty.util.internal.logging.MessageFormatter;
import org.spongepowered.asm.logging.ILogger;
import org.spongepowered.asm.logging.Level;

public class LoggerBridge implements ILogger {

    private final HytaleLogger logger;

    public LoggerBridge(String name) {
        this.logger = HytaleLogger.get(name);
    }

    @Override
    public String getId() {
        return logger.getName();
    }

    @Override
    public String getType() {
        return logger.getName();
    }

    private void logFormatted(Level level, String message, Object... params) {
        String formatted = MessageFormatter.arrayFormat(message, params).getMessage();
        asLevel(level).log(formatted);
    }

    private void logFormatted(Level level, String message, Throwable t) {
        asLevel(level).log(message, t);
    }

    @Override
    public void catching(Level level, Throwable t) {
        asLevel(level).log("An error occurred", t);
    }

    @Override
    public void catching(Throwable t) {
        catching(Level.ERROR, t);
    }

    @Override
    public void debug(String message, Object... params) {
        logFormatted(Level.DEBUG, message, params);
    }

    @Override
    public void debug(String message, Throwable t) {
        logFormatted(Level.DEBUG, message, t);
    }

    @Override
    public void error(String message, Object... params) {
        logFormatted(Level.ERROR, message, params);
    }

    @Override
    public void error(String message, Throwable t) {
        logFormatted(Level.ERROR, message, t);
    }

    @Override
    public void fatal(String message, Object... params) {
        logFormatted(Level.FATAL, message, params);
    }

    @Override
    public void fatal(String message, Throwable t) {
        logFormatted(Level.FATAL, message, t);
    }

    @Override
    public void info(String message, Object... params) {
        logFormatted(Level.INFO, message, params);
    }

    @Override
    public void info(String message, Throwable t) {
        logFormatted(Level.INFO, message, t);
    }

    @Override
    public void log(Level level, String message, Object... params) {
        logFormatted(level, message, params);
    }

    @Override
    public void log(Level level, String message, Throwable t) {
        logFormatted(level, message, t);
    }

    @Override
    public <T extends Throwable> T throwing(T t) {
        catching(Level.ERROR, t);
        return t;
    }

    @Override
    public void trace(String message, Object... params) {
        logFormatted(Level.TRACE, message, params);
    }

    @Override
    public void trace(String message, Throwable t) {
        logFormatted(Level.TRACE, message, t);
    }

    @Override
    public void warn(String message, Object... params) {
        logFormatted(Level.WARN, message, params);
    }

    @Override
    public void warn(String message, Throwable t) {
        logFormatted(Level.WARN, message, t);
    }

    public HytaleLogger.Api asLevel(Level level) {

        java.util.logging.Level internalLevel = switch (level) {
            case INFO -> java.util.logging.Level.INFO;
            case WARN -> java.util.logging.Level.WARNING;
            case ERROR, FATAL -> java.util.logging.Level.SEVERE;
            case DEBUG, TRACE -> java.util.logging.Level.FINEST;
        };

        return logger.at(internalLevel);
    }
}
