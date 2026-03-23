package net.nmccoy.legendgear.potion;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

public class PotionJumpPenalty extends Potion {
    protected PotionJumpPenalty(int id, boolean badEffect, int liquidColor) {
        super(id, badEffect, liquidColor);
        this.setIconIndex(1, 0);
    }

    @Override
    public int getStatusIconIndex() {
        Minecraft.getMinecraft().renderEngine.bindTexture(LegendGearPotions.icon);
        return super.getStatusIconIndex();
    }
}
