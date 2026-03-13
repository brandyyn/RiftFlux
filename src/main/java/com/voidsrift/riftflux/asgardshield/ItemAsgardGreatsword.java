package com.voidsrift.riftflux.asgardshield;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.world.World;

import java.util.Locale;

public class ItemAsgardGreatsword extends ItemSword {
    private final ToolMaterial material;
    private final String soundProfile;
    private final String swordType;
    private final ItemStack repairStack;
    private final float damageMultiplier;
    private final float knockback;
    private final String textureName;

    public ItemAsgardGreatsword(String textureName,
                                ToolMaterial material,
                                String soundProfile,
                                String swordType,
                                ItemStack repairStack) {
        super(material);
        this.textureName = textureName;
        this.material = material;
        this.soundProfile = soundProfile;
        this.swordType = swordType == null ? "" : swordType.toLowerCase(Locale.ROOT);
        this.repairStack = repairStack == null ? null : repairStack.copy();

        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxStackSize(1);
        this.setMaxDamage(computeDurability());
        this.damageMultiplier = computeDamageMultiplier();
        this.knockback = -0.1F;
    }

    private int computeDurability() {
        int base = Math.max(1, material.getMaxUses());
        int out = (base + base / 2) * Math.max(1, ModConfig.asgardShieldEquipmentDurabilityMultiplier);
        return Math.max(1, out);
    }

    private float computeDamageMultiplier() {
        if ("stone".equals(swordType) || "skull".equals(swordType)) {
            return 0.90F;
        }
        if ("diamond".equals(swordType) || "ender".equals(swordType)) {
            return 0.80F;
        }
        if ("iron".equals(swordType)
                || "nether".equals(swordType)
                || "livingmetal".equals(swordType)
                || "biomass".equals(swordType)) {
            return 0.85F;
        }
        return 0.95F;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public String getSoundProfile() {
        return soundProfile;
    }

    public String getSwordType() {
        return swordType;
    }

    @Override
    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null || AsgardShieldState.isGuardBroken(player)) {
            return stack;
        }
        player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        return stack;
    }

    @Override
    public boolean hitEntity(ItemStack stack, EntityLivingBase target, EntityLivingBase attacker) {
        if (stack != null) {
            stack.damageItem(1, attacker);
        }
        if (target != null) {
            if ("ender".equals(swordType)) {
                AsgardShieldLogic.spawnEnderFx(target.worldObj, target.posX, target.posY, target.posZ, target.getRNG());
            } else if ("skull".equals(swordType) && target.worldObj != null) {
                target.worldObj.spawnParticle("cloud", target.posX, target.posY + 1.0D, target.posZ, 0.0D, 0.0D, 0.0D);
            }
            float yaw = target.rotationYaw * (float) Math.PI / 180.0F;
            target.addVelocity(-Math.sin(yaw) * knockback * 0.5F, 0.1D, Math.cos(yaw) * knockback * 0.5F);
        }
        return true;
    }

    @Override
    public boolean onBlockDestroyed(ItemStack stack, World world, Block block, int x, int y, int z, EntityLivingBase entity) {
        if (block != null && block.getBlockHardness(world, x, y, z) > 0.0F) {
            stack.damageItem(2, entity);
        }
        return true;
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        if (repair != null && repairStack != null
                && repair.getItem() == repairStack.getItem()
                && (repairStack.getItemDamage() == 32767 || repair.getItemDamage() == repairStack.getItemDamage())) {
            return true;
        }
        if ("ender".equals(swordType) && repair != null && Item.getItemFromBlock(net.minecraft.init.Blocks.obsidian) == repair.getItem()) {
            return true;
        }
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    public int getItemEnchantability() {
        return material.getEnchantability();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon("riftflux:asgardshield/" + textureName);
    }
}
