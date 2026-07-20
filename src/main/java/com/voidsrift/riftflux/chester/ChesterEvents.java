package com.voidsrift.riftflux.chester;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import java.lang.ref.WeakReference;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.WeakHashMap;
import makamys.satchels.EntityPropertiesSatchels;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.item.ItemTossEvent;

public final class ChesterEvents {
    private final Map<EntityPlayer, WeakReference<Container>> openContainers =
            new WeakHashMap<EntityPlayer, WeakReference<Container>>();
    private final Map<EntityPlayer, Set<UUID>> satchelBindings =
            new WeakHashMap<EntityPlayer, Set<UUID>>();

    @SubscribeEvent
    public void onPickup(EntityItemPickupEvent event) {
        if (!event.entityPlayer.worldObj.isRemote) {
            ChesterBinding.ensureChester(event.item.getEntityItem(), event.entityPlayer);
        }
    }

    @SubscribeEvent
    public void onToss(ItemTossEvent event) {
        EntityChester chester = ChesterBinding.findBoundChester(
                event.entityItem.getEntityItem(),
                event.player.worldObj
        );
        if (chester != null) {
            chester.clearStaffHeldBy(event.player);
            chester.setStaffAnchor(event.entityItem.posX, event.entityItem.posY, event.entityItem.posZ);
        }
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.worldObj.isRemote) {
            return;
        }
        WeakReference<Container> previousReference = openContainers.get(event.player);
        Container previous = previousReference == null ? null : previousReference.get();
        if (previous != event.player.openContainer) {
            openContainers.put(event.player, new WeakReference<Container>(event.player.openContainer));
        }
        if (previous != null && previous != event.player.openContainer) {
            inspectClosedContainer(previous, event.player);
        }
        EntityPropertiesSatchels satchels = EntityPropertiesSatchels.fromPlayer(event.player);
        if (satchels != null && satchels.consumeChesterContentsDirty()) {
            scanSatchels(event.player);
        }
    }

    @SuppressWarnings("unchecked")
    private void inspectClosedContainer(Container container, EntityPlayer player) {
        for (Slot slot : (java.util.List<Slot>) container.inventorySlots) {
            ItemStack stack = slot.getStack();
            EntityChester chester = ChesterBinding.findBoundChester(stack, player.worldObj);
            if (chester == null || !(slot.inventory instanceof TileEntity)) {
                continue;
            }
            TileEntity tile = (TileEntity) slot.inventory;
            chester.clearStaffHeldBy(player);
            chester.setStaffAnchor(tile.xCoord + 0.5D, tile.yCoord + 1.0D, tile.zCoord + 0.5D);
            ChesterBinding.updateBoundName(stack, chester.getCommandSenderName());
        }
    }

    private void scanSatchels(EntityPlayer player) {
        EntityPropertiesSatchels satchels = EntityPropertiesSatchels.fromPlayer(player);
        if (satchels == null) {
            return;
        }
        Set<UUID> previous = satchelBindings.get(player);
        Set<UUID> current = new HashSet<UUID>();
        for (int slot : satchels.getEnabledSlots()) {
            ItemStack stack = satchels.aggregate.getStackInSlot(slot);
            EntityChester chester = ChesterBinding.ensureChester(stack, player);
            UUID id = ChesterBinding.getBoundChesterId(stack);
            if (id != null) {
                current.add(id);
            }
            if (chester != null) {
                chester.markStaffHeldBy(player, true);
            }
        }
        if (previous != null) {
            for (UUID id : previous) {
                if (current.contains(id)) {
                    continue;
                }
                EntityChester chester = ChesterBinding.findBoundChester(id, player.worldObj);
                if (chester != null) {
                    chester.clearStaffHeldBy(player);
                }
            }
        }
        satchelBindings.put(player, current);
    }
}
