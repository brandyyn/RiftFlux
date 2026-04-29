package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.Constants;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.entity.RiftFluxEntityRegistry;
import com.voidsrift.riftflux.riftflux;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.init.Items;
import net.minecraftforge.common.ChestGenHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class TerrariaContent {
    public static Item lens;
    public static Item blackLens;
    public static Item suspiciousLookingEye;
    public static Item iceRod;
    public static Item demonEyeSpawnEgg;
    public static Item whoopieCushion;
    public static Block magicIceBlock;
    public static Block iceRodBlock;
    public static Block terraMushroomBlock;
    public static Block daybloomBlock;
    public static Block blinkrootBlock;
    public static Block waterleafBlock;
    public static Block deathweedBlock;
    public static Block fireblossomBlock;
    public static Block jungleSporeBlock;
    public static Block moonglowBlock;
    public static Block demonAltarBlock;
    public static Block hellForgeBlock;

    private static boolean preInited;
    private static boolean initialized;
    private static boolean clientInited;

    private TerrariaContent() {
    }

    public static void preInit(FMLPreInitializationEvent event) {
        if (preInited) {
            return;
        }
        preInited = true;

        if (!ModConfig.enableTerraModule) {
            return;
        }

        lens = new ItemLens().setUnlocalizedName("lens");
        blackLens = new ItemBlackLens().setUnlocalizedName("black_lens");
        suspiciousLookingEye = new ItemSuspiciousLookingEye().setUnlocalizedName("suspicious_looking_eye");
        iceRod = new ItemIceRod().setUnlocalizedName("ice_rod");
        demonEyeSpawnEgg = new ItemDemonEyeSpawnEgg(0xFFFFFF, 0xB52525).setUnlocalizedName("demon_eye_spawn_egg");
        whoopieCushion = new ItemWhoopieCushion().setUnlocalizedName("whoopie_cushion");
        magicIceBlock = new BlockMagicIce();
        iceRodBlock = new BlockIceRodIce();
        terraMushroomBlock = new BlockTerraPlant("terra_mushroom", "terra_mushroom", BlockTerraPlant.PlacementRule.SURFACE_ONLY);
        daybloomBlock = new BlockTerraPlant("daybloom", "daybloom", BlockTerraPlant.PlacementRule.SURFACE_ONLY);
        blinkrootBlock = new BlockTerraPlant("blinkroot", "blinkroot", BlockTerraPlant.PlacementRule.ANY_SOLID);
        waterleafBlock = new BlockTerraPlant("waterleaf", "waterleaf", BlockTerraPlant.PlacementRule.ANY_SOLID);
        deathweedBlock = new BlockTerraPlant("deathweed", "deathweed", BlockTerraPlant.PlacementRule.ANY_SOLID);
        fireblossomBlock = new BlockTerraPlant("fireblossom", "fireblossom", BlockTerraPlant.PlacementRule.ANY_SOLID);
        jungleSporeBlock = new BlockTerraPlant("jungle_spore", "jungle_spore", BlockTerraPlant.PlacementRule.ANY_SOLID);
        moonglowBlock = new BlockTerraPlant("moonglow", "moonglow", BlockTerraPlant.PlacementRule.ANY_SOLID);
        demonAltarBlock = new BlockDemonAltar();
        hellForgeBlock = new BlockHellForge();

        GameRegistry.registerItem(lens, "lens");
        GameRegistry.registerItem(blackLens, "black_lens");
        GameRegistry.registerItem(suspiciousLookingEye, "suspicious_looking_eye");
        GameRegistry.registerItem(iceRod, "ice_rod");
        GameRegistry.registerItem(demonEyeSpawnEgg, "demon_eye_spawn_egg");
        GameRegistry.registerItem(whoopieCushion, "whoopie_cushion");
        GameRegistry.registerBlock(magicIceBlock, "magic_ice");
        GameRegistry.registerBlock(iceRodBlock, "ice_rod_block");
        GameRegistry.registerBlock(terraMushroomBlock, "terra_mushroom");
        GameRegistry.registerBlock(daybloomBlock, "daybloom");
        GameRegistry.registerBlock(blinkrootBlock, "blinkroot");
        GameRegistry.registerBlock(waterleafBlock, "waterleaf");
        GameRegistry.registerBlock(deathweedBlock, "deathweed");
        GameRegistry.registerBlock(fireblossomBlock, "fireblossom");
        GameRegistry.registerBlock(jungleSporeBlock, "jungle_spore");
        GameRegistry.registerBlock(moonglowBlock, "moonglow");
        GameRegistry.registerBlock(demonAltarBlock, "demon_altar");
        GameRegistry.registerBlock(hellForgeBlock, "hellforge");
        GameRegistry.registerTileEntity(TileEntityDemonAltar.class, Constants.MODID + ":demon_altar");
        GameRegistry.registerTileEntity(TileEntityHellForge.class, Constants.MODID + ":hellforge");
        iceRod.setHarvestLevel("sword", 0);

        registerEntities();
        registerRecipes();
    }

    public static void init(FMLInitializationEvent event) {
        if (initialized) {
            return;
        }
        initialized = true;

        if (!ModConfig.enableTerraModule) {
            return;
        }

        GameRegistry.registerWorldGenerator(new TerraPlantWorldGenerator(), 0);
        MinecraftForge.EVENT_BUS.register(new TerraLifecycleEvents());
        ChestGenHooks.addItem(
                ChestGenHooks.DUNGEON_CHEST,
                new WeightedRandomChestContent(new ItemStack(whoopieCushion), 1, 1, 8)
        );
    }

    public static void initClient() {
        if (clientInited) {
            return;
        }
        clientInited = true;

        if (!ModConfig.enableTerraModule) {
            return;
        }

        RenderingRegistry.registerEntityRenderingHandler(EntityDemonEye.class, new RenderDemonEye(new ModelDemonEye(), 0.5F));
        RenderingRegistry.registerEntityRenderingHandler(EntityEyeOfCthulhu.class, new RenderEyeOfCthulhu(new ModelEyeOfCthulhu(), 0.5F));

        ResourceLocation demonAltarTexture = new ResourceLocation(Constants.MODID, "textures/models/demon_altar.png");
        ResourceLocation hellForgeTexture = new ResourceLocation(Constants.MODID, "textures/models/hellforge.png");

        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityDemonAltar.class,
                new RenderTerraModelTileEntity(demonAltarTexture, new ModelDemonAltar(), 270.0F, 90.0F, 0.0F, 180.0F)
        );
        ClientRegistry.bindTileEntitySpecialRenderer(
                TileEntityHellForge.class,
                new RenderTerraModelTileEntity(hellForgeTexture, new ModelHellForge(), 90.0F, 270.0F, 180.0F, 0.0F)
        );

        if (demonAltarBlock != null) {
            MinecraftForgeClient.registerItemRenderer(
                    Item.getItemFromBlock(demonAltarBlock),
                    new RenderTerraModelItem(demonAltarTexture, new ModelDemonAltar(), 225.0F)
            );
        }
        if (hellForgeBlock != null) {
            MinecraftForgeClient.registerItemRenderer(
                    Item.getItemFromBlock(hellForgeBlock),
                    new RenderTerraModelItem(hellForgeTexture, new ModelHellForge(), 225.0F)
            );
        }
    }

    private static void registerEntities() {
        RiftFluxEntityRegistry.registerModEntity(EntityDemonEye.class, "DemonEye", riftflux.instance, 80, 3, true);
        RiftFluxEntityRegistry.registerModEntity(EntityEyeOfCthulhu.class, "EyeOfCthulhu", riftflux.instance, 128, 3, true);
    }

    private static void registerRecipes() {
        GameRegistry.addShapelessRecipe(
                new ItemStack(suspiciousLookingEye, 1),
                new ItemStack(Items.ender_eye, 1),
                new ItemStack(lens, 1),
                new ItemStack(lens, 1),
                new ItemStack(lens, 1),
                new ItemStack(lens, 1),
                new ItemStack(lens, 1),
                new ItemStack(lens, 1)
        );
    }

    public static int getIceRodLifetimeTicks() {
        return Math.max(1, Math.round(ModConfig.iceRodBlockLifetimeSeconds * 20.0F));
    }

    public static void dropConfiguredEyeLoot(Entity source, Random rand) {
        if (source == null || source.worldObj == null || source.worldObj.isRemote) {
            return;
        }
        List<EyeDrop> drops = parseEyeDrops(ModConfig.eyeOfCthulhuDrops);
        for (EyeDrop drop : drops) {
            if (drop == null) {
                continue;
            }
            if (rand.nextFloat() > drop.chance) {
                continue;
            }
            dropStack(source, drop.stack.copy());
        }
    }

    private static void dropStack(Entity source, ItemStack stack) {
        if (stack == null || stack.stackSize <= 0) {
            return;
        }
        while (stack.stackSize > 0) {
            int size = Math.min(stack.stackSize, stack.getMaxStackSize());
            ItemStack piece = stack.splitStack(size);
            if (piece != null && piece.stackSize > 0) {
                source.entityDropItem(piece, 0.0F);
            }
        }
    }

    private static List<EyeDrop> parseEyeDrops(String[] entries) {
        List<EyeDrop> out = new ArrayList<EyeDrop>();
        if (entries == null || entries.length == 0) {
            return out;
        }

        for (String rawEntry : entries) {
            if (rawEntry == null) {
                continue;
            }

            String entry = rawEntry.trim();
            if (entry.isEmpty()) {
                continue;
            }

            String itemPart = entry;
            String chancePart = null;
            int pipe = entry.indexOf('|');
            if (pipe >= 0) {
                itemPart = entry.substring(0, pipe).trim();
                chancePart = entry.substring(pipe + 1).trim();
            }

            float chance = parseChance(chancePart, 1.0F);
            if (chance <= 0.0F) {
                continue;
            }

            int count = 1;
            int meta = 0;

            int star = itemPart.indexOf('*');
            if (star >= 0) {
                count = parseInt(itemPart.substring(star + 1), 1);
                itemPart = itemPart.substring(0, star).trim();
            }

            int at = itemPart.indexOf('@');
            if (at >= 0) {
                meta = parseInt(itemPart.substring(at + 1), 0);
                itemPart = itemPart.substring(0, at).trim();
            }

            String[] id = itemPart.split(":", 2);
            if (id.length != 2) {
                continue;
            }

            String modid = id[0].toLowerCase(Locale.ROOT);
            String name = id[1];
            Item item = GameRegistry.findItem(modid, name);
            if (item == null) {
                Object registryObj = Item.itemRegistry.getObject(itemPart);
                if (registryObj instanceof Item) {
                    item = (Item) registryObj;
                }
            }
            if (item == null) {
                continue;
            }

            ItemStack stack = new ItemStack(item, Math.max(1, count), Math.max(0, meta));
            out.add(new EyeDrop(stack, chance));
        }

        return out;
    }

    private static float parseChance(String value, float fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            float chance = Float.parseFloat(value.trim());
            if (chance > 1.0F) {
                chance /= 100.0F;
            }
            if (chance < 0.0F) {
                chance = 0.0F;
            }
            if (chance > 1.0F) {
                chance = 1.0F;
            }
            return chance;
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int parseInt(String value, int fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static final class EyeDrop {
        private final ItemStack stack;
        private final float chance;

        private EyeDrop(ItemStack stack, float chance) {
            this.stack = stack;
            this.chance = chance;
        }
    }
}
