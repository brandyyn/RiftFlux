/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Loader
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.core.manpack.util.modcompatibility;

import cpw.mods.fml.common.Loader;
import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.util.modcompatibility.IModInitHelper;
import java.lang.reflect.InvocationTargetException;
import org.apache.logging.log4j.Level;

public class ModInitHelperInst {
    private final IModInitHelper helperInst;

    private ModInitHelperInst(IModInitHelper instance) {
        this.helperInst = instance;
    }

    public static ModInitHelperInst loadWhenModAvailable(String modId, String helperClass) {
        if (modId == null || modId.isEmpty()) {
            ManPackLoadingPlugin.MOD_LOG.printf(Level.FATAL, "Cannot check for null/empty mod ID!", new Object[0]);
            throw new RuntimeException();
        }
        if (Loader.isModLoaded((String)modId)) {
            try {
                Class<?> helperClassInst = Class.forName(helperClass);
                if (IModInitHelper.class.isAssignableFrom(helperClassInst)) {
                    IModInitHelper inst = (IModInitHelper)helperClassInst.getConstructor(new Class[0]).newInstance(new Object[0]);
                    ManPackLoadingPlugin.MOD_LOG.printf(Level.INFO, "Mod %s is available. Initialized compatibillity class %s.", new Object[]{modId, helperClass});
                    return new ModInitHelperInst(inst);
                }
                ManPackLoadingPlugin.MOD_LOG.printf(Level.ERROR, "Class %s is not a subclass of IModInitHelper! This is a serious modder error!", new Object[]{helperClass});
                throw new RuntimeException();
            }
            catch (ClassNotFoundException | IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
                ManPackLoadingPlugin.MOD_LOG.printf(Level.ERROR, "Unexpected exception while trying to build instance of compatibility class!", new Object[0]);
                return new ModInitHelperInst(new EmptyModInitHelper());
            }
        }
        ManPackLoadingPlugin.MOD_LOG.printf(Level.INFO, "Mod %s is unavailable. Skipping initialization of compatibility class %s!", new Object[]{modId, helperClass});
        return new ModInitHelperInst(new EmptyModInitHelper());
    }

    public void preInitialize() {
        this.helperInst.preInitialize();
    }

    public void initialize() {
        this.helperInst.initialize();
    }

    public void postInitialize() {
        this.helperInst.postInitialize();
    }

    public static final class EmptyModInitHelper
    implements IModInitHelper {
        @Override
        public void preInitialize() {
        }

        @Override
        public void initialize() {
        }

        @Override
        public void postInitialize() {
        }
    }
}

