/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Loader
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  travellersgear.api.TravellersGearAPI
 */
package theoldone822.ArmorDamageRecalc.API;

import cpw.mods.fml.common.Loader;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import theoldone822.ArmorDamageRecalc.API.IExtendedArmor;

public class ExtendedHandler {
    public static int[] helmAmount;
    public static String[] helmList;
    public static int[] chestAmount;
    public static String[] chestList;
    public static int[] legsAmount;
    public static String[] legsList;
    public static int[] bootsAmount;
    public static String[] bootsList;
    public static int[] cloakAmount;
    public static String[] cloakList;
    public static int[] shoulderAmount;
    public static String[] shoulderList;
    public static int[] vambraceAmount;
    public static String[] vambraceList;
    public static boolean damagedArmor;
    public static float damagedArmorFactor;

    public static float getExtendedArmorValue(EntityLivingBase living) {
        float i = 0.0f;
        for (ItemStack itemstack : living.getLastActiveItems()) {
            if (itemstack == null || !(itemstack.getItem() instanceof ItemArmor)) continue;
            float l = ((ItemArmor)itemstack.getItem()).damageReduceAmount;
            int x = ((ItemArmor)itemstack.getItem()).armorType;
            switch (x) {
                case 0: {
                    float newArmorMax;
                    float newArmorMin;
                    float factordamagemax;
                    float factordamagemin;
                    int itmd;
                    int itd;
                    int n;
                    int m;
                    if (helmList.length <= 0) break;
                    for (m = 0; m < helmList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(helmList[m])) continue;
                        n = helmAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        itd = itemstack.getItemDamage();
                        itmd = itemstack.getItem().getMaxDamage();
                        factordamagemin = l * (1.0f / damagedArmorFactor);
                        factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        newArmorMin = l * factordamagemin;
                        newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
                case 1: {
                    float newArmorMax;
                    float newArmorMin;
                    float factordamagemax;
                    float factordamagemin;
                    int itmd;
                    int itd;
                    int n;
                    int m;
                    if (chestList.length <= 0) break;
                    for (m = 0; m < chestList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(chestList[m])) continue;
                        n = chestAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        itd = itemstack.getItemDamage();
                        itmd = itemstack.getItem().getMaxDamage();
                        factordamagemin = l * (1.0f / damagedArmorFactor);
                        factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        newArmorMin = l * factordamagemin;
                        newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
                case 2: {
                    float newArmorMax;
                    float newArmorMin;
                    float factordamagemax;
                    float factordamagemin;
                    int itmd;
                    int itd;
                    int n;
                    int m;
                    if (legsList.length <= 0) break;
                    for (m = 0; m < legsList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(legsList[m])) continue;
                        n = legsAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        itd = itemstack.getItemDamage();
                        itmd = itemstack.getItem().getMaxDamage();
                        factordamagemin = l * (1.0f / damagedArmorFactor);
                        factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        newArmorMin = l * factordamagemin;
                        newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
                case 3: {
                    float newArmorMax;
                    float newArmorMin;
                    float factordamagemax;
                    float factordamagemin;
                    int itmd;
                    int itd;
                    int n;
                    int m;
                    if (bootsList.length <= 0) break;
                    for (m = 0; m < bootsList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(bootsList[m])) continue;
                        n = bootsAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        itd = itemstack.getItemDamage();
                        itmd = itemstack.getItem().getMaxDamage();
                        factordamagemin = l * (1.0f / damagedArmorFactor);
                        factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        newArmorMin = l * factordamagemin;
                        newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
            }
            i += l;
        }
        if (Loader.isModLoaded((String)"TravellersGear") && living instanceof EntityPlayer) {
            ItemStack[] titemstack = getTravellersGearInventory((EntityPlayer)living);
            if (titemstack == null || titemstack.length == 0) {
                return i;
            }
            int j2 = titemstack.length - 1;
            for (int k = 0; k < j2; ++k) {
                ItemStack itemstack = titemstack[k];
                if (itemstack == null) continue;
                float l = 0.0f;
                if (itemstack.getItem() instanceof IExtendedArmor) {
                    l += (float)((IExtendedArmor)itemstack.getItem()).getDamageReduceAmount();
                }
                switch (k) {
                    case 0: {
                        float newArmorMax;
                        float newArmorMin;
                        float factordamagemax;
                        float factordamagemin;
                        int itmd;
                        int itd;
                        int n;
                        int m;
                        if (cloakList.length <= 0) break;
                        for (m = 0; m < cloakList.length; ++m) {
                            if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(cloakList[m])) continue;
                            n = cloakAmount[m];
                            l += (float)n;
                            if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                            itd = itemstack.getItemDamage();
                            itmd = itemstack.getItem().getMaxDamage();
                            factordamagemin = l * (1.0f / damagedArmorFactor);
                            factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                            newArmorMin = l * factordamagemin;
                            newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                            l = newArmorMin + newArmorMax;
                        }
                        break;
                    }
                    case 1: {
                        float newArmorMax;
                        float newArmorMin;
                        float factordamagemax;
                        float factordamagemin;
                        int itmd;
                        int itd;
                        int n;
                        int m;
                        if (shoulderList.length <= 0) break;
                        for (m = 0; m < shoulderList.length; ++m) {
                            if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(shoulderList[m])) continue;
                            n = shoulderAmount[m];
                            l += (float)n;
                            if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                            itd = itemstack.getItemDamage();
                            itmd = itemstack.getItem().getMaxDamage();
                            factordamagemin = l * (1.0f / damagedArmorFactor);
                            factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                            newArmorMin = l * factordamagemin;
                            newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                            l = newArmorMin + newArmorMax;
                        }
                        break;
                    }
                    case 2: {
                        float newArmorMax;
                        float newArmorMin;
                        float factordamagemax;
                        float factordamagemin;
                        int itmd;
                        int itd;
                        int n;
                        int m;
                        if (vambraceList.length <= 0) break;
                        for (m = 0; m < vambraceList.length; ++m) {
                            if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(vambraceList[m])) continue;
                            n = vambraceAmount[m];
                            l += (float)n;
                            if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                            itd = itemstack.getItemDamage();
                            itmd = itemstack.getItem().getMaxDamage();
                            factordamagemin = l * (1.0f / damagedArmorFactor);
                            factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                            newArmorMin = l * factordamagemin;
                            newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                            l = newArmorMin + newArmorMax;
                        }
                        break;
                    }
                }
                i += l;
            }
        }
        return i;
    }

    private static ItemStack[] getTravellersGearInventory(EntityPlayer player) {
        try {
            Class<?> apiClass = Class.forName("travellersgear.api.TravellersGearAPI");
            Object out = apiClass.getMethod("getExtendedInventory", EntityPlayer.class).invoke(null, player);
            if (out instanceof ItemStack[]) {
                return (ItemStack[])out;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    public static float getExtendedItemArmorValue(ItemStack itemstack) {
        float i = 0.0f;
        if (itemstack != null && itemstack.getItem() instanceof ItemArmor) {
            float l = ((ItemArmor)itemstack.getItem()).damageReduceAmount;
            int x = ((ItemArmor)itemstack.getItem()).armorType;
            switch (x) {
                case 0: {
                    if (helmList.length <= 0) break;
                    for (int m = 0; m < helmList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(helmList[m])) continue;
                        int n = helmAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        int itd = itemstack.getItemDamage();
                        int itmd = itemstack.getItem().getMaxDamage();
                        float factordamagemin = l * (1.0f / damagedArmorFactor);
                        float factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        float newArmorMin = l * factordamagemin;
                        float newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
                case 1: {
                    if (chestList.length <= 0) break;
                    for (int m = 0; m < chestList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(chestList[m])) continue;
                        int n = chestAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        int itd = itemstack.getItemDamage();
                        int itmd = itemstack.getItem().getMaxDamage();
                        float factordamagemin = l * (1.0f / damagedArmorFactor);
                        float factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        float newArmorMin = l * factordamagemin;
                        float newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
                case 2: {
                    if (legsList.length <= 0) break;
                    for (int m = 0; m < legsList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(legsList[m])) continue;
                        int n = legsAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        int itd = itemstack.getItemDamage();
                        int itmd = itemstack.getItem().getMaxDamage();
                        float factordamagemin = l * (1.0f / damagedArmorFactor);
                        float factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        float newArmorMin = l * factordamagemin;
                        float newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
                case 3: {
                    if (bootsList.length <= 0) break;
                    for (int m = 0; m < bootsList.length; ++m) {
                        if (itemstack.getItem() != (Item)Item.itemRegistry.getObject(bootsList[m])) continue;
                        int n = bootsAmount[m];
                        l += (float)n;
                        if (!damagedArmor || !(damagedArmorFactor > 0.0f) || itemstack.getItem().getMaxDamage() <= 0) continue;
                        int itd = itemstack.getItemDamage();
                        int itmd = itemstack.getItem().getMaxDamage();
                        float factordamagemin = l * (1.0f / damagedArmorFactor);
                        float factordamagemax = l * ((damagedArmorFactor - 1.0f) / damagedArmorFactor);
                        float newArmorMin = l * factordamagemin;
                        float newArmorMax = l * factordamagemax * (float)((itmd - itd) / itmd);
                        l = newArmorMin + newArmorMax;
                    }
                    break;
                }
            }
            i += l;
        }
        return i;
    }

    static {
        damagedArmor = false;
        damagedArmorFactor = 1.0f;
    }
}
