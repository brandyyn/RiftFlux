/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaublesApi
 *  cpw.mods.fml.common.Loader
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemCraftedEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemPickupEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemSmeltedEvent
 *  cpw.mods.fml.relauncher.ReflectionHelper
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.effect.EntityLightningBolt
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.monster.EntityEnderman
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.IInventory
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.ItemTool
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.stats.StatBase
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 *  net.minecraftforge.event.entity.living.LivingAttackEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.living.LivingDropsEvent
 *  net.minecraftforge.event.entity.living.LivingEvent$LivingUpdateEvent
 *  net.minecraftforge.event.entity.living.LivingHurtEvent
 *  net.minecraftforge.event.entity.player.ItemTooltipEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.event.entity.player.PlayerPickupXpEvent
 *  net.minecraftforge.event.world.BlockEvent$HarvestDropsEvent
 */
package net.nmccoy.legendgear;

import baubles.api.BaublesApi;
import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.legendgear.LegendGearAdditionsContent;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.relauncher.ReflectionHelper;
import java.lang.reflect.Method;
import java.util.Iterator;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.stats.StatBase;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerPickupXpEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.StarSpirit;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.entity.EntityHeart;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.MagicRing;
import net.nmccoy.legendgear.item.StarDust;
import net.nmccoy.legendgear.item.StarglassOrb;
import net.nmccoy.legendgear.item.spell.SpellItem;

public class PlayerEventHandler {
    static final String PNT = "PlayerPersisted";
    static final String LG2PD = "LegendGear2PersistedData";
    static final int invThunderChancePerFrame = 1500;
    static final int strikeRadius = 48;
    static final float WISH_RING_BONUS = 0.3f;
    private long lastPoofSoundTime = 0L;

    private static int getNightFallingStarProgress(World world, int baseAmount) {
        if (world == null || baseAmount <= 0) {
            return 0;
        }

        float frequency = LegendGear2.CONFIG_NIGHT_FALLING_STAR_FREQUENCY;
        if (frequency <= 0.0f) {
            return 0;
        }

        float scaledAmount = (float)baseAmount * frequency;
        int wholeAmount = (int)scaledAmount;
        float fractionalAmount = scaledAmount - (float)wholeAmount;
        if (fractionalAmount > 0.0f && world.rand.nextFloat() < fractionalAmount) {
            ++wholeAmount;
        }
        return wholeAmount;
    }

    public static void addPlayerStarCharge(EntityPlayer player, int amount, boolean respectCap) {
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
        if (respectCap) {
            if (pse.starChargePoints >= LegendGear2.starKarmaCap) {
                return;
            }
            if (pse.starChargePoints + amount >= LegendGear2.starKarmaCap) {
                pse.starChargePoints = LegendGear2.starKarmaCap;
                return;
            }
        }
        pse.starChargePoints += amount;
    }

    public static void considerGrassDrops(EntityPlayer player, World world, int x, int y, int z, int fortune) {
        int randomRoll;
        int die;
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
        if (pse.grassEmeraldSupply > 0 && LegendGear2.CONFIG_ALLOW_EMERALD_DROPS) {
            die = 6 - fortune;
            if (die < 1) {
                die = 1;
            }
            randomRoll = world.rand.nextInt(die);
            boolean arrowSubstitute = false;
            if (MagicRing.PlayerWears(player, MagicRing.RingType.ARROWFIND_RING)) {
                int arrowChance = 2;
                if (MagicRing.PlayerWears(player, MagicRing.RingType.RESONANCE_RING)) {
                    arrowChance = 3;
                }
                if (world.rand.nextInt(4) < arrowChance) {
                    arrowSubstitute = true;
                }
            }
            if (randomRoll == 0) {
                EntityItem ei = arrowSubstitute ? new EntityItem(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, new ItemStack(Items.arrow, 1, 0)) : new EntityItem(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, new ItemStack((Item)LegendGear2.emeraldShard, 1, 0));
                ei.delayBeforeCanPickup = 10;
                world.spawnEntityInWorld((Entity)ei);
                --pse.grassEmeraldSupply;
            }
        }
        if (player.getHealth() < player.getMaxHealth() && LegendGear2.CONFIG_ALLOW_HEART_DROPS) {
            die = 6 - fortune;
            if (die < 1) {
                die = 1;
            }
            if ((randomRoll = world.rand.nextInt(die)) == 0) {
                world.spawnEntityInWorld((Entity)new EntityHeart(world, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5));
            }
        }
    }

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        if (event.crafting.getItem() == LegendGear2.starsteelIngot) {
            event.player.addStat((StatBase)LegendGear2.achievementStarsteel, 1);
        }
    }

    @SubscribeEvent
    public void onSmelting(PlayerEvent.ItemSmeltedEvent event) {
        if (event.smelting.getItem() == LegendGear2.starsteelIngot) {
            event.player.addStat((StatBase)LegendGear2.achievementStarsteel, 1);
        }
    }

    @SubscribeEvent
    public void addTooltipInfo(ItemTooltipEvent event) {
        if (event.itemStack.hasTagCompound() && event.itemStack.getTagCompound().getBoolean("soulTether")) {
            event.toolTip.add(EnumChatFormatting.DARK_PURPLE + "Soul Tethered");
        }
        this.addUseTooltipInfo(event);
    }

    private void addUseTooltipInfo(ItemTooltipEvent event) {
        ItemStack stack = event.itemStack;
        if (stack == null || stack.getItem() == null) {
            return;
        }
        Item item = stack.getItem();
        int meta = stack.getItemDamage();
        if (item == LegendGear2.magicBoomerang) {
            this.addUseLine(event, "Deals " + this.formatTooltipNumber(ModConfig.legendGearMagicBoomerangDamage) + " damage and can carry dropped items.");
            return;
        }
        if (item == LegendGear2.spottingScope) {
            this.addUseLine(event, "Hold right-click to zoom.");
            this.addUseLine(event, "Ping: Quick-tap to place a marker on a block.");
            return;
        }
        if (item == LegendGear2.starDust) {
            if (meta == 1) {
                this.addUseLine(event, "Placeable Star Piece block.");
            } else if (meta == 2) {
                this.addUseLine(event, "Placeable Starstone block.");
            } else if (meta == 4) {
                this.addUseLine(event, "Placeable Infused Star Piece.");
                if (ModConfig.legendGearInfusedStarPiecesActAsStarbeamRails) {
                    this.addUseLine(event, "Grind at Star Speed!");
                }
            } else if (meta == 3) {
                if (LegendGear2.CONFIG_SPRINKLE_STARDUST_REQUIRE_SNEAK) {
                    this.addUseLine(event, "Crouch right-click to cast Sprinkle Stardust.");
                } else {
                    this.addUseLine(event, "Right-click to cast Sprinkle Stardust.");
                }
                this.addUseLine(event, "Invokes Ritual Blocks, charges Skylens over active Starwells, and transforms bookshelves into ritual notebooks.");
                this.addUseLine(event, "Consumes 1 charged stardust.");
            } else if (meta == 5) {
                this.addUseLine(event, "Placeable Infused Starstone block.");
            }
            return;
        }
        if (item == LegendGear2.emptyOrb) {
            if (meta == StarglassOrb.OrbTypes.empty.ordinal()) {
                this.addUseLine(event, "Base orb for crafting other orb types.");
            } else if (meta == StarglassOrb.OrbTypes.water.ordinal()) {
                this.addUseLine(event, "Right-click to throw; creates flowing water in its spell area.");
            } else if (meta == StarglassOrb.OrbTypes.lava.ordinal()) {
                this.addUseLine(event, "Right-click to throw; creates flowing lava in its spell area.");
            } else if (meta == StarglassOrb.OrbTypes.blast.ordinal()) {
                this.addUseLine(event, "Right-click to throw; explosion spell that damages entities and breaks fragile/cobble blocks.");
            } else if (meta == StarglassOrb.OrbTypes.twinkle.ordinal()) {
                this.addUseLine(event, "Right-click to throw a Twinkle spell orb.");
            } else if (meta == StarglassOrb.OrbTypes.fire.ordinal()) {
                this.addUseLine(event, "Right-click to throw a Fire spell orb.");
            } else if (meta == StarglassOrb.OrbTypes.ice.ordinal()) {
                this.addUseLine(event, "Right-click to throw an Ice spell orb.");
            } else if (meta == StarglassOrb.OrbTypes.zap.ordinal()) {
                this.addUseLine(event, "Right-click to throw a Lightning spell orb.");
            } else {
                this.addUseLine(event, "Throwable utility orb.");
            }
            return;
        }
        if (item == LegendGear2.tuningFork) {
            this.addUseLine(event, "Right-click a Ritual Block to cycle/tune its ritual state.");
            this.addUseLine(event, "Right-click elsewhere to play a tuning ping.");
            return;
        }
        if (item == LegendGear2.dimensionalCatalyst) {
            this.addUseLine(event, "Right-click compatible blocks to translocate them to nearby air.");
            this.addUseLine(event, "Special: Right-click Azurite Ore to convert it to stone and drop 3 azurite.");
            this.addUseLine(event, "Cost: Consumes 1 catalyst on successful transform.");
            return;
        }
        if (item == LegendGear2.reedPipes) {
            this.addUseLine(event, "Hold right-click to play; look up/down to change note.");
            this.addUseLine(event, "Sneak while playing for the alternate note scale.");
            return;
        }
        if (item == LegendGear2.emeraldShard) {
            if (meta == 0) {
                this.addUseLine(event, "Right-click with at least " + LegendGear2.emeraldExchangeRate + " shards to combine into 1 emerald piece.");
            } else {
                this.addUseLine(event, "Right-click with at least " + LegendGear2.emeraldExchangeRate + " pieces to combine into 1 emerald.");
            }
            return;
        }
        if (item == LegendGear2.azureFeather) {
            this.addUseLine(event, "Midair right-click while wearing Azure/Phoenix Mantle to start/extend glide and gain forward boost.");
            this.addUseLine(event, "Cost: Consumes 1 feather and 12 mana.");
            return;
        }
        if (item == LegendGear2.spiritEmblem) {
            if (meta == 1) {
                this.addUseLine(event, "Hold right-click to channel Phoenix intervention.");
                this.addUseLine(event, "Every 10 ticks spends 1 mana and can fully heal low HP, refill low hunger, or burn nearby undead in emergencies.");
            } else {
                this.addUseLine(event, "Blank emblem (crafting/ritual progression item).");
            }
            return;
        }
        if (item == LegendGear2.fortuneCookie) {
            this.addUseLine(event, "Eat to receive a random fortune message.");
            return;
        }
        if (item == Item.getItemFromBlock((Block)LegendGear2.caltropsBlock)) {
            this.addUseLine(event, "Right-click to toss a caltrops item.");
            this.addUseLine(event, "The dropped item auto-settles into a placed caltrops block when it lands.");
            return;
        }
        if (item == LegendGear2.magicRing) {
            this.addUseLine(event, "Equip in a Baubles ring slot.");
            this.addMagicRingUseInfo(event, meta);
            return;
        }
        if (item == LegendGear2.charmPendant) {
            this.addUseLine(event, "Equip in the Baubles amulet slot.");
            this.addCharmPendantUseInfo(event, meta);
            return;
        }
        if (item instanceof SpellItem) {
            this.addUseLine(event, "Hold right-click to charge, then release to cast.");
            this.addSpellUseInfo(event, (SpellItem)item, stack);
        }
    }

    private void addSpellUseInfo(ItemTooltipEvent event, SpellItem spellItem, ItemStack stack) {
        EntitySpellEffect.SpellType spell = spellItem.getSpell(stack);
        if (spell == null) {
            return;
        }
        switch (spell) {
            case Twinkle:
                this.addUseLine(event, "Star-element spell damage; also transforms sand into Star Sand.");
                break;
            case Fire1:
                this.addUseLine(event, "Fire-element spell damage that ignites targets.");
                break;
            case Ice1:
                this.addUseLine(event, "Ice-element spell damage; freezes source water and can heavily slow on crit casts.");
                break;
            case Lightning1:
                this.addUseLine(event, "Lightning-element spell damage with strong knockback on critical casts.");
                break;
            case ScytheWind:
                this.addUseLine(event, "Wind spell that shears entities/plants and pulls dropped items inward.");
                break;
            case Rayfire:
                this.addUseLine(event, "Radiant fire burst, extra effective against undead.");
                break;
            case Exit:
                this.addUseLine(event, "Teleports you to the first open surface space above your current position in the Overworld.");
                this.addUseLine(event, "Fails if bedrock is above you. Non-critical casts also cause confusion.");
                break;
            default:
                this.addUseLine(event, "Casts this item's bound spell.");
                break;
        }
    }

    private void addMagicRingUseInfo(ItemTooltipEvent event, int meta) {
        MagicRing.RingType[] types = MagicRing.RingType.values();
        if (meta < 0 || meta >= types.length) {
            return;
        }

        MagicRing.RingType type = types[meta];
        switch (type) {
            case SPEED_RING:
                if (LegendGear2.CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR) {
                    this.addUseLine(event, "Grants +50% speed while sprinting and enables original unlimited midair dash chaining.");
                } else if (LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS <= 0) {
                    this.addUseLine(event, "Grants +50% speed while sprinting; midair dash chaining is disabled by config.");
                } else {
                    this.addUseLine(event, "Grants +50% speed while sprinting and allows up to " + LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS + " extra midair dash jumps.");
                }
                if (LegendGear2.CONFIG_DASH_RING_AIR_JUMP_MANA_COST > 0.0F) {
                    this.addUseLine(event, "Each midair dash jump costs " + LegendGear2.CONFIG_DASH_RING_AIR_JUMP_MANA_COST + " mana (" + (LegendGear2.CONFIG_DASH_RING_AIR_JUMP_MANA_COST / 2.0F) + " stars before ring discounts).");
                }
                break;
            case CONVECTION_RING:
                this.addUseLine(event, "In lava, grants buoyant lift and reduced falling while consuming mana.");
                break;
            case SOFT_FALL_RING:
                this.addUseLine(event, "Caps effective fall distance to 3 blocks while consuming mana.");
                break;
            case COLDFEET_RING:
                this.addUseLine(event, "Freezes source water underfoot into thawing ice while consuming mana.");
                break;
            case THIEF_RING:
                this.addUseLine(event, "While sneaking, grants continuous invisibility while consuming mana.");
                break;
            case MAGE_RING:
                this.addUseLine(event, "Reduces staff/tome/scroll mana cost to 66% (or 50% when also wearing Resonance Ring).");
                break;
            case WARRIOR_RING:
                this.addUseLine(event, "Adds +4 melee damage per hit while consuming mana.");
                break;
            case FORTUNE_RING:
                this.addUseLine(event, "Increases emerald drop payout from kills (can double drops).");
                break;
            case ARROWFIND_RING:
                this.addUseLine(event, "Some emerald drops are converted into arrows.");
                break;
            case AZUREFIND_RING:
                this.addUseLine(event, "Some emerald drops are converted into azurite.");
                break;
            case WISH_RING:
                this.addUseLine(event, "In darkness under open sky, increases star-charge gain for falling stars.");
                break;
            case RESONANCE_RING:
                this.addUseLine(event, "Halves ring mana costs and boosts compatible ring conversion/proc chances.");
                break;
            case PHOENIX_RING:
                this.addUseLine(event, "Converts incoming fire damage into healing while consuming mana.");
                break;
            case PLAIN_RING:
                this.addUseLine(event, "Base ring with no passive bonus.");
                break;
            default:
                break;
        }
    }

    private void addCharmPendantUseInfo(ItemTooltipEvent event, int meta) {
        if (meta == 0) {
            this.addUseLine(event, "Phoenix Charm - consumed to revive you on death.");
        } else if (meta == 1) {
            this.addUseLine(event, "Azure Mantle - enables/maintains glide mechanics and Azure Feather boosts.");
        } else if (meta == 2) {
            this.addUseLine(event, "Phoenix Mantle - grants fire immunity and enables advanced glide behavior.");
        } else if (meta == 3) {
            this.addUseLine(event, "Blast Charm - consumed to negate a lethal explosion hit.");
        } else if (meta == 4) {
            this.addUseLine(event, "Feather Charm - consumed to negate lethal fall damage.");
        }
    }

    private void addUseLine(ItemTooltipEvent event, String line) {
        String formatted = EnumChatFormatting.GRAY + this.normalizeTooltipLine(line);
        if (!event.toolTip.contains(formatted)) {
            event.toolTip.add(formatted);
        }
    }

    private String normalizeTooltipLine(String line) {
        if (line == null) {
            return "";
        }
        return line.trim();
    }

    private String formatTooltipNumber(float value) {
        int rounded = Math.round(value);
        if (Math.abs(value - (float)rounded) < 1.0e-4f) {
            return Integer.toString(rounded);
        }
        float tenthRounded = Math.round(value * 10.0f) / 10.0f;
        if (Math.abs(tenthRounded - (float)Math.round(tenthRounded)) < 1.0e-4f) {
            return Integer.toString(Math.round(tenthRounded));
        }
        return Float.toString(tenthRounded);
    }

    @SubscribeEvent
    public void handleGrassDrops(BlockEvent.HarvestDropsEvent event) {
        if (event.block == Blocks.tallgrass && event.harvester != null) {
            EntityPlayer player = event.harvester;
            World world = event.world;
            PlayerEventHandler.considerGrassDrops(player, world, event.x, event.y, event.z, event.fortuneLevel);
        }
    }

    @SubscribeEvent
    public void fulguriteHarvest(BlockEvent.HarvestDropsEvent event) {
        ItemTool tool;
        ItemStack held;
        if (event.harvester == null || event.block != LegendGear2.struckGroundBlock) {
            return;
        }
        held = event.harvester.getHeldItem();
        if (held == null || !(held.getItem() instanceof ItemTool)) {
            return;
        }
        tool = (ItemTool) held.getItem();
        if (!tool.getToolClasses(held).contains("shovel")) {
            return;
        }
        if (ModConfig.legendGearFulguriteRequiresSilkTouch && !event.isSilkTouching) {
            return;
        }
        event.drops.add(new ItemStack(LegendGear2.fulgurite));
    }

    @SubscribeEvent
    public void dimensionalCatalystCraftingSound(PlayerEvent.ItemCraftedEvent event) {
        if (!event.player.worldObj.isRemote && this.lastPoofSoundTime != event.player.worldObj.getWorldTime()) {
            IInventory inv = event.craftMatrix;
            int slots = inv.getSizeInventory();
            for (int i = 0; i < slots; ++i) {
                if (inv.getStackInSlot(i) == null || inv.getStackInSlot(i).getItem() != LegendGear2.dimensionalCatalyst) continue;
                event.player.worldObj.playSoundAtEntity((Entity)event.player, "mob.endermen.portal", 0.5f, 1.0f);
                this.lastPoofSoundTime = event.player.worldObj.getWorldTime();
                break;
            }
        }
    }

    @SubscribeEvent
    public void onEntityConstructing(EntityEvent.EntityConstructing event) {
        if (event.entity instanceof EntityPlayer && PlayerStarstatsExtension.get((EntityPlayer)event.entity) == null) {
            PlayerStarstatsExtension.register((EntityPlayer)event.entity);
        }
    }

    @SubscribeEvent
    public void handleStatTransfer(net.minecraftforge.event.entity.player.PlayerEvent.Clone event) {
        EntityPlayer newCopy = event.entityPlayer;
        EntityPlayer original = event.original;
        NBTTagCompound storage = new NBTTagCompound();
        PlayerStarstatsExtension pseOld = PlayerStarstatsExtension.get(original);
        PlayerStarstatsExtension pseNew = PlayerStarstatsExtension.get(newCopy);
        pseOld.saveNBTData(storage);
        pseNew.loadNBTData(storage);
        if (!newCopy.worldObj.getGameRules().getGameRuleBooleanValue("keepInventory") && event.wasDeath) {
            for (int i = 0; i < original.inventory.mainInventory.length; ++i) {
                newCopy.inventory.mainInventory[i] = original.inventory.mainInventory[i];
            }
        }
        pseNew.starwellCharge = 0;
    }

    @SubscribeEvent
    public void saveTetheredItems(LivingDropsEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.entityLiving;
            Iterator i = event.drops.iterator();
            while (i.hasNext()) {
                EntityItem item = (EntityItem)i.next();
                ItemStack stack = item.getEntityItem();
                if (!stack.hasTagCompound() || !stack.getTagCompound().getBoolean("soulTether")) continue;
                stack.getTagCompound().removeTag("soulTether");
                player.inventory.addItemStackToInventory(stack);
                i.remove();
            }
        }
    }

    public static boolean isUnderSky(Entity e) {
        int px = (int)Math.floor(e.posX);
        int py = (int)Math.floor(e.posY);
        int pz = (int)Math.floor(e.posZ);
        return e.worldObj.canBlockSeeTheSky(px, py, pz);
    }

    public static boolean isInTheDark(Entity e) {
        int pz;
        int py;
        int px = (int)Math.floor(e.posX);
        return e.worldObj.getBlockLightValue(px, py = (int)Math.floor(e.posY), pz = (int)Math.floor(e.posZ)) < 5;
    }

    private static int computeEmeralds(int xp, World world, int looting) {
        if (xp < LegendGear2.minXpForEmeralds) {
            return 0;
        }
        float workingAmount = (float)xp * LegendGear2.emeraldsPerXP;
        int guaranteed = (int)(workingAmount * LegendGear2.guaranteedEmeraldRatio);
        int leftover = (int)(workingAmount + world.rand.nextFloat() - (float)guaranteed);
        if ((leftover += (int)(workingAmount * (float)looting / 2.0f)) > 0) {
            leftover = world.rand.nextInt(leftover);
        }
        return guaranteed + leftover;
    }

    @SubscribeEvent
    public void charmGuard(LivingHurtEvent lhe) {
        if (lhe.entityLiving instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)lhe.entityLiving;
            if (Loader.isModLoaded((String)"Baubles")) {
                if (lhe.source.isExplosion()
                        && lhe.ammount >= player.getHealth()
                        && LegendGearBaublesHelper.consumeFirstMatching(player, LegendGear2.charmPendant, 3)) {
                    lhe.setCanceled(true);
                    player.worldObj.playSoundAtEntity((Entity)player, "random.break", 1.0f, 1.0f);
                }
                if (lhe.source == DamageSource.fall
                        && lhe.ammount >= player.getHealth()
                        && LegendGearBaublesHelper.consumeFirstMatching(player, LegendGear2.charmPendant, 4)) {
                    lhe.setCanceled(true);
                    player.worldObj.playSoundAtEntity((Entity)player, "random.break", 1.0f, 1.0f);
                }
            }
        }
    }

    @SubscribeEvent
    public void warriorRingBoost(LivingHurtEvent lae) {
        EntityPlayer player;
        if (lae.source.getSourceOfDamage() instanceof EntityPlayer && MagicRing.PlayerWears(player = (EntityPlayer)lae.source.getSourceOfDamage(), MagicRing.RingType.WARRIOR_RING) && PlayerStarstatsExtension.availableMana(player) > 0.0f) {
            lae.ammount += MagicRing.WARRIOR_DAMAGE_BOOST;
            MagicRing.spendRingMana(player, MagicRing.WARRIOR_MANA_COST);
            player.worldObj.playSoundAtEntity((Entity)lae.entityLiving, "legendgear:smash", 0.3f, 1.0f);
        }
    }

    @SubscribeEvent
    public void collectItemAchievements(PlayerEvent.ItemPickupEvent ipe) {
        ItemStack item = ipe.pickedUp.getEntityItem();
        if (item.getItem() == LegendGear2.sunfireDiamond) {
            ipe.player.addStat((StatBase)LegendGear2.achievementSunfireDiamond, 1);
        }
    }

    @SubscribeEvent
    public void phoenixRingAbsorb(LivingAttackEvent lhe) {
        if (lhe.entityLiving instanceof EntityPlayer && lhe.source.isFireDamage()) {
            EntityPlayer player = (EntityPlayer)lhe.entityLiving;
            if (MagicRing.PlayerWears(player, MagicRing.RingType.PHOENIX_RING) && PlayerStarstatsExtension.availableMana(player) > 0.0f) {
                if ((float)player.hurtResistantTime > (float)player.maxHurtResistantTime / 2.0f) {
                    lhe.setCanceled(true);
                    return;
                }
                float amount = lhe.ammount;
                player.heal(amount);
                player.hurtResistantTime = player.maxHurtResistantTime;
                MagicRing.spendRingMana(player, amount);
                player.worldObj.playSoundAtEntity((Entity)player, "legendgear:heart", LegendGear2.CONFIG_PICKUP_SOUND_VOLUME, 1.0f);
                lhe.setCanceled(true);
            }
            if (Loader.isModLoaded((String)"Baubles")
                    && LegendGearBaublesHelper.findFirstMatchingSlot(player, LegendGear2.charmPendant, 2) >= 0) {
                lhe.setCanceled(true);
            }
        }
    }

    @SubscribeEvent
    public void ManaFromXPGain(PlayerPickupXpEvent ppxe) {
        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(ppxe.entityPlayer);
        pse.adjustManaFatigue((float)(-ppxe.orb.xpValue) * 0.25f);
    }

    private int substituteItems(LivingDropsEvent lde, EntityPlayer player, int emeralds) {
        int roll;
        if (emeralds > 0 && MagicRing.PlayerWears(player, MagicRing.RingType.AZUREFIND_RING)) {
            int chance = 2;
            if (MagicRing.PlayerWears(player, MagicRing.RingType.RESONANCE_RING)) {
                chance = 3;
            }
            if ((roll = player.worldObj.rand.nextInt(16)) < chance) {
                --emeralds;
                lde.drops.add(new EntityItem(lde.entity.worldObj, lde.entity.posX, lde.entity.posY, lde.entity.posZ, new ItemStack((Item)LegendGear2.azurite, 1, 1)));
            }
        }
        if (emeralds > 0 && MagicRing.PlayerWears(player, MagicRing.RingType.ARROWFIND_RING)) {
            int arrowChance = 2;
            if (MagicRing.PlayerWears(player, MagicRing.RingType.RESONANCE_RING)) {
                arrowChance = 3;
            }
            if ((roll = player.worldObj.rand.nextInt(4)) < arrowChance) {
                --emeralds;
                lde.drops.add(new EntityItem(lde.entity.worldObj, lde.entity.posX, lde.entity.posY, lde.entity.posZ, new ItemStack(Items.arrow)));
            }
        }
        return emeralds;
    }

    @SubscribeEvent
    public void handleLootDrop(LivingDropsEvent lde) {
        Entity source;
        if (lde.recentlyHit && LegendGear2.CONFIG_ALLOW_EMERALD_DROPS && (source = lde.source.getEntity()) != null && source instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)source;
            try {
                Method method = ReflectionHelper.findMethod(
                        EntityLivingBase.class,
                        lde.entityLiving,
                        new String[]{"getExperiencePoints", "func_70693_a"},
                        EntityPlayer.class
                );
                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }
                Object result = method.invoke((Object)lde.entityLiving, source);
                int loot = 0;
                if (result instanceof Integer) {
                    loot = (Integer)result;
                }
                if (!(lde.entityLiving instanceof EntityPlayer)) {
                    int emeralds = PlayerEventHandler.computeEmeralds(loot, lde.entityLiving.worldObj, lde.lootingLevel);
                    if (MagicRing.PlayerWears(player, MagicRing.RingType.FORTUNE_RING)) {
                        int roll = player.worldObj.rand.nextInt(6);
                        int target = 1;
                        if (MagicRing.PlayerWears(player, MagicRing.RingType.RESONANCE_RING)) {
                            target = 2;
                        }
                        if (roll <= target) {
                            emeralds *= 2;
                        }
                    }
                    int hearts = 0;
                    if (player.getHealth() < player.getMaxHealth() && LegendGear2.CONFIG_ALLOW_HEART_DROPS) {
                        int missingHearts = (int)(player.getMaxHealth() / 2.0f - (float)((int)(player.getHealth() / 2.0f)));
                        int rolledHearts = player.worldObj.rand.nextInt(missingHearts + 1);
                        if (rolledHearts > emeralds / 2 && emeralds > 1) {
                            rolledHearts = emeralds / 2;
                        }
                        if (rolledHearts > emeralds) {
                            rolledHearts = emeralds;
                        }
                        hearts = rolledHearts;
                    }
                    if (emeralds > 0 && player instanceof EntityPlayerMP) {
                        int cz;
                        int dz;
                        PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
                        int cx = (int)Math.floor(player.posX);
                        int dx = pse.lastKillX - cx;
                        double distance = Math.sqrt(dx * dx + (dz = pse.lastKillZ - (cz = (int)Math.floor(player.posZ))) * dz);
                        int bonus = (int)Math.floor(distance / (double)LegendGear2.emeraldAccumulationDistance);
                        if (bonus > 0) {
                            pse.lastKillX = cx;
                            pse.lastKillZ = cz;
                            pse.emeraldDropsRemaining += bonus;
                            PlayerEventHandler.addPlayerStarCharge(player, LegendGear2.huntingKarmaBonus, true);
                            if (StarSpirit.isCarryingEmblem(0, player)) {
                                if (lde.entityLiving.isEntityUndead()) {
                                    StarSpirit.observeActivity(0, player, 10);
                                }
                                if (player.getTotalArmorValue() == 0 && loot > 3) {
                                    StarSpirit.observeActivity(0, player, 10);
                                }
                            }
                            pse.grassEmeraldSupply += bonus;
                            if (pse.grassEmeraldSupply > LegendGear2.maxEmeraldGrassDropsBanked) {
                                pse.grassEmeraldSupply = LegendGear2.maxEmeraldGrassDropsBanked;
                            }
                        }
                        if (pse.emeraldDropsRemaining > 0) {
                            emeralds -= hearts;
                            if ((emeralds = this.substituteItems(lde, player, emeralds)) > 0 && LegendGear2.CONFIG_ALLOW_EMERALD_DROPS) {
                                ItemStack stack;
                                if (emeralds > LegendGear2.emeraldExchangeRate * LegendGear2.emeraldExchangeRate) {
                                    if ((emeralds /= LegendGear2.emeraldExchangeRate * LegendGear2.emeraldExchangeRate) > 64) {
                                        emeralds = 64;
                                    }
                                    stack = new ItemStack(Items.emerald, emeralds);
                                } else {
                                    stack = emeralds > LegendGear2.emeraldExchangeRate ? new ItemStack((Item)LegendGear2.emeraldShard, emeralds /= LegendGear2.emeraldExchangeRate, 1) : new ItemStack((Item)LegendGear2.emeraldShard, emeralds, 0);
                                }
                                lde.drops.add(new EntityItem(lde.entity.worldObj, lde.entity.posX, lde.entity.posY, lde.entity.posZ, stack));
                            }
                            if (pse.emeraldDropsRemaining > LegendGear2.maxEmeraldDropsBanked) {
                                pse.emeraldDropsRemaining = LegendGear2.maxEmeraldDropsBanked;
                            }
                            --pse.emeraldDropsRemaining;
                            if (!player.worldObj.isRemote) {
                                for (int i = 0; i < hearts; ++i) {
                                    player.worldObj.spawnEntityInWorld((Entity)new EntityHeart(player.worldObj, lde.entityLiving.posX, lde.entityLiving.posY + 0.5, lde.entityLiving.posZ));
                                }
                            }
                            if (lde.entityLiving instanceof EntityEnderman) {
                                boolean pearl = false;
                                for (EntityItem item : lde.drops) {
                                    if (item.getEntityItem().getItem() != Items.ender_pearl) continue;
                                    pearl = true;
                                    break;
                                }
                                if (!pearl) {
                                    ItemStack stack = new ItemStack(Items.ender_pearl);
                                    lde.drops.add(new EntityItem(lde.entity.worldObj, lde.entity.posX, lde.entity.posY, lde.entity.posZ, stack));
                                }
                            }
                        }
                    }
                }
            }
            catch (Exception nsmx) {
                System.err.println(nsmx.getMessage());
                throw new RuntimeException("Reflection Failure: " + nsmx.getMessage());
            }
        }
    }

    private void revivePlayer(EntityPlayer player) {
        player.setHealth(1.0f);
        LegendGear2.addConfiguredPotionEffect(player, LegendGear2.CONFIG_PHOENIX_REVIVE_RESISTANCE_POTION_ID, Potion.resistance, 140, 4, false);
        LegendGear2.addConfiguredPotionEffect(player, LegendGear2.CONFIG_PHOENIX_REVIVE_REGENERATION_POTION_ID, Potion.regeneration, 28, 4, false);
        LegendGear2.addConfiguredPotionEffect(player, LegendGear2.CONFIG_PHOENIX_REVIVE_FIRE_RESISTANCE_POTION_ID, Potion.fireResistance, 140, 0, false);
        player.setFire(7);
        player.worldObj.playSoundAtEntity((Entity)player, "legendgear:revive", 1.0f, 1.0f);
        if (!player.worldObj.isRemote) {
            EntitySpellEffect fire = new EntitySpellEffect(player.worldObj, EntitySpellEffect.SpellType.Fire1, player, Vec3.createVectorHelper((double)player.posX, (double)(player.posY + 1.0), (double)player.posZ), 2.0, 10.0, false);
            player.worldObj.spawnEntityInWorld((Entity)fire);
        }
    }

    @SubscribeEvent
    public void phoenixReviveTrigger(LivingDeathEvent lde) {
        if (lde.entityLiving instanceof EntityPlayer) {
            boolean intervened;
            EntityPlayer player = (EntityPlayer)lde.entityLiving;
            if (Loader.isModLoaded((String)"Baubles")
                    && LegendGearBaublesHelper.consumeFirstMatching(player, LegendGear2.charmPendant, 0)) {
                lde.setCanceled(true);
                this.revivePlayer(player);
                return;
            }
            if (StarSpirit.isCarryingEmblem(0, player) && (intervened = StarSpirit.attemptIntervention(0, player, 300))) {
                lde.setCanceled(true);
                this.revivePlayer(player);
                return;
            }
        }
    }

    @SubscribeEvent
    public void applyLegendGearJumpPenalty(LivingEvent.LivingJumpEvent event) {
        if (event == null || event.entityLiving == null || LegendGear2.jumpPenaltyPotion == null) {
            return;
        }

        PotionEffect effect = event.entityLiving.getActivePotionEffect(LegendGear2.jumpPenaltyPotion);
        if (effect == null) {
            return;
        }

        double reducedMotionY = event.entityLiving.motionY - (double)(effect.getAmplifier() + 1) * 0.1D;
        event.entityLiving.motionY = Math.max(0.02D, reducedMotionY);
    }

    @SubscribeEvent
    public void onPlayerLoginSparkle(PlayerEvent.PlayerLoggedInEvent event) {
        if (event == null || event.player == null || !ModConfig.legendGearLoginSparkleEffectEnabled) {
            return;
        }
        this.spawnPlayerSparkles(event.player, 28, 1.1f);
    }

    @SubscribeEvent
    public void onPlayerChangedDimensionSparkle(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event == null || event.player == null || !ModConfig.legendGearDimensionChangeSparkleEffectEnabled) {
            return;
        }
        this.spawnPlayerSparkles(event.player, 36, 1.25f);
    }

    private void spawnPlayerSparkles(EntityPlayer player, int count, float scale) {
        World world = player.worldObj;
        if (world == null || !world.isRemote || count <= 0) {
            return;
        }

        for (int i = 0; i < count; i++) {
            double angle = world.rand.nextDouble() * Math.PI * 2.0;
            double radius = 0.2 + world.rand.nextDouble() * 0.75;
            double x = player.posX + Math.cos(angle) * radius;
            double y = player.posY + 0.1 + world.rand.nextDouble() * 1.8;
            double z = player.posZ + Math.sin(angle) * radius;
            double vx = (world.rand.nextDouble() - 0.5) * 0.03;
            double vy = 0.02 + world.rand.nextDouble() * 0.04;
            double vz = (world.rand.nextDouble() - 0.5) * 0.03;
            LegendGear2.proxy.addSparkleParticle(world, x, y, z, vx, vy, vz, scale);
        }
    }

    @SubscribeEvent
    public void handleGlide(LivingEvent.LivingUpdateEvent lue) {
        if (lue.entityLiving instanceof EntityPlayer) {
            Vec3 realVelocity;
            double energy;
            EntityPlayer player = (EntityPlayer)lue.entityLiving;
            PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
            if (pse.skylensTagCharge > 0) {
                --pse.skylensTagCharge;
            }
            if (pse.getGlide() == 0.0f) {
                return;
            }
            boolean equipped = false;
            int damage = 0;
            ItemStack neckStack = Loader.isModLoaded((String)"Baubles")
                    ? LegendGearBaublesHelper.findFirstMatchingStack(player, LegendGear2.charmPendant, 1, 2)
                    : null;
            if (neckStack != null) {
                damage = neckStack.getItemDamage();
                if (neckStack.getItem() == LegendGear2.charmPendant && (damage == 1 || damage == 2)) {
                    equipped = true;
                }
            }
            if (!equipped) {
                pse.setGlide(0.0f);
                return;
            }
            double height = (double)pse.getGlide() - player.posY;
            if (player.onGround || player.isCollided || player.isInWater()) {
                pse.setGlide(0.0f);
                return;
            }
            double gravity = 0.08;
            double drag = 0.01;
            double max_accel = 0.24;
            double overspeed_drag = 0.0;
            double speed_cap = 1.5;
            double min_airspeed = 0.5;
            double glide_ratio = 4.0;
            if (damage == 2) {
                glide_ratio = 7.0;
            }
            if ((energy = gravity * height) < 0.0) {
                energy = 0.0;
            }
            double speed = Math.sqrt(energy);
            Vec3 velocity = player.getLookVec();
            double airspeed = velocity.dotProduct(realVelocity = Vec3.createVectorHelper((double)player.motionX, (double)(player.motionY + gravity), (double)player.motionZ));
            if (airspeed < 0.0) {
                airspeed = 0.0;
            }
            double lift_factor = 1.0;
            if (airspeed < min_airspeed) {
                lift_factor = airspeed / min_airspeed;
            }
            velocity.xCoord *= speed;
            velocity.yCoord *= speed;
            velocity.zCoord *= speed;
            Vec3 neededAccel = realVelocity.subtract(velocity);
            double needed = neededAccel.lengthVector();
            double overspeed = 0.0;
            max_accel = Math.min(max_accel, needed * lift_factor);
            overspeed = needed - max_accel;
            neededAccel = neededAccel.normalize();
            neededAccel.xCoord *= max_accel;
            neededAccel.yCoord *= max_accel;
            neededAccel.zCoord *= max_accel;
            Vec3 result = realVelocity.addVector(neededAccel.xCoord, neededAccel.yCoord, neededAccel.zCoord);
            result.yCoord -= gravity;
            double excess_speed = 0.0;
            if (result.lengthVector() > speed_cap) {
                excess_speed = result.lengthVector() - speed_cap;
                result = result.normalize();
                result.xCoord *= speed_cap;
                result.yCoord *= speed_cap;
                result.zCoord *= speed_cap;
            }
            if (pse.lastGlideCharge + 4.0f < pse.getGlide()) {
                double x = player.posX + result.xCoord * 4.0;
                double y = player.posY + result.yCoord * 4.0;
                double z = player.posZ + result.zCoord * 4.0;
                LegendGear2.proxy.addRippleParticle(player.worldObj, x, y, z, 0.0, 0.0, 0.0, 30.0f);
            }
            player.motionX = result.xCoord;
            player.motionY = result.yCoord;
            player.motionZ = result.zCoord;
            pse.setGlide((float)((height -= result.lengthVector() / glide_ratio) + player.posY));
            if (player.motionY > -0.7) {
                player.fallDistance = 0.0f;
            }
        }
    }

    @SubscribeEvent
    public void handleTimeTick(LivingEvent.LivingUpdateEvent lue) {
        if (lue.entityLiving instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)lue.entityLiving;
            PlayerStarstatsExtension pse = PlayerStarstatsExtension.get((EntityPlayer)player);
            pse.rechargeDelay -= 0.05f;
            if (pse.rechargeDelay <= 0.0f) {
                pse.rechargeDelay = 0.0f;
                if (!player.isUsingItem() && !player.worldObj.isRemote) {
                    if (pse.fatigueLevel() == 0) {
                        pse.adjustManaFatigue(LegendGear2.manaRechargeRate * -0.05f);
                    } else {
                        pse.adjustManaFatigue(LegendGear2.fatiguedRechargeRate * -0.05f);
                    }
                }
            }
            if (PlayerEventHandler.isUnderSky((Entity)player)) {
                int strikeZ;
                int strikeY;
                int strikeX;
                pse.lastSkyX = (int)Math.floor(player.posX);
                pse.lastSkyZ = (int)Math.floor(player.posZ);
                pse.lastSkyWorld = player.dimension;
                if (PlayerEventHandler.isInTheDark((Entity)player) && player.dimension == 0) {
                    int chargeRate = 1;
                    if ((double)player.worldObj.getCurrentMoonPhaseFactor() > 0.9) {
                        ++chargeRate;
                    }
                    if (MagicRing.PlayerWears((EntityPlayer)player, MagicRing.RingType.WISH_RING)) {
                        float bonus = 0.3f;
                        if (MagicRing.PlayerWears((EntityPlayer)player, MagicRing.RingType.RESONANCE_RING)) {
                            bonus = (float)((double)bonus * 1.5);
                        }
                        int chargeBonus = (int)(player.worldObj.rand.nextFloat() + bonus);
                        chargeRate += chargeBonus;
                    }
                    int scaledChargeRate = PlayerEventHandler.getNightFallingStarProgress(player.worldObj, chargeRate);
                    if (scaledChargeRate > 0) {
                        PlayerEventHandler.addPlayerStarCharge((EntityPlayer)player, scaledChargeRate, true);
                    }
                    if (pse.starChargePoints >= LegendGear2.starKarmaCost) {
                        pse.starCooldownTimer += PlayerEventHandler.getNightFallingStarProgress(player.worldObj, 1);
                        if (pse.starCooldownTimer >= LegendGear2.starCooldown) {
                            pse.starChargePoints -= LegendGear2.starKarmaCost;
                            pse.starCooldownTimer = 0 - player.worldObj.rand.nextInt(LegendGear2.starCooldownFuzz);
                            if (!player.worldObj.isRemote) {
                                player.worldObj.spawnEntityInWorld((Entity)new EntityFallingStar((EntityPlayer)player));
                            }
                        }
                    }
                }
                if (!player.worldObj.isRemote && player.worldObj.isThundering() && player.worldObj.rand.nextInt(1500) == 0 && player.worldObj.canLightningStrikeAt(strikeX = (int)player.posX + player.worldObj.rand.nextInt(96) - 48, strikeY = player.worldObj.getPrecipitationHeight(strikeX, strikeZ = (int)player.posZ + player.worldObj.rand.nextInt(96) - 48), strikeZ)) {
                    player.worldObj.addWeatherEffect((Entity)new EntityLightningBolt(player.worldObj, (double)strikeX, (double)strikeY, (double)strikeZ));
                    if (strikeY > 0) {
                        Block struckBlock = player.worldObj.getBlock(strikeX, strikeY - 1, strikeZ);
                        int struckMeta = player.worldObj.getBlockMetadata(strikeX, strikeY - 1, strikeZ);
                        if (struckBlock == Blocks.sand) {
                            if (struckMeta == 1 && LegendGearAdditionsContent.lightningStruckRedSand != null) {
                                player.worldObj.setBlock(strikeX, strikeY - 1, strikeZ, LegendGearAdditionsContent.lightningStruckRedSand, 0, 3);
                            } else {
                                player.worldObj.setBlock(strikeX, strikeY - 1, strikeZ, (Block)LegendGear2.struckGroundBlock, 0, 3);
                            }
                        }
                        if (struckBlock == Blocks.dirt || struckBlock == Blocks.grass || struckBlock == Blocks.mycelium) {
                            player.worldObj.setBlock(strikeX, strikeY - 1, strikeZ, (Block)LegendGear2.struckGroundBlock, 1, 3);
                        }
                    }
                }
            }
        }
    }
}
