package gravestone.core;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import gravestone.block.BlockGSAltar;
import gravestone.block.BlockGSBoneBlock;
import gravestone.block.BlockGSBoneSlab;
import gravestone.block.BlockGSBoneStairs;
import gravestone.block.BlockGSCandle;
import gravestone.block.BlockGSGraveStone;
import gravestone.block.BlockGSHauntedChest;
import gravestone.block.BlockGSInvisibleWall;
import gravestone.block.BlockGSMemorial;
import gravestone.block.BlockGSPileOfBones;
import gravestone.block.BlockGSSkullCandle;
import gravestone.block.BlockGSSpawner;
import gravestone.block.BlockGSTrap;
import gravestone.block.GraveStoneHelper;
import gravestone.block.enums.EnumBoneBlock;
import gravestone.block.enums.EnumGraves;
import gravestone.block.enums.EnumHauntedChest;
import gravestone.block.enums.EnumMemorials;
import gravestone.block.enums.EnumPileOfBones;
import gravestone.block.enums.EnumSkullCandle;
import gravestone.block.enums.EnumSpawner;
import gravestone.block.enums.EnumTrap;
import gravestone.block.enums.IBlockEnum;
import gravestone.item.ItemBlockGSBoneBlock;
import gravestone.item.ItemBlockGSCandle;
import gravestone.item.ItemBlockGSGraveStone;
import gravestone.item.ItemBlockGSHauntedChest;
import gravestone.item.ItemBlockGSMemorial;
import gravestone.item.ItemBlockGSPileOfBones;
import gravestone.item.ItemBlockGSSkullCandle;
import gravestone.item.ItemBlockGSSpawner;
import gravestone.item.ItemBlockGSTrap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class GSBlock {
   public static BlockGSGraveStone graveStone;
   public static BlockGSSpawner spawner;
   public static BlockGSTrap trap;
   public static BlockGSMemorial memorial;
   public static BlockGSInvisibleWall invisibleWall;
   public static BlockGSPileOfBones pileOfBones;
   public static BlockGSBoneBlock boneBlock;
   public static BlockGSBoneSlab boneSlab;
   public static BlockGSBoneStairs boneStairs;
   public static BlockGSHauntedChest hauntedChest;
   public static BlockGSCandle candle;
   public static BlockGSSkullCandle skullCandle;
   public static BlockGSAltar altar;

   private GSBlock() {
   }

   public static void registration() {
      graveStone = new BlockGSGraveStone();
      GameRegistry.registerBlock(graveStone, ItemBlockGSGraveStone.class, "GSGraveStone");

      for(byte i = 0; i < EnumGraves.values().length; ++i) {
         ItemStack graveStoneStack = new ItemStack(graveStone, 1, 0);
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte("GraveType", i);
         if (GraveStoneHelper.isSwordGrave(i)) {
            nbt.setByte("SwordType", GraveStoneHelper.oldGraveTypeToSwordType(i));
         }

         graveStoneStack.setTagCompound(nbt);
         LanguageRegistry.addName(graveStoneStack, EnumGraves.values()[i].getName());
      }

      memorial = new BlockGSMemorial();
      advancedNTBBlockRegistration(memorial, "GSMemorial", "Memorial", EnumMemorials.values(), "GraveType", ItemBlockGSMemorial.class);
      invisibleWall = new BlockGSInvisibleWall();
      GameRegistry.registerBlock(invisibleWall, "GSInvisibleWall");
      spawner = new BlockGSSpawner();
      advancedMetaBlockRegistration(spawner, ItemBlockGSSpawner.class, "GSSpawner", EnumSpawner.values());
      trap = new BlockGSTrap();
      advancedMetaBlockRegistration(trap, ItemBlockGSTrap.class, "GSTrap", EnumTrap.values());
      pileOfBones = new BlockGSPileOfBones();
      advancedMetaBlockRegistration(pileOfBones, ItemBlockGSPileOfBones.class, "GSPileOfBones", EnumPileOfBones.values());
      boneBlock = new BlockGSBoneBlock();
      advancedMetaBlockRegistration(boneBlock, ItemBlockGSBoneBlock.class, "GSBoneBlock", EnumBoneBlock.values());
      boneSlab = new BlockGSBoneSlab();
      simpleBlockRegistration(boneSlab, "GSBoneSlab", "Bone slab");
      boneStairs = new BlockGSBoneStairs();
      simpleBlockRegistration(boneStairs, "GSBoneStairs", "Bone stairs");
      hauntedChest = new BlockGSHauntedChest();
      advancedNTBBlockRegistration(hauntedChest, "GSHauntedChest", "Haunted chest", EnumHauntedChest.values(), "ChestType", ItemBlockGSHauntedChest.class);
      candle = new BlockGSCandle();
      simpleBlockRegistration(candle, "Candle", ItemBlockGSCandle.class, "GSCandle");
      skullCandle = new BlockGSSkullCandle();
      advancedMetaBlockRegistration(skullCandle, ItemBlockGSSkullCandle.class, "GSSkullCandle", EnumSkullCandle.values());
      altar = new BlockGSAltar();
      simpleBlockRegistration(altar, "GSAltar", "Altar");
   }

   private static void simpleBlockRegistration(Block block, String name, Class itemClass, String registerName) {
      GameRegistry.registerBlock(block, itemClass, registerName);
      LanguageRegistry.addName(block, name);
   }

   private static void simpleBlockRegistration(Block block, String registerName, String name) {
      GameRegistry.registerBlock(block, registerName);
      LanguageRegistry.addName(block, name);
   }

   private static void advancedMetaBlockRegistration(Block block, Class itemClass, String registerName, IBlockEnum[] blockEnums) {
      GameRegistry.registerBlock(block, itemClass, registerName);
      subMetaBlocksRegistration(block, blockEnums);
   }

   private static void advancedNTBBlockRegistration(Block block, String registerName, String name, IBlockEnum[] blockEnums, String nbtName) {
      simpleBlockRegistration(block, registerName, name);
      subNTBBlocksRegistration(block, blockEnums, nbtName);
   }

   private static void advancedNTBBlockRegistration(Block block, String registerName, String name, IBlockEnum[] blockEnums, String nbtName, Class itemClass) {
      GameRegistry.registerBlock(block, itemClass, registerName);
      LanguageRegistry.addName(block, name);
      subNTBBlocksRegistration(block, blockEnums, nbtName);
   }

   private static void subMetaBlocksRegistration(Block block, IBlockEnum[] blockEnums) {
      for(byte meta = 0; meta < blockEnums.length; ++meta) {
         LanguageRegistry.addName(new ItemStack(block, 1, meta), blockEnums[meta].getName());
      }

   }

   private static void subNTBBlocksRegistration(Block block, IBlockEnum[] blockEnums, String nbtName) {
      for(byte i = 0; i < blockEnums.length; ++i) {
         ItemStack stack = new ItemStack(block, 1, 0);
         NBTTagCompound nbt = new NBTTagCompound();
         nbt.setByte(nbtName, i);
         stack.setTagCompound(nbt);
         LanguageRegistry.addName(stack, blockEnums[i].getName());
      }

   }
}
