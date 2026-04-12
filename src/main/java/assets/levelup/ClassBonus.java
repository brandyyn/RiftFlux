/*
 * Decompiled with CFR 0.152.
 */
package assets.levelup;

import assets.levelup.PlayerExtendedProperties;

public final class ClassBonus {
    public static final String SKILL_ID = "LevelUpSkills";
    public static final String[] skillNames = new String[]{"Mining", "Sword", "Defense", "WoodCutting", "Smelting", "Archery", "Athletics", "Cooking", "Sneaking", "Farming", "Fishing", "Digging", "XP"};
    private static int bonusPoints = 20;
    private static int maxSkillPoints = 50;

    public static int getBonusPoints() {
        return bonusPoints;
    }

    public static void setBonusPoints(int value) {
        if (value >= 0) {
            bonusPoints = value <= maxSkillPoints * 2 ? value : maxSkillPoints * 2;
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
        CLASSES clas = CLASSES.from(playerClass);
        if (clas.isNone()) {
            return;
        }
        if (clas.hasOnlyOneSkill()) {
            ClassBonus.addBonusToSkill(properties, skillNames[clas.main], bonusPoints, isNew);
            return;
        }
        int small = bonusPoints / 4;
        int big = bonusPoints - 2 * small;
        ClassBonus.addBonusToSkill(properties, skillNames[clas.main], big, isNew);
        ClassBonus.addBonusToSkill(properties, skillNames[clas.sec1], small, isNew);
        ClassBonus.addBonusToSkill(properties, skillNames[clas.sec2], small, isNew);
    }

    public static void applyBonus(PlayerExtendedProperties properties, byte oldClass, byte newClass) {
        ClassBonus.applyBonus(properties, oldClass, false);
        ClassBonus.applyBonus(properties, newClass, true);
    }

    public static enum CLASSES {
        NONE(-1, -1, -1),
        MINER(0, 11, 4),
        WARRIOR(1, 2, 5),
        ARTISAN(4, 3, 7),
        SPELUNKER(2, 6, 0),
        SCOUT(5, 8, 6),
        FARMER(9, 10, 3),
        ARCHAEOLOGIST(11, 3, 0),
        ASSASSIN(8, 1, 5),
        LUMBERJACK(3, 2, 6),
        HERMIT(7, 11, 0),
        ZEALOT(6, 1, 2),
        FISHERMAN(10, 7, 3),
        FREELANCE(12, 12, 12);

        private final int main;
        private final int sec1;
        private final int sec2;

        private CLASSES(int main, int sec1, int sec2) {
            this.main = main;
            this.sec1 = sec1;
            this.sec2 = sec2;
        }

        public static CLASSES from(byte b) {
            if (b < 0) {
                return NONE;
            }
            return CLASSES.values()[b];
        }

        public boolean isNone() {
            return this == NONE;
        }

        public boolean hasOnlyOneSkill() {
            return this.main == this.sec1 && this.main == this.sec2;
        }
    }
}

