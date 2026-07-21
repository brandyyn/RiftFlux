package makamys.satchels.compat;

import baubles.api.BaublesApi;
import baubles.api.expanded.BaubleExpandedSlots;
import com.voidsrift.riftflux.ModConfig;
import makamys.satchels.Satchels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class BaublesCompat {

    public static final String TYPE_SATCHEL = "satchel";
    public static final String TYPE_POUCH = "pouch";
    public static final String TYPE_BACKPACK = "backpack";

    public static final String ITEM_EYEBONE = "chester:eyebone";
    public static final String ITEM_BACKPACK = "riftflux:backpack";
    public static final String ITEM_SATCHEL = "riftflux:satchel";
    public static final String ITEM_POUCH = "riftflux:pouch";

    private static boolean slotsRegistered = false;
    private static Map<String, String[]> itemSlotMappings;

    private BaublesCompat() {
    }

    public static void registerSlots() {
        if (slotsRegistered) {
            return;
        }
        slotsRegistered = true;
        parseItemSlotMappings();
        if (ModConfig.customBaubleSlots == null) {
            return;
        }
        for (String entry : ModConfig.customBaubleSlots) {
            CustomSlotDefinition definition = parseCustomSlot(entry);
            if (definition == null) continue;
            if ("chester_staff".equals(definition.type)
                    && (!ModConfig.enableChesterModule || !ModConfig.enableChesterBaubleSlot)) {
                continue;
            }
            tryRegister(definition.type, definition.count);
        }
    }

    public static String[] getTypes(String itemId, String... defaults) {
        if (itemSlotMappings == null) {
            parseItemSlotMappings();
        }
        String[] configured = itemSlotMappings.get(normalize(itemId));
        String[] result = configured != null ? configured : defaults;
        return result == null ? new String[0] : result;
    }

    public static boolean equipToFirstEmpty(EntityPlayer player, ItemStack stack, String[] types) {
        if (player == null || stack == null || types == null || types.length == 0) return false;
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return false;
        Set<Integer> visited = new HashSet<Integer>();
        for (String type : types) {
            int[] slots = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(type);
            if (slots == null) continue;
            for (int slot : slots) {
                if (!visited.add(slot)) continue;
                if (baubles.getStackInSlot(slot) != null) continue;
                if (!baubles.isItemValidForSlot(slot, stack)) continue;
                ItemStack equip = stack.copy();
                equip.stackSize = 1;
                baubles.setInventorySlotContents(slot, equip);
                baubles.markDirty();
                stack.stackSize -= 1;
                return true;
            }
        }
        return false;
    }

    public static ItemStack getBaubleStack(EntityPlayer player, Item expectedItem, int ordinal, String[] types) {
        if (player == null || expectedItem == null || ordinal < 0 || types == null) return null;
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return null;
        int found = 0;
        for (int typeIndex = 0; typeIndex < types.length; typeIndex++) {
            String type = types[typeIndex];
            int[] slots = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(type);
            if (slots == null) continue;
            for (int slot : slots) {
                if (wasSlotVisited(types, typeIndex, slot)) continue;
                ItemStack stack = baubles.getStackInSlot(slot);
                if (stack == null || stack.getItem() != expectedItem) continue;
                if (found++ == ordinal) return stack;
            }
        }
        return null;
    }

    private static boolean wasSlotVisited(String[] types, int typeIndex, int slot) {
        for (int i = 0; i < typeIndex; i++) {
            int[] earlierSlots = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(types[i]);
            if (earlierSlots == null) continue;
            for (int earlierSlot : earlierSlots) {
                if (earlierSlot == slot) return true;
            }
        }
        return false;
    }

    private static void tryRegister(String type, int minSlots) {
        try {
            BaubleExpandedSlots.tryRegisterType(type);
            boolean ok = BaubleExpandedSlots.tryAssignSlotsUpToMinimum(type, minSlots);
            if (!ok) {
                Satchels.LOGGER.warn("Failed to assign {} bauble slot(s) for type {}", minSlots, type);
            }
        } catch (Throwable t) {
            Satchels.LOGGER.warn("Failed to register bauble type {}", type, t);
        }
    }

    public static ItemStack getBaubleStack(EntityPlayer player, String type, int ordinal) {
        if (player == null) return null;
        IInventory baubles = BaublesApi.getBaubles(player);
        if (baubles == null) return null;
        int[] slots = BaubleExpandedSlots.getIndexesOfAssignedSlotsOfType(type);
        if (slots == null || slots.length <= ordinal) return null;
        return baubles.getStackInSlot(slots[ordinal]);
    }

    public static boolean equipToFirstEmpty(EntityPlayer player, ItemStack stack, String type) {
        return equipToFirstEmpty(player, stack, new String[]{type});
    }

    private static void parseItemSlotMappings() {
        Map<String, String[]> parsed = new HashMap<String, String[]>();
        if (ModConfig.baubleItemSlotMappings != null) {
            for (String entry : ModConfig.baubleItemSlotMappings) {
                if (entry == null) continue;
                int equals = entry.indexOf('=');
                if (equals <= 0) {
                    Satchels.LOGGER.warn("Ignoring invalid Bauble item slot mapping: {}", entry);
                    continue;
                }
                String itemId = normalize(entry.substring(0, equals));
                String value = entry.substring(equals + 1);
                List<String> types = new ArrayList<String>();
                Set<String> unique = new HashSet<String>();
                for (String rawType : value.split(",")) {
                    String type = normalize(rawType);
                    if (!type.isEmpty() && unique.add(type)) {
                        types.add(type);
                    }
                }
                parsed.put(itemId, types.toArray(new String[types.size()]));
            }
        }
        itemSlotMappings = parsed;
    }

    private static CustomSlotDefinition parseCustomSlot(String entry) {
        if (entry == null) return null;
        int equals = entry.indexOf('=');
        if (equals <= 0) {
            Satchels.LOGGER.warn("Ignoring invalid custom Bauble slot definition: {}", entry);
            return null;
        }
        String type = normalize(entry.substring(0, equals));
        try {
            int count = Integer.parseInt(entry.substring(equals + 1).trim());
            if (type.isEmpty() || count <= 0) throw new NumberFormatException();
            return new CustomSlotDefinition(type, count);
        } catch (NumberFormatException ignored) {
            Satchels.LOGGER.warn("Ignoring invalid custom Bauble slot definition: {}", entry);
            return null;
        }
    }

    private static String normalize(String value) {
        return value == null ? "" : value.trim().toLowerCase(Locale.ROOT);
    }

    private static final class CustomSlotDefinition {
        private final String type;
        private final int count;

        private CustomSlotDefinition(String type, int count) {
            this.type = type;
            this.count = count;
        }
    }
}
