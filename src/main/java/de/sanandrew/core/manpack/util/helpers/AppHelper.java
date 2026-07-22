/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.core.manpack.util.helpers;

import cpw.mods.fml.common.FMLCommonHandler;
import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.concurrent.RejectedExecutionException;
import org.apache.logging.log4j.Level;

final class AppHelper {
    AppHelper() {
    }

    static void restartApp() {
        try {
            String java = System.getProperty("java.home") + "/bin/javaw";
            ArrayList<String> vmArguments = new ArrayList<String>(ManagementFactory.getRuntimeMXBean().getInputArguments());
            Iterator it = vmArguments.iterator();
            while (it.hasNext()) {
                if (!((String)it.next()).contains("-agentlib")) continue;
                it.remove();
            }
            final ArrayList<String> cmd = new ArrayList<String>();
            cmd.add('\"' + java + '\"');
            String[] mainCommand = System.getProperty("sun.java.command").split(" ");
            if (mainCommand[0].endsWith(".jar")) {
                cmd.add("-jar");
                cmd.add(new File(mainCommand[0]).getPath());
            } else {
                cmd.add("-cp");
                cmd.add('\"' + System.getProperty("java.class.path") + '\"');
                cmd.add(mainCommand[0]);
            }
            cmd.addAll(Arrays.asList(mainCommand).subList(1, mainCommand.length));
            cmd.addAll(vmArguments);
            PrintStream os = System.out;
            Runtime.getRuntime().addShutdownHook(new Thread(){

                @Override
                public void run() {
                    try {
                        ProcessBuilder builder = new ProcessBuilder(cmd);
                        builder.inheritIO();
                        builder.start();
                    }
                    catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
            System.out.println();
            ManPackLoadingPlugin.MOD_LOG.log(Level.INFO, "---=== Restarting Minecraft! ===---");
            FMLCommonHandler.instance().exitJava(0, false);
        }
        catch (Throwable e) {
            throw new RejectedExecutionException("Error while trying to restart the application", e);
        }
    }

    static void shutdownApp() {
        System.out.println();
        ManPackLoadingPlugin.MOD_LOG.log(Level.INFO, "---=== Shutting down Minecraft! ===---");
        FMLCommonHandler.instance().exitJava(0, false);
    }
}

