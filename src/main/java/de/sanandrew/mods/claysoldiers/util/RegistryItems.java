/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util;

import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.item.ItemBrickManDoll;
import de.sanandrew.mods.claysoldiers.item.ItemBunnyDoll;
import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.item.ItemClayMonitor;
import de.sanandrew.mods.claysoldiers.item.ItemDisruptor;
import de.sanandrew.mods.claysoldiers.item.ItemGeckoDoll;
import de.sanandrew.mods.claysoldiers.item.ItemHorseDoll;
import de.sanandrew.mods.claysoldiers.item.ItemShearBlade;
import de.sanandrew.mods.claysoldiers.item.ItemTurtleDoll;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import net.minecraft.item.Item;

public final class RegistryItems {
    public static Item dollSoldier;
    public static Item disruptor;
    public static Item disruptorHardened;
    public static Item shearBlade;
    public static Item statDisplay;
    public static Item dollBrick;
    public static ItemHorseDoll dollHorseMount;
    public static ItemTurtleDoll dollTurtleMount;
    public static ItemBunnyDoll dollBunnyMount;
    public static ItemGeckoDoll dollGeckoMount;

    public static void initialize() {
        dollSoldier = new ItemClayManDoll();
        dollBrick = new ItemBrickManDoll();
        disruptor = new ItemDisruptor(false);
        disruptorHardened = new ItemDisruptor(true);
        statDisplay = new ItemClayMonitor();
        shearBlade = new ItemShearBlade();
        dollHorseMount = new ItemHorseDoll();
        dollTurtleMount = new ItemTurtleDoll();
        dollBunnyMount = new ItemBunnyDoll();
        dollGeckoMount = new ItemGeckoDoll();
        dollSoldier.setCreativeTab(ClaySoldiersMod.clayTab);
        dollSoldier.setUnlocalizedName("claysoldiers:clayman_doll");
        dollBrick.setCreativeTab(ClaySoldiersMod.clayTab);
        dollBrick.setUnlocalizedName("claysoldiers:clayman_brick_doll");
        disruptor.setCreativeTab(ClaySoldiersMod.clayTab);
        disruptor.setUnlocalizedName("claysoldiers:disruptor");
        disruptorHardened.setCreativeTab(ClaySoldiersMod.clayTab);
        disruptorHardened.setUnlocalizedName("claysoldiers:disruptor_cooked");
        statDisplay.setCreativeTab(ClaySoldiersMod.clayTab);
        statDisplay.setUnlocalizedName("claysoldiers:stat_display");
        shearBlade.setCreativeTab(ClaySoldiersMod.clayTab);
        shearBlade.setUnlocalizedName("claysoldiers:shear_blade");
        dollHorseMount.setCreativeTab(ClaySoldiersMod.clayTab);
        dollHorseMount.setUnlocalizedName("claysoldiers:horsemount_doll");
        dollTurtleMount.setCreativeTab(ClaySoldiersMod.clayTab);
        dollTurtleMount.setUnlocalizedName("claysoldiers:turtlemount_doll");
        dollBunnyMount.setCreativeTab(ClaySoldiersMod.clayTab);
        dollBunnyMount.setUnlocalizedName("claysoldiers:bunnymount_doll");
        dollGeckoMount.setCreativeTab(ClaySoldiersMod.clayTab);
        dollGeckoMount.setUnlocalizedName("claysoldiers:geckomount_doll");
        SAPUtils.registerItems(dollSoldier, dollBrick, disruptor, disruptorHardened, statDisplay, shearBlade, dollHorseMount, dollTurtleMount, dollBunnyMount, dollGeckoMount);
    }
}

