package com.voidsrift.riftflux.client.hud;

import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;
import zelda.Config;

public final class HudHealthRowHelper {
    private HudHealthRowHelper() {
    }

    public static int getHealthRows(EntityPlayer player) {
        if (player == null) {
            return 1;
        }

        int maxHealth = 20;
        IAttributeInstance maxHealthAttribute = player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (maxHealthAttribute != null) {
            maxHealth = MathHelper.ceiling_float_int((float) maxHealthAttribute.getAttributeValue());
        }

        int absorption = MathHelper.ceiling_float_int(player.getAbsorptionAmount());
        float healthPerRow = Config.HEARTS_ENABLED ? 40.0F : 20.0F;
        int healthRows = MathHelper.ceiling_float_int((maxHealth + absorption) / healthPerRow);
        return Math.max(1, healthRows);
    }
}
