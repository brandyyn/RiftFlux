package gravestone.tileentity;

import gravestone.block.GraveStoneHelper;
import gravestone.block.enums.EnumGraves;
import gravestone.block.enums.EnumMemorials;
import gravestone.config.GraveStoneConfig;
import java.util.ArrayList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class GSGraveStoneDeathText {
   private String name = "";
   private String deathText = "";
   private String killerName = "";
   private boolean isLocalized = false;
   private TileEntityGSGrave tileEntity;

   public GSGraveStoneDeathText(TileEntityGSGrave tileEntity) {
      this.tileEntity = tileEntity;
   }

   public void readText(NBTTagCompound nbtTag) {
      if (nbtTag.hasKey("isLocalized")) {
         this.isLocalized = nbtTag.getBoolean("isLocalized");
      }

      if (this.isLocalized) {
         this.name = nbtTag.getString("name");
         this.deathText = nbtTag.getString("DeathText");
         this.killerName = nbtTag.getString("KillerName");
      } else {
         this.deathText = nbtTag.getString("DeathText");
      }

   }

   public void saveText(NBTTagCompound nbtTag) {
      if (this.isLocalized) {
         nbtTag.setString("name", this.name);
         nbtTag.setString("DeathText", this.deathText);
         nbtTag.setString("KillerName", this.killerName);
      } else {
         nbtTag.setString("DeathText", this.deathText);
      }

      nbtTag.setBoolean("isLocalized", this.isLocalized);
   }

   public boolean isLocalized() {
      return this.isLocalized;
   }

   public void setLocalized() {
      this.isLocalized = true;
   }

   public String getName() {
      return this.name;
   }

   public String getDeathText() {
      return this.deathText;
   }

   public String getKillerName() {
      return this.killerName;
   }

   public void setName(String name) {
      this.name = name == null ? "" : name;
   }

   public void setDeathText(String text) {
      this.deathText = text == null ? "" : text;
   }

   public void setKillerName(String name) {
      this.killerName = name == null ? "" : name;
   }

   public void setRandomDeathTextAndName(Random random, byte grave, boolean isMemorial, boolean changeGraveType) {
      this.isLocalized = true;
      EnumGraves graveType = EnumGraves.getByID(grave);
      EnumMemorials memorialType = EnumMemorials.getByID(grave);
      if (isMemorial) {
         switch(memorialType) {
         case STONE_DOG_STATUE:
            this.getRandomMemorialContent(random, GraveStoneConfig.graveDogsNames, GraveStoneConfig.dogsMemorialText);
            break;
         case STONE_CAT_STATUE:
            this.getRandomMemorialContent(random, GraveStoneConfig.graveCatsNames, GraveStoneConfig.catsMemorialText);
            break;
         case STONE_CREEPER_STATUE:
            this.deathText = "Sssssssssssssss...";
            break;
         default:
            this.getRandomMemorialContent(random, GraveStoneConfig.graveNames, GraveStoneConfig.memorialText);
         }
      } else if (this.getDeathMessage(random)) {
         switch(graveType) {
         case WOODEN_DOG_STATUE:
         case SANDSTONE_DOG_STATUE:
         case STONE_DOG_STATUE:
         case MOSSY_DOG_STATUE:
         case IRON_DOG_STATUE:
         case GOLDEN_DOG_STATUE:
         case DIAMOND_DOG_STATUE:
         case EMERALD_DOG_STATUE:
         case LAPIS_DOG_STATUE:
         case REDSTONE_DOG_STATUE:
         case OBSIDIAN_DOG_STATUE:
         case QUARTZ_DOG_STATUE:
         case ICE_DOG_STATUE:
            this.name = this.getValue(random, GraveStoneConfig.graveDogsNames);
            if (changeGraveType) {
               byte newGraveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getDogGraveForDeath((DamageSource)null, this.deathText), random);
               if (newGraveType != 0) {
                  this.tileEntity.setGraveType(newGraveType);
               }
            }
            break;
         case WOODEN_CAT_STATUE:
         case SANDSTONE_CAT_STATUE:
         case STONE_CAT_STATUE:
         case MOSSY_CAT_STATUE:
         case IRON_CAT_STATUE:
         case GOLDEN_CAT_STATUE:
         case DIAMOND_CAT_STATUE:
         case EMERALD_CAT_STATUE:
         case LAPIS_CAT_STATUE:
         case REDSTONE_CAT_STATUE:
         case OBSIDIAN_CAT_STATUE:
         case QUARTZ_CAT_STATUE:
         case ICE_CAT_STATUE:
            this.name = this.getValue(random, GraveStoneConfig.graveCatsNames);
            if (changeGraveType) {
               byte newGraveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getCatGraveForDeath((DamageSource)null, this.deathText), random);
               if (newGraveType != 0) {
                  this.tileEntity.setGraveType(newGraveType);
               }
            }
            break;
         default:
            this.name = this.getValue(random, GraveStoneConfig.graveNames);
            if (changeGraveType) {
               byte newGraveType = GraveStoneHelper.getRandomGrave(GraveStoneHelper.getPlayerGraveForDeath((DamageSource)null, this.deathText), random);
               if (newGraveType != 0) {
                  this.tileEntity.setGraveType(newGraveType);
               }
            }
         }
      }

      if (changeGraveType) {
         this.tileEntity.setEnchanted(GraveStoneHelper.isMagicDamage((DamageSource)null, this.deathText));
      }

   }

   private void getRandomMemorialContent(Random random, ArrayList<String> nameList, ArrayList<String> textList) {
      if (this.getDeathMessage(random)) {
         this.name = this.getValue(random, nameList);
      }

   }

   private boolean getDeathMessage(Random random) {
      DeathMessageInfo deathMessageInfo = DeathMessageInfo.getRandomDeathMessage(random);
      this.deathText = deathMessageInfo.getDeathMessage();
      this.killerName = deathMessageInfo.getKillerNameForTE();
      if (deathMessageInfo.getName().length() != 0) {
         this.name = deathMessageInfo.getName();
         return false;
      } else {
         return true;
      }
   }

   private String getValue(Random random, ArrayList<String> list) {
      return list != null && list.size() > 0 ? list.get(random.nextInt(list.size())) : "";
   }
}
