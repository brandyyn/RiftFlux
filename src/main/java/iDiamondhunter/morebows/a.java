/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.config.GuiConfig
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraftforge.common.config.ConfigElement
 */
package iDiamondhunter.morebows;

import cpw.mods.fml.client.config.GuiConfig;
import iDiamondhunter.morebows.MoreBows;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

public final class a
extends GuiConfig {
    public a(GuiScreen guiScreen) {
        super(guiScreen, new ConfigElement(MoreBows.var_net_minecraftforge_common_config_Configuration_a.getCategory("general")).getChildElements(), "MoreBows", false, false, "MoreBows");
    }
}

