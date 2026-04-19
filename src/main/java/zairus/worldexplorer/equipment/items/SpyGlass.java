/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemStack
 *  net.minecraft.world.World
 */
package zairus.worldexplorer.equipment.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zairus.worldexplorer.core.WorldExplorer;
import zairus.worldexplorer.core.gui.IGuiOverlay;
import zairus.worldexplorer.core.items.WEItem;
import zairus.worldexplorer.equipment.gui.GuiScreenSpyGlass;

public class SpyGlass
extends WEItem {
    public SpyGlass() {
        this.setUnlocalizedName("spyglass");
        this.setTextureName("worldexplorer:spyglass");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.setFull3D();
        this.setMaxDamage(3000);
        this.maxStackSize = 1;
    }

    @Override
    public boolean updatesFOV() {
        return true;
    }

    @Override
    public float getFOVValue() {
        return 6.5f;
    }

    @Override
    public float getFOVSpeedFactor() {
        return 1500.0f;
    }

    @Override
    @SideOnly(value=Side.CLIENT)
    public IGuiOverlay getUseOverlay() {
        return new GuiScreenSpyGlass();
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote) {
            world.playSoundAtEntity((Entity)player, "worldexplorer:spyglass_look", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 1.5f);
        }
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        if (!world.isRemote) {
            world.playSoundAtEntity((Entity)player, "worldexplorer:spyglass_close", 1.0f, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 1.5f);
        }
    }

    public int getMaxItemUseDuration(ItemStack duration) {
        return 10000;
    }

    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        return stack;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.bow;
    }

    public int getItemEnchantability() {
        return 1;
    }
}

