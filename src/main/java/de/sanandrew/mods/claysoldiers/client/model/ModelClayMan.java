/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.Event
 */
package de.sanandrew.mods.claysoldiers.client.model;

import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.event.SoldierRenderEvent;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.ClaySoldiersMod;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.entity.Entity;

@SideOnly(value=Side.CLIENT)
public class ModelClayMan
extends ModelBiped {
    public ModelClayMan() {
        super(0.0f, 0.0f, 64, 64);
    }

    @Override
    public void setRotationAngles(float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks, Entity entity) {
        super.setRotationAngles(limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks, entity);
        ClaySoldiersMod.EVENT_BUS.post((Event)new SoldierRenderEvent.SetRotationAnglesEvent((EntityClayMan)entity, this, limbSwing, limbSwingAmount, rotFloat, rotYaw, rotPitch, partTicks));
    }
}

