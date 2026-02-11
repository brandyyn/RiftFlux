package com.voidsrift.riftflux.vortex.network;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.placeditem.PlacedItemContent;
import com.voidsrift.riftflux.placeditem.TilePlacedItem;
import cpw.mods.fml.common.network.simpleimpl.IMessage;
import cpw.mods.fml.common.network.simpleimpl.IMessageHandler;
import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraft.util.Facing;

public class PacketPlacedItem implements IMessage, IMessageHandler<PacketPlacedItem, IMessage> {
    private byte side;
    private int blockX;
    private int blockY;
    private int blockZ;

    public PacketPlacedItem() {
    }

    public PacketPlacedItem(byte side, int blockX, int blockY, int blockZ) {
        this.side = side;
        this.blockX = blockX;
        this.blockY = blockY;
        this.blockZ = blockZ;
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeByte(side);
        buf.writeInt(blockX);
        buf.writeInt(blockY);
        buf.writeInt(blockZ);
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        side = buf.readByte();
        blockX = buf.readInt();
        blockY = buf.readInt();
        blockZ = buf.readInt();
    }

    @Override
    public IMessage onMessage(PacketPlacedItem message, MessageContext ctx) {
        if (!ModConfig.enablePlacedItem) {
            return null;
        }
        EntityPlayerMP player = ctx.getServerHandler().playerEntity;
        if (player == null) {
            return null;
        }
        World world = player.worldObj;
        ForgeDirection dir = ForgeDirection.getOrientation(message.side);
        int x = message.blockX + dir.offsetX;
        int y = message.blockY + dir.offsetY;
        int z = message.blockZ + dir.offsetZ;
        if (!world.isAirBlock(x, y, z)) {
            return null;
        }
        ItemStack held = player.getHeldItem();
        if (held == null) {
            return null;
        }
        if (PlacedItemContent.placedItemBlock == null) {
            return null;
        }

        BlockSnapshot snapshot = new BlockSnapshot(world, x, y, z, PlacedItemContent.placedItemBlock, 0);
        BlockEvent.PlaceEvent placeEvent = new BlockEvent.PlaceEvent(
                snapshot,
                world.getBlock(message.blockX, message.blockY, message.blockZ),
                player
        );
        MinecraftForge.EVENT_BUS.post(placeEvent);
        if (placeEvent.isCanceled()) {
            return null;
        }

        int placedMeta = Facing.oppositeSide[message.side];
        world.setBlock(x, y, z, PlacedItemContent.placedItemBlock, placedMeta, 2);
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TilePlacedItem)) {
            world.setBlockToAir(x, y, z);
            return null;
        }
        TilePlacedItem placedItem = (TilePlacedItem) te;
        placedItem.rotation = getInitialRotation(player, message.side, held);
        ItemStack placedStack = held.copy();
        placedItem.setStack(placedStack);
        if (!player.capabilities.isCreativeMode) {
            player.destroyCurrentEquippedItem();
        }
        return null;
    }

    private static float getInitialRotation(EntityPlayerMP player, byte side, ItemStack stack) {
        int dir = MathHelper.floor_double((player.rotationYaw * 4.0F / 360.0F) + 0.5D) & 3;
        int meta = side >= 0 && side < Facing.oppositeSide.length
                ? Facing.oppositeSide[side]
                : side;
        if (meta != 0 && meta != 1) {
            return 0.0F;
        }
        int rotationIndex = dir;
        if (meta == 0) {
            rotationIndex = (rotationIndex + 2) % 4;
        } else {
            rotationIndex = (4 - rotationIndex) % 4;
        }
        return (rotationIndex * 90.0F) % 360.0F;
    }
}
