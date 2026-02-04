package com.voidsrift.riftflux.vortex.client.gui;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Iterator;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.EnumChatFormatting;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.network.PacketHCPlayerRespawn;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public class GuiRuneGameOver extends GuiScreen {
   private int ticks;

   public void initGui() {
      this.buttonList.clear();
      this.buttonList.add(new GuiTaintedButton(0, this.width / 2 - 100, this.height / 4 + 140, I18n.format("gui.runegameover.blurb2", new Object[0])));
      if (this.mc.isIntegratedServerRunning()) {
         this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.titleScreen", new Object[0])));
      } else {
         this.buttonList.add(new GuiButton(1, this.width / 2 - 100, this.height / 4 + 96, I18n.format("deathScreen.leaveServer", new Object[0])));
      }

      GuiButton guibutton;
      for(Iterator iterator = this.buttonList.iterator(); iterator.hasNext(); guibutton.enabled = false) {
         guibutton = (GuiButton)iterator.next();
      }

   }

   public void drawScreen(int p_73863_1_, int p_73863_2_, float p_73863_3_) {
      this.drawGradientRect(0, 0, this.width, this.height, 1615855616, -1602211792);
      GL11.glPushMatrix();
      GL11.glScalef(2.0F, 2.0F, 2.0F);
      this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.title.hardcore", new Object[0]), this.width / 2 / 2, 30, 16777215);
      GL11.glPopMatrix();
      this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.hardcoreInfo", new Object[0]), this.width / 2, 144, 16777215);
      this.drawCenteredString(this.fontRendererObj, I18n.format("deathScreen.score", new Object[0]) + ": " + EnumChatFormatting.YELLOW + this.mc.thePlayer.getScore(), this.width / 2, 100, 16777215);
      this.drawCenteredString(this.fontRendererObj, I18n.format("gui.runegameover.blurb1", new Object[0]), this.width / 2, this.height / 4 + 124, 16777215);
      super.drawScreen(p_73863_1_, p_73863_2_, p_73863_3_);
   }

   protected void keyTyped(char p_73869_1_, int p_73869_2_) {
   }

   protected void actionPerformed(GuiButton button) {
      switch(button.id) {
      case 0:
         ModPackets.instance.sendToServer(new PacketHCPlayerRespawn(this.mc.thePlayer));
         break;
      case 1:
         this.mc.theWorld.sendQuittingDisconnectingPacket();
         this.mc.loadWorld((WorldClient)null);
         this.mc.displayGuiScreen(new GuiMainMenu());
      }

   }

   public void updateScreen() {
      super.updateScreen();
      ++this.ticks;
      GuiButton guibutton;
      if (this.ticks == 20) {
         for(Iterator iterator = this.buttonList.iterator(); iterator.hasNext(); guibutton.enabled = true) {
            guibutton = (GuiButton)iterator.next();
         }
      }

   }

   public boolean doesGuiPauseGame() {
      return false;
   }
}
