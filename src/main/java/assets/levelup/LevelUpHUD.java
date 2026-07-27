/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.ScaledResolution
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.util.StatCollector
 *  net.minecraftforge.client.event.FOVUpdateEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$ElementType
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Pre
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Text
 */
package assets.levelup;

import assets.levelup.ClassBonus;
import assets.levelup.FMLEventHandler;
import assets.levelup.LevelUp;
import assets.levelup.PlayerEventHandler;
import assets.levelup.PlayerExtendedProperties;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.awt.Color;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public final class LevelUpHUD
extends Gui {
    public static final LevelUpHUD INSTANCE = new LevelUpHUD();
    private static final float MIN_PULSE_BRIGHTNESS = 0.4f;
    private static final float MAX_PULSE_BRIGHTNESS = 1.0f;
    private static final float PULSE_HUE = 0.2929688f;

    private LevelUpHUD() {
    }

    public void addToText(List<String> left) {
        byte playerClass = PlayerExtendedProperties.getPlayerClass(LevelUp.proxy.getPlayer());
        if (playerClass != 0) {
            int skillXP;
            if (!LevelUp.renderExpBar && (skillXP = PlayerExtendedProperties.from(LevelUp.proxy.getPlayer()).getSkillFromIndex("XP")) > 0) {
                left.add(StatCollector.translateToLocalFormatted((String)"hud.skill.text1", (Object[])new Object[]{skillXP}));
            }
            left.add(StatCollector.translateToLocalFormatted((String)"hud.skill.text2", (Object[])new Object[]{ClassBonus.getClassName(playerClass)}));
        } else if (LevelUpHUD.canSelectClass() && !LevelUp.renderExpBar) {
            left.add(StatCollector.translateToLocal((String)"hud.skill.select"));
        }
    }

    @SubscribeEvent
    public void renderLvlUpHUD(RenderGameOverlayEvent.Pre event) {
        if (LevelUp.allowHUD && LevelUp.proxy.getPlayer() != null) {
            if (LevelUp.renderTopLeft && event.type == RenderGameOverlayEvent.ElementType.TEXT) {
                this.addToText(((RenderGameOverlayEvent.Text)event).left);
            }
            if (LevelUp.renderExpBar && event.type == RenderGameOverlayEvent.ElementType.EXPERIENCE) {
                this.addToExpBar(event.resolution);
            }
        }
    }

    @SubscribeEvent
    public void onFOV(FOVUpdateEvent event) {
        if (!LevelUp.changeFOV && !event.entity.isUsingItem()) {
            float speedPercent = 0.0F;
            if (event.entity.isSneaking()) {
                speedPercent = (float)FMLEventHandler.getSkill((EntityPlayer)event.entity, 8)
                        * ModConfig.levelUpSneakingSpeedPercentPerPoint;
            } else if (event.entity.isSprinting()) {
                speedPercent = (float)FMLEventHandler.getSkill((EntityPlayer)event.entity, 6)
                        * ModConfig.levelUpAthleticsSprintSpeedPercentPerPoint;
            }
            if (speedPercent > 0.0F) {
                event.newfov -= 0.5f;
                event.newfov *= 1.0f / (1.0f + speedPercent / 100.0f);
                event.newfov += 0.5f;
            }
        }
    }

    private void addToExpBar(ScaledResolution res) {
        String text = null;
        if (LevelUpHUD.canShowSkills()) {
            int skillXP = PlayerExtendedProperties.from(LevelUp.proxy.getPlayer()).getSkillFromIndex("XP");
            if (skillXP > 0) {
                text = StatCollector.translateToLocalFormatted((String)"hud.skill.text1", (Object[])new Object[]{skillXP});
            }
        } else if (LevelUpHUD.canSelectClass()) {
            text = StatCollector.translateToLocal((String)"hud.skill.select");
        }
        if (text != null) {
            int x = (res.getScaledWidth() - Minecraft.getMinecraft().fontRenderer.getStringWidth(text)) / 2;
            int y = res.getScaledHeight() - 29;
            int col = getPulseColor();
            Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, col);
        }
        Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.icons);
    }

    public static int getPulseColor() {
        return Color.HSBtoRGB(PULSE_HUE, 1.0f, getPulseBrightness()) & 0xFFFFFF;
    }

    private static float getPulseBrightness() {
        float speed = ModConfig.levelUpHudPulseSpeedHz;
        if (speed <= 0.0f) {
            return MAX_PULSE_BRIGHTNESS;
        }
        double phase = (Minecraft.getSystemTime() / 1000.0D) * speed;
        double fraction = phase - Math.floor(phase);
        double triangle = fraction < 0.5D ? fraction * 2.0D : (1.0D - fraction) * 2.0D;
        return (float)(MIN_PULSE_BRIGHTNESS + triangle * (MAX_PULSE_BRIGHTNESS - MIN_PULSE_BRIGHTNESS));
    }

    public static boolean canSelectClass() {
        return canSelectClass(LevelUp.proxy.getPlayer());
    }

    public static boolean canSelectClass(EntityPlayer player) {
        if (player == null) {
            return false;
        }
        int requiredLevel = ModConfig.levelUpClassSelectionLevel;
        if (player.experienceLevel >= requiredLevel) {
            return true;
        }
        int points = PlayerExtendedProperties.from(player).getSkillPoints();
        return (double)points >= (double)requiredLevel * PlayerEventHandler.xpPerLevel;
    }

    public static boolean canOpenClassSkillsMenu() {
        return ModConfig.levelUpOpenClassSkillsMenuAtAnyLevel || canSelectClass();
    }

    public static boolean canShowSkills() {
        return PlayerExtendedProperties.from(LevelUp.proxy.getPlayer()).hasClass();
    }
}
