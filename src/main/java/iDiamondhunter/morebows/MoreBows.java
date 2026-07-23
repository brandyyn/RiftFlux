/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.client.event.ConfigChangedEvent$OnConfigChangedEvent
 *  cpw.mods.fml.common.FMLCommonHandler
 *  cpw.mods.fml.common.Mod
 *  cpw.mods.fml.common.Mod$EventHandler
 *  cpw.mods.fml.common.Mod$Instance
 *  cpw.mods.fml.common.SidedProxy
 *  cpw.mods.fml.common.event.FMLPreInitializationEvent
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.registry.EntityRegistry
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.monster.EntityBlaze
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.IRecipe
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.world.World
 *  net.minecraft.world.WorldServer
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.common.config.Configuration
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.oredict.OreDictionary
 *  net.minecraftforge.oredict.ShapedOreRecipe
 */
package iDiamondhunter.morebows;

import cpw.mods.fml.client.event.ConfigChangedEvent;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import iDiamondhunter.morebows.b;
import iDiamondhunter.morebows.d;
import iDiamondhunter.morebows.e;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityBlaze;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class MoreBows {
    private static MoreBows var_iDiamondhunter_morebows_MoreBows_a;
    private static MoreBows var_iDiamondhunter_morebows_MoreBows_b;
    public static Configuration var_net_minecraftforge_common_config_Configuration_a;
    public static boolean var_boolean_a;
    private static boolean var_boolean_c;
    public static boolean var_boolean_b;
    protected static final Item var_net_minecraft_item_Item_a;
    protected static final Item var_net_minecraft_item_Item_b;
    protected static final Item var_net_minecraft_item_Item_c;
    protected static final Item d;
    protected static final Item e;
    protected static final Item f;
    protected static final Item g;
    protected static final Item h;

    private static final void b() {
        var_boolean_a = var_net_minecraftforge_common_config_Configuration_a.get("general", "frostArrowsShouldBeCold", true).getBoolean();
        var_boolean_c = var_net_minecraftforge_common_config_Configuration_a.get("general", "oldFrostArrowMobSlowdown", false).getBoolean();
        var_boolean_b = var_net_minecraftforge_common_config_Configuration_a.get("general", "oldFrostArrowRendering", false).getBoolean();
        var_net_minecraftforge_common_config_Configuration_a.save();
    }

    public static final void a(World world, Entity entity, String string, boolean bl, double d2) {
        if (!world.isRemote) {
            double d3;
            double d4;
            double d5;
            if (bl) {
                d5 = world.rand.nextFloat() * entity.width * 2.0f - entity.width;
                d4 = 0.5 + (double)(world.rand.nextFloat() * entity.height);
                d3 = world.rand.nextFloat() * entity.width * 2.0f - entity.width;
            } else {
                d5 = 0.0;
                d4 = 0.5;
                d3 = 0.0;
            }
            ((WorldServer)world).func_147487_a(string, entity.posX, entity.posY, entity.posZ, 1, d5, d4, d3, d2);
        }
    }

    @SubscribeEvent
    public final void a(LivingAttackEvent livingAttackEvent) {
        if (!livingAttackEvent.entity.worldObj.isRemote && livingAttackEvent.source.getSourceOfDamage() instanceof e) {
            e arrow = (e)livingAttackEvent.source.getSourceOfDamage();
            String particle;
            double d2;
            boolean bl;
            int n;
            switch (arrow.var_byte_a) {
                case 1: {
                    particle = "portal";
                    n = 3;
                    bl = true;
                    d2 = 1.0;
                    break;
                }
                case 2: {
                    particle = arrow.isBurning() ? "flame" : "smoke";
                    n = 5;
                    bl = true;
                    d2 = 0.05;
                    break;
                }
                case 3: {
                    particle = "splash";
                    n = 1;
                    bl = false;
                    d2 = 0.01;
                    break;
                }
                default: {
                    particle = "depthsuspend";
                    n = 20;
                    bl = true;
                    d2 = 0.0;
                }
            }
            for (int i = 0; i < n; ++i) {
                MoreBows.a(livingAttackEvent.entity.worldObj, livingAttackEvent.entity, particle, bl, d2);
            }
        }
    }

    @SubscribeEvent
    public final void a(LivingHurtEvent livingHurtEvent) {
        if (livingHurtEvent.source.getSourceOfDamage() instanceof e && ((e)livingHurtEvent.source.getSourceOfDamage()).var_byte_a == 3) {
            if (var_boolean_a) {
                if (livingHurtEvent.entityLiving instanceof EntityBlaze) {
                    livingHurtEvent.ammount *= 3.0f;
                }
                livingHurtEvent.entity.extinguish();
            }
            if (!var_boolean_c) {
                livingHurtEvent.entityLiving.addPotionEffect(new PotionEffect(Potion.moveSlowdown.getId(), 300, 2));
                return;
            }
            livingHurtEvent.entity.setInWeb();
        }
    }

    @SubscribeEvent
    public final void a(ConfigChangedEvent.OnConfigChangedEvent onConfigChangedEvent) {
        if ("MoreBows".equals(onConfigChangedEvent.modID)) {
            MoreBows.b();
        }
    }

    public final void a(FMLPreInitializationEvent fMLPreInitializationEvent) {
        var_net_minecraftforge_common_config_Configuration_a = new Configuration(fMLPreInitializationEvent.getSuggestedConfigurationFile());
        MoreBows.b();
        var_iDiamondhunter_morebows_MoreBows_b.a();
        MinecraftForge.EVENT_BUS.register((Object)var_iDiamondhunter_morebows_MoreBows_b);
        FMLCommonHandler.instance().bus().register((Object)var_iDiamondhunter_morebows_MoreBows_b);
    }

    protected void a() {
        GameRegistry.registerItem((Item)var_net_minecraft_item_Item_a, (String)"DiamondBow");
        GameRegistry.registerItem((Item)var_net_minecraft_item_Item_b, (String)"EnderBow");
        GameRegistry.registerItem((Item)var_net_minecraft_item_Item_c, (String)"FlameBow");
        GameRegistry.registerItem((Item)d, (String)"FrostBow");
        GameRegistry.registerItem((Item)e, (String)"GoldBow");
        GameRegistry.registerItem((Item)f, (String)"IronBow");
        GameRegistry.registerItem((Item)g, (String)"MultiBow");
        GameRegistry.registerItem((Item)h, (String)"StoneBow");
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(Blocks.dispenser, new Object[]{"AAA", "ABA", "ACA", Character.valueOf('A'), "cobblestone", Character.valueOf('B'), "bow", Character.valueOf('C'), "dustRedstone"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(var_net_minecraft_item_Item_a, new Object[]{" DC", "ABC", " DC", Character.valueOf('C'), "string", Character.valueOf('D'), "gemDiamond", Character.valueOf('A'), "ingotIron", Character.valueOf('B'), "bow"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(var_net_minecraft_item_Item_b, new Object[]{"CD", "AB", "CD", Character.valueOf('C'), "ingotGold", Character.valueOf('D'), "pearlEnder", Character.valueOf('B'), "bowIron", Character.valueOf('A'), "pearlEnderEye"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(var_net_minecraft_item_Item_c, new Object[]{"CD", "AB", "CD", Character.valueOf('A'), "ingotGold", Character.valueOf('D'), "rodBlaze", Character.valueOf('B'), "bowIron", Character.valueOf('C'), "netherrack"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(d, new Object[]{" DC", "ABC", " DC", Character.valueOf('C'), "string", Character.valueOf('D'), "ice", Character.valueOf('A'), "snowball", Character.valueOf('B'), "bowIron"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(e, new Object[]{" AC", "ABC", " AC", Character.valueOf('C'), "string", Character.valueOf('A'), "ingotGold", Character.valueOf('B'), "bow"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(f, new Object[]{" AC", "ABC", " AC", Character.valueOf('C'), "string", Character.valueOf('A'), "ingotIron", Character.valueOf('B'), "bow"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(g, new Object[]{" DC", "A C", " DC", Character.valueOf('C'), "string", Character.valueOf('A'), "ingotIron", Character.valueOf('D'), "bowIron"}));
        GameRegistry.addRecipe((IRecipe)new ShapedOreRecipe(h, new Object[]{" DC", "ABC", " DC", Character.valueOf('A'), "stickWood", Character.valueOf('C'), "string", Character.valueOf('D'), "stone", Character.valueOf('B'), "bow"}));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(var_net_minecraft_item_Item_a, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(var_net_minecraft_item_Item_b, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(var_net_minecraft_item_Item_c, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(d, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(e, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(f, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack((Item)Items.bow, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(g, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bow", (ItemStack)new ItemStack(h, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bowDiamond", (ItemStack)new ItemStack(var_net_minecraft_item_Item_a, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bowGold", (ItemStack)new ItemStack(e, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"bowIron", (ItemStack)new ItemStack(f, 1, Short.MAX_VALUE));
        OreDictionary.registerOre((String)"ice", (Block)Blocks.ice);
        OreDictionary.registerOre((String)"ice", (Block)Blocks.packed_ice);
        OreDictionary.registerOre((String)"netherrack", (Block)Blocks.netherrack);
        OreDictionary.registerOre((String)"pearlEnder", (Item)Items.ender_pearl);
        OreDictionary.registerOre((String)"pearlEnderEye", (Item)Items.ender_eye);
        OreDictionary.registerOre((String)"rodBlaze", (Item)Items.blaze_rod);
        OreDictionary.registerOre((String)"snowball", (Item)Items.snowball);
        OreDictionary.registerOre((String)"string", (Item)Items.string);
        com.voidsrift.riftflux.entity.RiftFluxEntityRegistry.registerModEntity(d.class, "ArrowSpawner", var_iDiamondhunter_morebows_MoreBows_a, -1, Integer.MAX_VALUE, false);
        com.voidsrift.riftflux.entity.RiftFluxEntityRegistry.registerModEntity(e.class, "CustomArrow", var_iDiamondhunter_morebows_MoreBows_a, 64, 20, true);
    }

    static {
        var_net_minecraft_item_Item_a = new b(1016, (byte)0, 2.25, new byte[]{8, 4}, false, 6.0f, EnumRarity.rare).setUnlocalizedName("DiamondBow").setTextureName("morebows:DiamondBow");
        var_net_minecraft_item_Item_b = new b(215, (byte)1, 1.0, new byte[]{19, 10}, true, 22.0f, EnumRarity.epic).setUnlocalizedName("EnderBow").setTextureName("morebows:EnderBow");
        var_net_minecraft_item_Item_c = new b(576, (byte)2, 2.0, new byte[]{14, 9}, false, 15.0f, EnumRarity.uncommon).setUnlocalizedName("FlameBow").setTextureName("morebows:FlameBow");
        d = new b(550, (byte)3, 1.0, new byte[]{26, 13}, false, 26.0f, EnumRarity.common).setUnlocalizedName("FrostBow").setTextureName("morebows:FrostBow");
        e = new b(68, (byte)0, 2.5, new byte[]{8, 4}, false, 6.0f, EnumRarity.uncommon).setUnlocalizedName("GoldBow").setTextureName("morebows:GoldBow");
        f = new b(550, (byte)0, 1.5, new byte[]{16, 11}, false, 17.0f, EnumRarity.common).setUnlocalizedName("IronBow").setTextureName("morebows:IronBow");
        g = new b(550, (byte)0, 1.0, new byte[]{12, 7}, true, 13.0f, EnumRarity.rare).setUnlocalizedName("MultiBow").setTextureName("morebows:MultiBow");
        h = new b(484, (byte)0, 1.15, new byte[]{18, 13}, false, 20.0f, EnumRarity.common).setUnlocalizedName("StoneBow").setTextureName("morebows:StoneBow");
    }
}
