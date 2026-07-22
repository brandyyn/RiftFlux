/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.IFMLLoadingPlugin
 *  cpw.mods.fml.relauncher.IFMLLoadingPlugin$DependsOn
 *  cpw.mods.fml.relauncher.IFMLLoadingPlugin$MCVersion
 *  cpw.mods.fml.relauncher.IFMLLoadingPlugin$SortingIndex
 *  cpw.mods.fml.relauncher.IFMLLoadingPlugin$TransformerExclusions
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package de.sanandrew.core.manpack.init;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;
import de.sanandrew.core.manpack.mod.ModCntManPack;
import de.sanandrew.core.manpack.transformer.ASMHelper;
import java.io.File;
import java.net.URISyntaxException;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@IFMLLoadingPlugin.SortingIndex(value=1001)
@IFMLLoadingPlugin.MCVersion(value="1.7.10")
@IFMLLoadingPlugin.DependsOn(value={"forge"})
@IFMLLoadingPlugin.TransformerExclusions(value={"de.sanandrew.core.manpack.transformer", "de.sanandrew.core.manpack.init"})
public class ManPackLoadingPlugin
implements IFMLLoadingPlugin {
    public static final String MC_VERSION = "1.7.10";
    public static final String MOD_ID = "sapmanpack";
    public static final Logger MOD_LOG = LogManager.getLogger((String)"sapmanpack");
    public static final String MOD_VERSION = "2.7.2";
    public static File source;

    public String getAccessTransformerClass() {
        return null;
    }

    public String[] getASMTransformerClass() {
        return new String[0];
    }

    public String getModContainerClass() {
        return ModCntManPack.class.getName();
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {
        ASMHelper.isMCP = (Boolean)data.get("runtimeDeobfuscationEnabled") == false;
        source = (File)data.get("coremodLocation");
        if (source == null) {
            try {
                source = new File(this.getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
                if (!new File(source, "assets").exists()) {
                    source = new File(source.getParentFile().getParentFile(), "resources/main");
                }
            }
            catch (URISyntaxException e) {
                throw new RuntimeException("Failed to acquire source location for SAPManPack!", e);
            }
        }
    }
}

