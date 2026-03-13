/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.util.Timer
 *  net.minecraftforge.client.GuiIngameForge
 *  net.minecraftforge.client.event.RenderGameOverlayEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  org.lwjgl.opengl.GL11
 */
package inurosen.healaltar.common.gui;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import inurosen.healaltar.common.entity.ExtendedPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class GuiSoulHearts
extends GuiIngameForge {
    private Minecraft mc;
    public final ResourceLocation soulHeart = new ResourceLocation("healingaltar", "textures/gui/soulheart.png");

    public GuiSoulHearts(Minecraft mc) {
        super(mc);
        this.mc = mc;
    }

    @SubscribeEvent
    public void onRenderExperienceBar(RenderGameOverlayEvent event) {
        if (event.isCancelable() || event.type != RenderGameOverlayEvent.ElementType.EXPERIENCE || this.mc.thePlayer == null || this.mc.thePlayer.capabilities.isCreativeMode) {
            return;
        }

        EntityPlayer player = this.mc.thePlayer;
        ExtendedPlayer props = ExtendedPlayer.get(player);
        if (props == null) {
            return;
        }

        float maxSoulHearts = player.getMaxHealth();
        if (maxSoulHearts <= 0.0f) {
            maxSoulHearts = 20.0f;
        }

        float soulHealth = Math.max(0.0f, Math.min(props.getSoulHearts(), maxSoulHearts));
        float hpPerSoulIcon = ModConfig.zeldaHeartsEnabled ? 4.0f : 2.0f;
        float displayedIconsRaw = soulHealth / hpPerSoulIcon;
        float displayedIcons = (float)Math.floor((double)(displayedIconsRaw * 2.0f)) / 2.0f;
        int heartsToDraw = MathHelper.ceiling_float_int((float)displayedIcons);
        if (heartsToDraw <= 0) {
            return;
        }

        int xs = event.resolution.getScaledWidth();
        int ys = event.resolution.getScaledHeight();
        GL11.glDisable((int)2929);
        GL11.glDepthMask((boolean)false);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)0.85f);
        GL11.glDisable((int)3008);
        this.mc.getTextureManager().bindTexture(this.soulHeart);
        int baseX = xs / 2 - 91;
        int baseY = ys - 39;
        for (int i = 0; i < heartsToDraw; ++i) {
            int row = i / 10;
            int col = i % 10;
            int x = baseX + col * 8;
            int y = baseY - row * 10;
            float remaining = displayedIcons - (float)i;
            if (remaining >= 1.0f) {
                this.drawTexturedModalRect(x, y, 0, 0, 9, 9);
                continue;
            }
            if (remaining >= 0.5f) {
                this.drawTexturedModalRect(x, y, 9, 0, 9, 9);
            }
        }
        GL11.glDepthMask((boolean)true);
        GL11.glEnable((int)2929);
        GL11.glEnable((int)3008);
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
    }
}
