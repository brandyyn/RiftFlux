package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.GameData;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.oredict.OreDictionary;

public final class SlingshotAmmoHelper {
    private static final int ANY_META = OreDictionary.WILDCARD_VALUE;
    private static final String ACTION_CAPTURE = "capture";
    private static final String ACTION_RELEASE = "release";
    private static final String ACTION_TELEPORT = "teleport";
    private static final String ACTION_ENDER_PEARL = "enderpearl";
    private static final String ACTION_SWAP = "swap";

    private SlingshotAmmoHelper() {
    }

    public static boolean hasAmmo(EntityPlayer player) {
        return peekAmmo(player) != null;
    }

    public static AmmoSelection peekAmmo(EntityPlayer player) {
        return findAmmoSelection(player, false);
    }

    public static AmmoSelection consumeOneAmmo(EntityPlayer player) {
        return findAmmoSelection(player, true);
    }

    public static boolean isValidAmmoStack(ItemStack stack) {
        return stack != null && stack.getItem() != null && stack.stackSize > 0
                && !isDisabledAmmoStack(stack)
                && (matchesExactAmmo(stack) || matchesOreDictionaryAmmo(stack) || getSpecialBehavior(stack) != null);
    }

    public static boolean isDisabledAmmoStack(ItemStack stack) {
        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
            return false;
        }
        String[] configured = ModConfig.riftExplorerSlingshotDisabledAmmoItems;
        if (configured == null || configured.length == 0) {
            return false;
        }
        for (int i = 0; i < configured.length; i++) {
            if (matchesConfiguredAmmoMatcher(stack, configured[i])) {
                return true;
            }
        }
        return false;
    }

    public static boolean isPebbleAllowed() {
        return WEArcheryItems.pebble != null && isValidAmmoStack(new ItemStack(WEArcheryItems.pebble, 1, 0));
    }

    public static boolean isCobblestoneAllowed() {
        return isValidAmmoStack(new ItemStack(Blocks.cobblestone, 1, 0));
    }

    public static ItemStack defaultPickupStack() {
        ItemStack configured = firstExactAmmoStack();
        if (configured != null) {
            return configured;
        }
        return new ItemStack(Blocks.cobblestone, 1, 0);
    }

    public static SpecialAmmoBehavior getSpecialBehavior(ItemStack stack) {
        if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
            return null;
        }
        if (isDisabledAmmoStack(stack)) {
            return null;
        }
        if (stack.getItem() == WEArcheryItems.captured_ender_chest && CapturedEnderChestItem.hasCapturedEntity(stack)) {
            return new SpecialAmmoBehavior(0.0D, 0.0f, 0.0f, ACTION_RELEASE);
        }
        List<SpecialAmmoEntry> entries = getSpecialAmmoEntries();
        for (int i = 0; i < entries.size(); i++) {
            SpecialAmmoEntry entry = entries.get(i);
            if (entry.matches(stack)) {
                return entry.behavior;
            }
        }
        return null;
    }

    public static void addSlingshotAmmoTooltip(ItemStack stack, List tooltip) {
        if (tooltip == null || !isValidAmmoStack(stack)) {
            return;
        }

        addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Slingshot Ammo");
        SpecialAmmoBehavior behavior = getSpecialBehavior(stack);
        if (behavior == null) {
            if (ModConfig.riftExplorerSlingshotBaseDamage > 0.0F) {
                addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Base damage: " + formatNumber(ModConfig.riftExplorerSlingshotBaseDamage) + " before speed/enchantments/blessings");
            }
            return;
        }

        if (behavior.getDamage() > 0.0D) {
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Damage: " + formatNumber(behavior.getDamage()) + " before enchantments/blessings");
        }
        if (behavior.getKnockbackStrength() > 0.0f) {
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Knockback: " + formatNumber(behavior.getKnockbackStrength()));
        }
        if (behavior.isExplosive()) {
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Explodes on impact: strength " + formatNumber(behavior.getExplosionStrength()));
        }
        if (behavior.isCapture()) {
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Captures hit mobs into a Rift Chest");
            if (ModConfig.riftExplorerSlingshotBlockBossCapture) {
                addTooltipLine(tooltip, EnumChatFormatting.DARK_GRAY + "Bosses cannot be captured");
            }
        } else if (behavior.isRelease()) {
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Releases the stored mob on impact");
        } else if (behavior.isShooterTeleport()) {
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Teleports you to where it lands");
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Mob hit: swaps places with the target");
        } else if (behavior.isTeleport()) {
            addTooltipLine(tooltip, EnumChatFormatting.GRAY + "Randomly teleports hit mobs");
        }
    }

    private static AmmoSelection findAmmoSelection(EntityPlayer player, boolean consume) {
        if (player == null || player.inventory == null || player.inventory.mainInventory == null) {
            return null;
        }
        int[] searchOrder = getAmmoSearchOrder(player);
        for (int orderIndex = 0; orderIndex < searchOrder.length; orderIndex++) {
            int i = searchOrder[orderIndex];
            ItemStack stack = player.inventory.mainInventory[i];
            if (!isValidAmmoStack(stack)) {
                continue;
            }
            ItemStack selected = stack.copy();
            selected.stackSize = 1;
            if (consume) {
                stack.stackSize--;
                if (stack.stackSize <= 0) {
                    player.inventory.mainInventory[i] = null;
                }
                player.inventory.markDirty();
            }
            return new AmmoSelection(selected, getSpecialBehavior(selected));
        }
        return null;
    }

    private static int[] getAmmoSearchOrder(EntityPlayer player) {
        int inventorySize = player == null || player.inventory == null || player.inventory.mainInventory == null
                ? 0
                : player.inventory.mainInventory.length;
        int[] order = new int[inventorySize];
        if (inventorySize <= 0) {
            return order;
        }

        int index = 0;
        int hotbarSize = Math.min(9, inventorySize);
        int heldSlot = player.inventory.currentItem;
        if (heldSlot < 0 || heldSlot >= hotbarSize) {
            heldSlot = 0;
        }

        for (int offset = 1; offset < hotbarSize; offset++) {
            order[index++] = (heldSlot + offset) % hotbarSize;
        }

        for (int slot = hotbarSize; slot < inventorySize; slot++) {
            order[index++] = slot;
        }

        order[index] = heldSlot;
        return order;
    }

    private static boolean matchesExactAmmo(ItemStack stack) {
        for (AmmoEntry entry : getExactAmmoEntries()) {
            if (entry.matches(stack)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesOreDictionaryAmmo(ItemStack stack) {
        if (!ModConfig.riftExplorerSlingshotEnableOreDictionaryAmmo || stack == null || stack.getItem() == null || stack.stackSize <= 0) {
            return false;
        }

        Set<String> acceptedNames = getAcceptedOreNames();
        int[] oreIds = OreDictionary.getOreIDs(stack);
        for (int i = 0; i < oreIds.length; i++) {
            String oreName = OreDictionary.getOreName(oreIds[i]);
            if (oreName != null && acceptedNames.contains(oreName.toLowerCase(Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private static ItemStack firstExactAmmoStack() {
        List<AmmoEntry> entries = getExactAmmoEntries();
        for (int i = 0; i < entries.size(); i++) {
            ItemStack stack = entries.get(i).toStack();
            if (stack != null) {
                return stack;
            }
        }
        return null;
    }

    private static List<AmmoEntry> getExactAmmoEntries() {
        List<AmmoEntry> entries = new ArrayList<AmmoEntry>();
        String[] configured = ModConfig.riftExplorerSlingshotAmmoItems;
        if (configured == null) {
            configured = new String[0];
        }

        for (int i = 0; i < configured.length; i++) {
            AmmoEntry entry = AmmoEntry.parse(configured[i]);
            if (entry != null) {
                entries.add(entry);
            }
        }

        if (entries.isEmpty()) {
            if (WEArcheryItems.pebble != null) {
                entries.add(new AmmoEntry(WEArcheryItems.pebble, 0));
            }
            entries.add(new AmmoEntry(Item.getItemFromBlock(Blocks.cobblestone), 0));
        }
        return entries;
    }

    private static List<SpecialAmmoEntry> getSpecialAmmoEntries() {
        List<SpecialAmmoEntry> entries = new ArrayList<SpecialAmmoEntry>();
        String[] configured = ModConfig.riftExplorerSlingshotSpecialAmmoEntries;
        if (configured == null) {
            return entries;
        }
        for (int i = 0; i < configured.length; i++) {
            SpecialAmmoEntry entry = SpecialAmmoEntry.parse(configured[i]);
            if (entry != null) {
                entries.add(entry);
            }
        }
        return entries;
    }

    private static Set<String> getAcceptedOreNames() {
        Set<String> names = new HashSet<String>();
        String[] configured = ModConfig.riftExplorerSlingshotAmmoOreDictionary;
        if (configured == null || configured.length == 0) {
            configured = new String[]{"cobblestone", "stone"};
        }
        for (int i = 0; i < configured.length; i++) {
            String normalized = normalizeOreName(configured[i]);
            if (!normalized.isEmpty()) {
                names.add(normalized);
            }
        }
        return names;
    }

    private static boolean matchesConfiguredAmmoMatcher(ItemStack stack, String raw) {
        if (stack == null || stack.getItem() == null || raw == null) {
            return false;
        }
        String match = extractMatchPart(raw);
        if (match.isEmpty()) {
            return false;
        }

        String lower = match.toLowerCase(Locale.ROOT);
        if (lower.startsWith("ore:")
                || lower.startsWith("oredict:")
                || lower.startsWith("dictionary:")
                || lower.startsWith("dict:")) {
            String oreName = normalizeOreName(match);
            if (oreName.isEmpty()) {
                return false;
            }
            int[] oreIds = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < oreIds.length; i++) {
                String candidate = OreDictionary.getOreName(oreIds[i]);
                if (candidate != null && normalizeOreName(candidate).equals(oreName)) {
                    return true;
                }
            }
            return false;
        }

        AmmoEntry entry = AmmoEntry.parse(match);
        return entry != null && entry.matches(stack);
    }

    private static String extractMatchPart(String raw) {
        if (raw == null) {
            return "";
        }
        String value = raw.trim();
        int pipe = value.indexOf('|');
        if (pipe >= 0) {
            value = value.substring(0, pipe).trim();
        }
        int spacedColon = value.indexOf(" : ");
        if (spacedColon >= 0) {
            value = value.substring(0, spacedColon).trim();
        }
        return value;
    }

    private static void addTooltipLine(List tooltip, String line) {
        if (tooltip == null || line == null || line.isEmpty()) {
            return;
        }
        for (int i = 0; i < tooltip.size(); i++) {
            Object existing = tooltip.get(i);
            if (existing instanceof String && stripFormatting((String)existing).equals(stripFormatting(line))) {
                return;
            }
        }
        tooltip.add(line);
    }

    private static String stripFormatting(String value) {
        return value == null ? "" : value.replaceAll("\u00A7[0-9A-FK-ORa-fk-or]", "");
    }

    private static String formatNumber(double value) {
        double rounded = Math.round(value * 100.0D) / 100.0D;
        if (Math.abs(rounded - Math.rint(rounded)) < 0.000001D) {
            return Integer.toString((int)Math.rint(rounded));
        }
        return String.format(Locale.ROOT, "%.2f", rounded).replaceAll("0+$", "").replaceAll("\\.$", "");
    }

    private static String normalizeOreName(String name) {
        if (name == null) {
            return "";
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("ore:")) {
            normalized = normalized.substring(4).trim();
        } else if (normalized.startsWith("oredict:")) {
            normalized = normalized.substring(8).trim();
        } else if (normalized.startsWith("dictionary:")) {
            normalized = normalized.substring(11).trim();
        } else if (normalized.startsWith("dict:")) {
            normalized = normalized.substring(5).trim();
        }
        return normalized;
    }

    public static final class AmmoSelection {
        private final ItemStack ammoStack;
        private final SpecialAmmoBehavior behavior;

        private AmmoSelection(ItemStack ammoStack, SpecialAmmoBehavior behavior) {
            this.ammoStack = ammoStack;
            this.behavior = behavior;
        }

        public ItemStack getAmmoStack() {
            return this.ammoStack == null ? null : this.ammoStack.copy();
        }

        public SpecialAmmoBehavior getBehavior() {
            return this.behavior;
        }
    }

    public static final class SpecialAmmoBehavior {
        private final double damage;
        private final float knockbackStrength;
        private final float explosionStrength;
        private final String action;

        private SpecialAmmoBehavior(double damage, float knockbackStrength, float explosionStrength, String action) {
            this.damage = damage;
            this.knockbackStrength = knockbackStrength;
            this.explosionStrength = explosionStrength;
            this.action = action == null ? "" : action.trim().toLowerCase(Locale.ROOT);
        }

        public double getDamage() {
            return this.damage;
        }

        public float getKnockbackStrength() {
            return this.knockbackStrength;
        }

        public float getExplosionStrength() {
            return this.explosionStrength;
        }

        public boolean isExplosive() {
            return this.explosionStrength > 0.0f;
        }

        public boolean isCapture() {
            return ACTION_CAPTURE.equals(this.action);
        }

        public boolean isRelease() {
            return ACTION_RELEASE.equals(this.action);
        }

        public boolean isTeleport() {
            return ACTION_TELEPORT.equals(this.action);
        }

        public boolean isShooterTeleport() {
            return ACTION_ENDER_PEARL.equals(this.action) || "ender_pearl".equals(this.action) || "pearl".equals(this.action) || ACTION_SWAP.equals(this.action);
        }

        public boolean isConsumedOnUse() {
            return this.isExplosive() || this.isRelease() || this.isTeleport() || this.isShooterTeleport();
        }
    }

    private static final class SpecialAmmoEntry {
        private final AmmoEntry exactEntry;
        private final String oreName;
        private final SpecialAmmoBehavior behavior;

        private SpecialAmmoEntry(AmmoEntry exactEntry, String oreName, SpecialAmmoBehavior behavior) {
            this.exactEntry = exactEntry;
            this.oreName = oreName;
            this.behavior = behavior;
        }

        private boolean matches(ItemStack stack) {
            if (stack == null || stack.getItem() == null || stack.stackSize <= 0) {
                return false;
            }
            if (this.exactEntry != null) {
                return this.exactEntry.matches(stack);
            }
            if (this.oreName == null || this.oreName.isEmpty()) {
                return false;
            }
            int[] oreIds = OreDictionary.getOreIDs(stack);
            for (int i = 0; i < oreIds.length; i++) {
                String candidate = OreDictionary.getOreName(oreIds[i]);
                if (candidate != null && normalizeOreName(candidate).equals(this.oreName)) {
                    return true;
                }
            }
            return false;
        }

        private static SpecialAmmoEntry parse(String raw) {
            if (raw == null) {
                return null;
            }
            String value = raw.trim();
            if (value.isEmpty()) {
                return null;
            }

            String[] parts;
            if (value.contains("|")) {
                parts = value.split("\\|");
            } else if (value.contains(" : ")) {
                parts = value.split("\\s+:\\s+");
            } else {
                return null;
            }
            if (parts.length < 2) {
                return null;
            }

            String match = parts[0].trim();
            if (match.isEmpty()) {
                return null;
            }

            double damage = parseDouble(parts[1], 0.0D);
            float knockback = parts.length >= 3 ? (float)parseDouble(parts[2], 0.0D) : 0.0f;
            float explosion = parts.length >= 4 ? (float)parseDouble(parts[3], 0.0D) : 0.0f;
            String action = parts.length >= 5 ? parts[4].trim() : "";
            SpecialAmmoBehavior behavior = new SpecialAmmoBehavior(Math.max(0.0D, damage), Math.max(0.0f, knockback), Math.max(0.0f, explosion), action);

            String normalizedOre = normalizeOreName(match);
            if (match.toLowerCase(Locale.ROOT).startsWith("ore:")
                    || match.toLowerCase(Locale.ROOT).startsWith("oredict:")
                    || match.toLowerCase(Locale.ROOT).startsWith("dictionary:")
                    || match.toLowerCase(Locale.ROOT).startsWith("dict:")) {
                return normalizedOre.isEmpty() ? null : new SpecialAmmoEntry(null, normalizedOre, behavior);
            }

            AmmoEntry exact = AmmoEntry.parse(match);
            return exact == null ? null : new SpecialAmmoEntry(exact, null, behavior);
        }

        private static double parseDouble(String raw, double fallback) {
            try {
                return Double.parseDouble(raw.trim());
            } catch (Exception ignored) {
                return fallback;
            }
        }
    }

    private static final class AmmoEntry {
        private final Item item;
        private final int meta;

        private AmmoEntry(Item item, int meta) {
            this.item = item;
            this.meta = meta;
        }

        private boolean matches(ItemStack stack) {
            if (stack == null || stack.getItem() != this.item) {
                return false;
            }
            return this.meta == ANY_META || stack.isItemStackDamageable() || stack.getItemDamage() == this.meta;
        }

        private ItemStack toStack() {
            if (this.item == null) {
                return null;
            }
            return new ItemStack(this.item, 1, this.meta == ANY_META ? 0 : this.meta);
        }

        private static AmmoEntry parse(String raw) {
            if (raw == null) {
                return null;
            }
            String value = raw.trim();
            if (value.isEmpty()) {
                return null;
            }

            int meta = ANY_META;
            String itemName = value;
            int lastColon = value.lastIndexOf(':');
            int firstColon = value.indexOf(':');
            if (lastColon > firstColon) {
                String suffix = value.substring(lastColon + 1).trim();
                if ("*".equals(suffix)) {
                    itemName = value.substring(0, lastColon);
                } else {
                    try {
                        meta = Integer.parseInt(suffix);
                        itemName = value.substring(0, lastColon);
                    } catch (NumberFormatException ignored) {
                        meta = ANY_META;
                    }
                }
            }

            Item item = resolveItem(itemName.trim());
            return item == null ? null : new AmmoEntry(item, meta);
        }

        private static Item resolveItem(String itemName) {
            if (itemName == null || itemName.isEmpty()) {
                return null;
            }

            String lower = itemName.toLowerCase(Locale.ROOT);
            if ("pebble".equals(lower) || lower.endsWith(":pebble") || lower.endsWith(":item.pebble")) {
                return WEArcheryItems.pebble;
            }

            Item item = lookupItem(itemName);
            if (item != null) {
                return item;
            }

            if (itemName.indexOf(':') < 0) {
                item = lookupItem("minecraft:" + itemName);
                if (item != null) {
                    return item;
                }
            }

            Block block = (Block)GameData.getBlockRegistry().getObject(itemName);
            return block == null ? null : Item.getItemFromBlock(block);
        }

        private static Item lookupItem(String itemName) {
            Item item = (Item)GameData.getItemRegistry().getObject(itemName);
            if (item != null) {
                return item;
            }

            int colon = itemName.indexOf(':');
            if (colon >= 0 && colon + 1 < itemName.length()) {
                String withItemPrefix = itemName.substring(0, colon + 1) + "item." + itemName.substring(colon + 1);
                item = (Item)GameData.getItemRegistry().getObject(withItemPrefix);
            }
            return item;
        }
    }
}
