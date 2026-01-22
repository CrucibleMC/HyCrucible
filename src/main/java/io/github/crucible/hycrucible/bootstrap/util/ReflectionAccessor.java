package io.github.crucible.hycrucible.bootstrap.util;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public final class ReflectionAccessor {

    private ReflectionAccessor() {
    }

    private static final ConcurrentHashMap<Key, Accessor<?>> METHOD_CACHE = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<ClassKey, Class<?>> CLASS_CACHE = new ConcurrentHashMap<>();


    public static Class<?> loadClass(ClassLoader classLoader, String className) {
        ClassKey key = new ClassKey(classLoader, className);

        return CLASS_CACHE.computeIfAbsent(key, k -> {
            try {
                return classLoader.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Failed to load class " + className, e);
            }
        });
    }

    public static <T> Accessor<T> loadAndGetMethod(ClassLoader classLoader, String ownerClassName, String methodName, Class<?>... parameterTypes) {
        loadClass(classLoader, ownerClassName);
        return method(classLoader, ownerClassName, methodName, parameterTypes);
    }

    @SuppressWarnings("unchecked")
    public static <T> Accessor<T> method(ClassLoader classLoader, String ownerClassName, String methodName, Class<?>... parameterTypes) {
        Key key = new Key(ownerClassName, methodName, parameterTypes);

        return (Accessor<T>) METHOD_CACHE.computeIfAbsent(key, k -> resolve(classLoader, ownerClassName, methodName, parameterTypes));
    }

    private static <T> Accessor<T> resolve(ClassLoader classLoader, String ownerClassName, String methodName, Class<?>[] parameterTypes) {
        try {
            Class<?> owner = Class.forName(ownerClassName, false, classLoader);

            Method method = owner.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);

            MethodHandle handle = MethodHandles.lookup().unreflect(method);

            return new Accessor<>(method, handle, ownerClassName);

        } catch (Throwable t) {
            throw new IllegalStateException("Failed to resolve method " + ownerClassName + "#" + methodName, t);
        }
    }

    public record Accessor<T>(Method method, MethodHandle handle, String owner) {

        @SuppressWarnings("unchecked")
        public T $$(Object instance, Object... args) {
            try {
                if (instance == null) {
                    return (T) handle.invokeWithArguments(args);
                }

                Object[] all = new Object[args.length + 1];
                all[0] = instance;
                System.arraycopy(args, 0, all, 1, args.length);

                return (T) handle.invokeWithArguments(all);

            } catch (Throwable t) {
                throw new IllegalStateException("Failed to invoke method " + owner + "#" + method.getName(), t);
            }
        }

        public T $() {
            return $$(null);
        }

        public T $(Object...args) {
            return $$(null, args);
        }

    }

    private record Key(String owner, String name, Class<?>[] params) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Key(String owner1, String name1, Class<?>[] params1))) return false;
            return owner.equals(owner1) && name.equals(name1) && Arrays.equals(params, params1);
        }

        @Override
        public int hashCode() {
            int result = owner.hashCode();
            result = 31 * result + name.hashCode();
            result = 31 * result + Arrays.hashCode(params);
            return result;
        }
    }

    private record ClassKey(ClassLoader loader, String name) {
    }
}
