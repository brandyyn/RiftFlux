package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.entity.EntityPebble;
import zairus.worldexplorer.core.WorldExplorer;

public class Slingshot extends WEItemRanged {
    public static final String[] bowPullIconNameArray = new String[]{"pulling_0", "pulling_1", "pulling_2"};
    @SideOnly(Side.CLIENT)
    private IIcon[] iconArray;

    public Slingshot() {
        this.setUnlocalizedName("slingshot");
        this.setTextureName("worldexplorer:slingshot");
        this.setCreativeTab(WorldExplorer.tabWorldExplorer);
        this.setFull3D();
        this.setMaxStackSize(1);
        this.setMaxDamage(Math.max(0, ModConfig.riftExplorerSlingshotDurability));
    }

    private static Item getAmmoItem() {
        return ModConfig.riftExplorerSlingshotUsesCobblestoneAmmo ? Item.getItemFromBlock(Blocks.cobblestone) : WEArcheryItems.pebble;
    }

    public void onPlayerStoppedUsing(ItemStack stack, World world, EntityPlayer player, int useCount) {
        int charge = this.getMaxItemUseDuration(stack) - useCount;
        boolean infiniteAmmo = player.capabilities.isCreativeMode
                || EnchantmentHelper.getEnchantmentLevel(Enchantment.infinity.effectId, stack) > 0;
        Item ammoItem = getAmmoItem();
        if (ammoItem == null) {
            return;
        }
        if (!infiniteAmmo && !player.inventory.hasItem(ammoItem)) {
            return;
        }

        float pull = (float) charge / 7.0F;
        pull = (pull * pull + pull * 2.0F) / 3.0F;
        if ((double) pull < 0.1D) {
            return;
        }
        if (pull > 1.0F) {
            pull = 1.0F;
        }

        EntityPebble entityPebble = new EntityPebble(world, player, pull * 1.0F);
        if (pull == 1.0F) {
            entityPebble.setIsCritical(true);
        }
        int power = EnchantmentHelper.getEnchantmentLevel(Enchantment.power.effectId, stack);
        if (power > 0) {
            entityPebble.setDamage(entityPebble.getDamage() + (double) power * 0.5D + 0.5D);
        }
        int punch = EnchantmentHelper.getEnchantmentLevel(Enchantment.punch.effectId, stack);
        if (punch > 0) {
            entityPebble.setKnockbackStrength(punch);
        }
        stack.damageItem(1, player);
        if (!world.isRemote) {
            world.playSoundAtEntity(player, "worldexplorer:slingshot_release_1", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + pull * 0.5F);
        }
        if (infiniteAmmo) {
            entityPebble.canBePickedUp = 2;
        } else {
            player.inventory.consumeInventoryItem(ammoItem);
        }
        if (!world.isRemote) {
            world.spawnEntityInWorld(entityPebble);
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        Item ammoItem = getAmmoItem();
        if (ammoItem != null && (player.capabilities.isCreativeMode || player.inventory.hasItem(ammoItem))) {
            world.playSoundAtEntity(player, "worldexplorer:slingshot_pull_1", 1.0F, 1.0F / (itemRand.nextFloat() * 0.4F + 1.2F) + 0.5F);
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1000;
    }

    @SideOnly(Side.CLIENT)
    public void registerIcons(net.minecraft.client.renderer.texture.IIconRegister iconRegister) {
        this.itemIcon = iconRegister.registerIcon(this.getIconString() + "_standby");
        this.iconArray = new IIcon[bowPullIconNameArray.length];
        for (int i = 0; i < this.iconArray.length; ++i) {
            this.iconArray[i] = iconRegister.registerIcon(this.getIconString() + "_" + bowPullIconNameArray[i]);
        }
    }

    @SideOnly(Side.CLIENT)
    public IIcon getItemIconForUseDuration(int duration) {
        return this.iconArray[duration];
    }

    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (player.getItemInUse() == null) {
            return this.itemIcon;
        }
        int pulling = stack.getMaxItemUseDuration() - useRemaining;
        if (pulling >= 6) {
            return this.iconArray[2];
        }
        if (pulling > 3) {
            return this.iconArray[1];
        }
        if (pulling > 0) {
            return this.iconArray[0];
        }
        return this.itemIcon;
    }
}
