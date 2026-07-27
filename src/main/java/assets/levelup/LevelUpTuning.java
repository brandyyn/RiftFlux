package assets.levelup;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.network.ByteBufUtils;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.util.StatCollector;

import java.util.Locale;
import java.util.Random;

public final class LevelUpTuning {
    private static final int NETWORK_VERSION = 3;
    private static byte[] serverSnapshot;

    private LevelUpTuning() {
    }

    public static void applyLocalConfig() {
        ClassBonus.configureBonuses(ModConfig.levelUpClassSkillBonuses);
    }

    public static int steps(int skill, int pointsPerStep) {
        return Math.max(0, skill) / Math.max(1, pointsPerStep);
    }

    public static boolean rollPerPoint(Random random, int skill, float percentPerPoint) {
        return roll(random, Math.max(0, skill) * Math.max(0.0F, percentPerPoint));
    }

    public static boolean rollPerStep(Random random, int skill, int pointsPerStep, float percentPerStep) {
        return roll(random, steps(skill, pointsPerStep) * Math.max(0.0F, percentPerStep));
    }

    public static boolean roll(Random random, float percent) {
        return random.nextDouble() * 100.0D < Math.min(100.0D, Math.max(0.0D, percent));
    }

    public static String getSkillTooltip(int skillIndex, int line) {
        String key = "skill.config." + (skillIndex + 1) + ".tooltip" + line;
        switch (skillIndex) {
            case 0:
                return line == 1
                        ? format(key, value(ModConfig.levelUpMiningOreBonusChancePerPointPercent), ModConfig.levelUpMiningBonusDropCount)
                        : format(key, ModConfig.levelUpMiningSpeedPointsPerStep, value(ModConfig.levelUpMiningSpeedIncreasePerStep));
            case 1:
                return line == 1
                        ? format(key, value(ModConfig.levelUpSwordCritChancePerPointPercent), value(ModConfig.levelUpSwordCritDamageMultiplier))
                        : format(key, ModConfig.levelUpSwordDamagePointsPerStep, value(ModConfig.levelUpSwordDamagePercentPerStep));
            case 2:
                return line == 1
                        ? format(key, value(ModConfig.levelUpDefenseSuperBlockChancePerPointPercent), value(ModConfig.levelUpDefenseSuperBlockDamageMultiplier))
                        : format(key, ModConfig.levelUpDefenseReductionPointsPerStep, value(ModConfig.levelUpDefenseReductionPercentPerStep));
            case 3:
                return line == 1
                        ? format(
                                key,
                                value(ModConfig.levelUpWoodPlankChancePerPointPercent),
                                ModConfig.levelUpWoodBonusPlankCount,
                                value(ModConfig.levelUpWoodStickChancePerPointPercent),
                                ModConfig.levelUpWoodBonusStickCount
                        )
                        : format(key, ModConfig.levelUpWoodSpeedPointsPerStep, value(ModConfig.levelUpWoodSpeedIncreasePerStep));
            case 4:
                return line == 1
                        ? format(key, value(ModConfig.levelUpSmeltingBonusYieldChancePerPointPercent), ModConfig.levelUpSmeltingBonusYieldExtraCopies)
                        : format(key, ModConfig.levelUpSmeltingSpeedPointsPerStep, ModConfig.levelUpSmeltingSpeedExtraTicksPerStep);
            case 5:
                return line == 1
                        ? format(
                                key,
                                value(ModConfig.levelUpArcheryProjectileSpeedPercentPerPoint),
                                value(ModConfig.levelUpArcheryProjectileDamagePercentPerPoint)
                        )
                        : format(key, ModConfig.levelUpArcheryDrawSpeedPointsPerTick);
            case 6:
                return line == 1
                        ? format(key, value(ModConfig.levelUpAthleticsSprintSpeedPercentPerPoint))
                        : format(
                                key,
                                ModConfig.levelUpAthleticsFallReductionPointsPerStep,
                                value(ModConfig.levelUpAthleticsFallReductionPercentPerStep)
                        );
            case 7:
                return line == 1
                        ? format(key, value(ModConfig.levelUpCookingBonusYieldChancePerPointPercent), ModConfig.levelUpCookingBonusYieldExtraCopies)
                        : format(key, ModConfig.levelUpCookingSpeedPointsPerStep, ModConfig.levelUpCookingSpeedExtraTicksPerStep);
            case 8:
                return line == 1
                        ? format(key, value(ModConfig.levelUpSneakingSpeedPercentPerPoint))
                        : format(
                                key,
                                value(ModConfig.levelUpSneakingBaseMobSightRange),
                                ModConfig.levelUpSneakingSightReductionPointsPerStep,
                                value(ModConfig.levelUpSneakingSightRangeReductionPerStep)
                        );
            case 9:
                return line == 1
                        ? format(
                                key,
                                value(ModConfig.levelUpFarmingGrowthChancePerPointPercent),
                                ModConfig.levelUpFarmingGrowthRangePointsPerBlock
                        )
                        : format(
                                key,
                                ModConfig.levelUpFarmingBonusDropPointsPerStep,
                                value(ModConfig.levelUpFarmingBonusDropChancePerStepPercent),
                                ModConfig.levelUpFarmingBonusDropCount
                        );
            case 10:
                return line == 1
                        ? format(key, ModConfig.levelUpFishingLootPointsPerStep)
                        : format(key, value(ModConfig.levelUpFishingLootChancePerStepPercent));
            case 11:
                return line == 1
                        ? format(
                                key,
                                value(ModConfig.levelUpDiggingLootChancePerPointPercent),
                                value(ModConfig.levelUpDiggingToolLootChancePercent),
                                value(ModConfig.levelUpDiggingValuableLootChancePercent),
                                value(ModConfig.levelUpDiggingDiamondLootChancePercent)
                        )
                        : format(
                                key,
                                ModConfig.levelUpDiggingFlintPointsPerStep,
                                value(ModConfig.levelUpDiggingFlintChancePerStepPercent),
                                ModConfig.levelUpDiggingFlintCount
                        );
            default:
                return "";
        }
    }

    private static String format(String key, Object... values) {
        return StatCollector.translateToLocalFormatted(key, values);
    }

    private static String value(float number) {
        String formatted = String.format(Locale.ROOT, "%.4f", number);
        int end = formatted.length();
        while (end > 0 && formatted.charAt(end - 1) == '0') {
            --end;
        }
        if (end > 0 && formatted.charAt(end - 1) == '.') {
            --end;
        }
        return end > 0 ? formatted.substring(0, end) : "0";
    }

    public static void writeTo(ByteBuf buf) {
        buf.writeInt(NETWORK_VERSION);
        buf.writeInt(ClassBonus.getClassCount());
        buf.writeInt(ClassBonus.skillNames.length);
        for (int classIndex = 0; classIndex < ClassBonus.getClassCount(); ++classIndex) {
            ByteBufUtils.writeUTF8String(buf, ClassBonus.getRawClassName(classIndex));
            buf.writeByte(ClassBonus.getBonusSources(classIndex));
            for (int skillIndex = 0; skillIndex < ClassBonus.skillNames.length; ++skillIndex) {
                buf.writeInt(ClassBonus.getConfiguredBonus(classIndex, skillIndex));
            }
        }

        buf.writeFloat(ModConfig.levelUpMiningOreBonusChancePerPointPercent);
        buf.writeInt(ModConfig.levelUpMiningBonusDropCount);
        buf.writeInt(ModConfig.levelUpMiningSpeedPointsPerStep);
        buf.writeFloat(ModConfig.levelUpMiningSpeedIncreasePerStep);
        buf.writeFloat(ModConfig.levelUpSwordCritChancePerPointPercent);
        buf.writeFloat(ModConfig.levelUpSwordCritDamageMultiplier);
        buf.writeInt(ModConfig.levelUpSwordDamagePointsPerStep);
        buf.writeFloat(ModConfig.levelUpSwordDamagePercentPerStep);
        buf.writeFloat(ModConfig.levelUpDefenseSuperBlockChancePerPointPercent);
        buf.writeFloat(ModConfig.levelUpDefenseSuperBlockDamageMultiplier);
        buf.writeInt(ModConfig.levelUpDefenseReductionPointsPerStep);
        buf.writeFloat(ModConfig.levelUpDefenseReductionPercentPerStep);
        buf.writeFloat(ModConfig.levelUpWoodPlankChancePerPointPercent);
        buf.writeInt(ModConfig.levelUpWoodBonusPlankCount);
        buf.writeFloat(ModConfig.levelUpWoodStickChancePerPointPercent);
        buf.writeInt(ModConfig.levelUpWoodBonusStickCount);
        buf.writeInt(ModConfig.levelUpWoodSpeedPointsPerStep);
        buf.writeFloat(ModConfig.levelUpWoodSpeedIncreasePerStep);
        buf.writeFloat(ModConfig.levelUpSmeltingBonusYieldChancePerPointPercent);
        buf.writeInt(ModConfig.levelUpSmeltingBonusYieldExtraCopies);
        buf.writeInt(ModConfig.levelUpSmeltingSpeedPointsPerStep);
        buf.writeInt(ModConfig.levelUpSmeltingSpeedExtraTicksPerStep);
        buf.writeFloat(ModConfig.levelUpArcheryProjectileSpeedPercentPerPoint);
        buf.writeFloat(ModConfig.levelUpArcheryProjectileDamagePercentPerPoint);
        buf.writeInt(ModConfig.levelUpArcheryDrawSpeedPointsPerTick);
        buf.writeFloat(ModConfig.levelUpAthleticsSprintSpeedPercentPerPoint);
        buf.writeInt(ModConfig.levelUpAthleticsFallReductionPointsPerStep);
        buf.writeFloat(ModConfig.levelUpAthleticsFallReductionPercentPerStep);
        buf.writeFloat(ModConfig.levelUpCookingBonusYieldChancePerPointPercent);
        buf.writeInt(ModConfig.levelUpCookingBonusYieldExtraCopies);
        buf.writeInt(ModConfig.levelUpCookingSpeedPointsPerStep);
        buf.writeInt(ModConfig.levelUpCookingSpeedExtraTicksPerStep);
        buf.writeFloat(ModConfig.levelUpSneakingSpeedPercentPerPoint);
        buf.writeFloat(ModConfig.levelUpSneakingBaseMobSightRange);
        buf.writeInt(ModConfig.levelUpSneakingSightReductionPointsPerStep);
        buf.writeFloat(ModConfig.levelUpSneakingSightRangeReductionPerStep);
        buf.writeFloat(ModConfig.levelUpFarmingGrowthChancePerPointPercent);
        buf.writeInt(ModConfig.levelUpFarmingGrowthRangePointsPerBlock);
        buf.writeInt(ModConfig.levelUpFarmingBonusDropPointsPerStep);
        buf.writeFloat(ModConfig.levelUpFarmingBonusDropChancePerStepPercent);
        buf.writeInt(ModConfig.levelUpFarmingBonusDropCount);
        buf.writeInt(ModConfig.levelUpFishingLootPointsPerStep);
        buf.writeFloat(ModConfig.levelUpFishingLootChancePerStepPercent);
        buf.writeFloat(ModConfig.levelUpDiggingLootChancePerPointPercent);
        buf.writeFloat(ModConfig.levelUpDiggingToolLootChancePercent);
        buf.writeFloat(ModConfig.levelUpDiggingValuableLootChancePercent);
        buf.writeFloat(ModConfig.levelUpDiggingDiamondLootChancePercent);
        buf.writeFloat(ModConfig.levelUpDiggingExtraStackItemChancePercent);
        buf.writeInt(ModConfig.levelUpDiggingFlintPointsPerStep);
        buf.writeFloat(ModConfig.levelUpDiggingFlintChancePerStepPercent);
        buf.writeInt(ModConfig.levelUpDiggingFlintCount);
    }

    public static void readFrom(ByteBuf buf) {
        int start = buf.readerIndex();
        byte[] snapshot = new byte[buf.readableBytes()];
        buf.getBytes(start, snapshot);
        readPayload(buf);
        serverSnapshot = snapshot;
    }

    public static void restoreServerValues() {
        if (serverSnapshot != null) {
            readPayload(Unpooled.wrappedBuffer(serverSnapshot));
        }
    }

    private static void readPayload(ByteBuf buf) {
        if (buf.readableBytes() < 12 || buf.readInt() != NETWORK_VERSION) {
            return;
        }
        int classCount = buf.readInt();
        int skillCount = buf.readInt();
        if (classCount <= 0 || classCount > Byte.MAX_VALUE || skillCount < 0 || skillCount > 256) {
            return;
        }
        String[] classNames = new String[classCount];
        int[] bonusSources = new int[classCount];
        int[][] bonuses = new int[classCount][skillCount];
        for (int classIndex = 0; classIndex < classCount; ++classIndex) {
            classNames[classIndex] = ByteBufUtils.readUTF8String(buf);
            bonusSources[classIndex] = buf.readUnsignedByte();
            for (int skillIndex = 0; skillIndex < skillCount; ++skillIndex) {
                bonuses[classIndex][skillIndex] = buf.readInt();
            }
        }
        ClassBonus.setConfiguredClasses(classNames, bonusSources, bonuses);

        ModConfig.levelUpMiningOreBonusChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpMiningBonusDropCount = buf.readInt();
        ModConfig.levelUpMiningSpeedPointsPerStep = buf.readInt();
        ModConfig.levelUpMiningSpeedIncreasePerStep = buf.readFloat();
        ModConfig.levelUpSwordCritChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpSwordCritDamageMultiplier = buf.readFloat();
        ModConfig.levelUpSwordDamagePointsPerStep = buf.readInt();
        ModConfig.levelUpSwordDamagePercentPerStep = buf.readFloat();
        ModConfig.levelUpDefenseSuperBlockChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpDefenseSuperBlockDamageMultiplier = buf.readFloat();
        ModConfig.levelUpDefenseReductionPointsPerStep = buf.readInt();
        ModConfig.levelUpDefenseReductionPercentPerStep = buf.readFloat();
        ModConfig.levelUpWoodPlankChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpWoodBonusPlankCount = buf.readInt();
        ModConfig.levelUpWoodStickChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpWoodBonusStickCount = buf.readInt();
        ModConfig.levelUpWoodSpeedPointsPerStep = buf.readInt();
        ModConfig.levelUpWoodSpeedIncreasePerStep = buf.readFloat();
        ModConfig.levelUpSmeltingBonusYieldChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpSmeltingBonusYieldExtraCopies = buf.readInt();
        ModConfig.levelUpSmeltingSpeedPointsPerStep = buf.readInt();
        ModConfig.levelUpSmeltingSpeedExtraTicksPerStep = buf.readInt();
        ModConfig.levelUpArcheryProjectileSpeedPercentPerPoint = buf.readFloat();
        ModConfig.levelUpArcheryProjectileDamagePercentPerPoint = buf.readFloat();
        ModConfig.levelUpArcheryDrawSpeedPointsPerTick = buf.readInt();
        ModConfig.levelUpAthleticsSprintSpeedPercentPerPoint = buf.readFloat();
        ModConfig.levelUpAthleticsFallReductionPointsPerStep = buf.readInt();
        ModConfig.levelUpAthleticsFallReductionPercentPerStep = buf.readFloat();
        ModConfig.levelUpCookingBonusYieldChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpCookingBonusYieldExtraCopies = buf.readInt();
        ModConfig.levelUpCookingSpeedPointsPerStep = buf.readInt();
        ModConfig.levelUpCookingSpeedExtraTicksPerStep = buf.readInt();
        ModConfig.levelUpSneakingSpeedPercentPerPoint = buf.readFloat();
        ModConfig.levelUpSneakingBaseMobSightRange = buf.readFloat();
        ModConfig.levelUpSneakingSightReductionPointsPerStep = buf.readInt();
        ModConfig.levelUpSneakingSightRangeReductionPerStep = buf.readFloat();
        ModConfig.levelUpFarmingGrowthChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpFarmingGrowthRangePointsPerBlock = buf.readInt();
        ModConfig.levelUpFarmingBonusDropPointsPerStep = buf.readInt();
        ModConfig.levelUpFarmingBonusDropChancePerStepPercent = buf.readFloat();
        ModConfig.levelUpFarmingBonusDropCount = buf.readInt();
        ModConfig.levelUpFishingLootPointsPerStep = buf.readInt();
        ModConfig.levelUpFishingLootChancePerStepPercent = buf.readFloat();
        ModConfig.levelUpDiggingLootChancePerPointPercent = buf.readFloat();
        ModConfig.levelUpDiggingToolLootChancePercent = buf.readFloat();
        ModConfig.levelUpDiggingValuableLootChancePercent = buf.readFloat();
        ModConfig.levelUpDiggingDiamondLootChancePercent = buf.readFloat();
        ModConfig.levelUpDiggingExtraStackItemChancePercent = buf.readFloat();
        ModConfig.levelUpDiggingFlintPointsPerStep = buf.readInt();
        ModConfig.levelUpDiggingFlintChancePerStepPercent = buf.readFloat();
        ModConfig.levelUpDiggingFlintCount = buf.readInt();
    }
}
