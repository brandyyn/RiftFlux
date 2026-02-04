package com.voidsrift.riftflux.vortex.potion;

import java.util.UUID;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.BaseAttributeMap;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.potion.Potion;

public class PotionHeadBuff extends Potion {
   private static final UUID buff = UUID.fromString("66b26a71-795a-4c5b-9f35-1187a43f5093");

   protected PotionHeadBuff(int id, boolean bad, int color) {
      super(id, bad, color);
      this.setIconIndex(1, 0);
   }

   public int getStatusIconIndex() {
      Minecraft.getMinecraft().renderEngine.bindTexture(ModPotions.icon);
      return super.getStatusIconIndex();
   }

   public void performEffect(EntityLivingBase target, int par2) {
   }

   public void applyAttributesModifiersToEntity(EntityLivingBase target, BaseAttributeMap p_111185_2_, int amplifier) {
      if (target instanceof EntityPlayer) {
         IAttributeInstance movespeed = target.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
         AttributeModifier speedBuff = new AttributeModifier(buff, "generic.movementSpeed", (double)(amplifier + 9) / 81.0D, 0);
         movespeed.applyModifier(speedBuff);
      }

      super.applyAttributesModifiersToEntity(target, p_111185_2_, amplifier);
   }

   public void removeAttributesModifiersFromEntity(EntityLivingBase target, BaseAttributeMap p_111187_2_, int amplifier) {
      IAttributeInstance movespeed = target.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
      if (movespeed.getModifier(buff) != null) {
         movespeed.removeModifier(movespeed.getModifier(buff));
      }

      super.removeAttributesModifiersFromEntity(target, p_111187_2_, amplifier);
   }
}
