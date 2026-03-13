/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.BlockJukebox
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemRecord
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.StatCollector
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.block.BlockJukebox;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemRecord;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;

public class LGRecord
extends ItemRecord {
    private static final Map<String, LGRecord> records = new HashMap<String, LGRecord>();
    public final String recordName;

    public LGRecord(String name) {
        super(name);
        this.recordName = name;
        this.maxStackSize = 1;
        this.setCreativeTab(LegendGear2.legendgearTab);
        records.put(this.recordName, this);
        this.setUnlocalizedName("record." + this.recordName);
    }

    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon("legendgear:record_" + this.recordName);
    }

    public boolean onItemUse(ItemStack itemStack, EntityPlayer player, World world, int x, int y, int z, int par7, float par8, float par9, float par10) {
        if (world.getBlock(x, y, z) == Blocks.jukebox && world.getBlockMetadata(x, y, z) == 0) {
            if (world.isRemote) {
                return true;
            }
            ((BlockJukebox)Blocks.jukebox).func_149926_b(world, x, y, z, itemStack);
            world.playAuxSFXAtEntity((EntityPlayer)null, 1005, x, y, z, Item.getIdFromItem((Item)this));
            --itemStack.stackSize;
            return true;
        }
        return false;
    }

    public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
        par3List.add(this.getRecordNameLocal());
    }

    public String getRecordNameLocal() {
        return StatCollector.translateToLocal((String)(this.getUnlocalizedName() + ".desc"));
    }

    public EnumRarity getRarity(ItemStack itemStack) {
        return EnumRarity.rare;
    }

    public static LGRecord getRecord(String par0Str) {
        return records.get(par0Str);
    }

    public ResourceLocation getRecordResource(String name) {
        return new ResourceLocation("legendgear:" + name);
    }
}

