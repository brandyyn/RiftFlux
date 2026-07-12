package makamys.satchels;

import static java.nio.file.StandardWatchEventKinds.ENTRY_CREATE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_DELETE;
import static java.nio.file.StandardWatchEventKinds.ENTRY_MODIFY;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Arrays;
import java.util.regex.Pattern;

import codechicken.lib.colour.Colour;
import codechicken.lib.colour.ColourRGBA;
import com.voidsrift.riftflux.ModConfig;
import makamys.mclib.config.item.BackpackConfigHelper;

public class ConfigSatchels {
    
    private static File configFile;
    private static WatchService watcher;
    

    public static Colour pouchBgColor;
    public static Colour satchelBgColor;
    
    public static boolean hotSwap;
    
    public static int pouchUpgradeWeight;
    public static boolean enablePouchUpgrades;
    public static boolean enablePouchUpgradeLoot;
    
    public static boolean drawSatchel;
    public static boolean drawSatchelStrap;
    public static boolean drawLeftPouch;
    public static boolean drawRightPouch;
    
    public static boolean compatTechguns;

    public static String satchelIngredient1;
    public static String satchelIngredient2;

    public static BackpackConfigHelper backpackHelper;
    
    public static void init() {
        configFile = new File("config/riftflux.cfg");
        reparse();
        try {
            registerWatchService();
        } catch(IOException e) {
            System.out.println("Failed to register watch service: " + e + " (" + e.getMessage() + "). Changes to the config file will not be reflected");
        }
    }
    
    public static void reload() {
        if(ModConfig.reload()) {
            reparse();
        }
    }
    
    public static void reloadIfChanged() {
        if(watcher != null) {
            WatchKey key = watcher.poll();
            
            if(key != null) {
                for(WatchEvent<?> event: key.pollEvents()) {
                    if(event.context().toString().equals(configFile.getName())) {
                        reload();
                    }
                }
                key.reset();
            }
        }
    }
    
    public static void reparse() {
        hotSwap = ModConfig.satchelsHotSwap;

        pouchBgColor = getColor(ModConfig.satchelsPouchBgColor, "FFB266");
        satchelBgColor = getColor(ModConfig.satchelsSatchelBgColor, "FFBF99");
        
        pouchUpgradeWeight = ModConfig.satchelsPouchUpgradeWeight;
        enablePouchUpgrades = ModConfig.satchelsEnablePouchUpgrades;
        enablePouchUpgradeLoot = ModConfig.satchelsEnablePouchUpgradeLoot;
        
        drawSatchel = ModConfig.satchelsDrawSatchel;
        drawSatchelStrap = ModConfig.satchelsDrawSatchelStrap;
        drawLeftPouch = ModConfig.satchelsDrawLeftPouch;
        drawRightPouch = ModConfig.satchelsDrawRightPouch;
        
        satchelIngredient1 = ModConfig.satchelsIngredient1;
        satchelIngredient2 = ModConfig.satchelsIngredient2;
        
        compatTechguns = ModConfig.satchelsCompatTechguns;
        
        backpackHelper = new BackpackConfigHelper(Arrays.asList(ModConfig.satchelsItemBlacklist));
    }
    
    private static Colour getColor(String value, String defaultValue) {
        Pattern colorPattern = Pattern.compile("(0x)?[0-9a-fA-F]{6}");
        String str = value == null ? defaultValue : value.trim();
        if(!colorPattern.matcher(str).matches()) {
            str = defaultValue;
        }
        str = str.replace("0x", "");
        int rgb = Integer.valueOf(str, 16);
        return new ColourRGBA((rgb >> 16) & 0xFF, (rgb >> 8) & 0xFF, rgb & 0xFF, 0xFF);
    }
    
    private static void registerWatchService() throws IOException {
        watcher = FileSystems.getDefault().newWatchService();
        configFile.toPath().getParent().register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
    }
}
