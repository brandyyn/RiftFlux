package gravestone.core.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import gravestone.config.GraveStoneConfig;
import gravestone.core.TimeHelper;
import gravestone.core.compatibility.forestry.GSCompatibilityForestry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;
import net.minecraftforge.client.event.EntityViewRenderEvent.RenderFogEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import org.lwjgl.opengl.GL11;

public class GSRenderEventHandler {
   public static float fogDensityPerTick = 0.0F;
   private static float fogDensity = 0.0F;
   public static final float MAX_DENSITY = 0.08F;
   public static final float DENSITY_PER_GRAVE = 0.001F;
   public static final float DENSITY_PER_TICK = 5.0E-6F;
   private static int amountOfFogSources = 0;

   public static void addFog() {
      ++amountOfFogSources;
      updateFogDensity();
   }

   public static void resetAmountOfFogSources(World world) {
      amountOfFogSources = 0;
      if (fogDensityPerTick != 0.0F && !TimeHelper.isFogTime(world)) {
         fogDensityPerTick = 0.0F;
      }

   }

   private static void updateFogDensity() {
      fogDensityPerTick = 0.001F * (float)amountOfFogSources;
      if (fogDensityPerTick > 0.08F) {
         fogDensityPerTick = 0.08F;
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void backpackTooltip(ItemTooltipEvent event) {
      ItemStack stack = event.itemStack;
      if (stack != null && (stack.getItem() == GSCompatibilityForestry.backpackItemT1 || stack.getItem() == GSCompatibilityForestry.backpackItemT2)) {
         event.toolTip.add(StatCollector.translateToLocal("item.backpack.undertaker.tooltip"));
      }
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void fogEvent(RenderFogEvent event) {
      if (GraveStoneConfig.isFogEnabled) {
         if (fogDensity < fogDensityPerTick) {
            fogDensity += 5.0E-6F;
         } else if (fogDensity > fogDensityPerTick) {
            fogDensity -= 5.0E-6F;
         }

         if (fogDensity < 5.0E-6F) {
            fogDensity = 0.0F;
         }

         if (fogDensity > 0.0F) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, fogDensity);
         }
      }

   }
}
