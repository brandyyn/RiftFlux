package gravestone.core;

import com.google.common.io.Files;
import gravestone.core.logger.GSLogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class MobHandler {
   public static final String MOBS_SPAWN_TIME_FILE_NAME = "mobsSpawnTime.gs";
   public static final String MOBS_SPAWN_TIME_BACKUP_FILE_NAME = "mobsSpawnTimeBackup.gs";
   private static HashMap<String, Long> mobsSpawnTime = new HashMap<>();

   public static void clearMobsSpawnTime(Entity entity) {
      mobsSpawnTime.remove(entity.getUniqueID().toString());
      saveMobsSpawnTime(entity.worldObj);
   }

   public static long getAndRemoveSpawnTime(Entity entity) {
      if (mobsSpawnTime.containsKey(entity.getUniqueID().toString())) {
         long time = mobsSpawnTime.get(entity.getUniqueID().toString());
         clearMobsSpawnTime(entity);
         return time;
      } else {
         return entity.worldObj.getWorldTime();
      }
   }

   public static long getMobSpawnTime(Entity entity) {
      if (!mobsSpawnTime.containsKey(entity.getUniqueID().toString())) {
         mobsSpawnTime.put(entity.getUniqueID().toString(), entity.worldObj.getWorldTime());
         saveMobsSpawnTime(entity.worldObj);
      }

      return mobsSpawnTime.get(entity.getUniqueID().toString());
   }

   public static void setMobSpawnTime(Entity entity) {
      if (!mobsSpawnTime.containsKey(entity.getUniqueID().toString())) {
         mobsSpawnTime.put(entity.getUniqueID().toString(), entity.worldObj.getWorldTime());
         saveMobsSpawnTime(entity.worldObj);
      }

   }

   public static void loadMobsSpawnTime(World world) {
      try {
         File file = new File(world.getSaveHandler().getWorldDirectory(), "mobsSpawnTime.gs");
         File backup = new File(world.getSaveHandler().getWorldDirectory(), "mobsSpawnTimeBackup.gs");
         NBTTagCompound data = null;
         boolean save = false;
         if (file != null && file.exists()) {
            data = getDataFromFile(file);
         }

         if (file == null || !file.exists() || data == null || data.hasNoTags()) {
            GSLogger.logError("Data not found. Trying to load backup data.");
            if (backup != null && backup.exists()) {
               data = getDataFromFile(backup);
               save = true;
            }
         }

         if (data != null) {
            for(Object tagNameObject : data.func_150296_c()) {
               String tagName = (String)tagNameObject;
               mobsSpawnTime.put(tagName, data.getLong(tagName));
            }

            if (save) {
               saveMobsSpawnTime(world);
            }
         }
      } catch (Exception var8) {
         GSLogger.logError("Error loading mobs spawn time");
         var8.printStackTrace();
      }

   }

   public static void saveMobsSpawnTime(World world) {
      if (world != null && !world.isRemote && mobsSpawnTime != null) {
         try {
            File file = new File(world.getSaveHandler().getWorldDirectory(), "mobsSpawnTime.gs");
            File backup = new File(world.getSaveHandler().getWorldDirectory(), "mobsSpawnTimeBackup.gs");
            if (file != null && file.exists()) {
               try {
                  Files.copy(file, backup);
               } catch (Exception var7) {
                  GSLogger.logError("Could not backup old spawn time file");
               }
            }

            try {
               if (file != null) {
                  NBTTagCompound data = new NBTTagCompound();

                  for(Entry<String, Long> entry : mobsSpawnTime.entrySet()) {
                     if (entry != null) {
                        data.setLong(entry.getKey(), entry.getValue());
                     }
                  }

                  FileOutputStream fileoutputstream = new FileOutputStream(file);
                  CompressedStreamTools.writeCompressed(data, fileoutputstream);
                  fileoutputstream.close();
               }
            } catch (Exception var8) {
               GSLogger.logError("Could not save spawn time file");
               var8.printStackTrace();
               if (file.exists()) {
                  try {
                     file.delete();
                  } catch (Exception var6) {
                  }
               }
            }
         } catch (Exception var9) {
            GSLogger.logError("Error saving mobs spawn time");
            var9.printStackTrace();
         }
      }

   }

   private static NBTTagCompound getDataFromFile(File file) {
      NBTTagCompound data = null;

      try {
         FileInputStream fileinputstream = new FileInputStream(file);
         data = CompressedStreamTools.readCompressed(fileinputstream);
         fileinputstream.close();
      } catch (IOException var3) {
         var3.printStackTrace();
      }

      return data;
   }
}
