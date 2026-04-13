/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.potion.Potion
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.GuiIngameForge
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Pre
 *  net.minecraftforge.common.ForgeHooks
 *  org.lwjgl.opengl.GL11
 */
package zelda;

import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.GuiIngameForge;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.ForgeHooks;
import org.lwjgl.opengl.GL11;
import zelda.Config;

public class GuiHUD
extends Gui {
    private Minecraft mc;
    protected final Random rand = new Random();
    public static final ResourceLocation hearts = new ResourceLocation("zelda", "textures/gui/icons.png");

    public GuiHUD(Minecraft mc) {
        this.mc = Minecraft.getMinecraft();
    }

    private void bind(ResourceLocation res) {
        this.mc.getTextureManager().bindTexture(res);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST, receiveCanceled = true)
    public void armorGUI(RenderGameOverlayEvent.Pre event) {
        if (event == null) {
            return;
        }
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        if (event.type == null) {
            return;
        }
        if (event.type.equals((Object)RenderGameOverlayEvent.ElementType.ARMOR)) {
            event.setCanceled(true);
            this.mc.mcProfiler.startSection("armor");
            ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            GuiIngameForge cfr_ignored_0 = (GuiIngameForge)this.mc.ingameGUI;
            int left_height = GuiIngameForge.left_height;
            int healthRows = MathHelper.ceiling_float_int((float)(((float)this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue() + (float)MathHelper.ceiling_float_int((float)this.mc.thePlayer.getAbsorptionAmount())) / 4.0f / 10.0f));
            GL11.glEnable((int)3042);
            int left = width / 2 - 91;
            int top = height - left_height;
            int level = ForgeHooks.getTotalArmorValue((EntityPlayer)this.mc.thePlayer);
            for (int i = 1; level > 0 && i < 20; i += 2) {
                if (i < level) {
                    this.drawTexturedModalRect(left, top - healthRows * 10, 34, 9, 9, 9);
                } else if (i == level) {
                    this.drawTexturedModalRect(left, top - healthRows * 10, 25, 9, 9, 9);
                } else if (i > level) {
                    this.drawTexturedModalRect(left, top - healthRows * 10, 16, 9, 9, 9);
                }
                left += 8;
            }
            left_height += 10;
            GL11.glDisable((int)3042);
            this.mc.mcProfiler.endSection();
        }
    }

    @SubscribeEvent
    public void heartGUI(RenderGameOverlayEvent.Pre event) {
        if (event == null) {
            return;
        }
        if (!Config.HEARTS_ENABLED) {
            return;
        }
        if (event.type == null) {
            return;
        }
        if (event.type.equals((Object)RenderGameOverlayEvent.ElementType.HEALTH)) {
            boolean highlight;
            event.setCanceled(true);
            this.bind(hearts);
            this.mc.mcProfiler.startSection("health");
            GL11.glEnable((int)3042);
            boolean bl = highlight = this.mc.thePlayer.hurtResistantTime / 3 % 2 == 1;
            if (this.mc.thePlayer.hurtResistantTime < 10) {
                highlight = false;
            }
            IAttributeInstance attrMaxHealth = this.mc.thePlayer.getEntityAttribute(SharedMonsterAttributes.maxHealth);
            int health = MathHelper.ceiling_float_int((float)this.mc.thePlayer.getHealth());
            int healthLast = MathHelper.ceiling_float_int((float)this.mc.thePlayer.prevHealth);
            float healthMax = (float)attrMaxHealth.getAttributeValue();
            int absorb = MathHelper.ceiling_float_int((float)this.mc.thePlayer.getAbsorptionAmount());
            int baseHearts = MathHelper.ceiling_float_int((float)(healthMax / 4.0f));
            int totalHearts = baseHearts + MathHelper.ceiling_float_int((float)absorb / 4.0f);
            int healthRows = MathHelper.ceiling_float_int((float)((float)totalHearts / 10.0f));
            int rowHeight = 10;
            this.rand.setSeed(this.mc.ingameGUI.getUpdateCounter() * 312871);
            ScaledResolution res = new ScaledResolution(this.mc, this.mc.displayWidth, this.mc.displayHeight);
            int width = res.getScaledWidth();
            int height = res.getScaledHeight();
            GuiIngameForge cfr_ignored_0 = (GuiIngameForge)this.mc.ingameGUI;
            int left_height = GuiIngameForge.left_height;
            int left = width / 2 - 91;
            int top = height - left_height;
            left_height += healthRows * rowHeight;
            int regen = -1;
            if (this.mc.thePlayer.isPotionActive(Potion.regeneration)) {
                regen = this.mc.ingameGUI.getUpdateCounter() % 25;
            }
            int TOP = 9 * (this.mc.theWorld.getWorldInfo().isHardcoreModeEnabled() ? 5 : 0);
            int BACKGROUND = highlight ? 25 : 16;
            int MARGIN = 16;
            if (this.mc.thePlayer.isPotionActive(Potion.poison)) {
                MARGIN += 36;
            } else if (this.mc.thePlayer.isPotionActive(Potion.wither)) {
                MARGIN += 72;
            }
            int absorbRemaining = absorb;
            int myRow = this.mc.thePlayer.isPotionActive(Potion.wither) ? 4 : (this.mc.thePlayer.isPotionActive(Potion.poison) ? 2 : 0);
            for (int i = totalHearts - 1; i >= 0; --i) {
                int j;
                int STEPS_PER_HEART;
                int HEALTH_PER_STEP;
                int[] heartStage;
                int row = MathHelper.ceiling_float_int((float)((float)(i + 1) / 10.0f)) - 1;
                int x = left + i % 10 * 8;
                int y = top - row * rowHeight;
                if (health <= 4) {
                    y += this.rand.nextInt(2);
                }
                if (i == regen) {
                    y -= 2;
                }
                this.bind(icons);
                this.drawTexturedModalRect(x, y, BACKGROUND, TOP, 9, 9);
                this.bind(hearts);
                if (highlight) {
                    if (i * 2 + 1 < healthLast) {
                        this.drawTexturedModalRect(x, y, 0, 0, 9, 9);
                    } else if (i * 2 + 1 == healthLast) {
                        this.drawTexturedModalRect(x, y, 0, 0, 9, 9);
                    }
                }
                if ((float)absorbRemaining > 0.0f) {
                    heartStage = new int[]{200, 27, 18, 9, 0};
                    HEALTH_PER_STEP = 1;
                    STEPS_PER_HEART = 4;
                    this.drawHeartAt(x, y, heartStage[MathHelper.clamp_int((int)(MathHelper.ceiling_float_int((float)(absorb / HEALTH_PER_STEP)) - i * STEPS_PER_HEART), (int)0, (int)STEPS_PER_HEART)], 6);
                    absorbRemaining = (int)((float)absorbRemaining - 4.0f);
                    continue;
                }
                heartStage = new int[]{200, 27, 18, 9, 0};
                HEALTH_PER_STEP = 1;
                STEPS_PER_HEART = 4;
                this.drawHeartAt(x, y, heartStage[MathHelper.clamp_int((int)(MathHelper.ceiling_float_int((float)(health / HEALTH_PER_STEP)) - i * STEPS_PER_HEART), (int)0, (int)STEPS_PER_HEART)], myRow);
            }
            GL11.glDisable((int)3042);
            this.mc.mcProfiler.endSection();
            this.bind(icons);
        }
    }

    public void drawHeartAt(int x, int y, int heartStageImage, int row) {
        this.drawTexturedModalRect(x, y, heartStageImage, row * 9, 9, 9);
    }
}
