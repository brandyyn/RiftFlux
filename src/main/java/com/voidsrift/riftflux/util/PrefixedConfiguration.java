package com.voidsrift.riftflux.util;

import java.io.File;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;

/**
 * Routes all category reads/writes to a namespaced category on a backing configuration.
 * Example: prefix "gokistats", category "Options" -> "gokistats.Options".
 */
public final class PrefixedConfiguration extends Configuration {
    private final Configuration backing;
    private final String prefix;

    public PrefixedConfiguration(Configuration backing, String prefix) {
        super(new File("config/riftflux.cfg"));
        if (backing == null) {
            throw new IllegalArgumentException("backing configuration cannot be null");
        }
        this.backing = backing;
        this.prefix = sanitizePrefix(prefix);
    }

    private static String sanitizePrefix(String rawPrefix) {
        if (rawPrefix == null) {
            return "module";
        }
        String trimmed = rawPrefix.trim();
        return trimmed.isEmpty() ? "module" : trimmed;
    }

    private String prefixed(String category) {
        if (this.prefix == null) {
            return category == null ? "module" : category;
        }
        if (category == null || category.trim().isEmpty()) {
            return this.prefix;
        }
        String trimmed = category.trim();
        if (trimmed.equals(this.prefix) || trimmed.startsWith(this.prefix + ".")) {
            return trimmed;
        }
        return this.prefix + "." + trimmed;
    }

    public Property get(String category, String key, int defaultValue) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue);
        }
        return this.backing.get(prefixed(category), key, defaultValue);
    }

    public Property get(String category, String key, int defaultValue, String comment) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue, comment);
        }
        return this.backing.get(prefixed(category), key, defaultValue, comment);
    }

    public Property get(String category, String key, boolean defaultValue) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue);
        }
        return this.backing.get(prefixed(category), key, defaultValue);
    }

    public Property get(String category, String key, boolean defaultValue, String comment) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue, comment);
        }
        return this.backing.get(prefixed(category), key, defaultValue, comment);
    }

    public Property get(String category, String key, double defaultValue) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue);
        }
        return this.backing.get(prefixed(category), key, defaultValue);
    }

    public Property get(String category, String key, double defaultValue, String comment) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue, comment);
        }
        return this.backing.get(prefixed(category), key, defaultValue, comment);
    }

    public Property get(String category, String key, String defaultValue) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue);
        }
        return this.backing.get(prefixed(category), key, defaultValue);
    }

    public Property get(String category, String key, String defaultValue, String comment) {
        if (this.backing == null) {
            return super.get(category, key, defaultValue, comment);
        }
        return this.backing.get(prefixed(category), key, defaultValue, comment);
    }

    public Property get(String category, String key, String[] defaultValues) {
        if (this.backing == null) {
            return super.get(category, key, defaultValues);
        }
        return this.backing.get(prefixed(category), key, defaultValues);
    }

    public Property get(String category, String key, String[] defaultValues, String comment) {
        if (this.backing == null) {
            return super.get(category, key, defaultValues, comment);
        }
        return this.backing.get(prefixed(category), key, defaultValues, comment);
    }

    public ConfigCategory getCategory(String category) {
        if (this.backing == null) {
            return super.getCategory(category);
        }
        return this.backing.getCategory(prefixed(category));
    }

    public boolean hasKey(String category, String key) {
        if (this.backing == null) {
            return super.hasKey(category, key);
        }
        return this.backing.hasKey(prefixed(category), key);
    }

    public boolean moveProperty(String oldCategory, String propName, String newCategory) {
        if (this.backing == null) {
            return super.moveProperty(oldCategory, propName, newCategory);
        }
        return this.backing.moveProperty(prefixed(oldCategory), propName, prefixed(newCategory));
    }

    public void load() {
        if (this.backing == null) {
            super.load();
            return;
        }
        this.backing.load();
    }

    public void save() {
        if (this.backing == null) {
            super.save();
            return;
        }
        this.backing.save();
    }

    public boolean hasChanged() {
        if (this.backing == null) {
            return super.hasChanged();
        }
        return this.backing.hasChanged();
    }
}
