package invmod.common.nexus;

import net.minecraft.util.EnumChatFormatting;

public abstract interface ISpawnerAccess
{
  public abstract boolean attemptSpawn(EntityConstruct mobConstruct, int minAngle, int maxAngle);

  public abstract int getNumberOfPointsInRange(int minAngle, int maxAngle, SpawnType type);

  public abstract void sendSpawnAlert(String message, EnumChatFormatting color);

  public abstract void noSpawnPointNotice();
}