package com.voidsrift.riftflux.compat.waila;

import java.util.List;

import mcp.mobius.waila.api.IWailaConfigHandler;
import mcp.mobius.waila.api.IWailaDataAccessor;
import mcp.mobius.waila.api.IWailaDataProvider;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.nmccoy.legendgear.legacy.blocks.TileEntityJar;

public class ClayJarWailaProvider implements IWailaDataProvider {
    @Override
    public ItemStack getWailaStack(IWailaDataAccessor accessor, IWailaConfigHandler config) {
        return null;
    }

    @Override
    public List<String> getWailaHead(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public List<String> getWailaBody(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        NBTTagCompound tag = accessor == null ? null : accessor.getNBTData();
        if (tag != null && tag.hasKey(TileEntityJar.TAG_JAR_DATA, 10)) {
            TileEntityJar preview = new TileEntityJar();
            preview.readJarData(tag.getCompoundTag(TileEntityJar.TAG_JAR_DATA));
            currenttip.add(this.formatContentsLine(preview.contents, preview.getStoredItemCount()));
            return currenttip;
        }

        TileEntity tile = accessor == null ? null : accessor.getTileEntity();
        if (tile instanceof TileEntityJar) {
            TileEntityJar jar = (TileEntityJar) tile;
            currenttip.add(this.formatContentsLine(jar.contents, jar.getStoredItemCount()));
        }
        return currenttip;
    }

    @Override
    public List<String> getWailaTail(ItemStack itemStack, List<String> currenttip, IWailaDataAccessor accessor,
            IWailaConfigHandler config) {
        return currenttip;
    }

    @Override
    public NBTTagCompound getNBTData(EntityPlayerMP player, TileEntity te, NBTTagCompound tag, World world, int x, int y,
            int z) {
        if (!(te instanceof TileEntityJar)) {
            return tag;
        }

        NBTTagCompound result = tag == null ? new NBTTagCompound() : tag;
        result.setTag(TileEntityJar.TAG_JAR_DATA, ((TileEntityJar) te).writeJarData(new NBTTagCompound()));
        return result;
    }

    private String formatContentsLine(ItemStack stack, int storedCount) {
        if (stack == null || stack.getItem() == null || storedCount <= 0) {
            return "Contains: Empty";
        }
        return "Contains: " + storedCount + "x " + stack.getDisplayName();
    }
}
