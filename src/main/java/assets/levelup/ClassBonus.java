package assets.levelup;

import net.minecraft.util.StatCollector;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public final class ClassBonus {
    public static final String SKILL_ID = "LevelUpSkills";
    public static final String[] skillNames = new String[]{"Mining", "Sword", "Defense", "WoodCutting", "Smelting", "Archery", "Athletics", "Cooking", "Sneaking", "Farming", "Fishing", "Digging", "XP"};
    private static final String[] BUILT_IN_NAMES = new String[]{"None", "Miner", "Warrior", "Artisan", "Spelunker", "Scout", "Farmer", "Archaeologist", "Assassin", "Lumberjack", "Hermit", "Zealot", "Fisherman", "Freelancer"};
    private static int bonusPoints = 20;
    private static int maxSkillPoints = 50;
    private static String[] classNames = new String[]{"None"};
    private static int[] bonusSources = new int[]{0};
    private static int[][] configuredBonuses = new int[1][skillNames.length];

    static {
        configureBonuses(new String[]{
                "Miner=Bonus:Mining,Mining:10,Digging:5,Smelting:5",
                "Warrior=Bonus:Combat,Sword:10,Defense:5,Archery:5",
                "Artisan=Bonus:Crafting,Smelting:10,WoodCutting:5,Cooking:5",
                "Spelunker=Bonus:Mining,Defense:10,Athletics:5,Mining:5",
                "Scout=Bonus:Combat,Archery:10,Sneaking:5,Athletics:5",
                "Farmer=Bonus:Crafting,Farming:10,Fishing:5,WoodCutting:5",
                "Archaeologist=Bonus:Mining,Digging:10,WoodCutting:5,Mining:5",
                "Assassin=Bonus:Combat,Sneaking:10,Sword:5,Archery:5",
                "Lumberjack=Bonus:Crafting,WoodCutting:10,Defense:5,Athletics:5",
                "Hermit=Bonus:Mining,Cooking:10,Digging:5,Mining:5",
                "Zealot=Bonus:Combat,Athletics:10,Sword:5,Defense:5",
                "Fisherman=Bonus:Crafting,Fishing:10,Cooking:5,WoodCutting:5",
                "Freelancer=Bonus:None,XP:20",
                "Rift Explorer=Bonus:None,Athletics:10,Sword:5,Defense:5,XP:5",
                "Rift Demon=Bonus:None,XP:33",
                "Adventurer=Bonus:Mining+Combat+Crafting,Mining:5,Sword:5",
                "Blacksmith=Bonus:Mining+Crafting,Smelting:10,Mining:5,Defense:5",
                "Treasure Hunter=Bonus:Mining+Combat,Digging:10,Sneaking:5,Athletics:5",
                "Corsair=Bonus:Combat+Crafting,Sword:10,Fishing:5,Athletics:5",
                "Survivalist=Bonus:Mining+Crafting,WoodCutting:5,Cooking:5,Fishing:5,Defense:5",
                "Alchemist=Bonus:Crafting,Smelting:10,Cooking:5,Farming:5"
        });
    }

    private ClassBonus() {
    }

    public static int getBonusPoints() {
        return bonusPoints;
    }

    public static void setBonusPoints(int value) {
        if (value >= 0) {
            bonusPoints = Math.min(value, maxSkillPoints * 2);
        }
    }

    public static int getMaxSkillPoints() {
        return maxSkillPoints;
    }

    public static void setSkillMax(int value) {
        if (value > 0) {
            maxSkillPoints = value;
        }
    }

    public static void addBonusToSkill(PlayerExtendedProperties properties, String name, int bonus, boolean isNew) {
        properties.addToSkill(name, bonus * (isNew ? 1 : -1));
    }

    private static void applyBonus(PlayerExtendedProperties properties, byte playerClass, boolean isNew) {
        int classIndex = getClassIndex(playerClass);
        if (classIndex <= 0) {
            return;
        }
        for (int skillIndex = 0; skillIndex < skillNames.length; ++skillIndex) {
            int bonus = getAppliedBonus(classIndex, skillIndex);
            if (bonus != 0) {
                addBonusToSkill(properties, skillNames[skillIndex], bonus, isNew);
            }
        }
    }

    public static void applyBonus(PlayerExtendedProperties properties, byte oldClass, byte newClass) {
        applyBonus(properties, oldClass, false);
        applyBonus(properties, newClass, true);
    }

    public static void configureBonuses(String[] profiles) {
        List<String> names = new ArrayList<String>();
        List<Integer> sources = new ArrayList<Integer>();
        List<int[]> bonusesByClass = new ArrayList<int[]>();
        names.add("None");
        sources.add(0);
        bonusesByClass.add(new int[skillNames.length]);

        if (profiles != null) {
            for (String profile : profiles) {
                if (profile == null || names.size() >= Byte.MAX_VALUE) {
                    continue;
                }
                int equals = profile.indexOf('=');
                if (equals <= 0 || equals >= profile.length() - 1) {
                    continue;
                }
                String name = profile.substring(0, equals).trim();
                if (name.length() == 0 || findName(names, name) >= 0) {
                    continue;
                }
                int[] bonuses = new int[skillNames.length];
                int classBonusSources = defaultBuiltInBonus(name).getMask();
                boolean explicitBonusSources = false;
                String[] entries = profile.substring(equals + 1).split(",");
                for (String entry : entries) {
                    int colon = entry.indexOf(':');
                    if (colon <= 0 || colon >= entry.length() - 1) {
                        continue;
                    }
                    String key = entry.substring(0, colon);
                    String value = entry.substring(colon + 1).trim();
                    if (normalize(key).equals("bonus")) {
                        if (!explicitBonusSources) {
                            classBonusSources = 0;
                            explicitBonusSources = true;
                        }
                        classBonusSources |= BonusType.parseMask(value);
                        continue;
                    }
                    int skillIndex = findSkillIndex(key);
                    if (skillIndex < 0) {
                        continue;
                    }
                    try {
                        bonuses[skillIndex] = Math.max(0, Integer.parseInt(value));
                    } catch (NumberFormatException ignored) {
                    }
                }
                names.add(name);
                sources.add(classBonusSources);
                bonusesByClass.add(bonuses);
            }
        }

        classNames = names.toArray(new String[names.size()]);
        bonusSources = new int[sources.size()];
        for (int index = 0; index < sources.size(); ++index) {
            bonusSources[index] = sources.get(index);
        }
        configuredBonuses = bonusesByClass.toArray(new int[bonusesByClass.size()][]);
    }

    public static void setConfiguredClasses(String[] names, int[] sources, int[][] values) {
        int count = Math.min(Byte.MAX_VALUE, Math.min(names == null ? 0 : names.length, values == null ? 0 : values.length));
        if (count <= 0) {
            return;
        }
        classNames = new String[count];
        bonusSources = new int[count];
        configuredBonuses = new int[count][skillNames.length];
        classNames[0] = "None";
        for (int classIndex = 1; classIndex < count; ++classIndex) {
            classNames[classIndex] = names[classIndex] == null || names[classIndex].trim().length() == 0 ? "Class " + classIndex : names[classIndex];
            bonusSources[classIndex] = sources != null && classIndex < sources.length ? sources[classIndex] & BonusType.ALL_MASK : 0;
            if (values[classIndex] == null) {
                continue;
            }
            for (int skillIndex = 0; skillIndex < skillNames.length && skillIndex < values[classIndex].length; ++skillIndex) {
                configuredBonuses[classIndex][skillIndex] = Math.max(0, values[classIndex][skillIndex]);
            }
        }
    }

    public static int getConfiguredBonus(int classIndex, int skillIndex) {
        if (classIndex < 0 || classIndex >= configuredBonuses.length || skillIndex < 0 || skillIndex >= skillNames.length) {
            return 0;
        }
        return configuredBonuses[classIndex][skillIndex];
    }

    public static int getClassCount() {
        return configuredBonuses.length;
    }

    public static boolean isValidClass(byte playerClass) {
        int index = playerClass;
        return index > 0 && index < getClassCount();
    }

    public static String getClassName(int classIndex) {
        if (classIndex < 0 || classIndex >= classNames.length) {
            return "";
        }
        if (classIndex == 0) {
            return StatCollector.translateToLocal("class0.name");
        }
        if (classIndex < BUILT_IN_NAMES.length
                && (normalize(BUILT_IN_NAMES[classIndex]).equals(normalize(classNames[classIndex]))
                || classIndex == 13 && normalize(classNames[classIndex]).equals("freelance"))) {
            return StatCollector.translateToLocal("class" + classIndex + ".name");
        }
        return classNames[classIndex];
    }

    public static String getRawClassName(int classIndex) {
        return classIndex >= 0 && classIndex < classNames.length ? classNames[classIndex] : "";
    }

    public static int getBonusSources(int classIndex) {
        return classIndex >= 0 && classIndex < bonusSources.length ? bonusSources[classIndex] : 0;
    }

    public static boolean hasBonusSource(int classIndex, BonusType type) {
        return type != null && type != BonusType.NONE && (getBonusSources(classIndex) & type.getMask()) != 0;
    }

    public static String getLocalizedBonuses(int classIndex) {
        int sources = getBonusSources(classIndex);
        if (sources == 0) {
            return BonusType.NONE.getLocalizedName();
        }
        StringBuilder names = new StringBuilder();
        for (BonusType type : BonusType.values()) {
            if (type == BonusType.NONE || (sources & type.getMask()) == 0) {
                continue;
            }
            if (names.length() > 0) {
                names.append(" + ");
            }
            names.append(StatCollector.translateToLocal(type.sourceLangKey));
        }
        return StatCollector.translateToLocalFormatted("gui.class.bonus.multiple", names.toString());
    }

    public static int getTotalBonus(byte playerClass) {
        int classIndex = getClassIndex(playerClass);
        int total = 0;
        for (int skillIndex = 0; classIndex > 0 && skillIndex < skillNames.length; ++skillIndex) {
            total += getAppliedBonus(classIndex, skillIndex);
        }
        return total;
    }

    private static int getAppliedBonus(int classIndex, int skillIndex) {
        int value = getConfiguredBonus(classIndex, skillIndex);
        return skillIndex == skillNames.length - 1 ? value : Math.min(value, maxSkillPoints);
    }

    private static int getClassIndex(byte playerClass) {
        int classIndex = playerClass;
        return classIndex >= 0 && classIndex < configuredBonuses.length ? classIndex : 0;
    }

    private static int findName(List<String> names, String name) {
        String normalized = normalize(name);
        for (int index = 0; index < names.size(); ++index) {
            if (normalize(names.get(index)).equals(normalized)) {
                return index;
            }
        }
        return -1;
    }

    private static int findSkillIndex(String name) {
        String normalized = normalize(name);
        for (int skillIndex = 0; skillIndex < skillNames.length; ++skillIndex) {
            if (normalize(skillNames[skillIndex]).equals(normalized)) {
                return skillIndex;
            }
        }
        return -1;
    }

    private static BonusType defaultBuiltInBonus(String name) {
        String normalized = normalize(name);
        if (normalized.equals("miner") || normalized.equals("spelunker") || normalized.equals("archaeologist") || normalized.equals("hermit")) {
            return BonusType.MINING;
        }
        if (normalized.equals("warrior") || normalized.equals("scout") || normalized.equals("assassin") || normalized.equals("zealot")) {
            return BonusType.COMBAT;
        }
        if (normalized.equals("artisan") || normalized.equals("farmer") || normalized.equals("lumberjack") || normalized.equals("fisherman")) {
            return BonusType.CRAFTING;
        }
        return BonusType.NONE;
    }

    private static String normalize(String value) {
        return value == null ? "" : value.replace("_", "").replace(" ", "").trim().toLowerCase(Locale.ROOT);
    }

    public enum BonusType {
        NONE(0, "gui.class.bonus.none", ""),
        MINING(1, "gui.class.bonus.mining", "gui.class.bonus.source.mining"),
        COMBAT(2, "gui.class.bonus.combat", "gui.class.bonus.source.combat"),
        CRAFTING(4, "gui.class.bonus.crafting", "gui.class.bonus.source.crafting");

        private static final int ALL_MASK = MINING.mask | COMBAT.mask | CRAFTING.mask;
        private final int mask;
        private final String langKey;
        private final String sourceLangKey;

        BonusType(int mask, String langKey, String sourceLangKey) {
            this.mask = mask;
            this.langKey = langKey;
            this.sourceLangKey = sourceLangKey;
        }

        public int getMask() {
            return mask;
        }

        public String getLocalizedName() {
            return StatCollector.translateToLocal(langKey);
        }

        public static BonusType fromName(String name) {
            String normalized = normalize(name);
            if (normalized.equals("mining")) {
                return MINING;
            }
            if (normalized.equals("combat") || normalized.equals("fighting")) {
                return COMBAT;
            }
            if (normalized.equals("crafting") || normalized.equals("craft")) {
                return CRAFTING;
            }
            return NONE;
        }

        public static int parseMask(String names) {
            int mask = 0;
            if (names != null) {
                for (String name : names.split("\\+")) {
                    mask |= fromName(name).mask;
                }
            }
            return mask & ALL_MASK;
        }
    }
}
