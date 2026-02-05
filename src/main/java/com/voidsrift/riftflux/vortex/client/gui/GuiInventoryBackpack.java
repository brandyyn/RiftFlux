package com.voidsrift.riftflux.vortex.client.gui;

import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import com.voidsrift.riftflux.vortex.item.ItemBackpack;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.container.ContainerBackpack;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.network.PacketBackpackGuiHandle;
import org.lwjgl.opengl.GL11;

public class GuiInventoryBackpack extends GuiInventory {
   public static final ResourceLocation texture = new ResourceLocation("riftflux", "textures/gui/inventorybackpack.png");
   public static final ResourceLocation overlay = new ResourceLocation("riftflux", "textures/gui/inventorybackpack_overlay.png");
   private static final int EXTENDED_HEIGHT = 224;
   private final EntityPlayer player;
   private final ItemStack backpack;
   private Integer backpackIdentityId;
   private int reopenCooldown = 0;
   private int syncRequestCooldown = 0;
   private boolean pendingReopen = false;

   public GuiInventoryBackpack(EntityPlayer player) {
      super(player);
      this.player = player;
      this.backpack = ItemBackpack.getEquippedBackpack(player);
      this.inventorySlots = new ContainerBackpack(player);
      this.backpackIdentityId = getBackpackGuiId(this.backpack);
   }

   public void initGui() {
      // Keep the vanilla inventory position (GuiInventory uses ySize=166 for centering),
      // then extend the GUI downwards for the backpack slots.
      this.ySize = 166;
      super.initGui();
      this.ySize = 166;
   }

   public void updateScreen() {
      super.updateScreen();
      if (this.reopenCooldown > 0) {
         --this.reopenCooldown;
      }
      if (this.syncRequestCooldown > 0) {
         --this.syncRequestCooldown;
      }
      ItemStack armor = ItemBackpack.getEquippedBackpack(this.player);
      boolean hasBackpack = armor != null && armor.getItem() == ModItems.backpack;
      if (!hasBackpack) {
         this.closeBackpackGui();
         return;
      }
      Integer armorId = getBackpackGuiId(armor);
      if (this.backpackIdentityId == null) {
         this.backpackIdentityId = getBackpackGuiId(this.backpack);
      }
      if (this.backpackIdentityId == null && armorId != null) {
         this.backpackIdentityId = armorId;
      }
      if (armorId == null || this.backpackIdentityId == null) {
         if (this.syncRequestCooldown == 0) {
            // Ask server to ensure the equipped backpack has an ID and reopen if needed.
            ModPackets.instance.sendToServer(new PacketBackpackGuiHandle(this.player, true));
            this.syncRequestCooldown = 20;
         }
         if (armor != this.backpack) {
            this.pendingReopen = true;
         }
         return;
      }
      boolean sameBackpack = armorId.equals(this.backpackIdentityId);
      if (!sameBackpack || this.pendingReopen) {
         // Avoid dropping the cursor stack if the player is mid-drag.
         if (this.player.inventory.getItemStack() != null) {
            this.pendingReopen = true;
            return;
         }
         if (this.reopenCooldown > 0) {
            return;
         }
         this.pendingReopen = false;
         this.reopenCooldown = 5;
         this.closeBackpackGui();
         ModPackets.instance.sendToServer(new PacketBackpackGuiHandle(this.player, true));
      }
      if (sameBackpack) {
         this.pendingReopen = false;
      }

   }

   private void closeBackpackGui() {
      if (this.player.openContainer instanceof ContainerBackpack) {
         ContainerBackpack containerBackpack = (ContainerBackpack)this.player.openContainer;

         for(int i = 0; i < 4; ++i) {
            ItemStack itemStack = containerBackpack.craftMatrix.getStackInSlotOnClosing(i);
            if (itemStack != null) {
               this.player.dropPlayerItemWithRandomChoice(itemStack, false);
            }
         }

         containerBackpack.craftResult.setInventorySlotContents(0, (ItemStack)null);
      }

      ModPackets.instance.sendToServer(new PacketBackpackGuiHandle(this.player, false));
      this.mc.displayGuiScreen(new GuiInventory(this.player));
   }

   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
      int prev = this.ySize;
      this.ySize = EXTENDED_HEIGHT;
      try {
         super.mouseClicked(mouseX, mouseY, mouseButton);
      } finally {
         this.ySize = prev;
      }
   }

   protected void mouseMovedOrUp(int mouseX, int mouseY, int state) {
      int prev = this.ySize;
      this.ySize = EXTENDED_HEIGHT;
      try {
         super.mouseMovedOrUp(mouseX, mouseY, state);
      } finally {
         this.ySize = prev;
      }
   }

   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      int prev = this.ySize;
      this.ySize = EXTENDED_HEIGHT;
      try {
         super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      } finally {
         this.ySize = prev;
      }
   }

   protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
      GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.mc.getTextureManager().bindTexture(texture);
      this.drawTexturedModalRect(this.guiLeft, this.guiTop, 0, 0, this.xSize, this.ySize);
      GL11.glPushMatrix();
      this.mc.getTextureManager().bindTexture(overlay);
      int color = this.backpack.getItem().getColorFromItemStack(this.backpack, 0);
      float red = (float)(color >> 16 & 255) / 255.0F;
      float green = (float)(color >> 8 & 255) / 255.0F;
      float blue = (float)(color & 255) / 255.0F;
      GL11.glColor4f(red, green, blue, 1.0F);
      GL11.glPopMatrix();
      // Backpack slot overlay: moved to bottom
      this.drawTexturedModalRect(this.guiLeft + 5, this.guiTop + 163, 0, 0, this.xSize, 76);
      func_147046_a(this.guiLeft + 51, this.guiTop + 75, 30, (float)(this.guiLeft + 51) - (float)mouseX, (float)(this.guiTop + 25) - (float)mouseY, this.mc.thePlayer);
   }

   private static Integer getBackpackGuiId(ItemStack stack) {
      if (stack == null) return null;
      NBTTagCompound tag = stack.getTagCompound();
      if (tag == null || !tag.hasKey("backpackGuiId", 3)) return null;
      return tag.getInteger("backpackGuiId");
   }
}
