/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.NBTTagCompound
 */
package zelda.proxy;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.nbt.NBTTagCompound;

public class CommonProxy {
    private static final Map<String, NBTTagCompound> extendedPlayerData = new HashMap<String, NBTTagCompound>();

    public void registerClientStuff() {
    }

    public static void storeEntityData(String uuid, NBTTagCompound compound) {
        extendedPlayerData.put(uuid, compound);
    }

    public static NBTTagCompound getEntityData(String uuid) {
        return extendedPlayerData.remove(uuid);
    }

    public static void clearEntityData(String uuid) {
        if (uuid != null) {
            extendedPlayerData.remove(uuid);
        }
    }
}
