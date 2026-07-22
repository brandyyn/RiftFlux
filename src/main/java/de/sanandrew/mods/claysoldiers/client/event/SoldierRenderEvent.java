/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.event;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.mods.claysoldiers.client.model.ModelClayMan;
import de.sanandrew.mods.claysoldiers.client.render.entity.RenderClayMan;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.event.SoldierEvent;
import net.minecraft.client.model.ModelBiped;

@SideOnly(value=Side.CLIENT)
public class SoldierRenderEvent
extends SoldierEvent {
    public final RenderClayMan clayManRender;
    public final double renderX;
    public final double renderY;
    public final double renderZ;
    public final float renderYaw;
    public final float partTicks;
    public final EnumRenderStage stage;

    public SoldierRenderEvent(EntityClayMan clayMan, EnumRenderStage stage, RenderClayMan clayManRender, double x, double y, double z, float yaw, float partTicks) {
        super(clayMan);
        this.clayManRender = clayManRender;
        this.stage = stage;
        this.renderX = x;
        this.renderY = y;
        this.renderZ = z;
        this.renderYaw = yaw;
        this.partTicks = partTicks;
    }

    @SideOnly(value=Side.CLIENT)
    public static class RenderLivingEvent
    extends SoldierRenderEvent {
        public RenderLivingEvent(EntityClayMan clayMan, RenderClayMan clayManRender, double x, double y, double z) {
            super(clayMan, EnumRenderStage.LIVING, clayManRender, x, y, z, 0.0f, 0.0f);
        }
    }

    @SideOnly(value=Side.CLIENT)
    public static class SetRotationAnglesEvent
    extends SoldierRenderEvent {
        public final ModelBiped model;
        public final float limbSwing;
        public final float limbSwingAmount;
        public final float rotFloat;
        public final float pitch;

        public SetRotationAnglesEvent(EntityClayMan clayMan, ModelClayMan clayManModel, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
            super(clayMan, EnumRenderStage.MODEL_ROTATIONS, null, clayMan.posX, clayMan.posY, clayMan.posZ, rotYaw, partTicks);
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.rotFloat = rotFloat;
            this.pitch = rotPitch;
            this.model = clayManModel;
        }
    }

    @SideOnly(value=Side.CLIENT)
    public static class RenderModelEvent
    extends SoldierRenderEvent {
        public final float limbSwing;
        public final float limbSwingAmount;
        public final float rotFloat;
        public final float pitch;

        public RenderModelEvent(EntityClayMan clayMan, RenderClayMan clayManRender, float limbSwing, float limbSwingAmount, float rotFloat, float rotYaw, float rotPitch, float partTicks) {
            super(clayMan, EnumRenderStage.MODEL, clayManRender, clayMan.posX, clayMan.posY, clayMan.posZ, rotYaw, partTicks);
            this.limbSwing = limbSwing;
            this.limbSwingAmount = limbSwingAmount;
            this.rotFloat = rotFloat;
            this.pitch = rotPitch;
        }
    }

    @SideOnly(value=Side.CLIENT)
    public static enum EnumRenderStage {
        PRE,
        POST,
        EQUIPPED,
        MODEL,
        MODEL_ROTATIONS,
        LIVING;

    }
}

