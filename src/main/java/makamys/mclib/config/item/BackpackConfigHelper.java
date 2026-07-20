package makamys.mclib.config.item;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * Minimal port of MCLib's backpack blacklist helper used by Satchels.
 */
public class BackpackConfigHelper {

    public static final String CONFIG_DESCRIPTION_SUFFIX =
            "\nEntries use registry names like 'minecraft:stone' or 'modid:item@meta'.\n" +
            "You can also use 'ore:Name' to match an ore dictionary entry.";

    public static final String[] NON_NESTABLE_BACKPACK_BLACKLIST = new String[] {
            "riftflux:satchel",
            "riftflux:pouch",
            "riftflux:pouch_upgrade",
            "riftflux:backpack"
    };

    private final List<Entry> entries;
    private final Set<String> rawEntries;

    public BackpackConfigHelper(List<String> blacklist) {
        if(blacklist == null) {
            blacklist = Collections.emptyList();
        }
        rawEntries = new HashSet<>();
        entries = new ArrayList<>();
        for(String entry : blacklist) {
            if(entry == null) continue;
            String trimmed = entry.trim();
            if(trimmed.isEmpty()) continue;
            rawEntries.add(trimmed);
            entries.add(Entry.parse(trimmed));
        }
    }

    public boolean isAllowed(ItemStack stack) {
        if(stack == null || stack.getItem() == null) {
            return true;
        }
        for(Entry entry : entries) {
            if(entry.matches(stack)) {
                return false;
            }
        }
        return true;
    }

    private static class Entry {
        final String registryName;
        final Integer meta;
        final String oreName;

        private Entry(String registryName, Integer meta, String oreName) {
            this.registryName = registryName;
            this.meta = meta;
            this.oreName = oreName;
        }

        static Entry parse(String raw) {
            if(raw.startsWith("ore:")) {
                return new Entry(null, null, raw.substring(4));
            }
            String name = raw;
            Integer meta = null;
            int atIdx = raw.indexOf('@');
            if(atIdx >= 0) {
                name = raw.substring(0, atIdx);
                try {
                    meta = Integer.valueOf(raw.substring(atIdx + 1));
                } catch(NumberFormatException ignored) {
                    meta = null;
                }
            }
            return new Entry(name, meta, null);
        }

        boolean matches(ItemStack stack) {
            if(stack == null || stack.getItem() == null) return false;
            if(oreName != null) {
                int[] ids = OreDictionary.getOreIDs(stack);
                for(int id : ids) {
                    if(oreName.equals(OreDictionary.getOreName(id))) {
                        return true;
                    }
                }
                return false;
            }
            String name = Item.itemRegistry.getNameForObject(stack.getItem());
            if(name == null || !name.equals(registryName)) {
                return false;
            }
            return meta == null || stack.getItemDamage() == meta.intValue();
        }
    }
}
