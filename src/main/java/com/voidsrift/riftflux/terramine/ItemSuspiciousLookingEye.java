package com.voidsrift.riftflux.terramine;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class ItemSuspiciousLookingEye extends Item {
    public ItemSuspiciousLookingEye() {
        this.setMaxStackSize(1);
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.setTextureName("riftflux:suspicious_looking_eye");
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (world.isRemote || player == null) {
            return stack;
        }

        if (world.isDaytime()) {
            player.addChatComponentMessage(new ChatComponentText("The eye can only be summoned at night."));
            return stack;
        }

        Vec3 look = player.getLookVec();
        if (look == null) {
            look = Vec3.createVectorHelper(0.0D, 0.0D, 1.0D);
        }

        double spawnX = player.posX + look.xCoord * 4.0D;
        double spawnZ = player.posZ + look.zCoord * 4.0D;
        double spawnY = player.posY + 70.0D;
        int cap = Math.max(8, world.getActualHeight() - 8);
        if (spawnY > cap) {
            spawnY = cap;
        }

        EntityEyeOfCthulhu eye = new EntityEyeOfCthulhu(world);
        eye.setLocationAndAngles(spawnX, spawnY, spawnZ, world.rand.nextFloat() * 360.0F, 0.0F);
        eye.setInitialTarget(player);
        world.spawnEntityInWorld(eye);

        world.playSoundAtEntity(player, "mob.enderdragon.growl", 1.0F, 1.0F);
        player.addChatComponentMessage(new ChatComponentText("The Eye of Cthulhu has awoken!"));

        if (!player.capabilities.isCreativeMode) {
            stack.stackSize--;
        }

        return stack;
    }
}
