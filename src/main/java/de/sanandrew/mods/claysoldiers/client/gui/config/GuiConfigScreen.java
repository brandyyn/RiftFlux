/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.config.GuiConfig
 *  net.minecraftforge.common.config.ConfigElement
 */
package de.sanandrew.mods.claysoldiers.client.gui.config;

import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.util.ModConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

@SideOnly(value=Side.CLIENT)
public class GuiConfigScreen
extends GuiConfig {
    public GuiConfigScreen(GuiScreen parent) {
        super(parent, new ConfigElement(ModConfig.config.getCategory("general")).getChildElements(), "claysoldiers", false, false, GuiConfig.getAbridgedConfigPath((String)ModConfig.config.toString()));
    }
}

