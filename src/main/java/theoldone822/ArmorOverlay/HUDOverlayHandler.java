/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Loader
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.entity.EntityClientPlayerMP
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.GuiIngameForge
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Post
 *  net.minecraftforge.common.MinecraftForge
 */
package theoldone822.ArmorOverlay;

import com.voidsrift.riftflux.dualhotbar.DualHotbarConfig;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityClientPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import theoldone822.ArmorDamageRecalc.API.ExtendedHandler;
import theoldone822.ArmorOverlay.ArmorOverlay;

@SideOnly(value=Side.CLIENT)
public class HUDOverlayHandler {
    private static final ResourceLocation modIcons = new ResourceLocation("armoroverlay", "textures/icons.png");

    public static void init() {
        HUDOverlayHandler hudOverlayHandler = new HUDOverlayHandler();
        FMLCommonHandler.instance().bus().register((Object)hudOverlayHandler);
        MinecraftForge.EVENT_BUS.register((Object)hudOverlayHandler);
    }

    @SubscribeEvent
    public void onRender(RenderGameOverlayEvent.Post event) {
        if (event.type != RenderGameOverlayEvent.ElementType.ALL) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        EntityClientPlayerMP player = mc.thePlayer;
        if (player == null) {
            return;
        }
        if ((player.capabilities != null && player.capabilities.isCreativeMode) || (mc.playerController != null && mc.playerController.isInCreativeMode())) {
            return;
        }
        ScaledResolution scale = event.resolution != null ? event.resolution : new ScaledResolution(mc, mc.displayWidth, mc.displayHeight);
        int left = scale.getScaledWidth() / 2 - 91;
        int top = HUDOverlayHandler.getArmorOverlayTop(scale, player);
        int stats = !Loader.isModLoaded((String)"ArmorDamageRecalc") ? ForgeHooks.getTotalArmorValue(player) : (int)Math.floor(ExtendedHandler.getExtendedArmorValue((EntityLivingBase)player));
        if (ArmorOverlay.overlayLevels == 3 && ArmorOverlay.armorPices == 2 && !Loader.isModLoaded((String)"ArmorDamageRecalc")) {
            HUDOverlayHandler.drawAromrOverlay(stats, mc, left, top);
        } else {
            if (ArmorOverlay.armorPices == 4) {
                HUDOverlayHandler.drawExtendedAromrOverlay(stats, mc, left, top);
            }
            if (ArmorOverlay.armorPices == 2) {
                HUDOverlayHandler.drawExtendedAromrOverlay(stats + stats, mc, left, top);
            }
            if (ArmorOverlay.armorPices == 1) {
                HUDOverlayHandler.drawExtendedAromrOverlay(stats * 4, mc, left, top);
            }
        }
    }

    private static int getArmorOverlayTop(ScaledResolution scale, EntityClientPlayerMP player) {
        int maxHealth = MathHelper.ceiling_float_int((float)player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue());
        int absorption = MathHelper.ceiling_float_int((float)player.getAbsorptionAmount());
        int healthRows = MathHelper.ceiling_float_int((float)((float)(maxHealth + absorption) / 4.0f / 10.0f));
        if (healthRows < 1) {
            healthRows = 1;
        }
        int y = scale.getScaledHeight() - 39 - healthRows * 10;
        return y - HUDOverlayHandler.getDualHotbarYOffset();
    }

    private static int getDualHotbarYOffset() {
        if (!DualHotbarConfig.enable) {
            return 0;
        }
        if (DualHotbarConfig.twoLayerRendering) {
            return 20 * Math.max(0, DualHotbarConfig.numHotbars - 1);
        }
        if (DualHotbarConfig.numHotbars == 4) {
            return 20 * Math.max(0, DualHotbarConfig.numHotbars / 2 - 1);
        }
        return 0;
    }

    public static void drawAromrOverlay(int armorLevel, Minecraft mc, int left, int top) {
        float HalfArmorBar;
        int y;
        int x;
        int i;
        if (armorLevel < 21) {
            return;
        }
        int armorLevel2 = armorLevel - 20;
        if (armorLevel > 40) {
            armorLevel2 = 20;
        }
        int endBar = (int)Math.ceil((float)Math.min(20, armorLevel2) / 2.0f);
        mc.getTextureManager().bindTexture(modIcons);
        for (i = 0; i < endBar + 1; ++i) {
            x = left + i * 8;
            y = top;
            HalfArmorBar = armorLevel2 / 2 - i;
            if (HalfArmorBar >= 1.0f) {
                mc.ingameGUI.drawTexturedModalRect(x, y, 9, 0, 9, 9);
                continue;
            }
            if (HalfArmorBar == 0.0f) continue;
            mc.ingameGUI.drawTexturedModalRect(x - 8, y, 0, 0, 9, 9);
        }
        if (armorLevel > 40) {
            if (armorLevel > 60) {
                armorLevel = 60;
            }
            endBar = (int)Math.ceil((float)Math.min(20, armorLevel - 40) / 2.0f);
            mc.getTextureManager().bindTexture(modIcons);
            for (i = 0; i < endBar + 1; ++i) {
                x = left + i * 8;
                y = top;
                HalfArmorBar = (armorLevel - 40) / 2 - i;
                if (HalfArmorBar >= 1.0f) {
                    mc.ingameGUI.drawTexturedModalRect(x, y, 27, 0, 9, 9);
                    continue;
                }
                if (HalfArmorBar == 0.0f) continue;
                mc.ingameGUI.drawTexturedModalRect(x - 8, y, 18, 0, 9, 9);
            }
        }
        if (ArmorOverlay.showNumbers) {
            mc.ingameGUI.drawString(mc.fontRenderer, Integer.toString(armorLevel), left + 82, top + 1, 0xFFFFFF);
        }
        mc.getTextureManager().bindTexture(Gui.icons);
    }

    public static void drawExtendedAromrOverlay(int armorLevel, Minecraft mc, int left, int top) {
        float al = armorLevel;
        if (al > (float)(ArmorOverlay.overlayLevels * 40)) {
            al = ArmorOverlay.overlayLevels * 40;
        }
        int textureOfset1 = 0;
        int textureOfset2 = 0;
        if (ArmorOverlay.overlayLevels != 20) {
            textureOfset2 = 36;
        }
        if (ArmorOverlay.overlayLevels == 3) {
            textureOfset1 = 54;
        }
        if (ArmorOverlay.overlayLevels == 10) {
            textureOfset1 = 90;
        }
        int f1 = (int)Math.floor(al / 40.0f);
        int f2 = (int)Math.floor(al - (float)(40 * f1));
        int f3 = (int)Math.floor(f2 / 4);
        if (f1 < 1 && f2 < 1 && f3 < 1) {
            return;
        }
        mc.getTextureManager().bindTexture(modIcons);
        for (int i = 0; i < 10; ++i) {
            int x = left + i * 8;
            int y = top;
            if (f2 > 4 * i) {
                mc.ingameGUI.drawTexturedModalRect(x, y, textureOfset1 + (f1 + 1) * 9, textureOfset2 + 9 * Math.min(f2 - 4 * i, 4), 9, 9);
                continue;
            }
            mc.ingameGUI.drawTexturedModalRect(x, y, textureOfset1 + f1 * 9, textureOfset2 + 36, 9, 9);
        }
        if (ArmorOverlay.showNumbers) {
            if (ArmorOverlay.armorPices == 4) {
                mc.ingameGUI.drawString(mc.fontRenderer, Integer.toString(armorLevel), left + 82, top + 1, 0xFFFFFF);
            }
            if (ArmorOverlay.armorPices == 2) {
                mc.ingameGUI.drawString(mc.fontRenderer, Integer.toString(armorLevel / 2), left + 82, top + 1, 0xFFFFFF);
            }
            if (ArmorOverlay.armorPices == 1) {
                mc.ingameGUI.drawString(mc.fontRenderer, Integer.toString(armorLevel / 4), left + 82, top + 1, 0xFFFFFF);
            }
        }
        mc.getTextureManager().bindTexture(Gui.icons);
    }
}
