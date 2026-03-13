package com.voidsrift.riftflux.asgardshield;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;

import java.util.List;
import java.util.Locale;

public class ItemAsgardShield extends Item {
    private final ToolMaterial material;
    private final boolean gilded;
    private final int perkId;
    private final String soundProfile;
    private final String shieldType;
    private final ItemStack repairStack;
    private final float damageMultiplier;
    private final float knockback;
    private final int weaponDamage;
    private final String textureName;

    @SideOnly(Side.CLIENT)
    private IIcon overlayIcon;

    public ItemAsgardShield(String textureName,
                            ToolMaterial material,
                            boolean gilded,
                            int perkId,
                            String soundProfile,
                            String shieldType,
                            ItemStack repairStack) {
        this.textureName = textureName;
        this.material = material;
        this.gilded = gilded;
        this.perkId = perkId;
        this.soundProfile = soundProfile;
        this.shieldType = shieldType == null ? "" : shieldType.toLowerCase(Locale.ROOT);
        this.repairStack = repairStack == null ? null : repairStack.copy();

        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxStackSize(1);
        this.setMaxDamage(computeDurability());
        this.damageMultiplier = computeDamageMultiplier();
        this.knockback = computeKnockback();
        this.weaponDamage = computeWeaponDamage();
    }

    private int computeDurability() {
        int base = Math.max(1, material.getMaxUses());
        if ("wood".equals(shieldType) || "patchwork".equals(shieldType)) {
            base *= 2;
        } else if ("diamond".equals(shieldType) || "ender".equals(shieldType)) {
            base = Math.max(1, base / 2);
        }
        int mult = Math.max(1, ModConfig.asgardShieldEquipmentDurabilityMultiplier);
        int out = base * mult;
        if (gilded) {
            out += 128;
        }
        return Math.max(1, out);
    }

    private float computeDamageMultiplier() {
        float value;
        if ("stone".equals(shieldType)) {
            value = 0.90F;
        } else if ("diamond".equals(shieldType) || "ender".equals(shieldType)) {
            value = 0.80F;
        } else if ("iron".equals(shieldType)
                || "nether".equals(shieldType)
                || "livingmetal".equals(shieldType)
                || "biomass".equals(shieldType)) {
            value = 0.85F;
        } else {
            value = 0.95F;
        }
        if (gilded) {
            value -= 0.05F;
        }
        return Math.max(0.05F, value);
    }

    private float computeKnockback() {
        float value = "stone".equals(shieldType) ? -0.2F : -0.1F;
        if (gilded) {
            value -= 0.1F;
        }
        return value;
    }

    private int computeWeaponDamage() {
        int halfMaterialDamage = (int) (material.getDamageVsEntity() / 2.0F);
        int bonus;
        if ("wood".equals(shieldType) || "patchwork".equals(shieldType)) {
            bonus = 1 + halfMaterialDamage;
        } else if ("stone".equals(shieldType)) {
            bonus = 2 + halfMaterialDamage;
        } else {
            bonus = Math.max(1, halfMaterialDamage);
        }
        if (gilded) {
            bonus += 1;
        }
        return bonus;
    }

    public int getPerkId() {
        return perkId;
    }

    public boolean isGilded() {
        return gilded;
    }

    public String getSoundProfile() {
        return soundProfile;
    }

    public String getShieldType() {
        return shieldType;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public int getWeaponDamage() {
        return weaponDamage;
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
            if ("ender".equals(shieldType)) {
                AsgardShieldLogic.spawnEnderFx(target.worldObj, target.posX, target.posY, target.posZ, target.getRNG());
            } else if ("skull".equals(shieldType) && target.worldObj != null) {
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
    public float func_150893_a(ItemStack stack, Block block) {
        if (block == null) {
            return 1.0F;
        }
        if (block.getMaterial().isToolNotRequired()) {
            return 1.5F;
        }
        return 1.0F;
    }

    @Override
    public int getItemEnchantability() {
        return material.getEnchantability() / 3 + (gilded ? 2 : 0);
    }

    @Override
    public boolean getIsRepairable(ItemStack toRepair, ItemStack repair) {
        if (repair != null && repairStack != null
                && repair.getItem() == repairStack.getItem()
                && (repairStack.getItemDamage() == 32767 || repair.getItemDamage() == repairStack.getItemDamage())) {
            return true;
        }
        if ("ender".equals(shieldType) && repair != null && Item.getItemFromBlock(net.minecraft.init.Blocks.obsidian) == repair.getItem()) {
            return true;
        }
        return super.getIsRepairable(toRepair, repair);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.itemIcon = register.registerIcon("riftflux:asgardshield/" + textureName);
        this.overlayIcon = register.registerIcon("riftflux:asgardshield/" + textureName + "_overlay");
    }

    @Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamageForRenderPass(int damage, int pass) {
        return pass == 1 ? overlayIcon : itemIcon;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getColorFromItemStack(ItemStack stack, int pass) {
        if (pass > 0) {
            return 0xF0F0F0;
        }
        return getColor(stack);
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add("Special Perk: " + AsgardShieldLogic.getPerkName(perkId));
        list.add("Weakness: " + AsgardShieldLogic.getPerkWeakness(perkId));
    }

    public boolean hasColor(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) {
            return false;
        }
        NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
        return display != null && display.hasKey("color", 3);
    }

    public int getColor(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) {
            return AsgardShieldContent.getShieldColor(shieldType);
        }
        NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
        if (display != null && display.hasKey("color", 3)) {
            return display.getInteger("color");
        }
        return AsgardShieldContent.getShieldColor(shieldType);
    }

    public void clearColor(ItemStack stack) {
        if (stack == null || stack.getTagCompound() == null) {
            return;
        }
        NBTTagCompound display = stack.getTagCompound().getCompoundTag("display");
        if (display != null && display.hasKey("color", 3)) {
            display.removeTag("color");
        }
    }

    public void setColor(ItemStack stack, int color) {
        if (stack == null) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag == null) {
            tag = new NBTTagCompound();
            stack.setTagCompound(tag);
        }
        NBTTagCompound display = tag.getCompoundTag("display");
        tag.setTag("display", display);
        display.setInteger("color", color);
    }
}
