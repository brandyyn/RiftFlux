/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  cpw.mods.fml.common.FMLLog
 *  org.apache.logging.log4j.Level
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import com.google.common.collect.Maps;
import cpw.mods.fml.common.FMLLog;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.EffectBlindingRedstone;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.EffectMagmaBomb;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.EffectSlimeFeet;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.EffectSlowMotion;
import de.sanandrew.mods.claysoldiers.util.soldier.effect.EffectThunder;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.Level;

public class SoldierEffects {
    public static final String EFF_SLIMEFEET = "slimefeet";
    public static final String EFF_SLOWMOTION = "slowmotion";
    public static final String EFF_THUNDER = "thunder";
    public static final String EFF_REDSTONE = "redstone";
    public static final String EFF_MAGMABOMB = "magmabomb";
    private static final Map<String, ASoldierEffect> NAME_TO_EFFECT_MAP_ = Maps.newHashMap();
    private static final Map<ASoldierEffect, String> EFFECT_TO_NAME_MAP_ = Maps.newHashMap();
    private static final Map<ASoldierEffect, Byte> EFFECT_TO_RENDER_ID_MAP_ = Maps.newHashMap();
    private static final Map<Byte, ASoldierEffect> RENDER_ID_TO_EFFECT_MAP_ = Maps.newHashMap();
    private static byte s_currRenderId = 0;

    public static void initialize() {
        SoldierEffects.registerEffect(EFF_SLIMEFEET, new EffectSlimeFeet(), SoldierEffects.getNewRenderId());
        SoldierEffects.registerEffect(EFF_SLOWMOTION, new EffectSlowMotion());
        SoldierEffects.registerEffect(EFF_THUNDER, new EffectThunder(), SoldierEffects.getNewRenderId());
        SoldierEffects.registerEffect(EFF_REDSTONE, new EffectBlindingRedstone(), SoldierEffects.getNewRenderId());
        SoldierEffects.registerEffect(EFF_MAGMABOMB, new EffectMagmaBomb(), SoldierEffects.getNewRenderId());
    }

    public static void registerEffect(String name, ASoldierEffect instance) {
        SoldierEffects.registerEffect(name, instance, -1);
    }

    public static void registerEffect(String name, ASoldierEffect instance, int clientRenderId) {
        NAME_TO_EFFECT_MAP_.put(name, instance);
        EFFECT_TO_NAME_MAP_.put(instance, name);
        if (clientRenderId >= 0) {
            if (clientRenderId > 127) {
                FMLLog.log((String)"ClaySoldiers", (Level)Level.WARN, (String)"The Effect \"%s\" cannot be bound to the render ID! The render ID is greater than 127!", (Object[])new Object[]{name});
            } else if (RENDER_ID_TO_EFFECT_MAP_.containsKey((byte)clientRenderId)) {
                FMLLog.log((String)"ClaySoldiers", (Level)Level.WARN, (String)"The Effect \"%s\" cannot be bound to the render ID! The render ID is already registered!", (Object[])new Object[]{name});
            } else {
                EFFECT_TO_RENDER_ID_MAP_.put(instance, (byte)clientRenderId);
                RENDER_ID_TO_EFFECT_MAP_.put((byte)clientRenderId, instance);
            }
        }
    }

    public static ASoldierEffect getEffect(String name) {
        return NAME_TO_EFFECT_MAP_.get(name);
    }

    public static String getEffectName(ASoldierEffect effect) {
        return EFFECT_TO_NAME_MAP_.get(effect);
    }

    public static byte getRenderId(ASoldierEffect upgrade) {
        if (EFFECT_TO_RENDER_ID_MAP_.containsKey(upgrade)) {
            return EFFECT_TO_RENDER_ID_MAP_.get(upgrade);
        }
        return -1;
    }

    public static ASoldierEffect getEffect(int renderId) {
        return RENDER_ID_TO_EFFECT_MAP_.get((byte)renderId);
    }

    public static Set<Byte> getRegisteredRenderIds() {
        return RENDER_ID_TO_EFFECT_MAP_.keySet();
    }

    public static byte getNewRenderId() {
        if (s_currRenderId == 127) {
            throw new RenderIdException();
        }
        byte by = s_currRenderId;
        s_currRenderId = (byte)(by + 1);
        return by;
    }

    public static class RenderIdException
    extends RuntimeException {
        public RenderIdException() {
            super("There are no more render IDs for the soldier effect available!");
        }
    }
}

