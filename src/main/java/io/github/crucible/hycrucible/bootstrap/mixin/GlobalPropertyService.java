package io.github.crucible.hycrucible.bootstrap.mixin;

import org.spongepowered.asm.service.IGlobalPropertyService;
import org.spongepowered.asm.service.IPropertyKey;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe global property service for a custom launcher.
 * Acts as a runtime blackboard shared across all Mixin components.
 */
public final class GlobalPropertyService implements IGlobalPropertyService {

    /**
     * Global blackboard storage
     */
    private final ConcurrentHashMap<IPropertyKey, Object> properties =
            new ConcurrentHashMap<>();

    /**
     * Key registry to guarantee canonical key instances per name
     */
    private final ConcurrentHashMap<String, IPropertyKey> keys =
            new ConcurrentHashMap<>();

    /**
     * Immutable key implementation
     */
    private static final class Key implements IPropertyKey {

        private final String name;

        private Key(String name) {
            this.name = Objects.requireNonNull(name, "Key name cannot be null");
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (!(obj instanceof Key)) return false;
            return this.name.equals(((Key) obj).name);
        }

        @Override
        public String toString() {
            return "GlobalPropertyKey[" + this.name + "]";
        }
    }

    /**
     * Atomically resolves or creates a global property key
     */
    @Override
    public IPropertyKey resolveKey(String name) {
        return this.keys.computeIfAbsent(name, Key::new);
    }

    /**
     * Returns the value associated with the key or null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key) {
        return (T) this.properties.get(key);
    }

    /**
     * Atomically sets or removes a property
     */
    @Override
    public void setProperty(IPropertyKey key, Object value) {
        if (value == null) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value);
        }
    }

    /**
     * Returns the value or defaultValue if absent or null
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProperty(IPropertyKey key, T defaultValue) {
        Object value = this.properties.get(key);
        return value != null ? (T) value : defaultValue;
    }

    /**
     * Returns the value as String or defaultValue if absent
     */
    @Override
    public String getPropertyString(IPropertyKey key, String defaultValue) {
        Object value = this.properties.get(key);
        return value != null ? value.toString() : defaultValue;
    }

}
