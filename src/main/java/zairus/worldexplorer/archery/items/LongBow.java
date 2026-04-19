package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
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
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.entity.EntitySpecialArrow;
import zairus.worldexplorer.core.WorldExplorer;

public class LongBow extends WEItemRanged {
    public static final String[] bowPullIconNameArray = new String[]{"pulling_0", "pulling_1", "pulling_2"};
    @SideOnly(Side.CLIENT)
    private IIcon[] iconArray;
    @SideOnly(Side.CLIENT)
    private IIcon[] stringIconArray;
    @SideOnly(Side.CLIENT)
    private IIcon[] arrowIconArray;
    @SideOnly(Side.CLIENT)
    private IIcon stringIcon;

    public LongBow() {
        this.setUnlocalizedName("longbow");
        this.setTextureName("worldexplorer:longbow_handle");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.setFull3D();
        this.setMaxStackSize(1);
        this.setMaxDamage(Math.max(0, ModConfig.riftExplorerLongbowDurability));
        this.addAllowedAmmo(Items.arrow, WEArcheryItems.specialarrow);
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

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        int charge = this.getMaxItemUseDuration(stack) - useCount;
        this.setBowHasArrow(stack, false);
        boolean infiniteAmmo = player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        ItemStack ammo = this.findPreferredAmmo(player);
        if (ammo == null && infiniteAmmo) {
            ammo = new ItemStack(WEArcheryItems.specialarrow, 1, 1);
        }
        if (ammo == null) {
            return;
        }

        float pull = (float) charge / 25.0F;
        pull = (pull * pull + pull * 2.0F) / 3.0F;
        if ((double) pull < 0.1D) {
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

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        ItemStack ammo = this.findPreferredAmmo(player);
        if (player.capabilities.isCreativeMode || ammo != null) {
            world.playSoundAtEntity(player, "worldexplorer:longbow_draw_1", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    private ItemStack findPreferredAmmo(EntityPlayer player) {
        if (player == null || player.inventory == null) {
            return null;
        }
        ItemStack vanillaArrow = null;
        for (int i = 0; i < 36; i++) {
            ItemStack slot = player.inventory.getStackInSlot(i);
            if (slot == null || slot.getItem() == null) {
                continue;
            }
            Item item = slot.getItem();
            if (item == WEArcheryItems.specialarrow) {
                return slot;
            }
            if (item == Items.arrow && vanillaArrow == null) {
                vanillaArrow = slot;
            }
        }
        return vanillaArrow;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 7200;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(this.getIconString() + "_standby");
        this.stringIcon = iconRegister.registerIcon("worldexplorer:longbow_string_standby");
        this.iconArray = new IIcon[bowPullIconNameArray.length];
        this.stringIconArray = new IIcon[bowPullIconNameArray.length];
        this.arrowIconArray = new IIcon[bowPullIconNameArray.length];
        for (int i = 0; i < this.iconArray.length; ++i) {
            this.iconArray[i] = iconRegister.registerIcon(this.getIconString() + "_" + bowPullIconNameArray[i]);
            this.stringIconArray[i] = iconRegister.registerIcon("worldexplorer:longbow_string_" + bowPullIconNameArray[i]);
            this.arrowIconArray[i] = iconRegister.registerIcon("worldexplorer:specialarrow");
        }
    }

    public IIcon getArrowIcon() {
        return this.arrowIconArray[0];
    }

    public IIcon getIcon(ItemStack stack, int renderPass) {
        return renderPass == 0 ? this.itemIcon : this.stringIcon;
    }

    @SideOnly(Side.CLIENT)
    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        IIcon icon = renderPass == 0 ? this.itemIcon : this.stringIcon;
        IIcon[] passIcon = renderPass == 0 ? this.iconArray : this.stringIconArray;
        if (player.getItemInUse() == null) {
            return icon;
        }
        int pulling = stack.getMaxItemUseDuration() - useRemaining;
        if (pulling >= 20) {
            return passIcon[2];
        }
        if (pulling > 10) {
            return passIcon[1];
        }
        if (pulling > 3) {
            return passIcon[0];
        }
        return icon;
    }
}
