package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import com.voidsrift.riftflux.riftexplorer.RiftChestRandomMobStateHelper;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.IBossDisplayData;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import com.voidsrift.riftflux.mixinhooks.IRiftChestRandomMobState;
import zairus.worldexplorer.core.items.WEItem;

public class CapturedEnderChestItem extends WEItem {
    private static final String TAG_ENTITY = "CapturedEntity";
    private static final String TAG_ENTITY_NAME = "CapturedEntityName";
    private static final String TAG_INITIAL_X = "CapturedInitialX";
    private static final String TAG_INITIAL_Y = "CapturedInitialY";
    private static final String TAG_INITIAL_Z = "CapturedInitialZ";
    private static final String TAG_INITIAL_BIOME = "CapturedInitialBiome";
    private static final String TAG_RANDOM_SEED = "CapturedRandomSeed";
    private static final Class<?> MCPATCHER_TRACKED_ENTITY_CLASS = findMcPatcherTrackedEntityClass();
    private static final Field MCPATCHER_INIT_FIELD = findMcPatcherField("mcp$init");
    private static final Field MCPATCHER_INITIAL_X_FIELD = findMcPatcherField("mcp$initX");
    private static final Field MCPATCHER_INITIAL_Y_FIELD = findMcPatcherField("mcp$initY");
    private static final Field MCPATCHER_INITIAL_Z_FIELD = findMcPatcherField("mcp$initZ");
    private static final Field MCPATCHER_INITIAL_BIOME_FIELD = findMcPatcherField("mcp$initBiome");
    private static final Field MCPATCHER_RANDOM_SEED_FIELD = findMcPatcherField("mcp$randomMobsSeed");
    private static final Field MCPATCHER_RANDOM_SEED_INIT_FIELD = findMcPatcherField("mcp$randomMobsSeedInit");
    private static final Method MCPATCHER_INITIAL_X = findMcPatcherMethod("mcp$initialX");
    private static final Method MCPATCHER_INITIAL_Y = findMcPatcherMethod("mcp$initialY");
    private static final Method MCPATCHER_INITIAL_Z = findMcPatcherMethod("mcp$initialZ");
    private static final Method MCPATCHER_INITIAL_BIOME = findMcPatcherMethod("mcp$initialBiome");
    private static final Method MCPATCHER_RANDOM_SEED = findMcPatcherMethod("mcp$randomMobsSeed");

    @SideOnly(Side.CLIENT)
    private IIcon enderChestIcon;

    public CapturedEnderChestItem() {
        this.setUnlocalizedName("captured_ender_chest");
        this.setTextureName("minecraft:ender_chest");
        this.setCreativeTab(CreativeTabs.tabCombat);
        this.setMaxStackSize(1);
    }

    public static boolean hasCapturedEntity(ItemStack stack) {
        return stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_ENTITY, 10);
    }

    public static ItemStack captureEntity(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null || entity.isDead || entity instanceof EntityPlayer) {
            return null;
        }
        if (ModConfig.riftExplorerSlingshotBlockBossCapture && isBoss(entity)) {
            return null;
        }
        if (isCaptureBlacklisted(entity)) {
            return null;
        }

        NBTTagCompound entityTag = new NBTTagCompound();
        if (!entity.writeToNBTOptional(entityTag)) {
            return null;
        }
        if (!entityTag.hasKey("id")) {
            String entityId = EntityList.getEntityString(entity);
            if (entityId == null || entityId.isEmpty()) {
                return null;
            }
            entityTag.setString("id", entityId);
        }

        ItemStack captured = new ItemStack(WEArcheryItems.captured_ender_chest, 1, 0);
        NBTTagCompound stackTag = new NBTTagCompound();
        stackTag.setTag(TAG_ENTITY, entityTag);
        stackTag.setString(TAG_ENTITY_NAME, entity.getCommandSenderName());
        RiftChestRandomMobStateHelper.ensureStoredState(entity);
        stackTag.setInteger(TAG_INITIAL_X, resolveInitialX(entity));
        stackTag.setInteger(TAG_INITIAL_Y, resolveInitialY(entity));
        stackTag.setInteger(TAG_INITIAL_Z, resolveInitialZ(entity));
        stackTag.setInteger(TAG_INITIAL_BIOME, resolveInitialBiomeId(entity));
        stackTag.setInteger(TAG_RANDOM_SEED, resolveRandomSeed(entity));
        captured.setTagCompound(stackTag);
        return captured;
    }

    public static boolean releaseCapturedEntity(ItemStack stack, World world, double x, double y, double z, EntityPlayer owner) {
        if (stack == null || world == null || !hasCapturedEntity(stack)) {
            return false;
        }
        if (world.isRemote) {
            return true;
        }

        NBTTagCompound entityTag = (NBTTagCompound)stack.getTagCompound().getCompoundTag(TAG_ENTITY).copy();
        Entity entity = EntityList.createEntityFromNBT(entityTag, world);
        if (entity == null) {
            return false;
        }

        applyCapturedRandomMobState(stack, entity);
        entity.setLocationAndAngles(x, y, z, owner == null ? entity.rotationYaw : owner.rotationYaw, entity.rotationPitch);
        entity.motionX = 0.0D;
        entity.motionY = 0.0D;
        entity.motionZ = 0.0D;
        return world.spawnEntityInWorld(entity);
    }

    public static void dropCapturedEntityItem(EntityLivingBase entity, ItemStack capturedStack) {
        if (entity == null || capturedStack == null || entity.worldObj == null || entity.worldObj.isRemote) {
            return;
        }
        EntityItem item = new EntityItem(entity.worldObj, entity.posX, entity.posY + (double)(entity.height * 0.5f), entity.posZ, capturedStack);
        item.delayBeforeCanPickup = 10;
        entity.worldObj.spawnEntityInWorld(item);
    }

    private static boolean isBoss(EntityLivingBase entity) {
        return entity instanceof IBossDisplayData;
    }

    private static boolean isCaptureBlacklisted(EntityLivingBase entity) {
        String[] blacklist = ModConfig.riftExplorerSlingshotCaptureMobBlacklist;
        if (entity == null || blacklist == null || blacklist.length == 0) {
            return false;
        }

        String entityId = EntityList.getEntityString(entity);
        String displayName = entity.getCommandSenderName();
        String simpleClassName = entity.getClass().getSimpleName();
        String fullClassName = entity.getClass().getName();

        for (int i = 0; i < blacklist.length; i++) {
            String raw = blacklist[i];
            if (raw == null || raw.trim().isEmpty()) {
                continue;
            }
            if (fullClassName.equalsIgnoreCase(raw.trim())
                    || matchesMobKey(raw, entityId)
                    || matchesMobKey(raw, displayName)
                    || matchesMobKey(raw, simpleClassName)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesMobKey(String raw, String candidate) {
        String rawKey = normalizeMobKey(raw);
        String candidateKey = normalizeMobKey(candidate);
        if (rawKey.isEmpty() || candidateKey.isEmpty()) {
            return false;
        }
        return rawKey.equals(candidateKey) || simpleMobKey(rawKey).equals(simpleMobKey(candidateKey));
    }

    private static String normalizeMobKey(String value) {
        if (value == null) {
            return "";
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("entity.")) {
            normalized = normalized.substring(7);
        }
        if (normalized.endsWith(".name")) {
            normalized = normalized.substring(0, normalized.length() - 5);
        }
        if (normalized.startsWith("minecraft:")) {
            normalized = normalized.substring(10);
        }
        return normalized.replace(" ", "").replace("_", "").replace("-", "");
    }

    private static String simpleMobKey(String value) {
        int colon = value.lastIndexOf(':');
        int dot = value.lastIndexOf('.');
        int index = Math.max(colon, dot);
        return index >= 0 && index + 1 < value.length() ? value.substring(index + 1) : value;
    }

    private static void applyCapturedRandomMobState(ItemStack stack, Entity entity) {
        if (stack == null || entity == null || !(entity instanceof IRiftChestRandomMobState) || !stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (!tag.hasKey(TAG_RANDOM_SEED, 3)) {
            return;
        }
        ((IRiftChestRandomMobState) entity).rf$setRiftChestRandomMobState(
                tag.getInteger(TAG_INITIAL_X),
                tag.getInteger(TAG_INITIAL_Y),
                tag.getInteger(TAG_INITIAL_Z),
                tag.getInteger(TAG_INITIAL_BIOME),
                tag.getInteger(TAG_RANDOM_SEED));
        RiftChestRandomMobStateHelper.applyStoredState(
                entity,
                tag.getInteger(TAG_INITIAL_X),
                tag.getInteger(TAG_INITIAL_Y),
                tag.getInteger(TAG_INITIAL_Z),
                tag.getInteger(TAG_INITIAL_BIOME),
                tag.getInteger(TAG_RANDOM_SEED));
    }

    private static Class<?> findMcPatcherTrackedEntityClass() {
        try {
            return Class.forName("com.falsepattern.mcpatcher.internal.modules.mob.TrackedEntity");
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Method findMcPatcherMethod(String name) {
        if (MCPATCHER_TRACKED_ENTITY_CLASS == null) {
            return null;
        }
        try {
            return MCPATCHER_TRACKED_ENTITY_CLASS.getMethod(name);
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Field findMcPatcherField(String name) {
        try {
            Field field = Entity.class.getDeclaredField(name);
            field.setAccessible(true);
            return field;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static int resolveInitialX(Entity entity) {
        if (RiftChestRandomMobStateHelper.hasStoredState(entity)) {
            return RiftChestRandomMobStateHelper.getInitialX(entity);
        }
        Integer value = readMcPatcherIntField(MCPATCHER_INITIAL_X_FIELD, entity, MCPATCHER_INIT_FIELD);
        if (value == null) {
            value = invokeMcPatcherInt(MCPATCHER_INITIAL_X, entity);
        }
        return value != null ? value.intValue() : MathHelper.floor_double(entity.posX);
    }

    private static int resolveInitialY(Entity entity) {
        if (RiftChestRandomMobStateHelper.hasStoredState(entity)) {
            return RiftChestRandomMobStateHelper.getInitialY(entity);
        }
        Integer value = readMcPatcherIntField(MCPATCHER_INITIAL_Y_FIELD, entity, MCPATCHER_INIT_FIELD);
        if (value == null) {
            value = invokeMcPatcherInt(MCPATCHER_INITIAL_Y, entity);
        }
        return value != null ? value.intValue() : MathHelper.floor_double(entity.posY);
    }

    private static int resolveInitialZ(Entity entity) {
        if (RiftChestRandomMobStateHelper.hasStoredState(entity)) {
            return RiftChestRandomMobStateHelper.getInitialZ(entity);
        }
        Integer value = readMcPatcherIntField(MCPATCHER_INITIAL_Z_FIELD, entity, MCPATCHER_INIT_FIELD);
        if (value == null) {
            value = invokeMcPatcherInt(MCPATCHER_INITIAL_Z, entity);
        }
        return value != null ? value.intValue() : MathHelper.floor_double(entity.posZ);
    }

    private static int resolveInitialBiomeId(EntityLivingBase entity) {
        if (entity == null || entity.worldObj == null) {
            return -1;
        }
        if (RiftChestRandomMobStateHelper.hasStoredState(entity)) {
            return RiftChestRandomMobStateHelper.getBiomeId(entity);
        }

        try {
            BiomeGenBase initialBiome = readMcPatcherBiomeField(MCPATCHER_INITIAL_BIOME_FIELD, entity, MCPATCHER_INIT_FIELD);
            if (initialBiome != null) {
                return initialBiome.biomeID;
            }
            if (MCPATCHER_INITIAL_BIOME != null && MCPATCHER_TRACKED_ENTITY_CLASS != null && MCPATCHER_TRACKED_ENTITY_CLASS.isInstance(entity)) {
                Object biome = MCPATCHER_INITIAL_BIOME.invoke(entity);
                if (biome instanceof BiomeGenBase) {
                    return ((BiomeGenBase) biome).biomeID;
                }
            }
        } catch (Throwable ignored) {
        }

        BiomeGenBase biome = entity.worldObj.getBiomeGenForCoords(resolveInitialX(entity), resolveInitialZ(entity));
        return biome == null ? -1 : biome.biomeID;
    }

    private static int resolveRandomSeed(Entity entity) {
        if (RiftChestRandomMobStateHelper.hasStoredState(entity)) {
            return RiftChestRandomMobStateHelper.getRandomSeed(entity);
        }
        Integer value = readMcPatcherIntField(MCPATCHER_RANDOM_SEED_FIELD, entity, MCPATCHER_RANDOM_SEED_INIT_FIELD);
        if (value == null) {
            value = invokeMcPatcherInt(MCPATCHER_RANDOM_SEED, entity);
        }
        if (value != null) {
            return value.intValue();
        }

        UUID uuid = entity == null ? null : entity.getUniqueID();
        if (uuid != null) {
            long most = uuid.getMostSignificantBits();
            long least = uuid.getLeastSignificantBits();
            int seed = (int) (most ^ (most >>> 32) ^ least ^ (least >>> 32));
            return mcpIntHash(seed) & Integer.MAX_VALUE;
        }

        int x = resolveInitialX(entity);
        int y = resolveInitialY(entity);
        int z = resolveInitialZ(entity);
        return mcpIntHash(x * 73428767 ^ y * 9122713 ^ z) & Integer.MAX_VALUE;
    }

    private static Integer invokeMcPatcherInt(Method method, Entity entity) {
        if (method == null || entity == null) {
            return null;
        }
        try {
            Object value = method.invoke(entity);
            return value instanceof Integer ? (Integer) value : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static Integer readMcPatcherIntField(Field valueField, Entity entity, Field initializedField) {
        if (valueField == null || entity == null || !isMcPatcherFieldInitialized(initializedField, entity)) {
            return null;
        }
        try {
            return Integer.valueOf(valueField.getInt(entity));
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static BiomeGenBase readMcPatcherBiomeField(Field field, Entity entity, Field initializedField) {
        if (field == null || entity == null || !isMcPatcherFieldInitialized(initializedField, entity)) {
            return null;
        }
        try {
            Object biome = field.get(entity);
            return biome instanceof BiomeGenBase ? (BiomeGenBase) biome : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static boolean isMcPatcherFieldInitialized(Field initializedField, Entity entity) {
        if (initializedField == null || entity == null) {
            return false;
        }
        try {
            return initializedField.getBoolean(entity);
        } catch (Throwable ignored) {
            return false;
        }
    }

    private static int mcpIntHash(int value) {
        value = 61 ^ value ^ value >> 16;
        value = value + (value << 3);
        value = value ^ value >> 4;
        value = value * 668265261;
        return value ^ value >> 15;
    }

    @Override
    public boolean onItemUse(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ) {
        int spawnX = x + offsetX(side);
        int spawnY = y + offsetY(side);
        int spawnZ = z + offsetZ(side);
        boolean released = releaseCapturedEntity(stack, world, (double)spawnX + 0.5D, (double)spawnY, (double)spawnZ + 0.5D, player);
        if (released && !world.isRemote && player != null && !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return released;
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null) {
            return stack;
        }
        double x = player.posX - Math.sin(Math.toRadians(player.rotationYaw)) * 1.5D;
        double z = player.posZ + Math.cos(Math.toRadians(player.rotationYaw)) * 1.5D;
        double y = player.posY;
        boolean released = releaseCapturedEntity(stack, world, x, y, z, player);
        if (released && !world.isRemote && !player.capabilities.isCreativeMode) {
            --stack.stackSize;
        }
        return stack;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void registerIcons(IIconRegister iconRegister) {
        Item enderChest = Item.getItemFromBlock(Blocks.ender_chest);
        this.enderChestIcon = enderChest == null ? null : enderChest.getIconFromDamage(0);
        this.itemIcon = this.enderChestIcon;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public IIcon getIconFromDamage(int damage) {
        if (this.enderChestIcon != null) {
            return this.enderChestIcon;
        }
        Item enderChest = Item.getItemFromBlock(Blocks.ender_chest);
        return enderChest == null ? this.itemIcon : enderChest.getIconFromDamage(0);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public boolean hasEffect(ItemStack stack, int pass) {
        return hasCapturedEntity(stack) || super.hasEffect(stack, pass);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        if (stack != null && stack.hasTagCompound() && stack.getTagCompound().hasKey(TAG_ENTITY_NAME)) {
            list.add("Contains: " + stack.getTagCompound().getString(TAG_ENTITY_NAME));
        }
    }

    private static int offsetX(int side) {
        return side == 4 ? -1 : side == 5 ? 1 : 0;
    }

    private static int offsetY(int side) {
        return side == 0 ? -1 : side == 1 ? 1 : 0;
    }

    private static int offsetZ(int side) {
        return side == 2 ? -1 : side == 3 ? 1 : 0;
    }
}
