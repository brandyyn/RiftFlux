/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraftforge.common.config.Configuration
 */
package theoldone822.ArmorOverlay;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.relauncher.Side;
import java.io.File;
import net.minecraftforge.common.config.Configuration;
import theoldone822.ArmorOverlay.HUDOverlayHandler;

public class ArmorOverlay {
    static int overlayLevels = 3;
    static int armorPices = 2;
    static boolean showNumbers = false;

    public void preInit(FMLPreInitializationEvent event) {
        if (ModConfig.config != null) {
            overlayLevels = ModConfig.armorOverlayLevels;
            armorPices = ModConfig.armorOverlayArmorPieces;
            showNumbers = ModConfig.armorOverlayShowNumbers;
            return;
        }

        File installDir = event.getModConfigurationDirectory();
        Configuration settings = new Configuration(new File(installDir, "ArmorOverlay.cfg"));
        settings.load();
        overlayLevels = settings.get("config", "OverlayLevels", 3, "Max armor 'bars'. can be 3 (iron-gold-diamond) 5 (leather-chain-iron..) 10 (adds mod armor icons with 2 past diamond) or 20").getInt();
        overlayLevels = overlayLevels <= 3 ? 3 : (overlayLevels <= 5 ? 5 : (overlayLevels <= 10 ? 10 : 20));
        armorPices = settings.get("config", "ArmorPices", 2, "Number of armor points to make 1 full armor icon. can be 1, 2 or 4").getInt();
        if (armorPices == 3 || armorPices > 4) {
            armorPices = 4;
        }
        if (armorPices < 1) {
            armorPices = 1;
        }
        showNumbers = settings.get("config", "Displaynumber", false, "Shows the raw number of how much armor you have next to the bar").getBoolean();
        settings.save();
    }

    public void init(FMLInitializationEvent event) {
        if (event.getSide() == Side.CLIENT) {
            HUDOverlayHandler.init();
        }
    }
}
