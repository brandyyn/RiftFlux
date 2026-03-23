/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.passive.EntityCow
 *  net.minecraft.entity.passive.EntityHorse
 *  net.minecraft.entity.passive.EntityMooshroom
 *  net.minecraft.entity.passive.EntityPig
 *  net.minecraft.entity.passive.EntitySheep
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.Item$ToolMaterial
 *  net.minecraft.item.ItemArmor
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemSword
 *  net.minecraft.item.ItemTool
 *  net.minecraft.item.crafting.FurnaceRecipes
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.IChatComponent
 */
package net.nmccoy.legendgear.ritual;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityCow;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.EntityMooshroom;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatBase;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.StarSpirit;
import net.nmccoy.legendgear.block.TileEntityRitual;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.item.ItemNucleus;
import net.nmccoy.legendgear.item.MagicRing;
import net.nmccoy.legendgear.ritual.Boon;
import net.nmccoy.legendgear.ritual.RecipeComponent;
import net.nmccoy.legendgear.ritual.Ritual;
import net.nmccoy.legendgear.ritual.RitualRecipe;

public class RitualManager {
    public Set<Ritual> rituals = new HashSet<Ritual>();

    public EntityFallingStar spawnRewardStar(TileEntityRitual location) {
        EntityFallingStar star = new EntityFallingStar(location.getWorldObj());
        star.setPosition((double)location.xCoord + 0.5, location.yCoord + 150, (double)location.zCoord + 0.5);
        star.special = true;
        location.getWorldObj().spawnEntityInWorld((Entity)star);
        return star;
    }

    public static boolean spendRitualLevels(EntityPlayer caster, int levels) {
        int spendLevels = caster.experienceLevel;
        if (spendLevels > levels) {
            spendLevels = levels;
        }
        caster.addExperienceLevel(-spendLevels);
        if (spendLevels < levels) {
            caster.attackEntityFrom(new DamageSource("soul").setDamageBypassesArmor().setDamageIsAbsolute(), (float)(4 * levels - 4 * spendLevels));
        }
        return !(caster.getHealth() <= 0.0f);
    }

    public boolean spiritOffering(int spirit, Object offering, TileEntityRitual location, EntityPlayer caster) {
        int offeringValue;
        boolean request = false;
        boolean granted = false;
        float altarBoostFactor = 1.0f;
        if (offering instanceof ItemStack) {
            List<ItemStack> result;
            boolean altar = false;
            if (caster.worldObj.getBlock(location.xCoord, location.yCoord + 1, location.zCoord) == LegendGear2.starAltarBlock) {
                altar = true;
            }
            if (altar) {
                altarBoostFactor = 1.5f;
            }
            ItemStack item = (ItemStack)offering;
            int count = item.stackSize;
            Boon boon = StarSpirit.phoenixPrizes.matchRequest(item);
            if (boon != null) {
                request = true;
            }
            if ((result = StarSpirit.handleItemRequest(spirit, caster, item, count)) != null) {
                EntityFallingStar star = this.spawnRewardStar(location);
                for (ItemStack stack : result) {
                    star.addItem(stack);
                }
                granted = true;
                if (spirit == 0) {
                    caster.addStat((StatBase)LegendGear2.achievementPhoenixBoon, 1);
                }
            }
            if (request && !granted) {
                this.spawnRewardStar(location).addItem(item);
            }
        }
        if ((offeringValue = StarSpirit.handleOffering(spirit, caster, offering, altarBoostFactor)) == -1 && !request) {
            return false;
        }
        if (request) {
            return true;
        }
        String mood = "indifferent to";
        if (offeringValue > 0) {
            mood = "appreciative of";
        }
        if (offeringValue >= 100) {
            mood = "grateful for";
        }
        if (offeringValue >= 500) {
            mood = "enthusiastic about";
        }
        if (offeringValue >= 2000) {
            mood = "ecstatic about";
        }
        String name = "missingno.";
        if (spirit == 0) {
            name = "the Phoenix";
        }
        if (!request) {
            caster.addChatMessage((IChatComponent)new ChatComponentText("" + EnumChatFormatting.GRAY + EnumChatFormatting.ITALIC + "(You sense " + name + " is " + mood + " your offering.)"));
            ItemStack thanks = StarSpirit.gratitude(spirit, caster, offeringValue);
            if (thanks != null) {
                this.spawnRewardStar(location).addItem(thanks);
                if (spirit == 0) {
                    caster.addStat((StatBase)LegendGear2.achievementPhoenixBoon, 1);
                }
            }
        }
        return true;
    }

    public RitualManager() {
        Ritual.Summoning summonMooshroom = new Ritual.Summoning("summonMooshroom", EntityMooshroom.class, new RecipeComponent((Block)Blocks.red_mushroom, (Block)Blocks.mycelium), (Object)Items.mushroom_stew);
        this.rituals.add(summonMooshroom);
        Ritual convertMooshroom = new Ritual("convertMooshroom", new RitualRecipe().add(new RecipeComponent((Block)Blocks.red_mushroom, (Block)Blocks.brown_mushroom))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                System.out.println("invoked " + this.unlocalizedName);
                if (caster != null && !caster.worldObj.isRemote) {
                    List<EntityCow> cows = location.targetsInRitual(EntityCow.class);
                    if (cows.size() == 0) {
                        return false;
                    }
                    EntityCow cow = cows.get(0);
                    cow.setDead();
                    EntityMooshroom moosh = new EntityMooshroom(caster.worldObj);
                    moosh.setLocationAndAngles(cow.posX, cow.posY, cow.posZ, cow.rotationYaw, cow.rotationPitch);
                    moosh.setHealth(cow.getHealth());
                    moosh.renderYawOffset = cow.renderYawOffset;
                    caster.worldObj.spawnEntityInWorld((Entity)moosh);
                    return true;
                }
                return false;
            }
        };
        this.rituals.add(convertMooshroom);
        Ritual.Summoning summonSheep = new Ritual.Summoning("summonSheep", EntitySheep.class, new RecipeComponent(Blocks.wool, (Block)Blocks.grass), (Object)Blocks.grass);
        this.rituals.add(summonSheep);
        Ritual.Summoning summonHorse = new Ritual.Summoning("summonHorse", EntityHorse.class, new RecipeComponent(Blocks.hay_block, (Block)Blocks.grass), (Object)Items.sugar);
        this.rituals.add(summonHorse);
        Ritual.Summoning summonPig = new Ritual.Summoning("summonPig", EntityPig.class, new RecipeComponent((Block)Blocks.brown_mushroom, (Block)Blocks.grass), (Object)Items.carrot);
        this.rituals.add(summonPig);
        Ritual testCase = new Ritual("testCase", new RitualRecipe().add(new RecipeComponent(Blocks.gravel, Blocks.gravel))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                List<EntityItem> items = location.itemsInRitual();
                return true;
            }
        };
        Ritual phoenixOffering = new Ritual("phoenixOffering", new RitualRecipe().add(new RecipeComponent((Block)Blocks.fire, Blocks.gold_block)).add(new RecipeComponent(LegendGear2.starstoneBlock))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                List<EntityItem> items = location.itemsInRitual();
                Object offering = null;
                Block focusBlock = caster.worldObj.getBlock(location.xCoord, location.yCoord + 1, location.zCoord);
                if (focusBlock != Blocks.air) {
                    offering = focusBlock;
                } else if (items.size() > 0) {
                    offering = items.get(0).getEntityItem();
                } else {
                    return false;
                }
                boolean result = RitualManager.this.spiritOffering(0, offering, location, caster);
                if (result) {
                    if (items.size() > 0) {
                        items.get(0).setDead();
                    }
                    caster.worldObj.setBlock(location.xCoord, location.yCoord + 1, location.zCoord, (Block)Blocks.fire, 14, 3);
                }
                return result;
            }
        };
        this.rituals.add(phoenixOffering);
        Ritual phoenixAltarOffering = new Ritual("phoenixAltarOffering", new RitualRecipe().add(new RecipeComponent((Block)Blocks.fire, (Block)Blocks.fire))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                List<EntityItem> items = location.itemsInRitual();
                ItemStack offering = null;
                Block focusBlock = caster.worldObj.getBlock(location.xCoord, location.yCoord + 1, location.zCoord);
                if (focusBlock != LegendGear2.starAltarBlock) {
                    return false;
                }
                if (items.size() <= 0) {
                    return false;
                }
                offering = items.get(0).getEntityItem();
                boolean result = RitualManager.this.spiritOffering(0, offering, location, caster);
                if (result && items.size() > 0) {
                    items.get(0).setDead();
                }
                return result;
            }
        };
        this.rituals.add(phoenixAltarOffering);
        Ritual soulTether = new Ritual("soulTether", new RitualRecipe().add(new RecipeComponent(Blocks.soul_sand, Blocks.tripwire)).add(new RecipeComponent(Blocks.iron_block))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                List<EntityItem> items = location.itemsInRitual();
                if (items.size() == 1) {
                    EntityItem ei = items.get(0);
                    ItemStack stack = ei.getEntityItem();
                    boolean pricePaid = RitualManager.spendRitualLevels(caster, 10);
                    if (!pricePaid) {
                        return false;
                    }
                    if (stack.getItem().getItemStackLimit(stack) == 1) {
                        if (!stack.hasTagCompound()) {
                            stack.setTagCompound(new NBTTagCompound());
                        }
                        stack.getTagCompound().setBoolean("soulTether", true);
                        return true;
                    }
                }
                return false;
            }
        };
        this.rituals.add(soulTether);
        Ritual crucible = new Ritual("crucible", new RitualRecipe().add(new RecipeComponent(Blocks.lava, Blocks.lava))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                List<EntityItem> items = location.itemsInRitual();
                if (items.size() == 1) {
                    int count;
                    EntityItem ei = items.get(0);
                    ItemStack stack = ei.getEntityItem();
                    ItemStack output = FurnaceRecipes.smelting().getSmeltingResult(stack);
                    if (output == null) {
                        return false;
                    }
                    for (int total = output.stackSize * stack.stackSize; total > 0; total -= count) {
                        ItemStack oneOutput = output.copy();
                        oneOutput.stackSize = count = Math.min(oneOutput.getMaxStackSize(), total);
                        EntityItem outEntity = new EntityItem(location.getWorldObj(), (double)location.xCoord + 0.5, (double)location.yCoord + 1.5, (double)location.zCoord + 0.5, oneOutput);
                        outEntity.motionX = 0.0;
                        outEntity.motionY = 0.0;
                        outEntity.motionZ = 0.0;
                        location.getWorldObj().spawnEntityInWorld((Entity)outEntity);
                    }
                    ei.setDead();
                    return true;
                }
                return false;
            }
        };
        this.rituals.add(crucible);
        Ritual stoneskin = new Ritual("stoneskin", new RitualRecipe().add(new RecipeComponent(Blocks.stone, Blocks.stone))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                EntityItem ei;
                ItemStack stack;
                List<EntityItem> items = location.itemsInRitual();
                if (items.size() == 1 && (stack = (ei = items.get(0)).getEntityItem()).getItem() == Items.leather) {
                    int count = stack.stackSize;
                    int baseDuration = 3600;
                    int bonusDuration = (count - 1) * 20 * 30;
                    LegendGear2.addConfiguredPotionEffect(caster, LegendGear2.CONFIG_STONESKIN_RESISTANCE_POTION_ID, Potion.resistance, baseDuration + bonusDuration, 1, true);
                    ei.setDead();
                    return true;
                }
                return false;
            }
        };
        this.rituals.add(stoneskin);
        Ritual dismantle = new Ritual("dismantle", new RitualRecipe().add(new RecipeComponent(Blocks.gravel, Blocks.gravel))){

            @Override
            public boolean invoke(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
                EntityItem ei;
                ItemStack stack;
                List<EntityItem> items = location.itemsInRitual();
                if (items.size() == 1 && ((stack = (ei = items.get(0)).getEntityItem()).getItem().isRepairable() || stack.getItem() == LegendGear2.magicRing)) {
                    ItemNucleus.NucleusType gem;
                    ItemStack part = null;
                    ItemStack[] repairCandidates = new ItemStack[]{
                            new ItemStack(Item.getItemFromBlock(Blocks.planks)),
                            new ItemStack(Item.getItemFromBlock(Blocks.cobblestone)),
                            new ItemStack(Items.iron_ingot),
                            new ItemStack(Items.gold_ingot),
                            new ItemStack(Items.diamond),
                            new ItemStack(Items.leather),
                            new ItemStack(LegendGear2.starglassIngot),
                            new ItemStack(LegendGear2.starsteelIngot)
                    };
                    for (ItemStack candidate : repairCandidates) {
                        if (candidate == null || !stack.getItem().getIsRepairable(stack, candidate)) {
                            continue;
                        }
                        part = candidate.copy();
                        part.stackSize = 1;
                        break;
                    }
                    if (stack.getItem() == LegendGear2.magicRing && (gem = MagicRing.RingType.values()[stack.getItemDamage()].gemType) != null) {
                        part = new ItemStack((Item)LegendGear2.elementNucleus, 1, 1000 + gem.ordinal());
                    }
                    if (part != null) {
                        ei.setDead();
                        EntityItem output = new EntityItem(location.getWorldObj(), (double)location.xCoord + 0.5, (double)location.yCoord + 1.5, (double)location.zCoord + 0.5, part);
                        location.getWorldObj().spawnEntityInWorld((Entity)output);
                        return true;
                    }
                }
                return false;
            }
        };
        this.rituals.add(dismantle);
    }

    public boolean attemptInvocation(RitualRecipe ingredients, TileEntityRitual location, EntityPlayer caster) {
        RitualRecipe generic = ingredients.generic();
        for (Ritual r : this.rituals) {
            System.out.println("considered " + r.unlocalizedName);
            if (!r.accepts(ingredients) && !r.accepts(generic)) continue;
            return r.invoke(ingredients, location, caster);
        }
        return false;
    }
}
