package com.voidsrift.riftflux.duckling;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;

public class ItemSootJar extends Item {
    public static final String TAG_SPRITE_DATA = "StoredSprites";
    public static final String TAG_SPRITE_COUNT = "SpriteCount";
    public static final int MAX_CAPACITY = 64;

    public ItemSootJar() {
        this.setMaxStackSize(1);
        this.setTextureName(DucklingContent.MODID + ":soot_jar");
        this.setUnlocalizedName("soot_jar");
        this.setCreativeTab(CreativeTabs.tabMisc);
    }

    @Override
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase target) {
        if (!(target instanceof EntitySootSprite) || !target.isEntityAlive()) {
            return false;
        }

        EntitySootSprite sprite = (EntitySootSprite)target;
        if (getStoredSpriteCount(stack) >= MAX_CAPACITY) {
            return false;
        }

        if (!player.worldObj.isRemote && storeSprite(stack, sprite)) {
            sprite.setDead();
            player.worldObj.playSoundAtEntity(player, "random.pop", 0.6F, 1.4F);
        }
        return true;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z,
                             int side, float hitX, float hitY, float hitZ) {
        if (getStoredSpriteCount(stack) <= 0) {
            return false;
        }

        if (!world.isRemote) {
            ForgeDirection direction = ForgeDirection.getOrientation(side);
            double spawnX = x + direction.offsetX + 0.5D;
            double spawnY = y + direction.offsetY;
            double spawnZ = z + direction.offsetZ + 0.5D;
            if (releaseSprite(stack, world, spawnX, spawnY, spawnZ)) {
                world.playSoundEffect(spawnX, spawnY + 0.5D, spawnZ, "random.pop", 0.6F, 0.9F);
                if (getStoredSpriteCount(stack) <= 0) {
                    player.inventory.setInventorySlotContents(player.inventory.currentItem, new ItemStack(Items.glass_bottle));
                }
            }
        }
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List tooltip, boolean advanced) {
        tooltip.add("Sprites: " + getStoredSpriteCount(stack) + "/" + MAX_CAPACITY);
    }

    public static ItemStack createFilledJar(EntitySootSprite sprite) {
        ItemStack jar = new ItemStack(DucklingContent.sootJar);
        storeSprite(jar, sprite);
        return jar;
    }

    public static boolean storeSprite(ItemStack stack, EntitySootSprite sprite) {
        if (stack == null || sprite == null) {
            return false;
        }

        NBTTagCompound tag = getOrCreateTag(stack);
        NBTTagList sprites = getSpriteList(tag);
        if (sprites.tagCount() >= MAX_CAPACITY) {
            return false;
        }

        NBTTagCompound spriteData = new NBTTagCompound();
        sprite.writeToNBTOptional(spriteData);
        sprites.appendTag(spriteData);
        tag.setTag(TAG_SPRITE_DATA, sprites);
        tag.setInteger(TAG_SPRITE_COUNT, sprites.tagCount());
        return true;
    }

    private static boolean releaseSprite(ItemStack stack, World world, double x, double y, double z) {
        NBTTagCompound tag = stack == null ? null : stack.getTagCompound();
        if (tag == null) {
            return false;
        }

        if (tag.hasKey(TAG_SPRITE_DATA, 9)) {
            NBTTagList sprites = tag.getTagList(TAG_SPRITE_DATA, 10);
            if (sprites.tagCount() <= 0) {
                tag.setInteger(TAG_SPRITE_COUNT, 0);
                return false;
            }

            NBTTagCompound spriteData = (NBTTagCompound)sprites.getCompoundTagAt(sprites.tagCount() - 1).copy();
            sprites.removeTag(sprites.tagCount() - 1);
            spawnSpriteFromData(world, spriteData, x, y, z);
            tag.setInteger(TAG_SPRITE_COUNT, sprites.tagCount());
            return true;
        }

        if (tag.hasKey(TAG_SPRITE_COUNT)) {
            int count = tag.getInteger(TAG_SPRITE_COUNT);
            if (count <= 0) {
                return false;
            }
            EntitySootSprite sprite = new EntitySootSprite(world);
            sprite.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
            world.spawnEntityInWorld(sprite);
            tag.setInteger(TAG_SPRITE_COUNT, count - 1);
            return true;
        }

        return false;
    }

    private static void spawnSpriteFromData(World world, NBTTagCompound spriteData, double x, double y, double z) {
        spriteData.removeTag("UUIDMost");
        spriteData.removeTag("UUIDLeast");
        spriteData.removeTag("PersistentIDMSB");
        spriteData.removeTag("PersistentIDLSB");
        spriteData.removeTag("Riding");

        EntitySootSprite sprite = new EntitySootSprite(world);
        sprite.readFromNBT(spriteData);
        sprite.motionX = 0.0D;
        sprite.motionY = 0.0D;
        sprite.motionZ = 0.0D;
        sprite.setLocationAndAngles(x, y, z, world.rand.nextFloat() * 360.0F, 0.0F);
        world.spawnEntityInWorld(sprite);
    }

    public static int getStoredSpriteCount(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return 0;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.hasKey(TAG_SPRITE_DATA, 9)) {
            return tag.getTagList(TAG_SPRITE_DATA, 10).tagCount();
        }
        return tag.hasKey(TAG_SPRITE_COUNT) ? Math.max(0, tag.getInteger(TAG_SPRITE_COUNT)) : 0;
    }

    private static NBTTagCompound getOrCreateTag(ItemStack stack) {
        if (!stack.hasTagCompound()) {
            stack.setTagCompound(new NBTTagCompound());
        }
        return stack.getTagCompound();
    }

    private static NBTTagList getSpriteList(NBTTagCompound tag) {
        return tag.hasKey(TAG_SPRITE_DATA, 9) ? tag.getTagList(TAG_SPRITE_DATA, 10) : new NBTTagList();
    }
}
