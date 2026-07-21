package gravestone.renderer.entity;

import gravestone.core.Resources;
import gravestone.entity.monster.EntitySkullCrawler;
import gravestone.models.entity.ModelSkullCrawler;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;

public class RenderSkullCrawler extends RenderLiving {
   private EntitySkullCrawler.SkullCrawlerType crawlerType;

   public RenderSkullCrawler(EntitySkullCrawler.SkullCrawlerType crawlerType) {
      super(new ModelSkullCrawler(), 0.2F);
      this.crawlerType = crawlerType;
      this.setRenderPassModel(new ModelSkullCrawler());
   }

   protected float setSpiderDeathMaxRotation(EntitySkullCrawler entity) {
      return 180.0F;
   }

   protected float getDeathMaxRotation(EntityLivingBase entity) {
      return this.setSpiderDeathMaxRotation((EntitySkullCrawler)entity);
   }

   protected ResourceLocation getEntityTexture(Entity entity) {
      switch(this.crawlerType) {
      case wither:
         return Resources.WITHER_SKULL_CRAWLER;
      case zombie:
         return Resources.ZOMBIE_SKULL_CRAWLER;
      case skeleton:
      default:
         return Resources.SKULL_CRAWLER;
      }
   }
}
