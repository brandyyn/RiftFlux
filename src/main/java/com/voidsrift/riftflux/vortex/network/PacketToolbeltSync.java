package com.voidsrift.riftflux.vortex.network;

import baubles.api.BaublesApi;
import cpw.mods.fml.common.network.ByteBufUtils;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import com.voidsrift.riftflux.vortex.lib.helper.ToolbeltState;
import com.voidsrift.riftflux.vortex.item.ModItems;
import com.voidsrift.riftflux.vortex.lib.helper.ItemHelper;

/**
 * Client-side sync for the Toolbelt bauble ItemStack NBT.
 *
 * Baubles' own container syncing doesn't always propagate in-place NBT edits
 * to the client, so we explicitly push the updated toolbelt stack after server
 * mutations (insert/withdraw/swap).
 */
public class PacketToolbeltSync implements IMessage, IMessageHandler<PacketToolbeltSync, IMessage> {

    private int dim;
    private int playerId;
    private ItemStack toolbeltStack;

    public PacketToolbeltSync() {
    }

    public PacketToolbeltSync(EntityPlayer player, ItemStack toolbeltStack) {
        this.dim = player.worldObj.provider.dimensionId;
        this.playerId = player.getEntityId();
        this.toolbeltStack = toolbeltStack;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dim = buf.readInt();
        this.playerId = buf.readInt();
        this.toolbeltStack = ByteBufUtils.readItemStack(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dim);
        buf.writeInt(this.playerId);
        ByteBufUtils.writeItemStack(buf, this.toolbeltStack);
    }

    @Override
    public IMessage onMessage(final PacketToolbeltSync message, final MessageContext ctx) {
        // Run on the client thread.
        Minecraft.getMinecraft().func_152344_a(new Runnable() {
            @Override
            public void run() {
                World w = DimensionManager.getWorld(message.dim);
                if (w == null) {
                    // Fallback for integrated client.
                    w = Minecraft.getMinecraft().theWorld;
                }
                if (w == null) return;

                Entity e = w.getEntityByID(message.playerId);
                if (!(e instanceof EntityPlayer)) return;

                EntityPlayer p = (EntityPlayer) e;
                try {
                    int toolbeltSlot = ItemHelper.findBaubleSlot(p, ModItems.toolbelt);
                    if (toolbeltSlot >= 0) {
                        BaublesApi.getBaubles(p).setInventorySlotContents(toolbeltSlot, message.toolbeltStack);
                        BaublesApi.getBaubles(p).markDirty();
                        ToolbeltState.bumpClientRevision();
                    }
                } catch (Throwable ignored) {
                }
            }
        });

        return null;
    }
}
