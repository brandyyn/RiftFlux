package com.voidsrift.riftflux.vortex.lib.helper;

import java.util.UUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.storage.MapStorage;
import com.voidsrift.riftflux.vortex.lib.world.CustomWorldData;

public class WorldHelper {
   public static CustomWorldData getGlobalCustomData(World world) {
      MapStorage storage = world.mapStorage;
      CustomWorldData worldData = (CustomWorldData)storage.loadData(CustomWorldData.class, "riftflux_vortex");
      if (worldData == null) {
         worldData = new CustomWorldData("riftflux_vortex");
         storage.setData("riftflux_vortex", worldData);
      }

      return worldData;
   }

   public static void setPlayerHCRevive(EntityPlayer player, boolean canRevive) {
      CustomWorldData worldData = getGlobalCustomData(player.worldObj);
      NBTTagCompound tag = worldData.getData();
      NBTTagList allowedPlayers = (NBTTagList)tag.getTag("HCReviveAllowedPlayerList");
      if (allowedPlayers == null) {
         tag.setTag("HCReviveAllowedPlayerList", new NBTTagList());
         allowedPlayers = (NBTTagList)tag.getTag("HCReviveAllowedPlayerList");
      }

      boolean added = false;

      for(int i = 0; i < allowedPlayers.tagCount(); ++i) {
         NBTTagCompound playerTag = allowedPlayers.getCompoundTagAt(i);
         UUID playerUuid = UUID.fromString(playerTag.getString("PlayerUUID"));
         if (playerUuid.equals(player.getPersistentID())) {
            added = true;
            if (!canRevive) {
               allowedPlayers.removeTag(i);
            }
            break;
         }
      }

      if (canRevive && !added) {
         NBTTagCompound playerTag = new NBTTagCompound();
         playerTag.setString("PlayerUUID", player.getPersistentID().toString());
         allowedPlayers.appendTag(playerTag);
      }

      tag.setTag("HCReviveAllowedPlayerList", allowedPlayers);
      worldData.markDirty();
   }

   public static boolean canPlayerHCRevive(EntityPlayer player) {
      CustomWorldData worldData = getGlobalCustomData(player.worldObj);
      NBTTagCompound tag = worldData.getData();
      NBTTagList allowedPlayers = (NBTTagList)tag.getTag("HCReviveAllowedPlayerList");
      if (allowedPlayers != null) {
         for(int i = 0; i < allowedPlayers.tagCount(); ++i) {
            NBTTagCompound playerTag = allowedPlayers.getCompoundTagAt(i);
            UUID playerUuid = UUID.fromString(playerTag.getString("PlayerUUID"));
            if (playerUuid.equals(player.getPersistentID())) {
               return true;
            }
         }
      }

      return false;
   }
}
