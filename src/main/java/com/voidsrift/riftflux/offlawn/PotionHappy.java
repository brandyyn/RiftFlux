package com.voidsrift.riftflux.offlawn;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.potion.Potion;

import java.util.UUID;

public final class PotionHappy extends Potion {
    private static final UUID SPEED_MODIFIER_UUID =
            UUID.fromString("1ddde743-55b6-4f24-9af8-aa89594ddd3b");

    public PotionHappy(int id) {
        super(id, false, 0xFFE34D);
        setIconIndex(0, 0);
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(OffLawnPotions.HAPPY_ICON);
        return super.getStatusIconIndex();
    }

    @Override
    public void applyAttributesModifiersToEntity(EntityLivingBase target, BaseAttributeMap attributes, int amplifier) {
        IAttributeInstance movementSpeed = target.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (movementSpeed != null) {
            AttributeModifier existing = movementSpeed.getModifier(SPEED_MODIFIER_UUID);
            if (existing != null) {
                movementSpeed.removeModifier(existing);
            }
            double amount = Math.max(0.0D, ModConfig.offLawnBrightSunflowerSpeedBoostPercent / 100.0D);
            movementSpeed.applyModifier(new AttributeModifier(
                    SPEED_MODIFIER_UUID,
                    "Happy! movement speed",
                    amount,
                    2).setSaved(false));
        }
        super.applyAttributesModifiersToEntity(target, attributes, amplifier);
    }

    @Override
    public void removeAttributesModifiersFromEntity(EntityLivingBase target, BaseAttributeMap attributes, int amplifier) {
        IAttributeInstance movementSpeed = target.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
        if (movementSpeed != null) {
            AttributeModifier modifier = movementSpeed.getModifier(SPEED_MODIFIER_UUID);
            if (modifier != null) {
                movementSpeed.removeModifier(modifier);
            }
        }
        super.removeAttributesModifiersFromEntity(target, attributes, amplifier);
    }
}
