package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;
import net.minecraft.world.World;
import zairus.worldexplorer.archery.entity.EntityBoomerang;
import zairus.worldexplorer.core.items.WEItem;

public class Boomerang extends WEItem {
    private static final float BOOMERANG_VOLUME_SCALE = 0.66f;
    private static final float BASE_DAMAGE = 2.5f;
    private static final double BASE_REACH = 16.0;
    private static final int BASE_CAPACITY = 1;
    private static final float PERCENT_TO_FRACTION = 0.01f;
    private static final float MODIFIER_EPSILON = 1.0E-4f;
    private static final int MODIFIER_DATA_VERSION = 3;
    private static final String MODIFIER_DATA_VERSION_KEY = "riftflux_boomerang_modifier_version";
    private static final String STAR_INFUSED_BONUS_KEY = "riftflux_boomerang_star_infused";
    private static final String LEGACY_NETHER_STAR_BOOST_KEY = "riftflux_boomerang_nether_star_boost";
    private static final String LEGACY_MODIFIER_COUNT_SUFFIX = " Count";
    private static final WEItem.ImprovementType[] ALL_MODIFIER_TYPES = new WEItem.ImprovementType[]{
            WEItem.ImprovementType.IMPACT,
            WEItem.ImprovementType.ADHERENCE,
            WEItem.ImprovementType.POWER,
            WEItem.ImprovementType.ENERGETIC,
            WEItem.ImprovementType.ENDER,
            WEItem.ImprovementType.UNBREAKING
    };
    private static final WEItem.ImprovementType[] COMBINED_PERCENT_MODIFIERS = new WEItem.ImprovementType[]{
            WEItem.ImprovementType.IMPACT,
            WEItem.ImprovementType.POWER,
            WEItem.ImprovementType.ENERGETIC,
            WEItem.ImprovementType.ENDER,
            WEItem.ImprovementType.UNBREAKING
    };
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconGunpowder;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconSlime;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconGlowstone;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconRedstone;
    @SideOnly(value=Side.CLIENT)
    protected IIcon iconEnder;

    public Boomerang() {
        this.setMaxStackSize(1);
        this.setUnlocalizedName("boomerang");
        this.setTextureName("worldexplorer:boomerang");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.setFull3D();
        this.setMaxDamage(Math.max(0, ModConfig.riftExplorerBoomerangDurability));
    }

    @Override
    public void addImprovements() {
        this.itemImprovements.clear();
        this.addConfiguredImprovements(ModConfig.riftExplorerBoomerangImpactModifierItems, WEItem.ImprovementType.IMPACT);
        this.addConfiguredImprovements(ModConfig.riftExplorerBoomerangCapacityModifierItems, WEItem.ImprovementType.ADHERENCE);
        this.addConfiguredImprovements(ModConfig.riftExplorerBoomerangPowerModifierItems, WEItem.ImprovementType.POWER);
        this.addConfiguredImprovements(ModConfig.riftExplorerBoomerangReachModifierItems, WEItem.ImprovementType.ENERGETIC);
        this.addConfiguredImprovements(ModConfig.riftExplorerBoomerangEnderModifierItems, WEItem.ImprovementType.ENDER);
        this.addConfiguredImprovements(ModConfig.riftExplorerBoomerangUnbreakingModifierItems, WEItem.ImprovementType.UNBREAKING);
    }

    private void addConfiguredImprovements(String[] entries, WEItem.ImprovementType type) {
        if (entries == null || type == null) {
            return;
        }
        Set<Item> seen = new HashSet<Item>();
        for (int i = 0; i < entries.length; i++) {
            ConfiguredImprovementEntry entry = this.parseConfiguredImprovementEntry(entries[i], type);
            if (entry == null || entry.item == null || entry.value <= 0.0f || !seen.add(entry.item)) {
                continue;
            }
            this.itemImprovements.add(new WEItem.Improvement(entry.item, type, entry.value));
        }
    }

    private ConfiguredImprovementEntry parseConfiguredImprovementEntry(String raw, WEItem.ImprovementType type) {
        if (raw == null || type == null) {
            return null;
        }
        String trimmed = raw.trim();
        if (trimmed.isEmpty()) {
            return null;
        }
        String[] split = trimmed.split("\\|", 2);
        if (split.length < 2) {
            return null;
        }
        Item item = this.resolveConfiguredItem(split[0].trim());
        float value = this.parseConfiguredImprovementValue(type, split[1].trim());
        if (item == null || value <= 0.0f) {
            return null;
        }
        return new ConfiguredImprovementEntry(item, value);
    }

    private float parseConfiguredImprovementValue(WEItem.ImprovementType type, String raw) {
        if (type == WEItem.ImprovementType.ADHERENCE) {
            return (float)this.parseCapacityStackValue(raw);
        }
        if (type == WEItem.ImprovementType.ENERGETIC) {
            return this.parseReachPercentValue(raw);
        }
        return this.parsePercent(raw);
    }

    private Item resolveConfiguredItem(String raw) {
        if (raw == null) {
            return null;
        }
        String itemId = raw.trim();
        if (itemId.isEmpty()) {
            return null;
        }
        int at = itemId.lastIndexOf('@');
        if (at >= 0) {
            itemId = itemId.substring(0, at).trim();
        }
        int colon = itemId.indexOf(':');
        if (colon <= 0 || colon >= itemId.length() - 1) {
            return null;
        }
        return GameRegistry.findItem(itemId.substring(0, colon), itemId.substring(colon + 1));
    }

    private float parsePercent(String raw) {
        try {
            return this.roundPercentValue(new BigDecimal(raw.trim()).setScale(4, RoundingMode.HALF_UP).floatValue());
        }
        catch (Exception ignored) {
            return 0.0f;
        }
    }

    private int parseCapacityStackValue(String raw) {
        try {
            BigDecimal value = new BigDecimal(raw.trim()).stripTrailingZeros();
            if (value.scale() > 0) {
                return 0;
            }
            return Math.max(0, value.intValueExact());
        }
        catch (Exception ignored) {
            return 0;
        }
    }

    private float parseReachPercentValue(String raw) {
        try {
            BigDecimal blocks = new BigDecimal(raw.trim()).setScale(4, RoundingMode.HALF_UP);
            if (blocks.compareTo(BigDecimal.ZERO) <= 0 || BASE_REACH <= 0.0) {
                return 0.0f;
            }
            return this.roundPercentValue((float)(blocks.doubleValue() / BASE_REACH * 100.0));
        }
        catch (Exception ignored) {
            return 0.0f;
        }
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        this.normalizeModifierData(stack);
        if (!this.shouldPreventDurabilityLoss(stack, player)) {
            stack.damageItem(1, (EntityLivingBase)player);
        }
        EntityBoomerang entityBoomerang = new EntityBoomerang(world, (EntityLivingBase)player, 1.0f);
        float impactPercent = this.getModifierPercent(stack, WEItem.ImprovementType.IMPACT);
        float powerPercent = this.getModifierPercent(stack, WEItem.ImprovementType.POWER);
        float reachPercent = this.getModifierPercent(stack, WEItem.ImprovementType.ENERGETIC);
        float enderPercent = this.getModifierPercent(stack, WEItem.ImprovementType.ENDER);
        float unbreakingPercent = this.getModifierPercent(stack, WEItem.ImprovementType.UNBREAKING);
        entityBoomerang.setReach(BASE_REACH + BASE_REACH * (double)this.toFraction(reachPercent));
        entityBoomerang.setInventoryCapacity(this.getTotalCapacityStacks(stack));
        entityBoomerang.setDamage((double)(BASE_DAMAGE + BASE_DAMAGE * this.toFraction(powerPercent)));
        entityBoomerang.setKnockbackStrength(impactPercent * PERCENT_TO_FRACTION);
        entityBoomerang.setInstantReturnChance(enderPercent * PERCENT_TO_FRACTION);
        entityBoomerang.setDurabilityPreservationChance(unbreakingPercent * PERCENT_TO_FRACTION);
        if (EnchantmentHelper.getEnchantmentLevel((int)Enchantment.flame.effectId, (ItemStack)stack) > 0) {
            entityBoomerang.setFire(100);
        }
        if (!world.isRemote) {
            world.playSoundAtEntity((Entity)player, "random.bow", 1.0f * BOOMERANG_VOLUME_SCALE, 1.0f / (itemRand.nextFloat() * 0.4f + 1.2f) + 0.5f);
        }
        entityBoomerang.setThrownBoomerang(player.inventory.getStackInSlot(player.inventory.currentItem).copy());
        entityBoomerang.setThrownFromSlot(player.inventory.currentItem);
        if (player.capabilities.isCreativeMode) {
            player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
        } else {
            --stack.stackSize;
        }
        if (!world.isRemote) {
            world.spawnEntityInWorld((Entity)entityBoomerang);
        }
        return stack;
    }

    public int getItemEnchantability() {
        return 1;
    }

    public EnumAction getItemUseAction(ItemStack stack) {
        return EnumAction.block;
    }

    @SideOnly(value=Side.CLIENT)
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p_77624_4_) {
        float impactPercent = this.getModifierPercent(stack, WEItem.ImprovementType.IMPACT);
        int capacityBonusStacks = this.getCapacityBonusStacks(stack);
        float powerPercent = this.getModifierPercent(stack, WEItem.ImprovementType.POWER);
        float reachPercent = this.getModifierPercent(stack, WEItem.ImprovementType.ENERGETIC);
        float enderPercent = this.getModifierPercent(stack, WEItem.ImprovementType.ENDER);
        float unbreakingPercent = this.getModifierPercent(stack, WEItem.ImprovementType.UNBREAKING);
        if (impactPercent > 0.0f) {
            list.add("Impact Modifier +" + this.formatPercentValue(impactPercent) + "%");
        }
        if (capacityBonusStacks > 0) {
            list.add("Capacity Modifier +" + capacityBonusStacks + " " + this.stackWord(capacityBonusStacks));
        }
        if (powerPercent > 0.0f) {
            list.add("Power Modifier +" + this.formatPercentValue(powerPercent) + "%");
        }
        if (reachPercent > 0.0f) {
            list.add("Reach Modifier +" + this.formatPercentValue(reachPercent) + "%");
        }
        if (enderPercent > 0.0f) {
            list.add("Ender Modifier +" + this.formatPercentValue(enderPercent) + "%");
        }
        if (unbreakingPercent > 0.0f) {
            list.add("Unbreaking Modifier +" + this.formatPercentValue(unbreakingPercent) + "%");
        }
        if (this.hasStarInfusedBonus(stack) && this.isStarInfusionEnabled()) {
            list.add("Star-Infused");
            if (this.hasStarInfusedSlotBonusConfig()) {
                int bonus = this.getConfiguredStarInfusedBonusModifierTypes();
                list.add("+" + bonus + " Modifier " + this.slotWord(bonus));
            }
            if (this.hasStarInfusedPercentBoostConfig()) {
                list.add("+" + this.formatPercentValue(this.getConfiguredStarInfusedBonusPercent()) + "% Modifiers");
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconregister) {
        this.itemIcon = iconregister.registerIcon(this.getIconString());
        this.iconGunpowder = iconregister.registerIcon(this.getIconString() + "_gunpowder");
        this.iconSlime = iconregister.registerIcon(this.getIconString() + "_slime");
        this.iconGlowstone = iconregister.registerIcon(this.getIconString() + "_glowstone");
        this.iconRedstone = iconregister.registerIcon(this.getIconString() + "_redstone");
        this.iconEnder = iconregister.registerIcon(this.getIconString() + "_ender");
    }

    public IIcon getModifierIconLayer(String layer) {
        IIcon iconLayer = null;
        switch (layer) {
            case "redstone": {
                iconLayer = this.iconRedstone;
                break;
            }
            case "glowstone": {
                iconLayer = this.iconGlowstone;
                break;
            }
            case "gunpowder": {
                iconLayer = this.iconGunpowder;
                break;
            }
            case "slime": {
                iconLayer = this.iconSlime;
                break;
            }
            case "ender": {
                iconLayer = this.iconEnder;
            }
        }
        return iconLayer;
    }

    public ItemStack applyImprovement(ItemStack stack, WEItem.Improvement improvement) {
        if (stack == null || improvement == null || improvement.improvementType == null || improvement.valuePerUnit <= 0.0f) {
            return null;
        }
        this.normalizeModifierData(stack);
        WEItem.ImprovementType type = improvement.improvementType;
        if (!this.canApplyModifierType(stack, type)) {
            return null;
        }
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (type == WEItem.ImprovementType.ADHERENCE) {
            int currentStacks = this.getCapacityBonusStacks(stack);
            int additionalStacks = this.roundCapacityStackValue(improvement.valuePerUnit);
            int maxStacks = this.getEffectiveMaxCapacityBonusStacks(stack);
            int newStacks = currentStacks + additionalStacks;
            if (maxStacks > 0) {
                newStacks = Math.min(maxStacks, newStacks);
            }
            if (newStacks <= currentStacks) {
                return null;
            }
            tag.setInteger(type.getKey(), newStacks);
        } else {
            float currentPercent = this.getModifierPercent(stack, type);
            float newPercent = currentPercent + this.roundPercentValue(improvement.valuePerUnit);
            float individualMax = this.getEffectiveMaxPercent(type, stack);
            if (individualMax > 0.0f) {
                newPercent = Math.min(individualMax, newPercent);
            }
            float combinedCapMax = this.getMaxAllowedPercentForTypeByCombinedCap(stack, type, currentPercent);
            if (combinedCapMax >= 0.0f) {
                newPercent = Math.min(combinedCapMax, newPercent);
            }
            newPercent = this.clampStoredPercent(type, newPercent);
            if (newPercent <= currentPercent + MODIFIER_EPSILON) {
                return null;
            }
            tag.setFloat(type.getKey(), newPercent);
        }
        tag.setInteger(MODIFIER_DATA_VERSION_KEY, MODIFIER_DATA_VERSION);
        return stack;
    }

    public ItemStack applyStarInfusedBoost(ItemStack stack) {
        if (stack == null || !this.isStarInfusionEnabled()) {
            return null;
        }
        this.normalizeModifierData(stack);
        if (this.hasStarInfusedBonus(stack)) {
            return null;
        }
        if (stack.getTagCompound() == null) {
            stack.setTagCompound(new NBTTagCompound());
        }
        NBTTagCompound tag = stack.getTagCompound();
        tag.setBoolean(STAR_INFUSED_BONUS_KEY, true);
        tag.removeTag(LEGACY_NETHER_STAR_BOOST_KEY);
        tag.setInteger(MODIFIER_DATA_VERSION_KEY, MODIFIER_DATA_VERSION);
        return stack;
    }

    public ItemStack applyNetherStarBoost(ItemStack stack) {
        return this.applyStarInfusedBoost(stack);
    }

    public boolean hasStarInfusedBonus(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return false;
        }
        NBTTagCompound tag = stack.getTagCompound();
        return tag.getBoolean(STAR_INFUSED_BONUS_KEY) || tag.getBoolean(LEGACY_NETHER_STAR_BOOST_KEY);
    }

    public boolean hasNetherStarBonus(ItemStack stack) {
        return this.hasStarInfusedBonus(stack);
    }

    public boolean isStarInfusionEnabled() {
        return this.hasConfiguredStarInfusedItems() && (this.hasStarInfusedSlotBonusConfig() || this.hasStarInfusedPercentBoostConfig());
    }

    public boolean isNetherStarBoostEnabled() {
        return this.isStarInfusionEnabled();
    }

    public boolean isStarInfusedMaterial(ItemStack stack) {
        if (stack == null || stack.getItem() == null) {
            return false;
        }
        String[] configured = ModConfig.riftExplorerBoomerangStarInfusedItems;
        if (configured == null) {
            return false;
        }
        for (int i = 0; i < configured.length; i++) {
            if (this.matchesConfiguredItemStack(stack, configured[i])) {
                return true;
            }
        }
        return false;
    }

    public float getModifierPercent(ItemStack stack, WEItem.ImprovementType type) {
        if (stack == null || !stack.hasTagCompound() || type == null || type == WEItem.ImprovementType.ADHERENCE) {
            return 0.0f;
        }
        return this.readModifierPercent(stack.getTagCompound(), type);
    }

    public int getCapacityBonusStacks(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return 0;
        }
        return this.readCapacityBonusStacks(stack.getTagCompound());
    }

    public int getTotalCapacityStacks(ItemStack stack) {
        return BASE_CAPACITY + this.getCapacityBonusStacks(stack);
    }

    private float readModifierPercent(NBTTagCompound tag, WEItem.ImprovementType type) {
        if (tag == null || type == null || type == WEItem.ImprovementType.ADHERENCE || !tag.hasKey(type.getKey(), 99)) {
            return 0.0f;
        }
        float stored = Math.max(0.0f, tag.getFloat(type.getKey()));
        int version = tag.getInteger(MODIFIER_DATA_VERSION_KEY);
        if (version >= MODIFIER_DATA_VERSION) {
            return this.clampStoredPercent(type, stored);
        }
        if (version >= 2) {
            return this.clampStoredPercent(type, stored);
        }
        return this.clampStoredPercent(type, this.convertLegacyStoredValueToPercent(type, stored));
    }

    private int readCapacityBonusStacks(NBTTagCompound tag) {
        if (tag == null || !tag.hasKey(WEItem.ImprovementType.ADHERENCE.getKey(), 99)) {
            return 0;
        }
        float stored = Math.max(0.0f, tag.getFloat(WEItem.ImprovementType.ADHERENCE.getKey()));
        int version = tag.getInteger(MODIFIER_DATA_VERSION_KEY);
        if (version >= MODIFIER_DATA_VERSION) {
            return Math.max(0, this.roundCapacityStackValue(stored));
        }
        if (version >= 2) {
            return this.convertCapacityPercentToBonusStacks(stored);
        }
        return Math.max(0, (int)Math.floor((double)stored + (double)MODIFIER_EPSILON));
    }

    private void normalizeModifierData(ItemStack stack) {
        if (stack == null || !stack.hasTagCompound()) {
            return;
        }
        NBTTagCompound tag = stack.getTagCompound();
        if (tag.getInteger(MODIFIER_DATA_VERSION_KEY) >= MODIFIER_DATA_VERSION) {
            if (tag.hasKey(WEItem.ImprovementType.ENDER.getKey(), 99)) {
                tag.setFloat(WEItem.ImprovementType.ENDER.getKey(), this.clampStoredPercent(WEItem.ImprovementType.ENDER, tag.getFloat(WEItem.ImprovementType.ENDER.getKey())));
            }
            if (tag.hasKey(WEItem.ImprovementType.UNBREAKING.getKey(), 99)) {
                tag.setFloat(WEItem.ImprovementType.UNBREAKING.getKey(), this.clampStoredPercent(WEItem.ImprovementType.UNBREAKING, tag.getFloat(WEItem.ImprovementType.UNBREAKING.getKey())));
            }
            return;
        }

        for (int i = 0; i < ALL_MODIFIER_TYPES.length; i++) {
            WEItem.ImprovementType type = ALL_MODIFIER_TYPES[i];
            if (!tag.hasKey(type.getKey(), 99)) {
                continue;
            }
            if (type == WEItem.ImprovementType.ADHERENCE) {
                tag.setInteger(type.getKey(), this.readCapacityBonusStacks(tag));
            } else {
                tag.setFloat(type.getKey(), this.readModifierPercent(tag, type));
            }
            tag.removeTag(type.getKey() + LEGACY_MODIFIER_COUNT_SUFFIX);
        }
        tag.setInteger(MODIFIER_DATA_VERSION_KEY, MODIFIER_DATA_VERSION);
    }

    private boolean canApplyModifierType(ItemStack stack, WEItem.ImprovementType type) {
        if (type == null) {
            return false;
        }
        int limit = this.getEffectiveModifierTypeLimit(stack);
        if (limit <= 0 || this.isModifierActive(stack, type)) {
            return true;
        }
        return this.getActiveModifierTypeCount(stack) < limit;
    }

    private boolean isModifierActive(ItemStack stack, WEItem.ImprovementType type) {
        if (type == WEItem.ImprovementType.ADHERENCE) {
            return this.getCapacityBonusStacks(stack) > 0;
        }
        return this.getModifierPercent(stack, type) > MODIFIER_EPSILON;
    }

    private int getActiveModifierTypeCount(ItemStack stack) {
        int count = 0;
        for (int i = 0; i < ALL_MODIFIER_TYPES.length; i++) {
            if (this.isModifierActive(stack, ALL_MODIFIER_TYPES[i])) {
                count++;
            }
        }
        return count;
    }

    private float getCombinedModifierPercent(ItemStack stack) {
        float total = 0.0f;
        for (int i = 0; i < COMBINED_PERCENT_MODIFIERS.length; i++) {
            total += this.getModifierPercent(stack, COMBINED_PERCENT_MODIFIERS[i]);
        }
        return this.roundPercentValue(total);
    }

    private float getMaxAllowedPercentForTypeByCombinedCap(ItemStack stack, WEItem.ImprovementType type, float currentPercent) {
        if (type == null || !this.usesCombinedPercentCap(type)) {
            return -1.0f;
        }
        float combinedCap = this.getEffectiveMaxCombinedModifierPercent(stack);
        if (combinedCap <= 0.0f) {
            return -1.0f;
        }
        float otherTotal = Math.max(0.0f, this.getCombinedModifierPercent(stack) - currentPercent);
        return this.roundPercentValue(Math.max(currentPercent, combinedCap - otherTotal));
    }

    private boolean usesCombinedPercentCap(WEItem.ImprovementType type) {
        return type != null && type != WEItem.ImprovementType.ADHERENCE;
    }

    private float convertLegacyStoredValueToPercent(WEItem.ImprovementType type, float storedValue) {
        switch (type) {
            case IMPACT:
            case ENDER:
            case UNBREAKING: {
                return storedValue * 100.0f;
            }
            case POWER: {
                return BASE_DAMAGE <= 0.0f ? 0.0f : storedValue / BASE_DAMAGE * 100.0f;
            }
            case ENERGETIC: {
                return BASE_REACH <= 0.0 ? 0.0f : (float)(storedValue / BASE_REACH * 100.0);
            }
        }
        return storedValue;
    }

    private int convertCapacityPercentToBonusStacks(float percent) {
        if (percent <= 0.0f) {
            return 0;
        }
        return Math.max(0, (int)Math.floor((double)((float)BASE_CAPACITY * this.toFraction(percent)) + (double)MODIFIER_EPSILON));
    }

    private int getEffectiveModifierTypeLimit(ItemStack stack) {
        int limit = this.getConfiguredMaxModifierTypes();
        if (limit <= 0) {
            return 0;
        }
        if (this.hasStarInfusedBonus(stack) && this.hasStarInfusedSlotBonusConfig()) {
            limit += this.getConfiguredStarInfusedBonusModifierTypes();
        }
        return limit;
    }

    private int getConfiguredMaxModifierTypes() {
        return Math.max(0, ModConfig.riftExplorerBoomerangMaxModifierTypes);
    }

    private int getConfiguredStarInfusedBonusModifierTypes() {
        return Math.max(0, ModConfig.riftExplorerBoomerangStarInfusedBonusModifierTypes);
    }

    private int getEffectiveMaxCapacityBonusStacks(ItemStack stack) {
        int maxStacks = Math.max(0, ModConfig.riftExplorerBoomerangCapacityModifierMaxStacks);
        if (maxStacks <= 0 || !this.hasStarInfusedBonus(stack) || !this.hasStarInfusedPercentBoostConfig()) {
            return maxStacks;
        }
        return this.scaleIntegerCap(maxStacks, this.getConfiguredStarInfusedBonusPercent());
    }

    private float getEffectiveMaxCombinedModifierPercent(ItemStack stack) {
        if (ModConfig.riftExplorerBoomerangMaxCombinedModifierPercent <= 0.0f) {
            return 0.0f;
        }
        float maxPercent = ModConfig.riftExplorerBoomerangMaxCombinedModifierPercent;
        if (this.hasStarInfusedBonus(stack) && this.hasStarInfusedPercentBoostConfig()) {
            maxPercent *= 1.0f + this.toFraction(this.getConfiguredStarInfusedBonusPercent());
        }
        return this.roundPercentValue(maxPercent);
    }

    private float getEffectiveMaxPercent(WEItem.ImprovementType type, ItemStack stack) {
        float configuredCap = 0.0f;
        switch (type) {
            case IMPACT: {
                configuredCap = ModConfig.riftExplorerBoomerangImpactModifierMaxPercent;
                break;
            }
            case POWER: {
                configuredCap = ModConfig.riftExplorerBoomerangPowerModifierMaxPercent;
                break;
            }
            case ENERGETIC: {
                configuredCap = ModConfig.riftExplorerBoomerangReachModifierMaxPercent;
                break;
            }
            case ENDER: {
                configuredCap = ModConfig.riftExplorerBoomerangEnderModifierMaxPercent;
                return this.getChancePercentCap(this.applyStarInfusedPercentBoost(configuredCap, stack));
            }
            case UNBREAKING: {
                configuredCap = ModConfig.riftExplorerBoomerangUnbreakingModifierMaxPercent;
                return this.getChancePercentCap(this.applyStarInfusedPercentBoost(configuredCap, stack));
            }
        }
        return this.getConfiguredPercentCap(this.applyStarInfusedPercentBoost(configuredCap, stack));
    }

    private float getConfiguredPercentCap(float configuredCap) {
        if (configuredCap <= 0.0f) {
            return 0.0f;
        }
        return this.roundPercentValue(configuredCap);
    }

    private float getChancePercentCap(float configuredCap) {
        float cap = configuredCap <= 0.0f ? 100.0f : configuredCap;
        return this.roundPercentValue(Math.min(100.0f, cap));
    }

    private float applyStarInfusedPercentBoost(float configuredCap, ItemStack stack) {
        if (configuredCap <= 0.0f || !this.hasStarInfusedBonus(stack) || !this.hasStarInfusedPercentBoostConfig()) {
            return configuredCap;
        }
        return configuredCap * (1.0f + this.toFraction(this.getConfiguredStarInfusedBonusPercent()));
    }

    private boolean hasStarInfusedSlotBonusConfig() {
        return this.getConfiguredMaxModifierTypes() > 0 && this.getConfiguredStarInfusedBonusModifierTypes() > 0;
    }

    private boolean hasStarInfusedPercentBoostConfig() {
        return this.getConfiguredStarInfusedBonusPercent() > 0.0f;
    }

    private float getConfiguredStarInfusedBonusPercent() {
        return this.roundPercentValue(Math.max(0.0f, ModConfig.riftExplorerBoomerangStarInfusedBonusPercent));
    }

    private boolean hasConfiguredStarInfusedItems() {
        String[] configured = ModConfig.riftExplorerBoomerangStarInfusedItems;
        if (configured == null || configured.length == 0) {
            return false;
        }
        for (int i = 0; i < configured.length; i++) {
            if (configured[i] != null && !configured[i].trim().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private boolean matchesConfiguredItemStack(ItemStack stack, String raw) {
        if (stack == null || stack.getItem() == null || raw == null) {
            return false;
        }
        String configured = raw.trim();
        if (configured.isEmpty()) {
            return false;
        }
        int meta = 32767;
        int lastColon = configured.lastIndexOf(':');
        int firstColon = configured.indexOf(':');
        if (lastColon > firstColon && lastColon + 1 < configured.length()) {
            String suffix = configured.substring(lastColon + 1).trim();
            if ("*".equals(suffix)) {
                meta = 32767;
                configured = configured.substring(0, lastColon).trim();
            } else {
                try {
                    meta = Integer.parseInt(suffix);
                    configured = configured.substring(0, lastColon).trim();
                }
                catch (NumberFormatException ignored) {
                    meta = 32767;
                }
            }
        }
        Item item = this.resolveConfiguredItem(configured);
        if (item == null || stack.getItem() != item) {
            return false;
        }
        return meta == 32767 || stack.getItemDamage() == meta || stack.isItemStackDamageable();
    }

    private int scaleIntegerCap(int baseValue, float bonusPercent) {
        if (baseValue <= 0 || bonusPercent <= 0.0f) {
            return baseValue;
        }
        return BigDecimal.valueOf((double)baseValue)
                .multiply(BigDecimal.valueOf(1.0 + (double)this.toFraction(bonusPercent)))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();
    }

    private float clampStoredPercent(WEItem.ImprovementType type, float value) {
        float clamped = this.roundPercentValue(Math.max(0.0f, value));
        if (type == WEItem.ImprovementType.ENDER || type == WEItem.ImprovementType.UNBREAKING) {
            clamped = Math.min(100.0f, clamped);
        }
        return clamped;
    }

    private boolean shouldPreventDurabilityLoss(ItemStack stack, EntityLivingBase holder) {
        if (stack == null || holder == null || stack.getMaxDamage() <= 0) {
            return true;
        }
        float chance = this.getModifierPercent(stack, WEItem.ImprovementType.UNBREAKING);
        return chance > 0.0f && holder.getRNG().nextFloat() < this.toFraction(chance);
    }

    private int roundCapacityStackValue(float value) {
        return Math.max(0, (int)Math.floor((double)value + (double)MODIFIER_EPSILON));
    }

    private float toFraction(float percent) {
        return percent * PERCENT_TO_FRACTION;
    }

    private float roundPercentValue(float value) {
        return BigDecimal.valueOf((double)value).setScale(4, RoundingMode.HALF_UP).floatValue();
    }

    private String formatPercentValue(float value) {
        return BigDecimal.valueOf((double)this.roundPercentValue(value)).stripTrailingZeros().toPlainString();
    }

    private String stackWord(int stackCount) {
        return stackCount == 1 ? "stack" : "stacks";
    }

    private String slotWord(int slotCount) {
        return slotCount == 1 ? "Slot" : "Slots";
    }

    private static final class ConfiguredImprovementEntry {
        private final Item item;
        private final float value;

        private ConfiguredImprovementEntry(Item item, float value) {
            this.item = item;
            this.value = value;
        }
    }
}
