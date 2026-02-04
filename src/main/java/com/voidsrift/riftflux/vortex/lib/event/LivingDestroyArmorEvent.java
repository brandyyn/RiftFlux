package com.voidsrift.riftflux.vortex.lib.event;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingEvent;

public class LivingDestroyArmorEvent extends LivingEvent {
   public final ItemStack armor;

   public LivingDestroyArmorEvent(EntityLivingBase entity, ItemStack armor) {
      super(entity);
      this.armor = armor;
   }
}
