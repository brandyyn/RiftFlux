package com.voidsrift.riftflux.axolotl;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.util.IIcon;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

public class ItemAxolotlBucket extends ItemBucket {
    private static final String TAG_ENTITY_DATA = "AxolotlData";
    private static final String TAG_VARIANT = "AxolotlVariant";
    private static final String TAG_AGE = "Age";
    private static final String TAG_HEALTH = "Health";
    private static final String TAG_TAMED = "Tamed";
    private static final String TAG_OWNER_UUID = "OwnerUUID";
    @SideOnly(Side.CLIENT)
    private IIcon[] variantIcons;

    public ItemAxolotlBucket() {
        super(Blocks.flowing_water);
        this.setHasSubtypes(true);
        this.setMaxStackSize(1);
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setTextureName("riftflux:axolotl_bucket");
        this.setUnlocalizedName("axolotl_bucket");
    }

    @Override
    public String getItemStackDisplayName(ItemStack stack) {
        if (stack != null && stack.hasDisplayName()) {
            NBTTagCompound tag = stack.getTagCompound();
            if (tag != null && tag.hasKey("display", 10)) {
                NBTTagCompound display = tag.getCompoundTag("display");
                if (display.hasKey("Name", 8)) {
                    String customName = display.getString("Name");
                    if (customName != null && !customName.isEmpty()) {
                        return customName;
                    }
                }
            }
        }
        return StatCollector.translateToLocal(this.getUnlocalizedNameInefficiently(stack) + ".name").trim();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister register) {
        this.variantIcons = new IIcon[AxolotlVariant.values().length];
        for (AxolotlVariant variant : AxolotlVariant.values()) {
            this.variantIcons[variant.getId()] = register.registerIcon("riftflux:axolotl_bucket_" + variant.getTextureKey());
        }
        this.itemIcon = this.variantIcons[0];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        if (this.variantIcons == null || damage < 0 || damage >= this.variantIcons.length || this.variantIcons[damage] == null) {
            return super.getIconFromDamage(damage);
        }
        return this.variantIcons[damage];
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        MovingObjectPosition hit = this.getMovingObjectPositionFromPlayer(world, player, false);
        if (hit == null || hit.typeOfHit != MovingObjectPosition.MovingObjectType.BLOCK) {
            return stack;
        }

        int x = hit.blockX;
        int y = hit.blockY;
        int z = hit.blockZ;

        if (!world.canMineBlock(player, x, y, z)) {
            return stack;
        }

        switch (hit.sideHit) {
            case 0:
                --y;
                break;
            case 1:
                ++y;
                break;
            case 2:
                --z;
                break;
            case 3:
                ++z;
                break;
            case 4:
                --x;
                break;
            case 5:
                ++x;
                break;
            default:
                break;
        }

        if (!player.canPlayerEdit(x, y, z, hit.sideHit, stack)) {
            return stack;
        }
        Material material = world.getBlock(x, y, z).getMaterial();
        boolean canReplace = world.isAirBlock(x, y, z) || material == Material.water || !material.isSolid();
        if (!canReplace || material == Material.lava) {
            return stack;
        }

        if (!world.isRemote) {
            EntityAxolotl axolotl = new EntityAxolotl(world);
            applyBucketData(stack, axolotl);
            axolotl.setLocationAndAngles(x + 0.5D, y + 0.35D, z + 0.5D, world.rand.nextFloat() * 360.0F, 0.0F);
            axolotl.motionX = 0.0D;
            axolotl.motionY = 0.0D;
            axolotl.motionZ = 0.0D;
            world.spawnEntityInWorld(axolotl);
        }

        return player.capabilities.isCreativeMode ? stack : new ItemStack(Items.bucket);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs tab, List list) {
        for (AxolotlVariant variant : AxolotlVariant.values()) {
            list.add(createBucketStack(variant, 0, 14.0F, null));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        list.add(StatCollector.translateToLocal("item.axolotl.variant." + getVariant(stack).getTextureKey()));
    }

    public static boolean captureAxolotl(ItemStack held, EntityPlayer player, EntityAxolotl axolotl) {
        if (held == null) {
            return false;
        }
        Item heldItem = held.getItem();
        if (heldItem != Items.water_bucket && heldItem != Items.bucket) {
            return false;
        }
        if (axolotl.worldObj.isRemote) {
            return true;
        }

        ItemStack bucket = createBucketStack(
                axolotl.getVariant(),
                axolotl.getGrowingAge(),
                axolotl.getHealth(),
                axolotl.hasCustomNameTag() ? axolotl.getCustomNameTag() : null
        );
        NBTTagCompound tag = bucket.getTagCompound();
        if (tag != null) {
            NBTTagCompound entityData = new NBTTagCompound();
            axolotl.writeToNBT(entityData);
            sanitizeBucketEntityData(entityData);
            tag.setTag(TAG_ENTITY_DATA, entityData);
        }

        axolotl.worldObj.playSoundAtEntity(player, "riftflux:axolotl_bucket_fill", 1.0F, 1.0F);
        axolotl.setDead();

        if (player.capabilities.isCreativeMode) {
            if (!player.inventory.addItemStackToInventory(bucket.copy())) {
                player.dropPlayerItemWithRandomChoice(bucket.copy(), false);
            }
            return true;
        }

        player.inventory.setInventorySlotContents(player.inventory.currentItem, bucket);
        return true;
    }

    public static ItemStack createBucketStack(AxolotlVariant variant, int age, float health, String customName) {
        ItemStack stack = new ItemStack(AxolotlContent.axolotlBucket, 1, variant.getId());
        NBTTagCompound tag = new NBTTagCompound();
        tag.setByte(TAG_VARIANT, (byte) variant.getId());
        tag.setInteger(TAG_AGE, age);
        tag.setFloat(TAG_HEALTH, health);
        stack.setTagCompound(tag);
        if (customName != null && !customName.isEmpty()) {
            stack.setStackDisplayName(customName);
        }
        return stack;
    }

    public static void applyBucketData(ItemStack stack, EntityAxolotl axolotl) {
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(TAG_ENTITY_DATA, 10)) {
            axolotl.readFromNBT(tag.getCompoundTag(TAG_ENTITY_DATA));
        } else {
            axolotl.setVariant(getVariant(stack));
            if (tag != null) {
                axolotl.setGrowingAge(tag.getInteger(TAG_AGE));
                if (tag.hasKey(TAG_HEALTH)) {
                    axolotl.setHealth(Math.max(1.0F, tag.getFloat(TAG_HEALTH)));
                }
                if (tag.getBoolean(TAG_TAMED)) {
                    axolotl.setTamed(true);
                    if (tag.hasKey(TAG_OWNER_UUID, 8)) {
                        axolotl.func_152115_b(tag.getString(TAG_OWNER_UUID));
                    }
                    axolotl.setSitting(false);
                }
            }
        }

        axolotl.setFromBucket(true);

        if (stack.hasDisplayName()) {
            axolotl.setCustomNameTag(stack.getDisplayName());
        }
    }

    public static AxolotlVariant getVariant(ItemStack stack) {
        if (stack == null) {
            return AxolotlVariant.LUCY;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag != null && tag.hasKey(TAG_ENTITY_DATA, 10)) {
            return AxolotlVariant.byId(tag.getCompoundTag(TAG_ENTITY_DATA).getByte(TAG_VARIANT));
        }
        if (tag != null && tag.hasKey(TAG_VARIANT)) {
            return AxolotlVariant.byId(tag.getByte(TAG_VARIANT));
        }
        return AxolotlVariant.byId(stack.getItemDamage());
    }

    private static void sanitizeBucketEntityData(NBTTagCompound entityData) {
        entityData.setTag("Pos", createDoubleList(0.0D, 0.0D, 0.0D));
        entityData.setTag("Motion", createDoubleList(0.0D, 0.0D, 0.0D));
        entityData.setTag("Rotation", createFloatList(0.0F, 0.0F));
        entityData.removeTag("UUIDMost");
        entityData.removeTag("UUIDLeast");
        entityData.removeTag("FallDistance");
        entityData.removeTag("Fire");
        entityData.removeTag("Air");
        entityData.removeTag("OnGround");
        entityData.removeTag("Dimension");
        entityData.removeTag("PortalCooldown");
        entityData.removeTag("Leashed");
        entityData.removeTag("Leash");
        entityData.removeTag("Riding");
        entityData.removeTag("HurtTime");
        entityData.removeTag("DeathTime");
        entityData.removeTag("AttackTime");
        entityData.removeTag("PlayDeadTicks");
        entityData.setBoolean("FromBucket", true);
    }

    private static NBTTagList createDoubleList(double... values) {
        NBTTagList list = new NBTTagList();
        for (double value : values) {
            list.appendTag(new NBTTagDouble(value));
        }
        return list;
    }

    private static NBTTagList createFloatList(float... values) {
        NBTTagList list = new NBTTagList();
        for (float value : values) {
            list.appendTag(new NBTTagFloat(value));
        }
        return list;
    }
}
