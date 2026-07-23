package invmod.common;

import com.voidsrift.riftflux.ModConfig;
import java.util.HashMap;

/** Preserves the original API while persisting only to riftflux.cfg. */
public class ConfigInvasion extends Config {
  public void saveConfig(HashMap<Integer, Float> strengthOverrides, boolean debug) {
    ModConfig.config.save();
  }
}
