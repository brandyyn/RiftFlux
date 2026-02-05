package com.voidsrift.riftflux.vortex.network;

import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.DimensionManager;
import com.voidsrift.riftflux.vortex.item.ItemBackpack;
import com.voidsrift.riftflux.vortex.item.ModItems;

/**
 * Client-side sync for backpackGuiId on the equipped backpack stack.
 */
public class PacketBackpackSync implements IMessage, IMessageHandler<PacketBackpackSync, IMessage> {

    private int dim;
    private int playerId;
    private int backpackGuiId;

    public PacketBackpackSync() {
    }

    public PacketBackpackSync(EntityPlayer player, int backpackGuiId) {
        this.dim = player.worldObj.provider.dimensionId;
        this.playerId = player.getEntityId();
        this.backpackGuiId = backpackGuiId;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.dim = buf.readInt();
        this.playerId = buf.readInt();
        this.backpackGuiId = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.dim);
        buf.writeInt(this.playerId);
        buf.writeInt(this.backpackGuiId);
    }

    @Override
    public IMessage onMessage(final PacketBackpackSync message, final MessageContext ctx) {
        Minecraft.getMinecraft().func_152344_a(new Runnable() {
            @Override
            public void run() {
                World w = DimensionManager.getWorld(message.dim);
                if (w == null) {
                    w = Minecraft.getMinecraft().theWorld;
                }
                if (w == null) return;

                Entity e = w.getEntityByID(message.playerId);
                if (!(e instanceof EntityPlayer)) return;

                EntityPlayer p = (EntityPlayer) e;
                ItemStack armor = ItemBackpack.getEquippedBackpack(p);
                if (armor == null || armor.getItem() != ModItems.backpack) return;

                NBTTagCompound tag = armor.getTagCompound();
                if (tag == null) {
                    tag = new NBTTagCompound();
                    armor.setTagCompound(tag);
                }
                tag.setInteger("backpackGuiId", message.backpackGuiId);
            }
        });

        return null;
    }
}
