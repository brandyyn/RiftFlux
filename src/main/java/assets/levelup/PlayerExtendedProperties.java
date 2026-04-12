/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package assets.levelup;

import assets.levelup.ClassBonus;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;

public final class PlayerExtendedProperties
implements IExtendedEntityProperties {
    private byte playerClass;
    private Map<String, Integer> skillMap = new HashMap<String, Integer>();
    private Map<String, int[]> counterMap = new HashMap<String, int[]>();
    public static final String[] counters = new String[]{"ore", "craft", "bonus"};

    public PlayerExtendedProperties() {
        for (String name : ClassBonus.skillNames) {
            this.skillMap.put(name, 0);
        }
        this.counterMap.put(counters[0], new int[]{0, 0, 0, 0});
        this.counterMap.put(counters[1], new int[]{0, 0, 0, 0});
        this.counterMap.put(counters[2], new int[]{0, 0, 0});
    }

    public void saveNBTData(NBTTagCompound compound) {
        compound.setByte("Class", this.playerClass);
        for (String name : ClassBonus.skillNames) {
            compound.setInteger(name, this.skillMap.get(name).intValue());
        }
        for (String cat : counters) {
            compound.setIntArray(cat, this.counterMap.get(cat));
        }
    }

    public void loadNBTData(NBTTagCompound compound) {
        this.playerClass = compound.getByte("Class");
        for (String name : ClassBonus.skillNames) {
            this.skillMap.put(name, compound.getInteger(name));
        }
        for (String cat : counters) {
            this.counterMap.put(cat, compound.getIntArray(cat));
        }
    }

    public void init(Entity entity, World world) {
    }

    public static PlayerExtendedProperties from(EntityPlayer player) {
        return (PlayerExtendedProperties)player.getExtendedProperties("LevelUpSkills");
    }

    public void addToSkill(String name, int value) {
        this.skillMap.put(name, this.skillMap.get(name) + value);
    }

    public int getSkillFromIndex(String name) {
        return this.skillMap.get(name);
    }

    public static int getSkillFromIndex(EntityPlayer player, int id) {
        return PlayerExtendedProperties.from(player).getSkillFromIndex(ClassBonus.skillNames[id]);
    }

    public int getSkillPoints() {
        int total = 0;
        for (String skill : ClassBonus.skillNames) {
            total += this.getSkillFromIndex(skill);
        }
        return total;
    }

    public boolean hasClass() {
        return this.playerClass != 0;
    }

    public static byte getPlayerClass(EntityPlayer player) {
        return PlayerExtendedProperties.from((EntityPlayer)player).playerClass;
    }

    public void setPlayerClass(byte newClass) {
        if (newClass != this.playerClass) {
            ClassBonus.applyBonus(this, this.playerClass, newClass);
            this.capSkills();
            this.playerClass = newClass;
        }
    }

    public static Map<String, int[]> getCounterMap(EntityPlayer player) {
        return PlayerExtendedProperties.from((EntityPlayer)player).counterMap;
    }

    public void capSkills() {
        for (String name : ClassBonus.skillNames) {
            int j;
            if (name.equals("XP") || (j = this.skillMap.get(name).intValue()) <= ClassBonus.getMaxSkillPoints()) continue;
            this.skillMap.put(name, ClassBonus.getMaxSkillPoints());
        }
    }

    public void takeSkillFraction(float ratio) {
        byte clas = this.playerClass;
        if (clas != 0) {
            ClassBonus.applyBonus(this, clas, (byte)0);
            this.playerClass = 0;
        }
        for (String name : ClassBonus.skillNames) {
            int value = this.skillMap.get(name);
            int remove = (int)((float)value * ratio);
            if (remove <= 0) continue;
            this.skillMap.put(name, value - remove);
        }
        if (clas != 0) {
            ClassBonus.applyBonus(this, (byte)0, clas);
            this.playerClass = clas;
        }
        this.capSkills();
    }

    public void convertPointsToXp(boolean resetClass) {
        byte clas = this.playerClass;
        this.setPlayerClass((byte)0);
        this.skillMap.put("XP", this.getSkillPoints());
        this.setPlayerData(new int[ClassBonus.skillNames.length - 1]);
        if (!resetClass) {
            this.setPlayerClass(clas);
        }
    }

    void setPlayerData(int[] data) {
        for (int i = 0; i < ClassBonus.skillNames.length && i < data.length; ++i) {
            this.skillMap.put(ClassBonus.skillNames[i], data[i]);
        }
    }

    int[] getPlayerData(boolean withClass) {
        int[] data = new int[ClassBonus.skillNames.length + (withClass ? 1 : 0)];
        for (int i = 0; i < ClassBonus.skillNames.length; ++i) {
            data[i] = this.getSkillFromIndex(ClassBonus.skillNames[i]);
        }
        if (withClass) {
            data[data.length - 1] = this.playerClass;
        }
        return data;
    }
}

