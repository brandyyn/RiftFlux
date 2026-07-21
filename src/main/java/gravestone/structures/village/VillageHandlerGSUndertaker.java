package gravestone.structures.village;

import cpw.mods.fml.common.registry.VillagerRegistry.IVillageCreationHandler;
import cpw.mods.fml.common.registry.VillagerRegistry.IVillageTradeHandler;
import gravestone.core.GSBlock;
import gravestone.core.GSItem;
import java.util.List;
import java.util.Random;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraft.village.MerchantRecipe;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;

public class VillageHandlerGSUndertaker implements IVillageCreationHandler, IVillageTradeHandler {
   public static final int UNDERTAKER_ID = 385;

   public void manipulateTradesForVillager(EntityVillager villager, MerchantRecipeList recipeList, Random random) {
      recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(GSItem.chisel, 1, 0)));
      recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 1), new ItemStack(GSBlock.candle, 10, 0)));
      recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 15), new ItemStack(Items.skull, 1, 0)));
      recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 15), new ItemStack(Items.skull, 1, 2)));
      recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 20), new ItemStack(Items.skull, 1, 3)));
      recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 20), new ItemStack(Items.skull, 1, 4)));
      recipeList.add(new MerchantRecipe(new ItemStack(Items.emerald, 30), new ItemStack(Items.skull, 1, 1)));
   }

   public PieceWeight getVillagePieceWeight(Random random, int size) {
      return new PieceWeight(ComponentGSVillageUndertaker.class, 3, MathHelper.getRandomIntegerInRange(random, 0, 1));
   }

   public Class getComponentClass() {
      return ComponentGSVillageUndertaker.class;
   }

   public Object buildComponent(PieceWeight villagePiece, Start startPiece, List pieces, Random random, int p1, int p2, int p3, int p4, int p5) {
      return ComponentGSVillageUndertaker.buildComponent(startPiece, pieces, random, p1, p2, p3, p4, p5);
   }
}
