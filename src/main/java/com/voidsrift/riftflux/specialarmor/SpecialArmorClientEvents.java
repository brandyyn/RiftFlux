package com.voidsrift.riftflux.specialarmor;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

public class SpecialArmorClientEvents {
    private static boolean jumpKeyDown;
    private int canJump = 0;

    @SubscribeEvent
    public void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityClientPlayerMP player = minecraft.thePlayer;
        if (player == null) {
            return;
        }

        if (player.inventory.armorItemInSlot(0) != null) {
            this.handleBoots(player.inventory.armorItemInSlot(0).getItem());
        } else {
            Blocks.ice.slipperiness = 0.98F;
        }

        if (player.inventory.armorItemInSlot(3) != null) {
            this.handleHelmet(player.inventory.armorItemInSlot(3).getItem());
        }

        jumpKeyDown = Keyboard.isKeyDown(minecraft.gameSettings.keyBindJump.getKeyCode());
    }

    private void handleBoots(Item item) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityClientPlayerMP player = minecraft.thePlayer;
        WorldClient world = minecraft.theWorld;
        if (player == null || world == null) {
            return;
        }

        if (item == SpecialArmorContent.heavyBoots) {
            if (player.onGround) {
                if (!player.isInWater()) {
                    player.motionX /= 1.95D;
                    player.motionZ /= 1.95D;
                } else {
                    player.motionX /= 1.55D;
                    player.motionZ /= 1.55D;
                }
            }
            if (!player.isInWater()) {
                player.motionY = player.motionY > 0.0D ? player.motionY / 1.1D : player.motionY * 1.1D;
            } else {
                player.motionY = player.motionY > 0.0D ? player.motionY / 50.0D : player.motionY * 1.2D;
                if (!player.onGround) {
                    player.motionX /= 1.2D;
                    player.motionZ /= 1.2D;
                }
            }
        } else if (item == SpecialArmorContent.skates) {
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.posY - 2.0D);
            int z = MathHelper.floor_double(player.posZ);
            if (player.onGround) {
                if (world.getBlock(x, y, z) == Blocks.ice) {
                    Blocks.ice.slipperiness = 0.6F;
                    player.motionX *= 1.1D;
                    player.motionZ *= 1.1D;
                } else {
                    player.motionX *= 0.7D;
                    player.motionZ *= 0.7D;
                }
            }
        } else {
            Blocks.ice.slipperiness = 0.98F;
        }

        if (item == SpecialArmorContent.doubleJumpBoots) {
            if (!player.onGround) {
                if (Keyboard.isKeyDown(minecraft.gameSettings.keyBindJump.getKeyCode()) && !jumpKeyDown && this.canJump == 1) {
                    player.motionY = 0.42F;
                    this.canJump = 2;
                }
                if (Keyboard.isKeyDown(minecraft.gameSettings.keyBindJump.getKeyCode()) && this.canJump == 0) {
                    this.canJump = 1;
                }
            } else {
                this.canJump = 0;
                if (Keyboard.isKeyDown(minecraft.gameSettings.keyBindJump.getKeyCode())) {
                    this.canJump = 1;
                }
            }
        }
    }

    private void handleHelmet(Item item) {
        Minecraft minecraft = Minecraft.getMinecraft();
        EntityClientPlayerMP player = minecraft.thePlayer;
        WorldClient world = minecraft.theWorld;
        if (player == null || world == null) {
            return;
        }

        if (item == SpecialArmorContent.slimeHelmet) {
            int x = MathHelper.floor_double(player.posX);
            int y = MathHelper.floor_double(player.posY + 1.0D);
            int z = MathHelper.floor_double(player.posZ);
            Block block = world.getBlock(x, y, z);
            if (block.getMaterial().isSolid()) {
                player.motionY = Keyboard.isKeyDown(minecraft.gameSettings.keyBindJump.getKeyCode()) ? 0.1D : player.motionY * 0.6D;
            }
        }
    }
}
