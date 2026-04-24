/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.block.Block
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemBlock
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.LGItem;

public class StarDust
extends LGItem {
    private static String[] names = new String[]{"starDust", "starPiece", "starStone", "starDustCharged", "starPieceCharged", "starStoneCharged"};
    IIcon icon_dust;
    IIcon icon_piece;
    IIcon icon_stone;

    public StarDust() {
        this.setUnlocalizedName("itemStarDust");
        this.tabs.add(CreativeTabs.tabMaterials);
        this.hasSubtypes = true;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
        if (stack.getItemDamage() == 1) {
            list.add(StarDust.getInfuseRequirementLine());
        }
    }

    public static int getInfuseLevelCost() {
        return Math.max(0, ModConfig.legendGearStarPieceInfuseLevels);
    }

    public static String getInfuseRequirementLine() {
        int cost = StarDust.getInfuseLevelCost();
        if (cost == 0) {
            return "Hold right-click to infuse for free";
        }
        return "Spend " + cost + " XP " + (cost == 1 ? "level" : "levels") + " to infuse";
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return par1ItemStack.getItemDamage() > 2;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        return 64;
    }

    public void addRecipes() {
        ItemStack threeDust = new ItemStack((Item)this, 3, 0);
        ItemStack onePiece = new ItemStack((Item)this, 1, 1);
        ItemStack ninePieces = new ItemStack((Item)this, 9, 1);
        ItemStack oneStone = new ItemStack((Item)this, 1, 2);
        ItemStack threeCDust = new ItemStack((Item)this, 3, 3);
        ItemStack oneCPiece = new ItemStack((Item)this, 1, 4);
        ItemStack nineCPieces = new ItemStack((Item)this, 9, 4);
        ItemStack oneCStone = new ItemStack((Item)this, 1, 5);
        GameRegistry.addShapelessRecipe((ItemStack)ninePieces, (Object[])new Object[]{oneStone});
        GameRegistry.addShapelessRecipe((ItemStack)threeDust, (Object[])new Object[]{onePiece});
        GameRegistry.addRecipe((ItemStack)oneStone, (Object[])new Object[]{"PPP", "PPP", "PPP", Character.valueOf('P'), onePiece});
        GameRegistry.addShapelessRecipe((ItemStack)nineCPieces, (Object[])new Object[]{oneCStone});
        GameRegistry.addShapelessRecipe((ItemStack)threeCDust, (Object[])new Object[]{oneCPiece});
        GameRegistry.addShapelessRecipe((ItemStack)new ItemStack((Item)this, 1, 0), (Object[])new Object[]{new ItemStack((Item)this, 1, 3)});
        GameRegistry.addRecipe((ItemStack)oneCStone, (Object[])new Object[]{"PPP", "PPP", "PPP", Character.valueOf('P'), oneCPiece});
        GameRegistry.addRecipe((ItemStack)onePiece, (Object[])new Object[]{"DD", "DD", Character.valueOf('D'), new ItemStack((Item)this, 1, 0)});
        GameRegistry.addRecipe((ItemStack)oneCPiece, (Object[])new Object[]{"DD", "DD", Character.valueOf('D'), new ItemStack((Item)this, 1, 3)});
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        this.icon_dust = par1IconRegister.registerIcon("legendgear:starDustAnim");
        this.icon_piece = par1IconRegister.registerIcon("legendgear:starPieceAnim");
        this.icon_stone = par1IconRegister.registerIcon("legendgear:starStoneAnim");
    }

    public String getUnlocalizedName(ItemStack par1ItemStack) {
        int damage = par1ItemStack.getItemDamage();
        if (damage >= names.length) {
            damage = 0;
        }
        return "item." + names[damage];
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (!player.worldObj.isRemote) {
            if (count == 56) {
                player.worldObj.playSoundAtEntity((Entity)player, "legendgear:partialcharge", 0.2f, 1.0f);
            }
        } else if (count <= 56) {
            Random rand = player.worldObj.rand;
            Vec3 forward = player.getLookVec();
            Vec3 right = forward.crossProduct(Vec3.createVectorHelper((double)0.0, (double)1.0, (double)0.0)).normalize();
            float fs = 0.3f;
            float rs = 0.5f;
            float rands = 0.1f;
            double x = player.posX + (double)fs * forward.xCoord + (double)rs * right.xCoord + (double)rands * rand.nextGaussian();
            double y = player.posY + (double)fs * forward.yCoord + (double)rs * right.yCoord - 0.2;
            double z = player.posZ + (double)fs * forward.zCoord + (double)rs * right.zCoord + (double)rands * rand.nextGaussian();
            LegendGear2.proxy.addSparkleParticle(player.worldObj, x, y, z, 0.0, 0.05, 0.0, 1.0f);
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getItemDamage() == 1 && player.experienceLevel >= StarDust.getInfuseLevelCost()) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        if (stack.getItemDamage() == 3) {
            if (LegendGear2.CONFIG_SPRINKLE_STARDUST_REQUIRE_SNEAK && !player.isSneaking()) {
                return stack;
            }
            if (!world.isRemote) {
                Vec3 aim = player.getLookVec();
                Vec3 eye = Vec3.createVectorHelper((double)player.posX, (double)(player.posY + (double)player.getEyeHeight()), (double)player.posZ);
                float range = 3.0f;
                Vec3 target = eye.addVector(aim.xCoord * (double)range, aim.yCoord * (double)range, aim.zCoord * (double)range);
                world.spawnEntityInWorld((Entity)new EntitySpellEffect(world, EntitySpellEffect.SpellType.SprinkleStardust, player, target, 2.0, 1.0, false));
            }
            player.swingItem();
            --stack.stackSize;
        }
        return stack;
    }

    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() >= 3 ? EnumRarity.epic : EnumRarity.uncommon;
    }

    public boolean onItemUse(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, World par3World, int par4, int par5, int par6, int par7, float par8, float par9, float par10) {
        if (par2EntityPlayer.capabilities.isCreativeMode && !par3World.isRemote && par1ItemStack.getItemDamage() == 0) {
            par3World.spawnEntityInWorld((Entity)new EntityFallingStar(par2EntityPlayer));
            return true;
        }
        int meta = par1ItemStack.getItemDamage();
        if (meta == 1 && ModConfig.legendGearPlaceableStarPieces) {
            if (par2EntityPlayer.capabilities.allowEdit) {
                Block placeBlock = LegendGear2.starPieceBlock;
                ItemBlock ib = (ItemBlock)ItemBlock.getItemFromBlock((Block)placeBlock);
                if (ib != null) {
                    boolean canPlace = this.canPlaceKludge(par3World, par4, par5, par6, par7, par2EntityPlayer, par1ItemStack, placeBlock);
                    if (canPlace) {
                        return ib.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
                    }
                }
            }
        } else if (meta == 4 && ModConfig.legendGearPlaceableStarPieces) {
            if (par2EntityPlayer.capabilities.allowEdit) {
                Block placeBlock = LegendGear2.infusedStarPieceBlock;
                ItemBlock ib = (ItemBlock)ItemBlock.getItemFromBlock((Block)placeBlock);
                if (ib != null) {
                    boolean canPlace = this.canPlaceKludge(par3World, par4, par5, par6, par7, par2EntityPlayer, par1ItemStack, placeBlock);
                    if (canPlace) {
                        return ib.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
                    }
                }
            }
        } else if (meta == 2) {
            if (par2EntityPlayer.capabilities.allowEdit) {
                ItemBlock ib = (ItemBlock)ItemBlock.getItemFromBlock((Block)LegendGear2.starstoneBlock);
                boolean canPlace = this.canPlaceKludge(par3World, par4, par5, par6, par7, par2EntityPlayer, par1ItemStack, (Block)LegendGear2.starstoneBlock);
                if (canPlace) {
                    return ib.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
                }
            }
        } else if (meta == 5) {
            ItemBlock ib = (ItemBlock)ItemBlock.getItemFromBlock((Block)LegendGear2.infusedStarstoneBlock);
            boolean canPlace = this.canPlaceKludge(par3World, par4, par5, par6, par7, par2EntityPlayer, par1ItemStack, (Block)LegendGear2.infusedStarstoneBlock);
            if (canPlace) {
                return ib.onItemUse(par1ItemStack, par2EntityPlayer, par3World, par4, par5, par6, par7, par8, par9, par10);
            }
        }
        return false;
    }

    public EnumAction getItemUseAction(ItemStack par1ItemStack) {
        return EnumAction.none;
    }

    @Override
    public ItemStack onEaten(ItemStack stack, World world, EntityPlayer player) {
        int infuseLevel = StarDust.getInfuseLevelCost();
        if (stack.getItemDamage() == 1 && player.experienceLevel >= infuseLevel) {
            --stack.stackSize;
            if (infuseLevel > 0) {
                player.addExperienceLevel(-infuseLevel);
            }
            if (!world.isRemote) {
                world.playSoundAtEntity((Entity)player, "legendgear:truncatedfullcharge", 0.2f, 1.0f);
            }
            if (stack.stackSize <= 0) {
                return new ItemStack((Item)this, 1, 4);
            }
            if (!player.inventory.addItemStackToInventory(new ItemStack((Item)this, 1, 4))) {
                player.dropPlayerItemWithRandomChoice(new ItemStack((Item)this, 1, 4), false);
            }
            if (player.worldObj.isRemote) {
                Random rand = player.worldObj.rand;
                Vec3 forward = player.getLookVec();
                Vec3 right = forward.crossProduct(Vec3.createVectorHelper((double)0.0, (double)1.0, (double)0.0)).normalize();
                float fs = 0.3f;
                float rs = 0.5f;
                float rands = 0.1f;
                double x = player.posX + (double)fs * forward.xCoord + (double)rs * right.xCoord;
                double y = player.posY + (double)fs * forward.yCoord + (double)rs * right.yCoord - 0.3;
                double z = player.posZ + (double)fs * forward.zCoord + (double)rs * right.zCoord;
                for (int i = 0; i < 20; ++i) {
                    LegendGear2.proxy.addSparkleParticle(player.worldObj, x, y, z, (double)rands * rand.nextGaussian(), (double)rands * rand.nextGaussian(), (double)rands * rand.nextGaussian(), 1.0f);
                }
            }
        }
        return stack;
    }

    public ItemStack onItemUseFinish(ItemStack stack, World world, EntityPlayer player) {
        return this.onEaten(stack, world, player);
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (int i = 0; i < names.length; ++i) {
            list.add(new ItemStack(item, 1, i));
        }
    }

    public IIcon getIconFromDamage(int par1) {
        if (par1 == 1 || par1 == 4) {
            return this.icon_piece;
        }
        if (par1 == 2 || par1 == 5) {
            return this.icon_stone;
        }
        return this.icon_dust;
    }

    public boolean canPlaceKludge(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack) {
        return this.canPlaceKludge(world, x, y, z, side, player, stack, (Block)LegendGear2.starstoneBlock);
    }

    public boolean canPlaceKludge(World world, int x, int y, int z, int side, EntityPlayer player, ItemStack stack, Block placeBlock) {
        Block block = world.getBlock(x, y, z);
        if (block == Blocks.snow_layer) {
            side = 1;
        } else if (block != Blocks.vine && block != Blocks.tallgrass && block != Blocks.deadbush && !block.isReplaceable((IBlockAccess)world, x, y, z)) {
            if (side == 0) {
                --y;
            }
            if (side == 1) {
                ++y;
            }
            if (side == 2) {
                --z;
            }
            if (side == 3) {
                ++z;
            }
            if (side == 4) {
                --x;
            }
            if (side == 5) {
                ++x;
            }
        }
        return world.canPlaceEntityOnSide(placeBlock, x, y, z, false, side, (Entity)null, stack);
    }
}
