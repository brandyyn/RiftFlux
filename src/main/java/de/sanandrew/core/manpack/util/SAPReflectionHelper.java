/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 */
package de.sanandrew.core.manpack.util;

import com.google.common.collect.Maps;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public final class SAPReflectionHelper {
    private static Map<String, Method> cachedMethods = Maps.newHashMap();
    private static Map<String, Field> cachedFields = Maps.newHashMap();

    public static boolean doesClassExist(String className) {
        try {
            Class.forName(className, false, null);
            return true;
        }
        catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static <T> Class<T> getClass(String className) {
        try {
            return (Class)SAPUtils.getCasted(Class.forName(className));
        }
        catch (ClassNotFoundException | UnsatisfiedLinkError ex) {
            return null;
        }
    }

    public static <T, E> void setCachedFieldValue(Class<? super E> classToAccess, E instance, String mcpName, String srgName, T value) {
        Field field = SAPReflectionHelper.getCachedField(classToAccess, mcpName, srgName);
        try {
            field.set(instance, value);
        }
        catch (Throwable ex) {
            throw new UnableToSetFieldException(ex);
        }
    }

    public static <T, E> T getCachedFieldValue(Class<? super E> classToAccess, E instance, String mcpName, String srgName) {
        Field field = SAPReflectionHelper.getCachedField(classToAccess, mcpName, srgName);
        try {
            return (T)field.get(instance);
        }
        catch (IllegalAccessException | IllegalArgumentException ex) {
            throw new UnableToGetFieldException(ex);
        }
    }

    public static <T, E> T invokeCachedMethod(Class<? super E> classToAccess, E instance, String mcpName, String srgName, Class<?>[] parameterTypes, Object[] parameterValues) {
        Method method = SAPReflectionHelper.getCachedMethod(classToAccess, mcpName, srgName, parameterTypes);
        try {
            return (T)method.invoke(instance, parameterValues);
        }
        catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw new UnableToInvokeMethodException(ex);
        }
    }

    public static Field getCachedField(Class<?> classToAccess, String mcpName, String srgName) {
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        if (cachedFields.containsKey(key)) {
            return cachedFields.get(key);
        }
        return SAPReflectionHelper.cacheAccessedField(classToAccess, mcpName, srgName);
    }

    private static Field cacheAccessedField(Class<?> classToAccess, String mcpName, String srgName) {
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        try {
            Field method = classToAccess.getDeclaredField(srgName);
            method.setAccessible(true);
            cachedFields.put(key, method);
            return method;
        }
        catch (Throwable ex1) {
            try {
                Field method = classToAccess.getDeclaredField(mcpName);
                method.setAccessible(true);
                cachedFields.put(key, method);
                return method;
            }
            catch (Throwable ex2) {
                throw new UnableToAccessFieldException(ex2);
            }
        }
    }

    public static Method getCachedMethod(Class<?> classToAccess, String mcpName, String srgName, Class<?> ... parameterTypes) {
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        if (cachedMethods.containsKey(key)) {
            return cachedMethods.get(key);
        }
        return SAPReflectionHelper.cacheAccessedMethod(classToAccess, mcpName, srgName, parameterTypes);
    }

    private static Method cacheAccessedMethod(Class<?> classToAccess, String mcpName, String srgName, Class<?> ... parameterTypes) {
        String key = classToAccess.getCanonicalName() + '_' + srgName;
        try {
            Method method = classToAccess.getDeclaredMethod(srgName, parameterTypes);
            method.setAccessible(true);
            cachedMethods.put(key, method);
            return method;
        }
        catch (Throwable ex1) {
            try {
                Method method = classToAccess.getDeclaredMethod(mcpName, parameterTypes);
                method.setAccessible(true);
                cachedMethods.put(key, method);
                return method;
            }
            catch (Throwable ex2) {
                throw new UnableToFindMethodException(ex2);
            }
        }
    }

    public static class UnableToAccessFieldException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToAccessFieldException(Throwable failed) {
            super(failed);
        }
    }

    public static class UnableToFindMethodException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToFindMethodException(Throwable failed) {
            super(failed);
        }
    }

    public static class UnableToInvokeMethodException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToInvokeMethodException(Throwable failed) {
            super(failed);
        }
    }

    public static class UnableToGetFieldException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToGetFieldException(Throwable failed) {
            super(failed);
        }
    }

    public static class UnableToSetFieldException
    extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public UnableToSetFieldException(Throwable ex) {
            super(ex);
        }
    }
}

