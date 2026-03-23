package net.nmccoy.legendgear.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;

public class PotionManaRegen
extends Potion {
    protected PotionManaRegen(int id, boolean badEffect, int liquidColor) {
        super(id, badEffect, liquidColor);
        this.setIconIndex(0, 0);
    }

    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(LegendGearPotions.icon);
        return super.getStatusIconIndex();
    }

    @Override
    public boolean isReady(int duration, int amplifier) {
        return true;
    }

    @Override
    public void performEffect(EntityLivingBase target, int amplifier) {
        if (!(target instanceof EntityPlayer) || target.worldObj == null || target.worldObj.isRemote) {
            return;
        }

        PlayerStarstatsExtension stats = PlayerStarstatsExtension.get((EntityPlayer)target);
        if (stats == null) {
            return;
        }

        float perSecond = Math.max(0.0f, LegendGear2.CONFIG_MANA_REGEN_POTION_PER_SECOND);
        if (perSecond <= 0.0f) {
            return;
        }

        float perTick = perSecond * (float)(amplifier + 1) / 20.0f;
        stats.adjustManaFatigue(-perTick);
    }
}
