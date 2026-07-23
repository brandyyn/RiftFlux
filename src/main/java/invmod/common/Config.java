package invmod.common;

import com.voidsrift.riftflux.ModConfig;
import java.util.Properties;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Property;

/** Compatibility facade backed entirely by RiftFlux's main configuration. */
public class Config {
  private static final String CATEGORY = "invasion";
  protected Properties properties;

  public void loadConfig() {
    mod_Invasion.log("Loading Invasion settings from riftflux.cfg");
    properties = new Properties();
  }

  public void setProperty(String key, String value) {
    properties.setProperty(key, value);
    ModConfig.config.get(CATEGORY, key, value).set(value);
  }

  public String getProperty(String key, String defaultValue) {
    ConfigCategory category = ModConfig.config.getCategory(CATEGORY);
    Property property = category.get(key);
    if (property == null) {
      return defaultValue;
    }
    String value = property.getString();
    properties.setProperty(key, value);
    return value;
  }

  public int getPropertyValueInt(String keyName, int defaultValue) {
    int value = ModConfig.config.get(CATEGORY, keyName, defaultValue).getInt(defaultValue);
    properties.setProperty(keyName, Integer.toString(value));
    return value;
  }

  public float getPropertyValueFloat(String keyName, float defaultValue) {
    float value = (float) ModConfig.config.get(CATEGORY, keyName, defaultValue).getDouble(defaultValue);
    properties.setProperty(keyName, Float.toString(value));
    return value;
  }

  public boolean getPropertyValueBoolean(String keyName, boolean defaultValue) {
    boolean value = ModConfig.config.get(CATEGORY, keyName, defaultValue).getBoolean(defaultValue);
    properties.setProperty(keyName, Boolean.toString(value));
    return value;
  }

  public String getPropertyValueString(String keyName, String defaultValue) {
    String value = ModConfig.config.get(CATEGORY, keyName, defaultValue).getString();
    properties.setProperty(keyName, value);
    return value;
  }
}
