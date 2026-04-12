/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.FMLClientHandler
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.ModContainer
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraftforge.common.MinecraftForge
 */
package assets.levelup;

import assets.levelup.LevelUp;
import assets.levelup.LevelUpHUD;
import assets.levelup.SkillKeyHandler;
import assets.levelup.SkillProxy;
import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.ModContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;

public final class SkillClientProxy
extends SkillProxy {
    @Override
    public void tryUseMUD() {
        try {
            Class.forName("mods.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, String.class, String.class).invoke(null, FMLCommonHandler.instance().findContainerFor((Object)LevelUp.instance), "https://raw.github.com/GotoLink/LevelUp/master/update.xml", "https://raw.github.com/GotoLink/LevelUp/master/changelog.md");
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    @Override
    public void registerGui() {
        MinecraftForge.EVENT_BUS.register((Object)LevelUpHUD.INSTANCE);
        FMLCommonHandler.instance().bus().register((Object)SkillKeyHandler.INSTANCE);
    }

    @Override
    public EntityPlayer getPlayer() {
        return FMLClientHandler.instance().getClient().thePlayer;
    }
}

