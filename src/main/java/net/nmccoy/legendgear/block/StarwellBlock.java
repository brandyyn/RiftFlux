/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.IMessage
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockContainer
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.IIcon
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.util.ForgeDirection
 */
package net.nmccoy.legendgear.block;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import com.voidsrift.riftflux.ModConfig;
import java.util.List;
import java.io.IOException;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import net.nmccoy.legendgear.LGUtil;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.block.TileEntityStarwell;
import net.nmccoy.legendgear.network.StarwellMessage;

public class StarwellBlock
extends BlockContainer {
    public IIcon sideIcon;
    public IIcon topActiveIcon;
    public IIcon topInertIcon;
    public static int STARWELL_RECOVER_TIME = 6000;

    public StarwellBlock() {
        super(Material.rock);
        this.setHarvestLevel("pickaxe", 3);
        this.setHardness(50.0f);
        this.setResistance(2000.0f);
        this.setLightLevel((float)Math.max(0, Math.min(15, ModConfig.legendGearStarInJarLightLevel)) / 15.0f);
        this.setStepSound(soundTypePiston);
        this.setBlockName("starwellCore");
        this.setCreativeTab(LegendGear2.legendgearTab);
    }

    public boolean renderAsNormalBlock() {
        return false;
    }

    public boolean isOpaqueCube() {
        return false;
    }

    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        if (world.getBlockMetadata(x, y, z) == 0) {
            return 0;
        }
        return Math.max(0, Math.min(15, ModConfig.legendGearStarInJarLightLevel));
    }

    public IIcon getIcon(int side, int meta) {
        if (side == 0 || side == 1) {
            if (meta == 0) {
                return this.topInertIcon;
            }
            return this.topActiveIcon;
        }
        return this.sideIcon;
    }

    @Override
    public void registerBlockIcons(IIconRegister reg) {
        this.sideIcon = reg.registerIcon(this.firstAvailableBlockTexture(
                "legendgear:starwell_frame",
                "legendgear:starwellFrame",
                "riftflux:starwell_frame",
                "riftflux:starwellFrame"
        ));
        this.topActiveIcon = reg.registerIcon(this.firstAvailableBlockTexture(
                "legendgear:starwell_core_active",
                "legendgear:starwellcore_active",
                "riftflux:starwell_core_active",
                "riftflux:starwellcore_active"
        ));
        this.topInertIcon = reg.registerIcon(this.firstAvailableBlockTexture(
                "legendgear:starwell_core",
                "legendgear:starwellcore",
                "riftflux:starwell_core",
                "riftflux:starwellcore"
        ));
    }

    private String firstAvailableBlockTexture(String ... keys) {
        for (String key : keys) {
            if (this.blockTextureExists(key)) {
                return key;
            }
        }
        return keys[0];
    }

    private boolean blockTextureExists(String key) {
        int split = key.indexOf(':');
        String domain = split >= 0 ? key.substring(0, split) : "minecraft";
        String path = split >= 0 ? key.substring(split + 1) : key;
        ResourceLocation texture = new ResourceLocation(domain, "textures/blocks/" + path + ".png");
        try {
            Minecraft.getMinecraft().getResourceManager().getResource(texture);
            return true;
        }
        catch (IOException ignored) {
            return false;
        }
    }

    public int damageDropped(int dam) {
        return 0;
    }

    public void getSubBlocks(Item item, CreativeTabs tabs, List list) {
        list.add(new ItemStack(Item.getItemFromBlock((Block)this), 1, 0));
        list.add(new ItemStack(Item.getItemFromBlock((Block)this), 1, 1));
    }

    public TileEntity createNewTileEntity(World world, int par) {
        if (par != 0) {
            return new TileEntityStarwell();
        }
        return null;
    }

    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float ox, float oy, float oz) {
        if (side == ForgeDirection.UP.ordinal() && world.getBlockMetadata(x, y, z) == 1) {
            ItemStack stack = player.getHeldItem();
            PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
            if (pse.starwellDrinkTime() >= (long)STARWELL_RECOVER_TIME) {
                pse.starwellCharge = 0;
            }
            if (stack == null || !(stack.getItem() instanceof ItemBlock)) {
                if (!world.isRemote) {
                    world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "legendgear:mysterySparkle", 1.0f, 1.0f);
                    pse.setMana(0.0f);
                    player.heal(6.0f);
                    player.getFoodStats().addStats(1, 1.0f);
                    int roll = LGUtil.rollDF(2, world.rand) - pse.starwellCharge;
                    if (roll >= 1) {
                        world.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "legendgear:starAppear", 1.0f, 1.0f);
                        int value = 0;
                        if (roll >= 2) {
                            value = 1;
                        }
                        EntityItem item = new EntityItem(world, (double)x + 0.5, (double)y + 1.5, (double)z + 0.5, new ItemStack((Item)LegendGear2.starDust, 1, value));
                        item.delayBeforeCanPickup = 20;
                        world.spawnEntityInWorld((Entity)item);
                    }
                    if (roll <= -3 || pse.starwellCharge >= 3) {
                        player.setFire(10);
                    }
                    if (roll <= -5 && pse.starwellCharge > 3) {
                        world.createExplosion(null, player.posX, player.posY + 0.25, player.posZ, 3.0f, false);
                    }
                    ++pse.starwellCharge;
                    if (pse.starwellCharge > 6) {
                        pse.starwellCharge = 6;
                    }
                    LegendGear2.snw.sendTo((IMessage)new StarwellMessage(pse.starwellCharge), (EntityPlayerMP)player);
                }
                pse.resetStarwellDrinkTime();
                return true;
            }
        }
        return false;
    }
}
