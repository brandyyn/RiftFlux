package zairus.worldexplorer.archery.items;

import cpw.mods.fml.common.eventhandler.Event;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import zairus.worldexplorer.archery.entity.EntitySpecialArrow;
import zairus.worldexplorer.core.WorldExplorer;

public class CrossBow extends WEItemRanged {
    public CrossBow() {
        this.setUnlocalizedName("crossbow");
        this.setTextureName("worldexplorer:crossbow");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.maxStackSize = 1;
        this.bFull3D = true;
        this.addAllowedAmmo(Items.arrow, WEArcheryItems.specialarrow);
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (count == 2275) {
            player.playSound("worldexplorer:crossbow_charge", 1.0F, 1.0F);
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        boolean hasArrow = this.getBowHasAwwor(stack);
        player.playSound("worldexplorer:crossbow_handle", 1.0F, 1.0F);
        if (!hasArrow) {
            player.playSound("worldexplorer:crossbow_pull", 1.0F, 1.0F);
            this.arrowCharge(stack, player);
        } else {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        boolean hasArrow = this.getBowHasAwwor(stack);
        if (hasArrow) {
            this.shootAmmo(stack, world, player);
        }
        if (!hasArrow && useCount <= 2275) {
            this.setBowHasArrow(stack, true);
        }
    }

    private void arrowCharge(ItemStack stack, EntityPlayer player) {
        ArrowNockEvent event = new ArrowNockEvent(player, stack);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        ItemStack ammo = WEItemRanged.getAmmo(stack, player);
        if (player.capabilities.isCreativeMode || ammo != null) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
    }

    private void initStackTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
    }

    private void setBowHasArrow(ItemStack stack, boolean hasArrow) {
        this.initStackTag(stack);
        stack.getTagCompound().setBoolean("hasArrow", hasArrow);
    }

    public boolean getBowHasAwwor(ItemStack stack) {
        boolean hasArrow = false;
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("hasArrow")) {
            hasArrow = stack.getTagCompound().getBoolean("hasArrow");
        }
        return hasArrow;
    }

    private void shootAmmo(ItemStack stack, World world, EntityPlayer player) {
        int charge = this.getMaxItemUseDuration(stack);
        ArrowLooseEvent event = new ArrowLooseEvent(player, stack, charge);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            return;
        }
        charge = event.charge;
        boolean infiniteAmmo = player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        ItemStack ammo = WEItemRanged.getAmmo(stack, player);
        if (ammo == null && infiniteAmmo) {
            ammo = new ItemStack(WEArcheryItems.specialarrow, 1, 1);
        }
        if (ammo == null) {
            this.setBowHasArrow(stack, false);
            return;
        }

        float pull = (float) charge / 25.0F;
        pull = (pull * pull + pull * 2.0F) / 3.0F;
        if ((double) pull < 0.1D) {
            this.setBowHasArrow(stack, false);
            return;
        }
        if (pull > 1.0F) {
            pull = 1.0F;
        }

        Entity projectile = this.getEntityToShoot(stack, ammo, pull, world, player, !infiniteAmmo);
        stack.damageItem(1, player);
        if (!world.isRemote) {
            world.playSoundAtEntity(player, "random.bow", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + pull * 0.5F);
        }
        if (!infiniteAmmo) {
            consumeSpecificAmmo(player.inventory, ammo);
        }
        if (!world.isRemote) {
            world.spawnEntityInWorld(projectile);
        }
        this.setBowHasArrow(stack, false);
    }

    private static void consumeSpecificAmmo(InventoryPlayer inventory, ItemStack ammo) {
        if (inventory == null || ammo == null || ammo.getItem() == null) {
            return;
        }
        for (int i = 0; i < inventory.getSizeInventory(); i++) {
            ItemStack slot = inventory.getStackInSlot(i);
            if (slot == null || slot.getItem() != ammo.getItem()) {
                continue;
            }
            if (slot.getHasSubtypes() && slot.getItemDamage() != ammo.getItemDamage()) {
                continue;
            }
            inventory.decrStackSize(i, 1);
            return;
        }
    }

    private Entity getEntityToShoot(ItemStack stack, ItemStack ammo, float pull, World world, EntityPlayer player, boolean canBePickedUp) {
        EntityArrow arrow = null;
        EntitySpecialArrow specialArrow = null;
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        int flame = EnchantmentHelper.getEnchantmentLevel(Enchantment.flame.effectId, stack);
        if (ammo.getItem() == WEArcheryItems.specialarrow) {
            specialArrow = new EntitySpecialArrow(world, player, pull * 2.5F, ammo);
            if (pull == 1.0F) {
                specialArrow.setIsCritical(true);
            }
            if (power > 0) {
                specialArrow.setDamage(specialArrow.getDamage() + (double) power * 0.5D + 0.5D);
            }
            if (punch > 0) {
                specialArrow.setKnockbackStrength(punch);
            }
            if (flame > 0) {
                specialArrow.setFire(100);
            }
            if (!canBePickedUp) {
                specialArrow.canBePickedUp = 2;
            }
        } else {
            arrow = new EntityArrow(world, player, pull * 2.5F);
            if (pull == 1.0F) {
                arrow.setIsCritical(true);
            }
            if (power > 0) {
                arrow.setDamage(arrow.getDamage() + (double) power * 0.5D + 0.5D);
            }
            if (punch > 0) {
                arrow.setKnockbackStrength(punch);
            }
            if (flame > 0) {
                arrow.setFire(100);
            }
            if (!canBePickedUp) {
                arrow.canBePickedUp = 2;
            }
        }
        if (specialArrow != null) {
            return specialArrow;
        }
        return arrow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        int duration = 2300;
        if (this.getBowHasAwwor(stack)) {
            duration = 1000;
        }
        return duration;
    }
}
