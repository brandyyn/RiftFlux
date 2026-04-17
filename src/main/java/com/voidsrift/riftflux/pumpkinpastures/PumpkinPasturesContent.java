package com.voidsrift.riftflux.pumpkinpastures;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.palaria.PalariaMobContent;
import com.voidsrift.riftflux.riftflux;
import com.voidsrift.riftflux.pumpkinpastures.client.render.RenderPumpkinCreeper;
import com.voidsrift.riftflux.pumpkinpastures.client.render.RenderPumpkinSkeleton;
import com.voidsrift.riftflux.pumpkinpastures.client.render.RenderPumpkinZombie;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemSoup;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.EnumHelper;
import net.nmccoy.legendgear.LegendGear2;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class PumpkinPasturesContent {
    public static Block pumpkinJBlock;
    public static Block suspiciousPumpkinBlock;
    public static Item roastPumpkin;
    public static Item pumpkinStew;
    public static Item soulCandy;
    public static Item pumpkinPotage;
    public static Item corruptedSoul;
    public static Item pumpkinSoul;
    public static Item pumpkinSword;
    public static Item pumpkinPickaxe;
    public static Item pumpkinAxe;
    public static Item pumpkinShovel;
    public static Item pumpkinZombieSpawnEgg;
    public static Item pumpkinSkeletonSpawnEgg;
    public static Item pumpkinCreeperSpawnEgg;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;
    private static boolean recipesRegistered;
    private static Item.ToolMaterial pumpkinToolMaterial;

    private PumpkinPasturesContent() {
    }

    public static boolean isEnabled() {
        return ModConfig.enablePumpkinPasturesModule;
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited || !isEnabled()) {
            return;
        }
        preInited = true;

        float efficiency = Math.max(0.1F, ModConfig.pumpkinPasturesEnderflameToolEfficiency);
        pumpkinToolMaterial = EnumHelper.addToolMaterial("RIFTFLUX_PUMPKIN", 2, 350, efficiency, 2.0F, 28);

        pumpkinJBlock = new BlockPumpkinJ();
        suspiciousPumpkinBlock = new BlockSuspiciousPumpkin();
        roastPumpkin = new ItemFood(3, 0.15F, false)
                .setPotionEffect(Potion.moveSpeed.id, 100, 0, 1.0F)
                .setUnlocalizedName("roast_pumpkin")
                .setTextureName("riftflux:pumpkinpastures/roast_pumpkin")
                .setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabFood);
        pumpkinStew = new ItemSoup(20)
                .setUnlocalizedName("pumpkin_stew")
                .setTextureName("riftflux:pumpkinpastures/pumpkin_stew")
                .setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabFood);
        soulCandy = new ItemFood(1, 0.33F, false)
                .setAlwaysEdible()
                .setPotionEffect(Potion.field_76444_x.id, 600, 0, 1.0F)
                .setUnlocalizedName("soul_candy")
                .setTextureName("riftflux:pumpkinpastures/soul_candy")
                .setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabFood);
        pumpkinPotage = new ItemSoup(7)
                .setPotionEffect(Potion.regeneration.id, 200, 0, 1.0F)
                .setUnlocalizedName("pumpkin_potage")
                .setTextureName("riftflux:pumpkinpastures/pumpkin_potage")
                .setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabFood);
        corruptedSoul = new ItemColoredDisplayName(EnumChatFormatting.LIGHT_PURPLE)
                .setUnlocalizedName("corrupted_soul")
                .setTextureName("riftflux:pumpkinpastures/corrupted_soul")
                .setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabMaterials);
        pumpkinSoul = new ItemColoredDisplayName(EnumChatFormatting.GOLD)
                .setUnlocalizedName("pumpkin_soul")
                .setTextureName("riftflux:pumpkinpastures/pumpkin_soul")
                .setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabMaterials);
        pumpkinSword = new ItemPumpkinSword(pumpkinToolMaterial);
        pumpkinPickaxe = new ItemPumpkinPickaxe(pumpkinToolMaterial);
        pumpkinAxe = new ItemPumpkinAxe(pumpkinToolMaterial);
        pumpkinShovel = new ItemPumpkinShovel();
        pumpkinZombieSpawnEgg = new ItemPumpkinSpawnEgg("pumpkin_zombie", 16344349, 5576727).setUnlocalizedName("pumpkin_zombie_spawn_egg");
        pumpkinSkeletonSpawnEgg = new ItemPumpkinSpawnEgg("pumpkin_skeleton", 16344349, 272420).setUnlocalizedName("pumpkin_skeleton_spawn_egg");
        pumpkinCreeperSpawnEgg = new ItemPumpkinSpawnEgg("pumpkin_creeper", 16344349, 7165457).setUnlocalizedName("pumpkin_creeper_spawn_egg");

        GameRegistry.registerBlock(pumpkinJBlock, ItemBlock.class, "pumpkin_j");
        GameRegistry.registerBlock(suspiciousPumpkinBlock, ItemBlock.class, "suspicious_pumpkin");
        GameRegistry.registerItem(roastPumpkin, "roast_pumpkin");
        GameRegistry.registerItem(pumpkinStew, "pumpkin_stew");
        GameRegistry.registerItem(soulCandy, "soul_candy");
        GameRegistry.registerItem(pumpkinPotage, "pumpkin_potage");
        GameRegistry.registerItem(corruptedSoul, "corrupted_soul");
        GameRegistry.registerItem(pumpkinSoul, "pumpkin_soul");
        GameRegistry.registerItem(pumpkinSword, "pumpkin_sword");
        GameRegistry.registerItem(pumpkinPickaxe, "pumpkin_pickaxe");
        GameRegistry.registerItem(pumpkinAxe, "pumpkin_axe");
        GameRegistry.registerItem(pumpkinShovel, "pumpkin_shovel");
        GameRegistry.registerItem(pumpkinZombieSpawnEgg, "pumpkin_zombie_spawn_egg");
        GameRegistry.registerItem(pumpkinSkeletonSpawnEgg, "pumpkin_skeleton_spawn_egg");
        GameRegistry.registerItem(pumpkinCreeperSpawnEgg, "pumpkin_creeper_spawn_egg");

        registerEntities();
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized || !isEnabled()) {
            return;
        }
        initialized = true;

        registerRecipes();
        MinecraftForge.EVENT_BUS.register(new PumpkinPasturesEvents());

        if (!ModConfig.enablePumpkinPasturesNaturalSpawns) {
            return;
        }

        BiomeGenBase[] biomes = getPumpkinSpawnBiomes();
        if (biomes.length <= 0) {
            return;
        }

        if (ModConfig.pumpkinPasturesZombieSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityPumpkinZombie.class, ModConfig.pumpkinPasturesZombieSpawnWeight, 3, 5, EnumCreatureType.monster, biomes);
        }
        if (ModConfig.pumpkinPasturesSkeletonSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityPumpkinSkeleton.class, ModConfig.pumpkinPasturesSkeletonSpawnWeight, 2, 4, EnumCreatureType.monster, biomes);
        }
        if (ModConfig.pumpkinPasturesCreeperSpawnWeight > 0) {
            EntityRegistry.addSpawn(EntityPumpkinCreeper.class, ModConfig.pumpkinPasturesCreeperSpawnWeight, 1, 3, EnumCreatureType.monster, biomes);
        }
    }

    public static void initClient() {
        if (clientInited || !isEnabled()) {
            return;
        }
        clientInited = true;

        RenderingRegistry.registerEntityRenderingHandler(EntityPumpkinZombie.class, new RenderPumpkinZombie());
        RenderingRegistry.registerEntityRenderingHandler(EntityPumpkinSkeleton.class, new RenderPumpkinSkeleton());
        RenderingRegistry.registerEntityRenderingHandler(EntityPumpkinCreeper.class, new RenderPumpkinCreeper());
    }

    private static void registerRecipes() {
        if (recipesRegistered) {
            return;
        }
        recipesRegistered = true;

        Item endermiteShard = PalariaMobContent.endermiteShard;
        Item emberStaff = LegendGear2.fireStaff;

        GameRegistry.addShapelessRecipe(
                new ItemStack(pumpkinJBlock, 1),
                new ItemStack(Blocks.lit_pumpkin, 1),
                new ItemStack(corruptedSoul, 1)
        );

        GameRegistry.addSmelting(Blocks.pumpkin, new ItemStack(roastPumpkin, 1), 0.25F);

        GameRegistry.addShapelessRecipe(
                new ItemStack(soulCandy, 1),
                new ItemStack(Items.sugar, 1),
                new ItemStack(pumpkinSoul, 1)
        );

        if (endermiteShard != null) {
            GameRegistry.addRecipe(
                    new ItemStack(pumpkinSword, 1),
                    " C ",
                    "#G#",
                    " / ",
                    '#', new ItemStack(pumpkinSoul, 1),
                    'C', new ItemStack(corruptedSoul, 1),
                    'G', new ItemStack(endermiteShard, 1),
                    '/', new ItemStack(Items.blaze_rod, 1)
            );

            GameRegistry.addRecipe(
                    new ItemStack(pumpkinPickaxe, 1),
                    "#C#",
                    " G ",
                    " / ",
                    '#', new ItemStack(pumpkinSoul, 1),
                    'C', new ItemStack(corruptedSoul, 1),
                    'G', new ItemStack(endermiteShard, 1),
                    '/', new ItemStack(Items.blaze_rod, 1)
            );

            GameRegistry.addRecipe(
                    new ItemStack(pumpkinAxe, 1),
                    "#C#",
                    "#G#",
                    " / ",
                    '#', new ItemStack(pumpkinSoul, 1),
                    'C', new ItemStack(corruptedSoul, 1),
                    'G', new ItemStack(endermiteShard, 1),
                    '/', new ItemStack(Items.blaze_rod, 1)
            );
        }

        if (emberStaff != null && endermiteShard != null) {
            GameRegistry.addRecipe(
                    new ItemStack(pumpkinShovel, 1),
                    "#C#",
                    "SGS",
                    " / ",
                    '#', new ItemStack(pumpkinSoul, 1),
                    'C', new ItemStack(corruptedSoul, 1),
                    'S', new ItemStack(endermiteShard, 1),
                    'G', new ItemStack(emberStaff, 1),
                    '/', new ItemStack(Items.blaze_rod, 1)
            );
        }

        GameRegistry.addRecipe(
                new ItemStack(pumpkinStew, 1),
                "#B#",
                "MCM",
                " P ",
                '#', new ItemStack(Items.wheat, 1),
                'B', new ItemStack(Blocks.brown_mushroom, 1),
                'M', new ItemStack(Items.milk_bucket, 1),
                'C', new ItemStack(Items.chicken, 1),
                'P', new ItemStack(Blocks.pumpkin, 1)
        );

        GameRegistry.addRecipe(
                new ItemStack(pumpkinPotage, 1),
                "#P#",
                " M ",
                " B ",
                '#', new ItemStack(Items.wheat, 1),
                'P', new ItemStack(Blocks.pumpkin, 1),
                'M', new ItemStack(Items.milk_bucket, 1),
                'B', new ItemStack(Items.bowl, 1)
        );
    }

    private static void registerEntities() {
        int id = 300;
        EntityRegistry.registerModEntity(EntityPumpkinZombie.class, "PumpkinZombie", id++, riftflux.instance, 80, 3, true);
        registerEntityEgg(EntityPumpkinZombie.class, "RiftFluxPumpkinZombie", 0xF9663D, 0x551A17);
        EntityRegistry.registerModEntity(EntityPumpkinSkeleton.class, "PumpkinSkeleton", id++, riftflux.instance, 80, 3, true);
        registerEntityEgg(EntityPumpkinSkeleton.class, "RiftFluxPumpkinSkeleton", 0xF9663D, 0x042814);
        EntityRegistry.registerModEntity(EntityPumpkinCreeper.class, "PumpkinCreeper", id, riftflux.instance, 80, 3, true);
        registerEntityEgg(EntityPumpkinCreeper.class, "RiftFluxPumpkinCreeper", 0xF9663D, 0x6D5451);
    }

    private static void registerEntityEgg(Class<? extends Entity> entityClass, String entityName, int primaryColor, int secondaryColor) {
        int entityId = EntityRegistry.findGlobalUniqueEntityId();
        EntityRegistry.registerGlobalEntityID(entityClass, entityName, entityId);
        EntityList.entityEggs.put(entityId, new EntityList.EntityEggInfo(entityId, primaryColor, secondaryColor));
    }

    public static void dropSoulItems(EntityLivingBase entity, int looting) {
        if (entity == null || entity.worldObj == null || entity.worldObj.isRemote) {
            return;
        }
        if (corruptedSoul == null || pumpkinSoul == null) {
            return;
        }

        float corruptedChance = getConfiguredDropChancePercent(entity, ModConfig.pumpkinPasturesCorruptedSoulDropEntries, 0.5F);
        if (corruptedChance > 0.0F) {
            float adjustedChance = Math.min(100.0F, corruptedChance + Math.max(0, looting) * 0.25F);
            if (entity.getRNG().nextFloat() * 100.0F < adjustedChance) {
                entity.entityDropItem(new ItemStack(corruptedSoul, 1), 0.0F);
            }
        }

        float pumpkinChance = getConfiguredDropChancePercent(entity, ModConfig.pumpkinPasturesPumpkinSoulDropEntries, 2.0F);
        if (pumpkinChance > 0.0F) {
            float adjustedChance = Math.min(100.0F, pumpkinChance + Math.max(0, looting) * 0.25F);
            if (entity.getRNG().nextFloat() * 100.0F < adjustedChance) {
                entity.entityDropItem(new ItemStack(pumpkinSoul, 1), 0.0F);
            }
        }
    }

    private static float getConfiguredDropChancePercent(EntityLivingBase entity, String[] entries, float fallbackChancePercent) {
        if (entity == null) {
            return 0.0F;
        }
        if (entries == null || entries.length == 0) {
            return Math.max(0.0F, Math.min(100.0F, fallbackChancePercent));
        }

        for (String rawEntry : entries) {
            if (rawEntry == null) {
                continue;
            }
            String entry = rawEntry.trim();
            if (entry.isEmpty()) {
                continue;
            }

            String entityToken = entry;
            float chancePercent = 0.5F;
            int separator = Math.max(entry.lastIndexOf('|'), entry.lastIndexOf(':'));
            if (separator >= 0) {
                entityToken = entry.substring(0, separator).trim();
                String chanceToken = entry.substring(separator + 1).trim();
                if (!chanceToken.isEmpty()) {
                    try {
                        chancePercent = Float.parseFloat(chanceToken);
                    } catch (NumberFormatException ignored) {
                        chancePercent = 0.5F;
                    }
                }
            }

            if (!matchesEntityToken(entity, entityToken)) {
                continue;
            }
            if (chancePercent < 0.0F) {
                return 0.0F;
            }
            return Math.min(100.0F, chancePercent);
        }
        return Math.max(0.0F, Math.min(100.0F, fallbackChancePercent));
    }

    private static boolean matchesEntityToken(EntityLivingBase entity, String token) {
        if (entity == null || token == null) {
            return false;
        }
        String normalizedToken = normalizeEntityToken(token);
        if (normalizedToken.isEmpty()) {
            return false;
        }

        String entityId = EntityList.getEntityString(entity);
        if (normalizedToken.equals(normalizeEntityToken(entityId))) {
            return true;
        }

        String simpleName = entity.getClass().getSimpleName();
        if (normalizedToken.equals(normalizeEntityToken(simpleName))) {
            return true;
        }

        String fullName = entity.getClass().getName();
        if (normalizedToken.equals(normalizeEntityToken(fullName))) {
            return true;
        }

        if (simpleName != null && simpleName.startsWith("Entity")) {
            String trimmedSimple = simpleName.substring("Entity".length());
            if (normalizedToken.equals(normalizeEntityToken(trimmedSimple))) {
                return true;
            }
        }

        return false;
    }

    private static String normalizeEntityToken(String value) {
        if (value == null) {
            return "";
        }
        String lower = value.toLowerCase(Locale.ROOT).trim();
        StringBuilder out = new StringBuilder(lower.length());
        for (int i = 0; i < lower.length(); i++) {
            char c = lower.charAt(i);
            if ((c >= 'a' && c <= 'z') || (c >= '0' && c <= '9')) {
                out.append(c);
            }
        }
        return out.toString();
    }

    private static BiomeGenBase[] getPumpkinSpawnBiomes() {
        Set<BiomeGenBase> out = new LinkedHashSet<BiomeGenBase>();
        addIfPresent(out, BiomeGenBase.taiga);
        addIfPresent(out, BiomeGenBase.swampland);
        addIfPresent(out, BiomeGenBase.plains);
        addIfPresent(out, BiomeGenBase.forest);
        addIfPresent(out, BiomeGenBase.extremeHills);
        addIfPresent(out, BiomeGenBase.savanna);

        BiomeGenBase[] all = BiomeGenBase.getBiomeGenArray();
        if (all != null) {
            addBiomeById(out, all, 129);
            addBiomeById(out, all, 132);
        }

        List<BiomeGenBase> list = new ArrayList<BiomeGenBase>(out);
        return list.toArray(new BiomeGenBase[list.size()]);
    }

    private static void addIfPresent(Set<BiomeGenBase> set, BiomeGenBase biome) {
        if (biome != null) {
            set.add(biome);
        }
    }

    private static void addBiomeById(Set<BiomeGenBase> set, BiomeGenBase[] all, int id) {
        if (id >= 0 && id < all.length && all[id] != null) {
            set.add(all[id]);
        }
    }
}
