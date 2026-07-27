/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemCraftedEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$ItemSmeltedEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerChangedDimensionEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerLoggedInEvent
 *  cpw.mods.fml.common.gameevent.PlayerEvent$PlayerRespawnEvent
 *  cpw.mods.fml.common.gameevent.TickEvent$Phase
 *  cpw.mods.fml.common.gameevent.TickEvent$PlayerTickEvent
 *  cpw.mods.fml.common.registry.GameData
 *  cpw.mods.fml.relauncher.Side
 *  net.minecraft.block.Block
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.player.EntityPlayerMP
 *  net.minecraft.init.Items
 *  net.minecraft.inventory.ContainerFurnace
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.ItemDye
 *  net.minecraft.item.ItemHoe
 *  net.minecraft.item.ItemStack
 *  net.minecraft.tileentity.TileEntityFurnace
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.ForgeHooks
 *  net.minecraftforge.common.IPlantable
 *  net.minecraftforge.common.util.ForgeDirection
 */
package assets.levelup;

import assets.levelup.ClassBonus;
import assets.levelup.LevelUp;
import assets.levelup.PlayerEventHandler;
import assets.levelup.PlayerExtendedProperties;
import assets.levelup.SkillPacketHandler;
import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import cpw.mods.fml.common.gameevent.TickEvent;
import cpw.mods.fml.common.registry.GameData;
import cpw.mods.fml.relauncher.Side;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ContainerFurnace;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityFurnace;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.util.ForgeDirection;

public final class FMLEventHandler {
    private static final UUID speedID = UUID.fromString("4f7637c8-6106-4050-96cb-e47f83bfa415");
    private static final UUID sneakID = UUID.fromString("a4dc0b04-f78a-43f6-8805-5ebfbab10b18");
    private static final int maxFurnaceCookTime = 200;
    public static final FMLEventHandler INSTANCE = new FMLEventHandler();
    private List<IPlantable> blackListedCrops;

    private FMLEventHandler() {
    }

    @SubscribeEvent
    public void onPlayerUpdate(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            int skill;
            double diff;
            int time;
            int bonus;
            ItemStack stack;
            TileEntityFurnace furnace;
            EntityPlayer player = event.player;
            if (!player.worldObj.isRemote
                    && player.openContainer instanceof ContainerFurnace
                    && (furnace = ((ContainerFurnace)player.openContainer).tileFurnace) != null
                    && furnace.isBurning()
                    && furnace.canSmelt()
                    && (stack = furnace.getStackInSlot(0)) != null) {
                boolean cooking = stack.getItem().getItemUseAction(stack) == EnumAction.eat;
                bonus = FMLEventHandler.getSkill(player, cooking ? 7 : 4);
                int speedRange = LevelUpTuning.steps(
                        bonus,
                        cooking ? ModConfig.levelUpCookingSpeedPointsPerStep : ModConfig.levelUpSmeltingSpeedPointsPerStep
                ) * (cooking ? ModConfig.levelUpCookingSpeedExtraTicksPerStep : ModConfig.levelUpSmeltingSpeedExtraTicksPerStep);
                if (speedRange > 1
                        && (time = player.getRNG().nextInt(speedRange)) != 0
                        && furnace.furnaceCookTime + time < 200) {
                    furnace.furnaceCookTime += time;
                }
            }
            PlayerExtendedProperties properties = PlayerExtendedProperties.from(player);
            boolean hasClass = PlayerExtendedProperties.getPlayerClass(player) != 0;
            if ((hasClass || ModConfig.levelUpEarnSkillPointsBeforeClassChoice) && (diff = PlayerEventHandler.xpPerLevel * (double)(player.experienceLevel - 4) + (double)(hasClass ? ClassBonus.getTotalBonus(PlayerExtendedProperties.getPlayerClass(player)) : 0) - (double)properties.getSkillPoints()) >= 1.0) {
                properties.addToSkill("XP", (int)Math.floor(diff));
            }
            if (!player.worldObj.isRemote
                    && player.getCurrentEquippedItem() != null
                    && player.getCurrentEquippedItem().getItem() instanceof ItemHoe
                    && (skill = FMLEventHandler.getSkill(player, 9)) != 0
                    && LevelUpTuning.rollPerPoint(player.getRNG(), skill, ModConfig.levelUpFarmingGrowthChancePerPointPercent)) {
                this.growCropsAround(
                        player.worldObj,
                        skill / Math.max(1, ModConfig.levelUpFarmingGrowthRangePointsPerBlock),
                        player
                );
            }
            IAttributeInstance atinst = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
            skill = FMLEventHandler.getSkill(player, 6);
            if (skill != 0) {
                AttributeModifier mod = new AttributeModifier(
                        speedID,
                        "SprintingSkillSpeed",
                        (double)((float)skill * ModConfig.levelUpAthleticsSprintSpeedPercentPerPoint / 100.0F),
                        2
                );
                if (player.isSprinting()) {
                    if (atinst.getModifier(speedID) == null) {
                        atinst.applyModifier(mod);
                    }
                } else if (atinst.getModifier(speedID) != null) {
                    atinst.removeModifier(mod);
                }
                if (player.fallDistance > 0.0f) {
                    float reduction = (float)LevelUpTuning.steps(
                            skill,
                            ModConfig.levelUpAthleticsFallReductionPointsPerStep
                    ) * ModConfig.levelUpAthleticsFallReductionPercentPerStep / 100.0F;
                    player.fallDistance *= Math.max(0.0F, 1.0F - reduction);
                }
            }
            if ((skill = FMLEventHandler.getSkill(player, 8)) != 0) {
                AttributeModifier mod = new AttributeModifier(
                        sneakID,
                        "SneakingSkillSpeed",
                        (double)((float)skill * ModConfig.levelUpSneakingSpeedPercentPerPoint / 100.0F),
                        2
                );
                if (player.isSneaking()) {
                    if (atinst.getModifier(sneakID) == null) {
                        atinst.applyModifier(mod);
                    }
                } else if (atinst.getModifier(sneakID) != null) {
                    atinst.removeModifier(mod);
                }
            }
        }
    }

    private void growCropsAround(World world, int range, EntityPlayer player) {
        int posX = (int)player.posX;
        int posY = (int)player.posY;
        int posZ = (int)player.posZ;
        int dist = range / 2 + 2;
        for (int x = posX - dist; x < posX + dist + 1; ++x) {
            block1: for (int z = posZ - dist; z < posZ + dist + 1; ++z) {
                for (int y = posY - dist; y < posY + dist + 1; ++y) {
                    Block soil;
                    if (!world.isAirBlock(x, y + 1, z)) continue;
                    Block block = world.getBlock(x, y, z);
                    if (!(block instanceof IPlantable) || this.blackListedCrops.contains(block) || (soil = world.getBlock(x, y - 1, z)).isAir((IBlockAccess)world, x, y - 1, z) || !soil.canSustainPlant((IBlockAccess)world, x, y - 1, z, ForgeDirection.UP, (IPlantable)block)) continue block1;
                    ItemDye.applyBonemeal((ItemStack)new ItemStack(Items.dye, 1, 15), (World)world, (int)x, (int)y, (int)z, (EntityPlayer)player);
                    continue block1;
                }
            }
        }
    }

    public void addCropsToBlackList(List<String> blackList) {
        if (this.blackListedCrops == null) {
            this.blackListedCrops = new ArrayList<IPlantable>(blackList.size());
        }
        for (String txt : blackList) {
            Object crop = GameData.getBlockRegistry().getObject(txt);
            if (!(crop instanceof IPlantable)) continue;
            this.blackListedCrops.add((IPlantable)crop);
        }
    }

    public static int getSkill(EntityPlayer player, int id) {
        return PlayerExtendedProperties.getSkillFromIndex(player, id);
    }

    @SubscribeEvent
    public void onSmelting(PlayerEvent.ItemSmeltedEvent event) {
        if (!event.player.worldObj.isRemote) {
            EntityItem entityitem;
            Random random = event.player.getRNG();
            ItemStack add = null;
            if (event.smelting.getItemUseAction() == EnumAction.eat) {
                if (ModConfig.levelUpCookingBonusYieldExtraCopies > 0
                        && LevelUpTuning.rollPerPoint(
                                random,
                                FMLEventHandler.getSkill(event.player, 7),
                                ModConfig.levelUpCookingBonusYieldChancePerPointPercent
                        )) {
                    add = event.smelting.copy();
                    add.stackSize *= ModConfig.levelUpCookingBonusYieldExtraCopies;
                }
            } else if (ModConfig.levelUpSmeltingBonusYieldExtraCopies > 0
                    && LevelUpTuning.rollPerPoint(
                            random,
                            FMLEventHandler.getSkill(event.player, 4),
                            ModConfig.levelUpSmeltingBonusYieldChancePerPointPercent
                    )) {
                add = event.smelting.copy();
                add.stackSize *= ModConfig.levelUpSmeltingBonusYieldExtraCopies;
            }
            if (add != null && (entityitem = ForgeHooks.onPlayerTossEvent((EntityPlayer)event.player, (ItemStack)add, (boolean)true)) != null) {
                entityitem.delayBeforeCanPickup = 0;
                entityitem.func_145797_a(event.player.getCommandSenderName());
            }
        }
    }

    @SubscribeEvent
    public void onCrafting(PlayerEvent.ItemCraftedEvent event) {
        LevelUp.takenFromCrafting(event.player, event.crafting, event.craftMatrix);
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        this.loadPlayer(event.player);
    }

    @SubscribeEvent
    public void onPlayerRespawn(PlayerEvent.PlayerRespawnEvent event) {
        this.loadPlayer(event.player);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.player instanceof EntityPlayerMP) {
            this.loadPlayer(event.player);
            LevelUp.configChannel.sendTo(SkillPacketHandler.getConfigPacket(LevelUp.instance.getServerProperties()), (EntityPlayerMP)event.player);
        }
    }

    public void loadPlayer(EntityPlayer player) {
        if (player instanceof EntityPlayerMP) {
            byte cl = PlayerExtendedProperties.getPlayerClass(player);
            int[] data = PlayerExtendedProperties.from(player).getPlayerData(false);
            LevelUp.initChannel.sendTo(SkillPacketHandler.getPacket(Side.CLIENT, 0, cl, data), (EntityPlayerMP)player);
        }
    }
}
