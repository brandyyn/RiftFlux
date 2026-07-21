package gravestone.core.proxy;

import cpw.mods.fml.client.FMLClientHandler;
import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.registry.LanguageRegistry;
import cpw.mods.fml.common.registry.VillagerRegistry;
import gravestone.core.GSBlock;
import gravestone.core.GSItem;
import gravestone.core.Resources;
import gravestone.core.event.GSRenderEventHandler;
import gravestone.core.event.GSTickEventHandler;
import gravestone.entity.monster.EntitySkeletonCat;
import gravestone.entity.monster.EntitySkeletonDog;
import gravestone.entity.monster.EntitySkullCrawler;
import gravestone.entity.monster.EntityWitherSkullCrawler;
import gravestone.entity.monster.EntityZombieCat;
import gravestone.entity.monster.EntityZombieDog;
import gravestone.entity.monster.EntityZombieSkullCrawler;
import gravestone.gui.GSGuiGrave;
import gravestone.models.entity.ModelUndeadCat;
import gravestone.models.entity.ModelUndeadDog;
import gravestone.renderer.entity.RenderAltar;
import gravestone.renderer.entity.RenderSkullCrawler;
import gravestone.renderer.entity.RenderUndeadCat;
import gravestone.renderer.entity.RenderUndeadDog;
import gravestone.renderer.item.ItemGSCandleRenderer;
import gravestone.renderer.item.ItemGSCorpseRenderer;
import gravestone.renderer.item.ItemGSGraveStoneRenderer;
import gravestone.renderer.item.ItemGSMemorialRenderer;
import gravestone.renderer.item.ItemGSPileOfBonesRenderer;
import gravestone.renderer.item.ItemGSSkullCandleRenderer;
import gravestone.renderer.item.ItemGSSpawnerRenderer;
import gravestone.renderer.tileentity.TileEntityGSCandleRenderer;
import gravestone.renderer.tileentity.TileEntityGSGraveStoneRenderer;
import gravestone.renderer.tileentity.TileEntityGSHauntedChestRenderer;
import gravestone.renderer.tileentity.TileEntityGSMemorialRenderer;
import gravestone.renderer.tileentity.TileEntityGSPileOfBonesRenderer;
import gravestone.renderer.tileentity.TileEntityGSSkullCandleRenderer;
import gravestone.renderer.tileentity.TileEntityGSSpawnerRenderer;
import gravestone.tileentity.TileEntityGSAltar;
import gravestone.tileentity.TileEntityGSCandle;
import gravestone.tileentity.TileEntityGSGrave;
import gravestone.tileentity.TileEntityGSGraveStone;
import gravestone.tileentity.TileEntityGSHauntedChest;
import gravestone.tileentity.TileEntityGSMemorial;
import gravestone.tileentity.TileEntityGSPileOfBones;
import gravestone.tileentity.TileEntityGSSkullCandle;
import gravestone.tileentity.TileEntityGSSpawner;
import net.minecraft.item.Item;
import net.minecraft.util.StatCollector;
import net.minecraftforge.client.MinecraftForgeClient;
import net.minecraftforge.common.MinecraftForge;
import org.apache.commons.lang3.StringUtils;

public class ClientProxy extends CommonProxy {
   public void registerRenderers() {
      this.registerBlocksRenderers();
      this.registerMobsRenderers();
   }

   private void registerBlocksRenderers() {
      ClientRegistry.registerTileEntity(TileEntityGSGraveStone.class, "GSGraveStone", new TileEntityGSGraveStoneRenderer());
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GSBlock.graveStone), new ItemGSGraveStoneRenderer());
      ClientRegistry.registerTileEntity(TileEntityGSMemorial.class, "GSMemorial", new TileEntityGSMemorialRenderer());
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GSBlock.memorial), new ItemGSMemorialRenderer());
      ClientRegistry.registerTileEntity(TileEntityGSSpawner.class, "GSSpawner", new TileEntityGSSpawnerRenderer());
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GSBlock.spawner), new ItemGSSpawnerRenderer());
      ClientRegistry.registerTileEntity(TileEntityGSHauntedChest.class, "GSHauntedChest", new TileEntityGSHauntedChestRenderer());
      ClientRegistry.registerTileEntity(TileEntityGSSkullCandle.class, "GSSkullCandle", new TileEntityGSSkullCandleRenderer());
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GSBlock.skullCandle), new ItemGSSkullCandleRenderer());
      ClientRegistry.registerTileEntity(TileEntityGSCandle.class, "GSCandle", new TileEntityGSCandleRenderer());
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GSBlock.candle), new ItemGSCandleRenderer());
      ClientRegistry.registerTileEntity(TileEntityGSPileOfBones.class, "GSPileOfBones", new TileEntityGSPileOfBonesRenderer());
      MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(GSBlock.pileOfBones), new ItemGSPileOfBonesRenderer());
      MinecraftForgeClient.registerItemRenderer(GSItem.corpse, new ItemGSCorpseRenderer());
      ClientRegistry.bindTileEntitySpecialRenderer(TileEntityGSAltar.class, new RenderAltar());
   }

   private void registerMobsRenderers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityZombieDog.class, new RenderUndeadDog(new ModelUndeadDog(), new ModelUndeadDog()));
      RenderingRegistry.registerEntityRenderingHandler(EntityZombieCat.class, new RenderUndeadCat(new ModelUndeadCat(), 0.0F));
      RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonDog.class, new RenderUndeadDog(new ModelUndeadDog(), new ModelUndeadDog()));
      RenderingRegistry.registerEntityRenderingHandler(EntitySkeletonCat.class, new RenderUndeadCat(new ModelUndeadCat(), 0.0F));
      RenderingRegistry.registerEntityRenderingHandler(EntitySkullCrawler.class, new RenderSkullCrawler(EntitySkullCrawler.SkullCrawlerType.skeleton));
      RenderingRegistry.registerEntityRenderingHandler(EntityWitherSkullCrawler.class, new RenderSkullCrawler(EntitySkullCrawler.SkullCrawlerType.wither));
      RenderingRegistry.registerEntityRenderingHandler(EntityZombieSkullCrawler.class, new RenderSkullCrawler(EntitySkullCrawler.SkullCrawlerType.zombie));
   }

   public void registerVillagers() {
      VillagerRegistry.instance().registerVillagerSkin(385, Resources.UNDERTAKER);
   }

   public String getLocalizedString(String str) {
      String localizedString = null;

      try {
         localizedString = LanguageRegistry.instance().getStringLocalization(str);
      } catch (Exception var4) {
      }

      return StringUtils.isBlank(localizedString) ? LanguageRegistry.instance().getStringLocalization(str, "en_US") : localizedString;
   }

   public String getLocalizedEntityName(String name) {
      return StatCollector.translateToLocal(name);
   }

   public void openGraveGui(TileEntityGSGrave tileEntity) {
      FMLClientHandler.instance().getClient().displayGuiScreen(new GSGuiGrave(tileEntity));
   }

   public void registerHandlers() {
      FMLCommonHandler.instance().bus().register(new GSTickEventHandler());
      MinecraftForge.EVENT_BUS.register(new GSRenderEventHandler());
   }
}
