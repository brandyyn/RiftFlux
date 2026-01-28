package com.voidsrift.riftflux.nei;

import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.FMLLog;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;

/**
 * GTNH NEI (NotEnoughItems fork) renders recipe-tab icons from HandlerInfo.
 * Those are normally loaded from NEI's CSV, but mods can register them via IMC.
 */
public final class GTNHNeiHandlerInfo {
    private static boolean registered;

    private GTNHNeiHandlerInfo() {}

    public static void register() {
        if (registered) {
            return;
        }
        registered = true;

        if (!Loader.isModLoaded("NotEnoughItems")) {
            return;
        }
        // Only add the icon if Clay Soldiers is present, otherwise NEI will log missing item.
        if (!Loader.isModLoaded("claysoldiers")) {
            return;
        }

        try {
            final NBTTagCompound tag = new NBTTagCompound();
            tag.setString("handler", ClaySoldiersRecipeHandler.class.getName());
            tag.setString("modName", "Clay Soldiers");
            tag.setString("modId", "claysoldiers");
            tag.setBoolean("modRequired", true);

            tag.setString("itemName", "claysoldiers:clayman_doll");
            tag.setString("nbtInfo", "{team:\"red\"}");

            tag.setInteger("handlerHeight", 65);
            tag.setInteger("handlerWidth", 166);
            tag.setBoolean("multipleWidgetsAllowed", true);
            tag.setBoolean("showFavoritesButton", true);
            tag.setBoolean("showOverlayButton", true);

            FMLInterModComms.sendMessage("NotEnoughItems", "registerHandlerInfo", tag);

            // Add NEI information pages for Clay Soldiers dolls
            final NBTTagCompound info = new NBTTagCompound();
            info.setString("filter", "claysoldiers:clayman_doll");
            final NBTTagList pages = new NBTTagList();
            pages.appendTag(new NBTTagString("Upgrades and Abilities\n" +
                    "\n" +

                    "Stick - 20 uses, increases melee damage by 2-3 points\n" +
                    "\n" +
                    "Arrow - 20 uses, increases melee damage by 4-5 points, drops feather, counts as stick upgraded with flint\n" +
                    "\n" +
                    "Blaze Rod - 20 uses, increases melee damage by 1-2 points, causes burn time on target\n" +
                    "\n" +
                    "Bone - 30 uses, increases melee damage by 3-4, increases melee range slightly, can not be equipped while riding mount, can not ride mount while equipped\n" +
                    "\n" +
                    "Shear Blade - 25 uses, increases melee damage by 1-2 points, by 3-4 damage on first hit while stealthed, can be held in either hand\n" +
                    "\n" +
                    "Glistering Melon - 25 uses, heals any ally below 25% hp by 15 points, soldier does not attack enemies while equipped with this\n" +
                    "\n" +

                    "Gravel - 15 uses, thrown for 2-3 points damage\n" +
                    "\n" +
                    "Snowball - 5-20 uses, thrown for 0 damage, slows target on hit (snowball gives 5, snow layer gives 10, snow block gives 20)\n" +
                    "\n" +
                    "Fire Charge - 15 uses, thrown for 1-2 damage, causes burn time on hit\n" +
                    "\n" +
                    "Bowl - 20 uses, reduces incoming physical damage by half (when used with leather, only receives 25% of original damage, no longer fully blocks ranged attacks)\n" +
                    "\n" +
                    "Emerald - 5-45 uses, deals 3-4 direct unavoidable damage at medium range, plus two with sugarcane boost, doubles if target is wet (emerald gives 5, emerald block gives 45)\n" +
                    "\n" +
                    "Nether Quartz - 4 uses, deals heavy knockback to all targets nearby if soldier takes too many hits\n" +
                    "\n" +

                    "Iron Ingot - makes soldier vastly resistant to knockback, increases knockback dealt to others, both effects neutralized vs another iron ingot user\n" +
                    "\n" +
                    "Brick - makes soldier extremely slow and less willing to move, will use ranged attacks if available before melee\n" +
                    "\n" +
                    "String - makes soldier resist all damage from one explosion\n" +
                    "\n" +
                    "Cactus - 75 percent chance to negate burn time, halves total burn time otherwise\n" +
                    "\n" +
                    "Nether Brick - gives burn time to any soldier that attacks nether brick user with unarmed attacks\n" +
                    "\n" +

                    "Wheat - makes soldier never attack, even if hit first\n" +
                    "\n" +
                    "Nether Wart - makes soldier attack its allies\n" +
                    "\n" +
                    "Fermented Spider Eye - makes soldier not attack, unless attacked first\n" +
                    "\n" +

                    "Leather - 20 uses, reduces incoming physical damage by half (when used with bowl, only receives 25% of original damage)\n" +
                    "\n" +
                    "Food - 4 uses, heals soldier when below 25 percent, healed XP is hungerPoints times 0.5\n" +
                    "\n" +
                    "Gold Nugget - gives soldier a crown, all of team follows the king\n" +
                    "\n" +
                    "Glowstone - makes soldier glow, block gives to infinite soldiers\n" +
                    "\n" +
                    "Gunpowder - 1 use, soldier explodes on death (incompatible with magma cream and firework star)\n" +
                    "\n" +
                    "Magma Cream - 1 use, places a 2 second time bomb on the soldier which deals the final attack if killed by melee hit (incompatible with gunpowder and firework star)\n" +
                    "\n" +
                    "Sugar - makes soldier move faster (incompatible with diamond upgrades)\n" +
                    "\n" +
                    "Clay - 4 uses, revives fallen soldiers of own color\n" +
                    "\n" +
                    "Ghast Tear - 2 uses, revives brick dolls to own color\n" +
                    "\n" +
                    "Redstone Dust - 4 uses, causes target to entirely stop attacking for 3 seconds, applied by all thrown and melee attacks, block gives to infinite soldiers\n" +
                    "\n" +
                    "Slimeball - 5 uses, causes target to entirely stop moving for 3 seconds, applied by all thrown and melee attacks\n" +
                    "\n" +
                    "Feather - soldier can not attack while falling, fall is slowed greatly and takes no damage on landing, no effect with iron ingot or while mounted\n" +
                    "\n" +
                    "Glass Bottle/Pane/Block - doubles vision range, soldier can see stealthed enemies, bottles and panes give 1, block gives infinite supply\n" +
                    "\n" +
                    "Paper/Book - gives a plain cape, dyed paper and books give colored capes, book gives infinite soldiers\n" +
                    "\n" +
                    "Diamond - gives super crown and cape, doubles limited use items, grants sugar speed boost, multiplies hp by 10, incompatible with diamond block\n" +
                    "\n" +
                    "Diamond Block - gives super crown and cape, multiplies limited use items by five, grants sugar speed boost, multiplies hp by 80, incompatible with diamond\n" +
                    "\n" +
                    "Ender Pearl - soldier zombifies, gains 5 hp, heals fully on last hit, slain targets become zombies, dies after 10 minutes of no fighting\n" +
                    "\n" +
                    "Blaze Powder - 1 use, instantly kills target with fire, turning it into brick doll\n" +
                    "\n" +
                    "Red Mushroom - 1 use, applies poison to target\n" +
                    "\n" +
                    "Brown Mushroom - 2 uses, heals soldier for 10 hp when below 25 percent\n" +
                    "\n" +
                    "Lily Pad - soldier has pants and floats on water, no effect with iron ingot\n" +
                    "\n" +
                    "Egg - soldier is ghostly and can not be detected by enemies without glass goggles, doubles shear damage, detected if others have glasses\n" +
                    "\n" +
                    "Wheat Seed - soldier can not be infected by zombification\n" +
                    "\n" +
                    "Wood Button - increases melee damage by 1 when no weapon is equipped, incompatible with stone button\n" +
                    "\n" +
                    "Stone Button - increases melee damage by 2 when no weapon is equipped, incompatible with wood button\n" +
                    "\n" +
                    "Mob Head - gives the soldier a mask to wear\n" +
                    "\n" +
                    "Firework Star - 1 use, soldier explodes on death in a firework effect, incompatible with gunpowder and magma cream\n"
            ));
            info.setTag("pages", pages);
            FMLInterModComms.sendMessage("NotEnoughItems", "addItemInfo", info);

        } catch (Throwable t) {
            FMLLog.severe("[RiftFlux] Failed to register GTNH NEI handler info for Clay Soldiers: %s", t);
        }
    }
}
