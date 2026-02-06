package com.voidsrift.riftflux.vortex.lib.helper;

import baubles.api.BaublesApi;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import com.voidsrift.riftflux.vortex.client.gui.ButtonToolbeltRadial;
import com.voidsrift.riftflux.vortex.event.KeyEventHandler;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.container.InventoryToolbelt;
import com.voidsrift.riftflux.vortex.lib.helper.ToolbeltState;
import com.voidsrift.riftflux.vortex.network.ModPackets;
import com.voidsrift.riftflux.vortex.network.PacketToolbeltSwap;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class REHToolbeltHelper {
   private LinkedHashMap<Integer, ItemStack> itemMap = new LinkedHashMap();
   private List buttonList = new ArrayList();
   private HashMap<ButtonToolbeltRadial, Boolean> buttonHover = new HashMap();
   private boolean lastState = false;
   private final int iradius = 30;
   private final int oradius = 55;
   private final int sides = 25;
   private final int itemradius = 42;
   private int active = -1;
   private final int cooldown = 5;
   private ItemStack lastToolbeltCopy;
   private int lastClientRev = -1;
   private ItemStack lastHeldCopy;
   private boolean lastMouseDown = false;

   @SideOnly(Side.CLIENT)
   public void handleToolbeltRadialMenu(Minecraft mc, RenderGameOverlayEvent event) {
      if (KeyEventHandler.TRMactive || this.active > 0) {
         --this.active;
         if (this.active <= 0) {
            this.itemMap.clear();
            this.buttonList.clear();
            this.buttonHover.clear();
         }

         if (KeyEventHandler.TRMactive) {
            if (mc.currentScreen != null) {
               KeyEventHandler.TRMactive = false;
               ToolbeltState.setRadialActive(false);
               mc.setIngameFocus();
               mc.setIngameNotInFocus();
               return;
            }

            if (!ItemHelper.hasBauble(mc.thePlayer, ModItems.toolbelt)) {
               ToolbeltState.setRadialActive(false);
               return;
            }

            ToolbeltState.setRadialActive(true);
            this.renderToolbeltRadialMenu(mc, event);
            this.active = 5;
            if (mc.inGameHasFocus) {
               mc.inGameHasFocus = false;
               mc.mouseHelper.ungrabMouseCursor();
            }
         } else if (mc.currentScreen == null && this.lastState) {
            if (Display.isActive() && !mc.inGameHasFocus) {
               mc.inGameHasFocus = true;
               mc.mouseHelper.grabMouseCursor();
            }

         }

         if (!KeyEventHandler.TRMactive) {
            this.itemMap.clear();
            this.buttonList.clear();
            this.buttonHover.clear();
         }

         this.lastState = KeyEventHandler.TRMactive;
         if (!KeyEventHandler.TRMactive) {
            ToolbeltState.setRadialActive(false);
         }
      }

   }

   private static boolean sameStack(ItemStack a, ItemStack b) {
      if (a == b) return true;
      if (a == null || b == null) return false;
      if (a.getItem() != b.getItem()) return false;
      if (a.getItemDamage() != b.getItemDamage()) return false;
      if (a.hasTagCompound() != b.hasTagCompound()) return false;
      if (!a.hasTagCompound()) return true;
      return ItemStack.areItemStackTagsEqual(a, b);
   }

   @SideOnly(Side.CLIENT)
   private void renderToolbeltRadialMenu(Minecraft mc, RenderGameOverlayEvent event) {
      Tessellator tessellator = Tessellator.instance;
      RenderItem ri = new RenderItem();
      ItemStack heldItem = mc.thePlayer.getCurrentEquippedItem();
      boolean valid = true;

      if (heldItem != null && !ContainerHelper.toolbeltValid(heldItem)) {
         valid = false;
      }

      
      // Only rebuild the radial contents when something actually changed:
      // - the server synced the toolbelt stack (ToolbeltState revision)
      // - the toolbelt NBT changed (fallback if the revision isn't bumped)
      // - the player changed the held item (insert candidate changes)
      final int rev = ToolbeltState.getClientRevision();
      ItemStack toolbeltStack = null;
      int toolbeltSlot = -1;
      try {
         toolbeltSlot = ItemHelper.findBaubleSlot(mc.thePlayer, ModItems.toolbelt);
         if (toolbeltSlot >= 0) {
            toolbeltStack = BaublesApi.getBaubles(mc.thePlayer).getStackInSlot(toolbeltSlot);
         }
      } catch (Throwable ignored) {
      }
      final boolean heldChanged = !sameStack(heldItem, this.lastHeldCopy);
      final boolean toolbeltChanged = !sameStack(toolbeltStack, this.lastToolbeltCopy);
      if (rev != this.lastClientRev || heldChanged || toolbeltChanged) {
         this.itemMap.clear();
         this.buttonList.clear();
         this.buttonHover.clear();
         this.lastClientRev = rev;
         this.lastHeldCopy = heldItem == null ? null : heldItem.copy();
         this.lastToolbeltCopy = toolbeltStack == null ? null : toolbeltStack.copy();
      }
      int mY;
      if (this.itemMap.isEmpty()) {
         if (toolbeltStack == null) {
            return;
         }
         InventoryToolbelt toolbelt = ContainerHelper.getToolbeltInventory(toolbeltStack);

         for(mY = 0; mY < toolbelt.getSizeInventory(); ++mY) {
            if (toolbelt.getStackInSlot(mY) != null) {
               this.itemMap.put(mY, toolbelt.getStackInSlot(mY));
            }
         }

         if (this.itemMap.size() < toolbelt.getSizeInventory() && valid && heldItem != null) {
            for(mY = 0; mY < toolbelt.getSizeInventory(); ++mY) {
               if (toolbelt.getStackInSlot(mY) == null) {
                  this.itemMap.put(mY, heldItem);
                  break;
               }
            }
         }
      }

      int mouse;
      int mX;
      if (this.buttonList.isEmpty()) {
         mX = 0;

         for(Iterator var14 = this.itemMap.keySet().iterator(); var14.hasNext(); ++mX) {
            mouse = (Integer)var14.next();
            ButtonToolbeltRadial button = new ButtonToolbeltRadial(mouse, event.resolution.getScaledWidth() / 2, event.resolution.getScaledHeight() / 2, 30, 55, 25, this.itemMap.size(), mX, (ItemStack)this.itemMap.get(mouse), 42);
            this.buttonList.add(button);
         }
      }

      if (this.buttonList.isEmpty()) {
         ButtonToolbeltRadial button = new ButtonToolbeltRadial(0, event.resolution.getScaledWidth() / 2, event.resolution.getScaledHeight() / 2, 30, 55, 25, 1, 1, (ItemStack)null, 42);
         this.buttonList.add(button);
      }

      mX = (int)((double)org.lwjgl.input.Mouse.getX() * event.resolution.getScaledWidth_double() / (double)mc.displayWidth);
      mY = (int)(event.resolution.getScaledHeight_double() - (double)org.lwjgl.input.Mouse.getY() * event.resolution.getScaledHeight_double() / (double)mc.displayHeight - 1.0D);
      final boolean mouseDown = org.lwjgl.input.Mouse.isButtonDown(0);
      final boolean mouseClick = mouseDown && !this.lastMouseDown;
      this.lastMouseDown = mouseDown;
      for(int i = 0; i < this.buttonList.size(); ++i) {
         ButtonToolbeltRadial button = (ButtonToolbeltRadial)this.buttonList.get(i);
         if (valid && button.itemstack != null && (button.isHovered(mX, mY) || button.isItemHovered(mX, mY))) {
            button.drawButton(mc, ri, tessellator, 1.0F, 1.0F, 1.0F, 0.25F);
            this.buttonHover.put(button, true);
            if (heldItem == null) {
               RenderHelper.drawCenteredString(mc.fontRenderer, "Withdraw", event.resolution.getScaledWidth() / 2, (event.resolution.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT) / 2, -1);
            } else if (heldItem == button.itemstack) {
               RenderHelper.drawCenteredString(mc.fontRenderer, "Insert", event.resolution.getScaledWidth() / 2, (event.resolution.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT) / 2, -1);
            } else {
               RenderHelper.drawCenteredString(mc.fontRenderer, "Swap", event.resolution.getScaledWidth() / 2, (event.resolution.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT) / 2, -1);
            }
            if (mouseClick) {
               this.doTrade(mc.thePlayer, heldItem, button.id, button.itemstack);
               // Force a rebuild next frame. The authoritative contents will arrive via PacketToolbeltSync.
               this.lastClientRev = -1;
               break;
            }
         } else {
            if (!valid) {
               button.drawButton(mc, ri, tessellator, 0.7F, 0.0F, 0.0F, 0.25F);
            } else if (heldItem != null && heldItem == button.itemstack) {
               button.drawButton(mc, ri, tessellator, 0.0F, 0.2F, 0.3F, 0.25F);
            } else {
               button.drawButton(mc, ri, tessellator, 0.0F, 0.0F, 0.0F, 0.25F);
            }

            this.buttonHover.put(button, false);
         }
      }

      if (!valid) {
         RenderHelper.drawCenteredString(mc.fontRenderer, "Invalid", event.resolution.getScaledWidth() / 2, (event.resolution.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT) / 2, -1);
      }

   }

   private void doTrade(EntityPlayer player, ItemStack heldItem, int id, ItemStack toolbeltItem) {
      int mode;
      if (heldItem == null) {
         mode = ContainerHelper.toolbeltWithdraw;
      } else if (heldItem == toolbeltItem) {
         mode = ContainerHelper.toolbeltInsert;
      } else {
         mode = ContainerHelper.toolbeltSwap;
      }
      int sendId = id;
      if (mode != ContainerHelper.toolbeltInsert) {
         int found = findToolbeltSlot(player, toolbeltItem);
         if (found >= 0) {
            sendId = found;
         }
      }
      ModPackets.instance.sendToServer(new PacketToolbeltSwap(player, mode, sendId, toolbeltItem));
      KeyEventHandler.markToolbeltRadialAction();
   }

   private int findToolbeltSlot(EntityPlayer player, ItemStack target) {
      if (player == null || target == null) {
         return -1;
      }
      try {
         int toolbeltSlot = ItemHelper.findBaubleSlot(player, ModItems.toolbelt);
         if (toolbeltSlot < 0) {
            return -1;
         }
         ItemStack toolbeltStack = BaublesApi.getBaubles(player).getStackInSlot(toolbeltSlot);
         if (toolbeltStack == null) {
            return -1;
         }
         InventoryToolbelt toolbelt = ContainerHelper.getToolbeltInventory(toolbeltStack);
         for (int i = 0; i < toolbelt.getSizeInventory(); i++) {
            if (sameStack(toolbelt.getStackInSlot(i), target)) {
               return i;
            }
         }
      } catch (Throwable ignored) {
      }
      return -1;
   }
}
