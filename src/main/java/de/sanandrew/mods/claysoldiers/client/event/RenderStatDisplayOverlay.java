/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraftforge.client.event.RenderGameOverlayEvent$Text
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.event;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.client.helpers.ItemRenderHelper;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Quartet;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.entity.mount.EntityHorseMount;
import de.sanandrew.mods.claysoldiers.item.ItemClayManDoll;
import de.sanandrew.mods.claysoldiers.util.ModConfig;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.mount.EnumHorseType;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class RenderStatDisplayOverlay
extends Gui {
    private FontRenderer p_titleRenderer = null;
    private FontRenderer p_statRenderer = null;

    @SubscribeEvent
    public void renderText(RenderGameOverlayEvent.Text event) {
        Minecraft mc = Minecraft.getMinecraft();
        if (this.p_titleRenderer == null) {
            this.p_titleRenderer = mc.fontRenderer;
        }
        if (this.p_statRenderer == null) {
            this.p_statRenderer = new FontRenderer(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.renderEngine, true);
            if (mc.gameSettings.language != null) {
                this.p_statRenderer.setBidiFlag(mc.getLanguageManager().isCurrentLanguageBidirectional());
            }
        }
        ItemStack equippedStack = mc.thePlayer.getCurrentEquippedItem();
        if (!mc.gameSettings.showDebugInfo && equippedStack != null && equippedStack.getItem() == RegistryItems.statDisplay) {
            this.renderSoldiers(mc);
            this.renderMounts(mc);
        }
    }

    private void renderSoldiers(Minecraft mc) {
        List<EntityClayMan> soldiers = mc.theWorld.getEntitiesWithinAABB(EntityClayMan.class, RenderStatDisplayOverlay.getRangeAabbFromPlayer(mc.thePlayer));
        ArrayList<Quartet<Integer, String, Integer, ItemStack>> teams = new ArrayList<Quartet<Integer, String, Integer, ItemStack>>();
        HashMap<String, Integer> teamCounts = Maps.newHashMap();
        for (EntityClayMan entityClayMan : soldiers) {
            String team = entityClayMan.getClayTeam();
            if (teamCounts.containsKey(team)) {
                teamCounts.put(team, (Integer)teamCounts.get(team) + 1);
                continue;
            }
            teamCounts.put(team, 1);
        }
        for (Map.Entry<String, Integer> entry : teamCounts.entrySet()) {
            ClaymanTeam teamInst = ClaymanTeam.getTeam(entry.getKey());
            ItemStack renderedItem = new ItemStack(RegistryItems.dollSoldier);
            ItemClayManDoll.setTeamForItem(entry.getKey(), renderedItem);
            teams.add(Quartet.with(teamInst.getTeamColor(), renderedItem.getUnlocalizedName() + ".color", entry.getValue(), renderedItem));
        }
        this.renderStats(mc, SAPUtils.translate(RegistryItems.statDisplay.getUnlocalizedName() + ".title.soldiers"), teams, 5, 5);
    }

    private void renderMounts(Minecraft mc) {
        List<EntityHorseMount> horses = mc.theWorld.getEntitiesWithinAABB(EntityHorseMount.class, RenderStatDisplayOverlay.getRangeAabbFromPlayer(mc.thePlayer));
        ArrayList<Quartet<Integer, String, Integer, ItemStack>> teams = new ArrayList<Quartet<Integer, String, Integer, ItemStack>>();
        HashMap<String, Integer> teamCounts = Maps.newHashMap();
        for (EntityHorseMount entityHorseMount : horses) {
            String team = EnumHorseType.VALUES[entityHorseMount.getType()].toString();
            if (teamCounts.containsKey(team)) {
                teamCounts.put(team, (Integer)teamCounts.get(team) + 1);
                continue;
            }
            teamCounts.put(team, 1);
        }
        for (Map.Entry<String, Integer> entry : teamCounts.entrySet()) {
            EnumHorseType teamInst = EnumHorseType.valueOf(entry.getKey());
            teams.add(Quartet.with(teamInst.typeColor, entry.getKey(), entry.getValue(), null));
        }
        this.renderStats(mc, SAPUtils.translate(RegistryItems.statDisplay.getUnlocalizedName() + ".title.mounts"), teams, 110, 5);
    }

    private void renderStats(Minecraft mc, String title, List<Quartet<Integer, String, Integer, ItemStack>> teams, int xPos, int yPos) {
        this.drawGradientRect(xPos, yPos, xPos + 100, yPos + 13, 0, -2130706433);
        this.p_titleRenderer.drawString(title, xPos + 50 - this.p_titleRenderer.getStringWidth(title) / 2 - 1, yPos + 1, 0);
        this.p_titleRenderer.drawString(title, xPos + 50 - this.p_titleRenderer.getStringWidth(title) / 2, yPos + 2, 0);
        this.p_titleRenderer.drawString(title, xPos + 50 - this.p_titleRenderer.getStringWidth(title) / 2 + 1, yPos + 1, 0);
        this.p_titleRenderer.drawString(title, xPos + 50 - this.p_titleRenderer.getStringWidth(title) / 2, yPos, 0);
        this.p_titleRenderer.drawString(title, xPos + 50 - this.p_titleRenderer.getStringWidth(title) / 2, yPos + 1, 0xFFFFFF);
        this.drawGradientRect(xPos - 1, yPos - 1, xPos, yPos + 13, 0, -1073741824);
        this.drawGradientRect(xPos + 100, yPos - 1, xPos + 101, yPos + 13, 0, -1073741824);
        int pos = 0;
        for (Quartet<Integer, String, Integer, ItemStack> team : teams) {
            String text = SAPUtils.translate(team.getValue1()) + ": " + team.getValue2().toString();
            RenderStatDisplayOverlay.drawRect(xPos, yPos + 13 + pos * 11, xPos + 100, yPos + 24 + pos * 11, -2130706433);
            RenderStatDisplayOverlay.drawRect(xPos, yPos + 13 + pos * 11, xPos + 100, yPos + 23 + pos * 11, 0xC0000000 | team.getValue0());
            this.p_statRenderer.drawString(text, xPos + 50 - this.p_statRenderer.getStringWidth(text) / 2, yPos + 14 + pos * 11, RenderStatDisplayOverlay.getContrastTextColor(team.getValue0()));
            GL11.glPushMatrix();
            GL11.glScalef((float)0.5f, (float)0.5f, (float)0.5f);
            ItemRenderHelper.renderItemInGui(mc, team.getValue3(), xPos * 2, (yPos + 14 + pos * 11) * 2);
            GL11.glDisable((int)2896);
            GL11.glPopMatrix();
            ++pos;
        }
        RenderStatDisplayOverlay.drawRect(xPos - 1, yPos + 13, xPos, yPos + 13 + pos * 11, -1073741824);
        RenderStatDisplayOverlay.drawRect(xPos + 100, yPos + 13, xPos + 101, yPos + 13 + pos * 11, -1073741824);
        this.drawGradientRect(xPos - 1, yPos + 13 + pos * 11, xPos, yPos + 19 + pos * 11, -1073741824, 0);
        this.drawGradientRect(xPos + 100, yPos + 13 + pos * 11, xPos + 101, yPos + 19 + pos * 11, -1073741824, 0);
        this.drawGradientRect(xPos, yPos + 13 + pos * 11, xPos + 100, yPos + 18 + pos * 11, -2130706433, 0);
    }

    private static AxisAlignedBB getRangeAabbFromPlayer(EntityPlayer player) {
        return AxisAlignedBB.getBoundingBox(player.posX - ModConfig.statItemRange, player.posY - ModConfig.statItemRange, player.posZ - ModConfig.statItemRange, player.posX + ModConfig.statItemRange, player.posY + ModConfig.statItemRange, player.posZ + ModConfig.statItemRange);
    }

    private static int getContrastTextColor(int bkgColor) {
        SAPUtils.RGBAValues splitClr = SAPUtils.getRgbaFromColorInt(bkgColor);
        int yiq = (splitClr.getRed() * 299 + splitClr.getGreen() * 587 + splitClr.getBlue() * 144) / 1000;
        return yiq >= 128 ? 0 : 0xFFFFFF;
    }
}
