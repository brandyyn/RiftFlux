/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 *  cpw.mods.fml.common.eventhandler.Event
 *  cpw.mods.fml.common.eventhandler.Event$Result
 *  cpw.mods.fml.common.eventhandler.EventPriority
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  net.minecraft.block.Block
 *  net.minecraft.block.BlockCrops
 *  net.minecraft.block.BlockDirt
 *  net.minecraft.block.BlockGravel
 *  net.minecraft.block.BlockLog
 *  net.minecraft.block.BlockMelon
 *  net.minecraft.block.BlockOre
 *  net.minecraft.block.BlockRedstoneOre
 *  net.minecraft.block.BlockStem
 *  net.minecraft.block.BlockStone
 *  net.minecraft.block.IGrowable
 *  net.minecraft.block.material.Material
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.item.EntityXPOrb
 *  net.minecraft.entity.monster.EntityMob
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.entity.player.InventoryPlayer
 *  net.minecraft.entity.projectile.EntityFishHook
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ContainerPlayer
 *  net.minecraft.inventory.InventoryCrafting
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemPickaxe
 *  net.minecraft.item.ItemSpade
 *  net.minecraft.item.ItemStack
 *  net.minecraft.item.crafting.CraftingManager
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.util.MathHelper
 *  net.minecraftforge.common.IExtendedEntityProperties
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.entity.EntityEvent$EntityConstructing
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.player.PlayerDestroyItemEvent
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.event.entity.player.PlayerEvent$Clone
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$Action
 *  net.minecraftforge.event.world.BlockEvent$BreakEvent
 *  net.minecraftforge.event.world.BlockEvent$HarvestDropsEvent
 *  net.minecraftforge.oredict.OreDictionary$OreRegisterEvent
 */
package assets.levelup;

import assets.levelup.LevelUp;
import assets.levelup.PlayerExtendedProperties;
import com.google.common.collect.Sets;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.common.eventhandler.EventPriority;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockGravel;
import net.minecraft.block.BlockLog;
import net.minecraft.block.BlockMelon;
import net.minecraft.block.BlockOre;
import net.minecraft.block.BlockRedstoneOre;
import net.minecraft.block.BlockStem;
import net.minecraft.block.BlockStone;
import net.minecraft.block.IGrowable;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.entity.projectile.EntityFishHook;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraftforge.common.IExtendedEntityProperties;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.oredict.OreDictionary;

public final class PlayerEventHandler {
    public static boolean oldSpeedDigging = true;
    public static boolean oldSpeedRedstone = true;
    public static float resetSkillOnDeath = 0.0f;
    public static boolean resetClassOnDeath = false;
    public static boolean noPlaceDuplicate = true;
    public static double xpPerLevel = 3.0;
    public static final int minLevel = 4;
    private static ItemStack[] lootList = new ItemStack[]{new ItemStack(Items.bone), new ItemStack(Items.reeds), new ItemStack(Items.arrow), new ItemStack(Items.apple), new ItemStack(Items.bucket), new ItemStack(Items.boat), new ItemStack(Items.ender_pearl), new ItemStack((Item)Items.fishing_rod), new ItemStack((Item)Items.chainmail_chestplate), new ItemStack(Items.iron_ingot)};
    private static Map<Block, Integer> blockToCounter = new IdentityHashMap<Block, Integer>();
    private static ItemStack[] digLoot;
    private static ItemStack[] digLoot1;
    private static ItemStack[] digLoot2;
    private static ItemStack[] digLoot3;
    private static Set<Block> ores;

    @SubscribeEvent(priority=EventPriority.LOW)
    public void onBreak(PlayerEvent.BreakSpeed event) {
        ItemStack itemstack = event.entityPlayer.getCurrentEquippedItem();
        if (itemstack != null) {
            if (oldSpeedDigging && itemstack.getItem() instanceof ItemSpade) {
                if (event.block instanceof BlockDirt || event.block instanceof BlockGravel) {
                    event.newSpeed = event.newSpeed * itemstack.func_150997_a(event.block) / 0.5f;
                }
            } else if (oldSpeedRedstone && itemstack.getItem() instanceof ItemPickaxe && event.block instanceof BlockRedstoneOre) {
                event.newSpeed = event.newSpeed * itemstack.func_150997_a(event.block) / 3.0f;
            }
        }
        if (event.block instanceof BlockStone || event.block == Blocks.cobblestone || event.block == Blocks.obsidian || event.block instanceof BlockOre) {
            event.newSpeed += (float)(PlayerEventHandler.getSkill(event.entityPlayer, 0) / 5) * 0.2f;
        } else if (event.block.getMaterial() == Material.wood) {
            event.newSpeed += (float)(PlayerEventHandler.getSkill(event.entityPlayer, 3) / 5) * 0.2f;
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onDeath(LivingDeathEvent event) {
        if (event.entityLiving instanceof EntityPlayer) {
            if (resetClassOnDeath) {
                PlayerExtendedProperties.from((EntityPlayer)event.entityLiving).setPlayerClass((byte)0);
            }
            if (resetSkillOnDeath > 0.0f) {
                PlayerExtendedProperties.from((EntityPlayer)event.entityLiving).takeSkillFraction(resetSkillOnDeath);
            }
        } else if (event.entityLiving instanceof EntityMob && event.source.getEntity() instanceof EntityPlayer) {
            LevelUp.giveBonusFightingXP((EntityPlayer)event.source.getEntity());
        }
    }

    @SubscribeEvent(priority=EventPriority.LOW)
    public void onInteract(PlayerInteractEvent event) {
        if (event.useItem != Event.Result.DENY) {
            ItemStack itemStack;
            if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_AIR) {
                int loot;
                EntityFishHook hook = event.entityPlayer.fishEntity;
                if (hook != null && hook.field_146043_c == null && hook.field_146045_ax > 0 && (loot = PlayerEventHandler.getFishingLoot(event.entityPlayer)) >= 0) {
                    ItemStack stack = event.entityPlayer.inventory.getCurrentItem();
                    int i = stack.stackSize;
                    int j = stack.getItemDamage();
                    stack.damageItem(loot, (EntityLivingBase)event.entityPlayer);
                    event.entityPlayer.swingItem();
                    event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, stack);
                    if (event.entityPlayer.capabilities.isCreativeMode) {
                        stack.stackSize = i;
                        if (stack.isItemStackDamageable()) {
                            stack.setItemDamage(j);
                        }
                    }
                    if (stack.stackSize <= 0) {
                        event.entityPlayer.inventory.setInventorySlotContents(event.entityPlayer.inventory.currentItem, null);
                        MinecraftForge.EVENT_BUS.post((Event)new PlayerDestroyItemEvent(event.entityPlayer, stack));
                    }
                    if (!event.entityPlayer.isUsingItem() && event.entityPlayer instanceof EntityPlayerMP) {
                        ((EntityPlayerMP)event.entityPlayer).sendContainerToPlayer(event.entityPlayer.inventoryContainer);
                    }
                    event.useItem = Event.Result.DENY;
                    if (!hook.worldObj.isRemote) {
                        EntityItem entityitem = new EntityItem(hook.worldObj, hook.posX, hook.posY, hook.posZ, lootList[loot]);
                        double d5 = hook.field_146042_b.posX - hook.posX;
                        double d6 = hook.field_146042_b.posY - hook.posY;
                        double d7 = hook.field_146042_b.posZ - hook.posZ;
                        double d8 = MathHelper.sqrt_double((double)(d5 * d5 + d6 * d6 + d7 * d7));
                        double d9 = 0.1;
                        entityitem.motionX = d5 * d9;
                        entityitem.motionY = d6 * d9 + (double)MathHelper.sqrt_double((double)d8) * 0.08;
                        entityitem.motionZ = d7 * d9;
                        hook.worldObj.spawnEntityInWorld((Entity)entityitem);
                        hook.field_146042_b.worldObj.spawnEntityInWorld((Entity)new EntityXPOrb(hook.field_146042_b.worldObj, hook.field_146042_b.posX, hook.field_146042_b.posY + 0.5, hook.field_146042_b.posZ + 0.5, event.entityPlayer.getRNG().nextInt(6) + 1));
                    }
                }
            } else if (event.action == PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK && noPlaceDuplicate && (itemStack = event.entityPlayer.inventory.getCurrentItem()) != null && itemStack.hasTagCompound() && itemStack.getTagCompound().hasKey("NoPlacing")) {
                event.useItem = Event.Result.DENY;
            }
        }
    }

    @SubscribeEvent
    public void onHarvest(BlockEvent.HarvestDropsEvent event) {
        if (event.harvester != null && !event.world.isRemote) {
            Random random = event.harvester.getRNG();
            if (event.block instanceof BlockOre || event.block instanceof BlockRedstoneOre || ores.contains(event.block)) {
                int skill = PlayerEventHandler.getSkill(event.harvester, 0);
                if (!blockToCounter.containsKey(event.block)) {
                    blockToCounter.put(event.block, blockToCounter.size());
                }
                if (!event.isSilkTouching) {
                    LevelUp.incrementOreCounter(event.harvester, blockToCounter.get(event.block));
                }
                if (random.nextDouble() <= (double)skill / 200.0) {
                    int qutity;
                    Item ID;
                    boolean foundBlock = false;
                    for (ItemStack stack : event.drops) {
                        if (stack == null || event.block != Block.getBlockFromItem((Item)stack.getItem())) continue;
                        this.writeNoPlacing(stack);
                        ++stack.stackSize;
                        foundBlock = true;
                        break;
                    }
                    if (!foundBlock && (ID = event.block.getItemDropped(event.blockMetadata, random, 0)) != null && (qutity = event.block.quantityDropped(event.blockMetadata, 0, random)) > 0) {
                        event.drops.add(new ItemStack(ID, qutity, event.block.damageDropped(event.blockMetadata)));
                    }
                }
            } else if (event.block instanceof BlockLog) {
                int skill = PlayerEventHandler.getSkill(event.harvester, 3);
                if (random.nextDouble() <= (double)skill / 150.0) {
                    ItemStack planks = null;
                    for (ItemStack stack : event.drops) {
                        if (stack == null || event.block != Block.getBlockFromItem((Item)stack.getItem())) continue;
                        planks = this.getPlanks(event.harvester, event.block, event.blockMetadata, stack.copy());
                        break;
                    }
                    if (planks != null) {
                        event.drops.add(planks);
                    }
                }
                if (random.nextDouble() <= (double)skill / 150.0) {
                    event.drops.add(new ItemStack(Items.stick, 2));
                }
            } else if (event.block.getMaterial() == Material.ground) {
                int skill = PlayerEventHandler.getSkill(event.harvester, 11);
                if (random.nextFloat() <= (float)skill / 200.0f) {
                    ItemStack[] aitemstack4 = digLoot;
                    float f = random.nextFloat();
                    if (f <= 0.002f) {
                        aitemstack4 = digLoot3;
                    } else if (f <= 0.1f) {
                        aitemstack4 = digLoot2;
                    } else if (f <= 0.4f) {
                        aitemstack4 = digLoot1;
                    }
                    this.removeFromList(event.drops, event.block);
                    ItemStack itemstack = aitemstack4[random.nextInt(aitemstack4.length)];
                    int size = itemstack.stackSize;
                    ItemStack toDrop = itemstack.copy();
                    toDrop.stackSize = 1;
                    if (toDrop.getMaxDamage() > 20) {
                        toDrop.setItemDamage(random.nextInt(80) + 20);
                    } else {
                        for (int i1 = 0; i1 < size - 1; ++i1) {
                            if (!(random.nextFloat() < 0.5f)) continue;
                            event.drops.add(toDrop.copy());
                        }
                    }
                    event.drops.add(toDrop);
                }
            } else if (event.block instanceof BlockGravel) {
                int skill = PlayerEventHandler.getSkill(event.harvester, 11);
                if (random.nextInt(10) < skill / 5) {
                    this.removeFromList(event.drops, event.block);
                    event.drops.add(new ItemStack(Items.flint));
                }
            }
        }
    }

    private void removeFromList(ArrayList<ItemStack> drops, Block block) {
        Iterator<ItemStack> itr = drops.iterator();
        while (itr.hasNext()) {
            ItemStack drop = itr.next();
            if (drop == null || block != Block.getBlockFromItem((Item)drop.getItem())) continue;
            itr.remove();
        }
    }

    private void writeNoPlacing(ItemStack toDrop) {
        if (!noPlaceDuplicate) {
            return;
        }
        NBTTagCompound tagCompound = toDrop.getTagCompound();
        if (tagCompound == null) {
            tagCompound = new NBTTagCompound();
        }
        tagCompound.setBoolean("NoPlacing", true);
        toDrop.setTagCompound(tagCompound);
    }

    private ItemStack getPlanks(EntityPlayer player, Block block, int meta, ItemStack drop) {
        if (block != Blocks.log) {
            InventoryCrafting craft = new ContainerPlayer((InventoryPlayer)player.inventory, (boolean)(!player.worldObj.isRemote ? true : false), (EntityPlayer)player).craftMatrix;
            craft.setInventorySlotContents(1, drop);
            ItemStack planks = CraftingManager.getInstance().findMatchingRecipe(craft, player.worldObj);
            if (planks != null) {
                planks.stackSize = 2;
                return planks;
            }
        }
        return new ItemStack(Blocks.planks, 2, meta & 3);
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public void onBlockBroken(BlockEvent.BreakEvent event) {
        if (!event.world.isRemote && event.getPlayer() != null) {
            if (event.block instanceof BlockCrops || event.block instanceof BlockStem) {
                if (!((IGrowable)event.block).func_149851_a(event.world, event.x, event.y, event.z, false)) {
                    this.doCropDrops(event);
                }
            } else if (event.block instanceof BlockMelon) {
                this.doCropDrops(event);
            }
        }
    }

    private void doCropDrops(BlockEvent.BreakEvent event) {
        Random random = event.getPlayer().getRNG();
        int skill = PlayerEventHandler.getSkill(event.getPlayer(), 9);
        if (random.nextInt(10) < skill / 5) {
            Item ID = event.block.getItemDropped(event.blockMetadata, random, 0);
            if (ID == null) {
                if (event.block == Blocks.pumpkin_stem) {
                    ID = Items.pumpkin_seeds;
                } else if (event.block == Blocks.melon_stem) {
                    ID = Items.melon_seeds;
                }
            }
            if (ID != null) {
                event.world.spawnEntityInWorld((Entity)new EntityItem(event.world, (double)event.x, (double)event.y, (double)event.z, new ItemStack(ID, 1, event.block.damageDropped(event.blockMetadata))));
            }
        }
    }

    @SubscribeEvent
    public void onPlayerConstruction(EntityEvent.EntityConstructing event) {
        IExtendedEntityProperties skills;
        if (event.entity instanceof EntityPlayer && (skills = event.entity.getExtendedProperties("LevelUpSkills")) == null) {
            skills = new PlayerExtendedProperties();
            event.entity.registerExtendedProperties("LevelUpSkills", skills);
        }
    }

    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.wasDeath || !resetClassOnDeath || resetSkillOnDeath < 1.0f) {
            NBTTagCompound data = new NBTTagCompound();
            PlayerExtendedProperties.from(event.original).saveNBTData(data);
            PlayerExtendedProperties.from(event.entityPlayer).loadNBTData(data);
        }
    }

    @SubscribeEvent
    public void onOreRegister(OreDictionary.OreRegisterEvent event) {
        Block ore;
        if (event.Name.startsWith("ore") && event.Ore != null && event.Ore.getItem() != null && (ore = Block.getBlockFromItem((Item)event.Ore.getItem())) != Blocks.air && !(ore instanceof BlockOre) && !(ore instanceof BlockRedstoneOre)) {
            ores.add(ore);
        }
    }

    public static int getFishingLoot(EntityPlayer player) {
        if (player.getRNG().nextDouble() > (double)(PlayerEventHandler.getSkill(player, 10) / 5) * 0.05) {
            return -1;
        }
        return player.getRNG().nextInt(lootList.length);
    }

    public static int getSkill(EntityPlayer player, int id) {
        return PlayerExtendedProperties.getSkillFromIndex(player, id);
    }

    static {
        blockToCounter.put(Blocks.coal_ore, 0);
        blockToCounter.put(Blocks.lapis_ore, 1);
        blockToCounter.put(Blocks.redstone_ore, 2);
        blockToCounter.put(Blocks.iron_ore, 3);
        blockToCounter.put(Blocks.gold_ore, 4);
        blockToCounter.put(Blocks.emerald_ore, 5);
        blockToCounter.put(Blocks.diamond_ore, 6);
        blockToCounter.put(Blocks.quartz_ore, 7);
        digLoot = new ItemStack[]{new ItemStack(Items.clay_ball, 8), new ItemStack(Items.bowl, 2), new ItemStack(Items.coal, 4), new ItemStack(Items.painting), new ItemStack(Items.stick, 4), new ItemStack(Items.string, 2)};
        digLoot1 = new ItemStack[]{new ItemStack(Items.stone_sword), new ItemStack(Items.stone_shovel), new ItemStack(Items.stone_pickaxe), new ItemStack(Items.stone_axe)};
        digLoot2 = new ItemStack[]{new ItemStack(Items.slime_ball, 2), new ItemStack(Items.redstone, 8), new ItemStack(Items.iron_ingot), new ItemStack(Items.gold_ingot)};
        digLoot3 = new ItemStack[]{new ItemStack(Items.diamond)};
        ores = Sets.newIdentityHashSet();
    }
}

