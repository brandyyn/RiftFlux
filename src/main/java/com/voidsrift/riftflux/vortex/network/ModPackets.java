package com.voidsrift.riftflux.vortex.network;

import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import cpw.mods.fml.relauncher.Side;

public class ModPackets {
   public static final SimpleNetworkWrapper instance;

   public static void init() {
      int id = 0;
      int id1 = id + 1;
      instance.registerMessage(PacketToolbeltSwap.class, PacketToolbeltSwap.class, id, Side.SERVER);
      // Client-side NBT sync for the toolbelt bauble stack.
      instance.registerMessage(PacketToolbeltSync.class, PacketToolbeltSync.class, id1++, Side.CLIENT);
      // Client-side sync for backpackGuiId on the equipped backpack stack.
      instance.registerMessage(PacketBackpackSync.class, PacketBackpackSync.class, id1++, Side.CLIENT);
      instance.registerMessage(PacketHCPlayerRespawn.class, PacketHCPlayerRespawn.class, id1++, Side.SERVER);
      instance.registerMessage(PacketBackpackGuiHandle.class, PacketBackpackGuiHandle.class, id1++, Side.SERVER);
      instance.registerMessage(PacketWorldDataSync.class, PacketWorldDataSync.class, id1++, Side.CLIENT);
      instance.registerMessage(PacketPlacedItem.class, PacketPlacedItem.class, id1++, Side.SERVER);
   }

   static {
      instance = NetworkRegistry.INSTANCE.newSimpleChannel("riftflux_vortex");
   }
}
