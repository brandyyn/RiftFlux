package gravestone.gui;

import gravestone.ModGraveStone;
import gravestone.core.GSMessageHandler;
import gravestone.packets.GraveDeathMessageToServer;
import gravestone.tileentity.TileEntityGSGrave;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import org.lwjgl.input.Keyboard;

public class GSGuiGrave extends GuiScreen {
   private final String titleStr = ModGraveStone.proxy.getLocalizedString("gui.edit_grave.title");
   private final String closeStr = ModGraveStone.proxy.getLocalizedString("gui.edit_grave.close");
   private final String randomTextStr = ModGraveStone.proxy.getLocalizedString("gui.edit_grave.randomText");
   private GuiButton closeButton;
   private GuiButton randomTextButton;
   private GuiTextField textField;
   private TileEntityGSGrave teGrave;
   private boolean isRandomTextButtonClicked = false;

   public GSGuiGrave(TileEntityGSGrave teGrave) {
      this.teGrave = teGrave;
   }

   public void initGui() {
      this.buttonList.clear();
      Keyboard.enableRepeatEvents(true);
      this.buttonList.add(this.randomTextButton = new GuiButton(1, this.width / 2 - 100, this.height / 4 + 95, this.randomTextStr));
      this.buttonList.add(this.closeButton = new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120, this.closeStr));
      this.textField = new GuiTextField(this.fontRendererObj, this.width / 2 - 100, 100, 200, 20);
      this.textField.setText("");
      this.textField.setEnabled(true);
      this.textField.setFocused(true);
      this.textField.setCanLoseFocus(false);
      this.textField.setMaxStringLength(30);
   }

   public void actionPerformed(GuiButton button) {
      switch(button.id) {
      case 0:
         this.mc.displayGuiScreen((GuiScreen)null);
         break;
      case 1:
         this.isRandomTextButtonClicked = true;
         this.mc.displayGuiScreen((GuiScreen)null);
      }

      GSMessageHandler.networkWrapper.sendToServer(new GraveDeathMessageToServer(this.teGrave.getWorldObj(), this.teGrave.xCoord, this.teGrave.yCoord, this.teGrave.zCoord, this.textField.getText(), this.isRandomTextButtonClicked));
   }

   public void drawScreen(int x, int y, float f) {
      this.drawDefaultBackground();
      this.drawString(this.fontRendererObj, this.titleStr, this.width / 2 - 40, 60, 16777215);
      this.textField.drawTextBox();
      super.drawScreen(x, y, f);
   }

   protected void keyTyped(char key, int keyCode) {
      this.textField.textboxKeyTyped(key, keyCode);
      if (keyCode == 1) {
         this.actionPerformed(this.closeButton);
      }

   }
}
