package io.github.crucible.hycrucible.bootstrap.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.concurrent.ConcurrentHashMap;

//https://github.com/Build-9/Hyxin/blob/main/src/main/java/com/build_9/hyxin/mixin/GlobalProperties.java
public class GlobalPropertyService implements IGlobalPropertyService {

    private final ConcurrentHashMap<String, IPropertyKey> keys = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<IPropertyKey, Object> values = new ConcurrentHashMap<>();

    @Override
    public IPropertyKey resolveKey(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Property names must not be null or empty!");
        }
        return this.keys.computeIfAbsent(name, Key::new);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key) {
        return (T) this.values.get(key);
    }

    @Override
    public void setProperty(IPropertyKey key, Object value) {
        this.values.put(key, value);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        return (T) this.values.getOrDefault(key, defaultValue);
    }

    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        return this.getProperty(key, defaultValue);
    }

    public record Key(String name) implements IPropertyKey {
    }

}
