/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 */
package de.sanandrew.mods.claysoldiers.util;

import cpw.mods.fml.common.registry.GameRegistry;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.block.BlockClayNexus;
import de.sanandrew.mods.claysoldiers.tileentity.TileEntityClayNexus;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import net.minecraft.block.Block;

public final class RegistryBlocks {
    public static Block clayNexus;

    public static void initialize() {
        clayNexus = new BlockClayNexus();
        clayNexus.setCreativeTab(ClaySoldiersMod.clayTab);
        clayNexus.setBlockName("claysoldiers:nexus");
        GameRegistry.registerTileEntity(TileEntityClayNexus.class, (String)"claysoldiers:nexus_te");
        SAPUtils.registerBlocks(clayNexus);
    }
}

