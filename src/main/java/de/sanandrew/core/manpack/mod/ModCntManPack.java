/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.EventBus
 *  com.google.common.eventbus.Subscribe
 *  cpw.mods.fml.client.FMLFileResourcePack
 *  cpw.mods.fml.client.FMLFolderResourcePack
 *  cpw.mods.fml.common.DummyModContainer
 *  cpw.mods.fml.common.LoadController
 *  cpw.mods.fml.common.ModContainer
 *  cpw.mods.fml.common.ModMetadata
 *  cpw.mods.fml.common.event.FMLConstructionEvent
 *  cpw.mods.fml.common.event.FMLInitializationEvent
 *  cpw.mods.fml.common.event.FMLInterModComms$IMCEvent
 *  cpw.mods.fml.common.event.FMLInterModComms$IMCMessage
 *  cpw.mods.fml.common.event.FMLPostInitializationEvent
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.event.FMLServerStartingEvent
 *  cpw.mods.fml.common.network.NetworkRegistry
 *  cpw.mods.fml.relauncher.FMLInjectionData
 *  org.apache.commons.lang3.mutable.MutableBoolean
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package de.sanandrew.core.manpack.mod;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import cpw.mods.fml.client.FMLFileResourcePack;
import cpw.mods.fml.client.FMLFolderResourcePack;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.ModMetadata;
import cpw.mods.fml.common.event.FMLConstructionEvent;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLInterModComms;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.event.FMLServerStartingEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.relauncher.FMLInjectionData;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.managers.SAPUpdateManager;
import de.sanandrew.core.manpack.mod.CommandSAPManPack;
import de.sanandrew.core.manpack.mod.CommonProxy;
import de.sanandrew.core.manpack.mod.ConfigurationManager;
import de.sanandrew.core.manpack.mod.client.ClientProxy;
import de.sanandrew.core.manpack.network.NetworkManager;
import de.sanandrew.core.manpack.util.MutableString;
import de.sanandrew.core.manpack.util.SAPReflectionHelper;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.command.ICommand;
import net.minecraft.nbt.NBTTagCompound;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ModCntManPack
extends DummyModContainer {
    public static final String MOD_CHANNEL = "sapmanpack";
    public static final Logger UPD_LOG = LogManager.getLogger((String)"SAPUpdateMgr");
    public static final int FORGE_BULD_MIN = 1448;
    public static CommonProxy proxy;
    private static final List<String> TITLES;

    public ModCntManPack() {
        super(new ModMetadata());
        ModMetadata meta = super.getMetadata();
        meta.modId = MOD_CHANNEL;
        meta.name = "SanAndreasPs Manager Pack CORE edition";
        meta.version = "2.7.2";
        meta.authorList = Collections.singletonList("SanAndreasP");
        meta.description = "A helper coremod which is needed for all my mods.";
        meta.url = "http://www.minecraftforge.net/forum/index.php/topic,2828.0.html";
        meta.screenshots = new String[0];
        meta.logoFile = "";
    }

    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register((Object)this);
        return true;
    }

    @Subscribe
    public void modConstruction(FMLConstructionEvent event) {
        if (Integer.parseInt(FMLInjectionData.data()[3].toString()) < 1448) {
            ManPackLoadingPlugin.MOD_LOG.log(Level.FATAL, "The installed version of Forge is outdated! Minimum build required is %d, installed is build %s. Either update Forge or remove this mod, it will cause problems otherwise!", new Object[]{1448, FMLInjectionData.data()[3]});
        }
        NetworkRegistry.INSTANCE.register((ModContainer)this, ((Object)((Object)this)).getClass(), null, event.getASMHarvestedData());
    }

    @Subscribe
    @SideOnly(value=Side.CLIENT)
    public void injectClientProxy(FMLPreInitializationEvent evt) {
        proxy = new ClientProxy();
        this.preInit(evt);
    }

    @Subscribe
    @SideOnly(value=Side.SERVER)
    public void injectServerProxy(FMLPreInitializationEvent evt) {
        proxy = new CommonProxy();
        this.preInit(evt);
    }

    public void preInit(FMLPreInitializationEvent event) {
        Class dspCls;
        ConfigurationManager.load(event.getSuggestedConfigurationFile());
        if (ConfigurationManager.enableWindowTitleMsg && (dspCls = SAPReflectionHelper.getClass("org.lwjgl.opengl.Display")) != null) {
            String currTitle = (String)SAPReflectionHelper.invokeCachedMethod(dspCls, null, null, "getTitle", null, null);
            currTitle = String.format("%s: %s", currTitle, TITLES.get(SAPUtils.RNG.nextInt(TITLES.size())));
            SAPReflectionHelper.invokeCachedMethod(dspCls, null, null, "setTitle", new Class[]{String.class}, new Object[]{currTitle});
        }
        SAPUpdateManager.createUpdateManager("SAP Manager Pack", new SAPUpdateManager.Version("2.7.2"), "https://raw.githubusercontent.com/SanAndreasP/SAPManagerPack/master/update.json", "http://www.curseforge.com/projects/226994/", this.getSource());
    }

    @Subscribe
    public void init(FMLInitializationEvent evt) {
        proxy.registerRenderStuff();
    }

    @Subscribe
    public void postInit(FMLPostInitializationEvent evt) {
        NetworkManager.initPacketHandler(proxy);
        if (ConfigurationManager.enableUpdater) {
            for (Triplet<SAPUpdateManager, MutableBoolean, MutableString> udm : SAPUpdateManager.UPD_MANAGERS) {
                udm.getValue0().checkForUpdate();
            }
        }
    }

    @Subscribe
    public void imcReceive(FMLInterModComms.IMCEvent event) {
        for (FMLInterModComms.IMCMessage msg : event.getMessages()) {
            if (!msg.key.equalsIgnoreCase("add-updatemgr") || !msg.isNBTMessage()) continue;
            NBTTagCompound nbt = msg.getNBTValue();
            SAPUpdateManager.createUpdateManager(nbt.getString("modName"), new SAPUpdateManager.Version(nbt.getString("version")), nbt.getString("updateInfoUrl"), nbt.getString("projectLink"), nbt.hasKey("jarLocation") ? new File(nbt.getString("jarLocation")) : null);
        }
    }

    @Subscribe
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand((ICommand)new CommandSAPManPack());
    }

    public File getSource() {
        return ManPackLoadingPlugin.source;
    }

    public Class<?> getCustomResourcePackClass() {
        return this.getSource().isDirectory() ? FMLFolderResourcePack.class : FMLFileResourcePack.class;
    }

    static {
        TITLES = new ArrayList<String>();
        TITLES.add("May contain nuts!");
        TITLES.add("Creepers gonna creep.");
        TITLES.add("Super Minecraft Miner Turbo X Diamond 2015 Deluxe GOTY Edition, Episode 2 Act 5.");
        TITLES.add("I can swing my sword!!!");
        TITLES.add("Herobrine isn't real! Get over it.");
        TITLES.add("PULL THE LEVER!!!");
    }
}
