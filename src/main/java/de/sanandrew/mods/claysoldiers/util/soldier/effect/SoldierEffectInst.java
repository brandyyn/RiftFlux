/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.util.soldier.effect;

import de.sanandrew.mods.claysoldiers.util.soldier.effect.ASoldierEffect;
import net.minecraft.nbt.NBTTagCompound;

public final class SoldierEffectInst {
    private ASoldierEffect p_effect;
    private NBTTagCompound p_nbt = new NBTTagCompound();

    public SoldierEffectInst(ASoldierEffect effect) {
        this.p_effect = effect;
    }

    public NBTTagCompound getNbtTag() {
        return this.p_nbt;
    }

    public void setNbtTag(NBTTagCompound nbt) {
        if (nbt != null) {
            this.p_nbt = nbt;
        }
    }

    public ASoldierEffect getEffect() {
        return this.p_effect;
    }
}

