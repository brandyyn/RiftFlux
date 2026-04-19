/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.util.ResourceLocation
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.equipment.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.core.gui.IGuiOverlay;

@SideOnly(value=Side.CLIENT)
public class GuiScreenSpyGlass
extends Gui
implements IGuiOverlay {
    private ResourceLocation spyglassOverlayTexture = new ResourceLocation("worldexplorer", "textures/misc/spyglass_overlay.png");
    private Minecraft mc = Minecraft.getMinecraft();

    @Override
    public void draw() {
        ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
        this.mc.entityRenderer.setupOverlayRendering();
        this.mc.renderEngine.bindTexture(this.spyglassOverlayTexture);
        float scaleX = (float)res.getScaledWidth_double() / 256.0f;
        float scaleY = (float)res.getScaledHeight_double() / 256.0f;
        GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
        GL11.glBlendFunc((int)770, (int)771);
        GL11.glEnable((int)3042);
        GL11.glScalef((float)scaleX, (float)scaleY, (float)0.0f);
        this.drawTexturedModalRect(0, 0, 0, 0, 256, 256);
        GL11.glScalef((float)(1.0f / scaleX), (float)(1.0f / scaleY), (float)0.0f);
    }
}

