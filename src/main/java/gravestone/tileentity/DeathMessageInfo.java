package gravestone.tileentity;

import java.util.Random;

public class DeathMessageInfo {
   private String name;
   private String deathMessage;
   private String killerName;
   private String itemName;
   private DeathMessageInfo.DeathTypes deathTypes;
   public static final DeathMessageInfo[] LOCALIZED_DEATH_TEXT = new DeathMessageInfo[]{new DeathMessageInfo("", "death.attack.anvil", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.arrow", "", (String)null, DeathMessageInfo.DeathTypes.ARROW), new DeathMessageInfo("", "death.attack.cactus", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.cactus.player", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.drown", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.drown.player", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.explosion", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.explosion.player", "", (String)null, DeathMessageInfo.DeathTypes.BLOW), new DeathMessageInfo("", "death.attack.fall", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.fallingBlock", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.fireball", "", (String)null, DeathMessageInfo.DeathTypes.FIREBALL), new DeathMessageInfo("", "death.attack.inFire", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.inFire.player", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.inWall", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.indirectMagic", "", (String)null, DeathMessageInfo.DeathTypes.MAGIC), new DeathMessageInfo("", "death.attack.lava", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.lava.player", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.magic", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.mob", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.onFire", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.onFire.player", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.outOfWorld", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.player", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.starve", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.attack.thorns", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.thrown", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.attack.wither", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.fell.accident.generic", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.fell.accident.ladder", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.fell.accident.vines", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.fell.accident.water", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.fell.assist", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.fell.finish", "", (String)null, DeathMessageInfo.DeathTypes.ALL), new DeathMessageInfo("", "death.fell.killer", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.GS.Herobrine", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.GS.death_sentence", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null), new DeathMessageInfo("", "death.GS.tortures", (String)null, (String)null, (DeathMessageInfo.DeathTypes)null)};
   public static final DeathMessageInfo[] SPECIAL_LOCALIZED_DEATH_TEXT = new DeathMessageInfo[]{new DeathMessageInfo("Notch", "death.GS.Herobrine", (String)null), new DeathMessageInfo("Steve", "death.GS.Herobrine", (String)null), new DeathMessageInfo("Jeb", "death.GS.Herobrine", (String)null), new DeathMessageInfo("Leeroy Jenkins", "death.attack.onFire.player", "entity.EnderDragon.name"), new DeathMessageInfo("Wilson", "death.attack.starve", (String)null), new DeathMessageInfo("Guide", "death.attack.lava", (String)null), new DeathMessageInfo("death.GS.mom", "death.GS.Herobrine", (String)null), new DeathMessageInfo("Horke", "death.attack.drown", (String)null)};
   public static final String[] ALL_KILLER_NAMES = new String[]{"entity.Blaze.name", "entity.CaveSpider.name", "entity.Creeper.name", "entity.EnderDragon.name", "entity.Enderman.name", "entity.Ghast.name", "entity.Giant.name", "entity.LavaSlime.name", "entity.PigZombie.name", "entity.Silverfish.name", "entity.Skeleton.name", "entity.Slime.name", "entity.SnowMan.name", "entity.Spider.name", "entity.VillagerGolem.name", "entity.Witch.name", "entity.WitherBoss.name", "entity.Wolf.name", "entity.Zombie.name", "entity.skeletonhorse.name", "entity.zombiehorse.name", "entity.GSZombieDog.name", "entity.GSZombieCat.name", "entity.GSSkeletonDog.name", "entity.GSSkeletonCat.name"};
   public static final String[] ARROW_KILLER_NAMES = new String[]{"entity.Blaze.name", "entity.Ghast.name", "entity.Skeleton.name", "entity.SnowMan.name", "entity.WitherBoss.name"};
   public static final String[] FIREBALL_KILLER_NAMES = new String[]{"entity.Blaze.name", "entity.Ghast.name", "entity.Witch.name", "entity.WitherBoss.name"};
   public static final String[] BLOW_KILLER_NAMES = new String[]{"entity.Creeper.name", "entity.Ghast.name", "entity.WitherBoss.name"};
   public static final String[] MAGIC_KILLER_NAMES = new String[]{"entity.EnderDragon.name", "entity.Enderman.name", "entity.Witch.name", "entity.WitherBoss.name"};

   public DeathMessageInfo(String name, String deathMessage, String killerName) {
      this(name, deathMessage, killerName, (String)null, (DeathMessageInfo.DeathTypes)null);
   }

   public DeathMessageInfo(String name, String deathMessage, String killerName, String itemName) {
      this(name, deathMessage, killerName, itemName, (DeathMessageInfo.DeathTypes)null);
   }

   public DeathMessageInfo(String name, String deathMessage, String killerName, String itemName, DeathMessageInfo.DeathTypes messageTypes) {
      this.name = name;
      this.deathMessage = deathMessage;
      this.killerName = killerName;
      this.itemName = itemName;
      this.deathTypes = messageTypes;
   }

   public String getName() {
      return this.name;
   }

   public String getDeathMessage() {
      return this.deathMessage;
   }

   public String getKillerName() {
      return this.killerName;
   }

   public String getKillerNameForTE() {
      return this.killerName == null ? "" : this.killerName;
   }

   public String getItemName() {
      return this.itemName;
   }

   public static DeathMessageInfo getRandomDeathMessage(Random random) {
      DeathMessageInfo info;
      if (random.nextInt(50) == 0) {
         info = SPECIAL_LOCALIZED_DEATH_TEXT[random.nextInt(SPECIAL_LOCALIZED_DEATH_TEXT.length)];
      } else {
         info = LOCALIZED_DEATH_TEXT[random.nextInt(LOCALIZED_DEATH_TEXT.length)];
         if (info.killerName != null) {
            info.killerName = getRandomKillerName(random, info.deathTypes);
         }
      }

      return info;
   }

   public static String getRandomKillerName(Random random, DeathMessageInfo.DeathTypes deathTypes) {
      switch(deathTypes) {
      case ARROW:
         return ARROW_KILLER_NAMES[random.nextInt(ARROW_KILLER_NAMES.length)];
      case FIREBALL:
         return FIREBALL_KILLER_NAMES[random.nextInt(FIREBALL_KILLER_NAMES.length)];
      case BLOW:
         return BLOW_KILLER_NAMES[random.nextInt(BLOW_KILLER_NAMES.length)];
      case MAGIC:
         return MAGIC_KILLER_NAMES[random.nextInt(MAGIC_KILLER_NAMES.length)];
      case ALL:
      default:
         return ALL_KILLER_NAMES[random.nextInt(ALL_KILLER_NAMES.length)];
      }
   }

   public static enum DeathTypes {
      ALL,
      ARROW,
      FIREBALL,
      BLOW,
      MAGIC;
   }
}
