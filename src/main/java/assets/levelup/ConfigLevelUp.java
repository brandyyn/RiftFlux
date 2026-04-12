/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.client.IModGuiFactory
 *  cpw.mods.fml.client.IModGuiFactory$RuntimeOptionCategoryElement
 *  cpw.mods.fml.client.IModGuiFactory$RuntimeOptionGuiHandler
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiButton
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.resources.I18n
 */
package assets.levelup;

import assets.levelup.LevelUp;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.IModGuiFactory;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;

public final class ConfigLevelUp
extends GuiScreen
implements IModGuiFactory {
    private GuiScreen parent;
    private boolean[] toggles;

    public ConfigLevelUp() {
    }

    public ConfigLevelUp(GuiScreen guiScreen) {
        this.parent = guiScreen;
    }

    public void initialize(Minecraft minecraftInstance) {
        this.mc = minecraftInstance;
    }

    public Class<? extends GuiScreen> mainConfigGuiClass() {
        return ConfigLevelUp.class;
    }

    public void initGui() {
        this.toggles = LevelUp.instance.getClientProperties();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 75, this.height - 38, I18n.format((String)"gui.done", (Object[])new Object[0])));
        for (int i = 0; i < this.toggles.length; ++i) {
            this.buttonList.add(new GuiButton(1 + i, this.width / 2 - 75, this.height - 68 - i * 40, I18n.format((String)("config.levelup.option" + i), (Object[])new Object[]{this.toggles[i]})));
        }
    }

    protected void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == 0) {
                FMLClientHandler.instance().showGuiScreen((Object)this.parent);
            } else if (button.id - 1 < this.toggles.length) {
                this.toggles[button.id - 1] = !this.toggles[button.id - 1];
                button.displayString = I18n.format((String)("config.levelup.option" + (button.id - 1)), (Object[])new Object[]{this.toggles[button.id - 1]});
            }
        }
    }

    public void drawScreen(int par1, int par2, float par3) {
        this.drawDefaultBackground();
        this.drawCenteredString(this.fontRendererObj, I18n.format((String)"config.levelup.title", (Object[])new Object[0]), this.width / 2, this.height / 2 - 115, 0xFFFFFF);
        super.drawScreen(par1, par2, par3);
    }

    public void onGuiClosed() {
        super.onGuiClosed();
        LevelUp.instance.refreshValues(this.toggles);
    }

    public Set<IModGuiFactory.RuntimeOptionCategoryElement> runtimeGuiCategories() {
        return null;
    }

    public IModGuiFactory.RuntimeOptionGuiHandler getHandlerFor(IModGuiFactory.RuntimeOptionCategoryElement element) {
        return null;
    }
}
