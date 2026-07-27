/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.registry.ClientRegistry
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.InputEvent$KeyInputEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiScreen
 *  net.minecraft.client.settings.KeyBinding
 */
package assets.levelup;

import assets.levelup.GuiClasses;
import assets.levelup.GuiSkills;
import assets.levelup.LevelUpHUD;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.InputEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.settings.KeyBinding;

public final class SkillKeyHandler {
    public static final SkillKeyHandler INSTANCE = new SkillKeyHandler();
    private final KeyBinding keys = new KeyBinding("Class Skills", 38, "RiftFlux");

    private SkillKeyHandler() {
        ClientRegistry.registerKeyBinding((KeyBinding)this.keys);
    }

    @SubscribeEvent
    public void keyDown(InputEvent.KeyInputEvent event) {
        if (this.keys.getIsKeyPressed() && Minecraft.getMinecraft().currentScreen == null && Minecraft.getMinecraft().thePlayer != null) {
            if (LevelUpHUD.canShowSkills()) {
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiSkills());
            } else if (LevelUpHUD.canOpenClassSkillsMenu()) {
                Minecraft.getMinecraft().displayGuiScreen((GuiScreen)new GuiClasses());
            }
        }
    }
}
