package com.voidsrift.riftflux.net;

import com.voidsrift.riftflux.client.PickupNotifierHud;
import com.voidsrift.riftflux.client.PickupStarClientTracker;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

/**
 * Send info about a pickup for client-side HUD + star highlighting.
 *
 * NOTE: Server currently builds the stack from item+meta only (Key.toStack),
 * so it may not carry NBT such as anvil renames.
 * The client handler compensates by looking up a better-matching stack
 * from the player's inventory before enqueuing HUD/star entries.
 */
public final class MsgPickup implements IMessage {

    public ItemStack stack;
    public int count;

    public MsgPickup() {
    }

    public MsgPickup(ItemStack stack, int count) {
        this.stack = (stack == null ? null : stack.copy());
        this.count = count;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        ByteBufUtils.writeItemStack(buf, stack);
        buf.writeInt(count);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        stack = ByteBufUtils.readItemStack(buf);
        count = buf.readInt();
    }

    public static final class ClientHandler implements IMessageHandler<MsgPickup, IMessage> {
        @Override
        public IMessage onMessage(MsgPickup msg, MessageContext ctx) {
            if (msg.stack != null) {
                Minecraft mc = Minecraft.getMinecraft();
                EntityPlayer player = mc.thePlayer;

                ItemStack display = msg.stack;

                // Try to find a "better" stack in the player's inventory:
                // same item/meta, but with full NBT (e.g. anvil renamed).
                if (player != null && msg.stack.getItem() != null) {
                    ItemStack base = msg.stack;
                    ItemStack best = null;

                    for (ItemStack inv : player.inventory.mainInventory) {
                        if (inv == null || inv.getItem() == null) continue;
                        if (inv.getItem() != base.getItem()) continue;

                        // If non-damageable, meta must match;
                        // if damageable, we ignore damage (durability changes).
                        if (!inv.isItemStackDamageable() &&
                                inv.getItemDamage() != base.getItemDamage()) {
                            continue;
                        }

                        if (best == null) {
                            best = inv;
                        } else {
                            // Prefer stacks with a custom name over ones without
                            if (!best.hasDisplayName() && inv.hasDisplayName()) {
                                best = inv;
                            }
                        }
                    }

                    if (best != null) {
                        // Use the inventory stack (with rename/NBT) as the visual basis
                        display = best.copy();
                        display.stackSize = Math.max(1, msg.count);
                    } else {
                        // Fallback: make sure count is sane on the original
                        display = msg.stack.copy();
                        display.stackSize = Math.max(1, msg.count);
                    }
                } else {
                    // No player or weird state: just clamp count and use original
                    display = msg.stack.copy();
                    display.stackSize = Math.max(1, msg.count);
                }

                // Star overlay + HUD use the same chosen display stack
                PickupStarClientTracker.enqueue(display);
                PickupNotifierHud.enqueue(display, msg.count);
            }
            return null;
        }
    }
}
