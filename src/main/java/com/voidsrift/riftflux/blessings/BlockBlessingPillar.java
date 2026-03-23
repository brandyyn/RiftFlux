package com.voidsrift.riftflux.blessings;

import com.voidsrift.riftflux.ModConfig;
import com.voidsrift.riftflux.net.MsgSyncBlessing;
import com.voidsrift.riftflux.net.RFNetwork;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.EnumChatFormatting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.world.EnumSkyBlock;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockBlessingPillar extends BlockContainer {
    public BlockBlessingPillar() {
        super(Material.rock);
        setBlockName("riftflux.blessing_pillar");
        setBlockTextureName("riftflux:blessing_pillar");
        setHardness(50.0F);
        setResistance(2000.0F);
        setLightOpacity(0);
        setLightLevel(getActiveLightLevel() / 15.0F);
        setCreativeTab(CreativeTabs.tabBlock);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int meta) {
        return new TileEntityBlessingPillar();
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase placer,
                                ItemStack stack) {
        super.onBlockPlacedBy(world, x, y, z, placer, stack);
        if (!world.isRemote) {
            TileEntity te = world.getTileEntity(x, y, z);
            if (te instanceof TileEntityBlessingPillar) {
                ((TileEntityBlessingPillar) te).getBlessing();
                world.markBlockForUpdate(x, y, z);
            }
        }
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return metadata % 2 == 0;
    }

    @Override
    public int quantityDropped(Random rand) {
        return 0;
    }

    @Override
    public Item getItemDropped(int meta, Random rand, int fortune) {
        return null;
    }

    @Override
    public int getMobilityFlag() {
        return 2;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public boolean getUseNeighborBrightness() {
        return false;
    }

    @Override
    public int getRenderType() {
        if (BlessingRenderIds.blessingPillarRenderId >= 0) {
            return BlessingRenderIds.blessingPillarRenderId;
        }
        return -1;
    }

    @Override
    public boolean shouldSideBeRendered(IBlockAccess world, int x, int y, int z, int side) {
        return false;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        return false;
    }

    @Override
    public boolean canPlaceBlockAt(World world, int x, int y, int z) {
        return y < 255 && super.canPlaceBlockAt(world, x, y, z) && world.isAirBlock(x, y + 1, z);
    }

    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z) {
        return getActiveLightLevel();
    }

    @Override
    public int getLightValue() {
        return getActiveLightLevel();
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess world, int x, int y, int z) {
        float inset = 2.0f / 16.0f;
        float yTrimTop = 2.0f / 16.0f;
        int meta = world.getBlockMetadata(x, y, z);
        if (meta % 2 == 0) {
            setBlockBounds(inset, 0.0F, inset, 1.0F - inset, 2.0F - yTrimTop, 1.0F - inset);
        } else {
            setBlockBounds(inset, -1.0F, inset, 1.0F - inset, 1.0F - yTrimTop, 1.0F - inset);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta % 2 == 1) {
            return null; // collision handled by base block
        }
        float inset = 2.0f / 16.0f;
        float yTrimTop = 2.0f / 16.0f;
        return AxisAlignedBB.getBoundingBox(
                x + inset,
                y,
                z + inset,
                x + 1 - inset,
                y + 2 - yTrimTop,
                z + 1 - inset
        );
    }

    @Override
    public void onBlockAdded(World world, int x, int y, int z) {
        super.onBlockAdded(world, x, y, z);
        if (world.isRemote) {
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if (meta % 2 != 0) {
            return;
        }
        if (world.provider != null) {
            BlessingPillarData.clearBroken(world, x, y, z, world.provider.dimensionId);
        }
        if (y >= 255 || !world.isAirBlock(x, y + 1, z)) {
            world.setBlockToAir(x, y, z);
            return;
        }
        int topMeta = meta >= 2 ? 3 : 1;
        world.setBlock(x, y + 1, z, this, topMeta, 2);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(World world, int x, int y, int z, Random rand) {
        int meta = world.getBlockMetadata(x, y, z);
        if (meta % 2 == 1) {
            return;
        }
        if (meta < 2) {
            return;
        }
        for (int i = 0; i < 2; i++) {
            world.spawnParticle(
                    "portal",
                    x + rand.nextDouble(),
                    y + rand.nextDouble() * 2.0,
                    z + rand.nextDouble(),
                    (rand.nextDouble() - 0.5) * 2.0,
                    -rand.nextDouble(),
                    (rand.nextDouble() - 0.5) * 2.0
            );
        }
    }

    @Override
    public void onNeighborBlockChange(World world, int x, int y, int z, Block neighbor) {
        super.onNeighborBlockChange(world, x, y, z, neighbor);
        if (world.isRemote) {
            return;
        }
        int meta = world.getBlockMetadata(x, y, z);
        if (meta % 2 == 0) {
            if (world.getBlock(x, y + 1, z) != this) {
                world.setBlockToAir(x, y, z);
            }
        } else if (world.getBlock(x, y - 1, z) != this) {
            world.setBlockToAir(x, y, z);
        }
    }

    @Override
    public void onBlockHarvested(World world, int x, int y, int z, int meta, EntityPlayer player) {
        if (!world.isRemote) {
            if (meta % 2 == 0) {
                if (world.getBlock(x, y + 1, z) == this) {
                    world.setBlockToAir(x, y + 1, z);
                }
            } else {
                if (world.getBlock(x, y - 1, z) == this) {
                    world.setBlockToAir(x, y - 1, z);
                }
            }
        }
        super.onBlockHarvested(world, x, y, z, meta, player);
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block block, int meta) {
        int baseX = x;
        int baseY = y;
        int baseZ = z;
        if (meta % 2 == 1) {
            baseY = y - 1;
        }
        if (!world.isRemote && ModConfig.loseBlessingOnArtifactBreak) {
            int dim = world.provider != null ? world.provider.dimensionId : 0;
            BlessingPillarData.markBroken(world, baseX, baseY, baseZ, dim);
            List<?> players = null;
            MinecraftServer server = MinecraftServer.getServer();
            if (server != null && server.getConfigurationManager() != null) {
                players = server.getConfigurationManager().playerEntityList;
            }
            if (players == null) {
                players = world.playerEntities;
            }
            for (Object obj : players) {
                if (!(obj instanceof EntityPlayer)) {
                    continue;
                }
                EntityPlayer player = (EntityPlayer) obj;
                if (BlessingHelper.isBlessingSource(player, baseX, baseY, baseZ, dim)) {
                    BlessingHelper.clearBlessing(player);
                    BlessingHelper.clearBlessingSource(player);
                    BlessingHelper.resetBlessingState(player);
                    BlessingLossNotifier.clear(player);
                    BlessingLossNotifier.sendNow(player);
                    if (player instanceof EntityPlayerMP && RFNetwork.CH != null) {
                        RFNetwork.CH.sendTo(new MsgSyncBlessing(player), (EntityPlayerMP) player);
                    }
                }
            }
        }
        if (!world.isRemote) {
            updateSourceLight(world, baseX, baseY, baseZ);
        }
        super.breakBlock(world, x, y, z, block, meta);
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player,
                                    int side, float hitX, float hitY, float hitZ) {
        if (world.getBlockMetadata(x, y, z) % 2 == 1) {
            y -= 1;
        }
        if (!ModConfig.blessingsEnabled) {
            return true;
        }
        if (world.isRemote || player == null) {
            return true;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityBlessingPillar)) {
            return true;
        }
        TileEntityBlessingPillar pillar = (TileEntityBlessingPillar) te;
        String blessing = pillar.getBlessing();
        if (blessing == null) {
            return true;
        }
        if (!BlessingHelper.isBlessingEnabled(blessing)) {
            pillar.setBlessing(BlessingHelper.getRandomBlessing(world.rand, true));
            blessing = pillar.getBlessing();
            if (blessing == null) {
                return true;
            }
        }
        if (ModConfig.artifactExclusiveActivation
                && isPillarInUseByOther(world, x, y, z, world.provider.dimensionId, player)) {
            sendYellow(player, "blessing.riftflux.artifact.taken");
            return true;
        }
        if (!ModConfig.artifactActivationAroundMonsters && hasNearbyMonsters(world, x, y, z)) {
            sendYellow(player, "blessing.riftflux.artifact.monsters");
            return true;
        }
        int oldX = 0;
        int oldY = 0;
        int oldZ = 0;
        int oldDim = 0;
        boolean hadOldSource = BlessingHelper.hasBlessingSource(player);
        if (hadOldSource) {
            oldX = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_X);
            oldY = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Y);
            oldZ = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_Z);
            oldDim = player.getEntityData().getInteger(BlessingHelper.NBT_BLESSING_PILLAR_DIM);
        }
        BlessingHelper.setBlessing(player, blessing);
        BlessingHelper.resetBlessingState(player);
        BlessingHelper.setBlessingSource(player, x, y, z, world.provider.dimensionId);
        BlessingLossNotifier.clear(player);
        world.playSoundEffect(
                x + 0.5D,
                y + 1.0D,
                z + 0.5D,
                "random.levelup",
                1.0F,
                1.0F
        );
        sendYellow(player, "blessing.riftflux.granted");
        String title = BlessingHelper.getLocalizedTitle(blessing);
        String desc = BlessingHelper.getDescription(blessing);
        if (desc != null && !desc.isEmpty()) {
            sendYellow(player, "blessing.riftflux.granted.with_desc", title, desc);
        } else {
            sendYellow(player, "blessing.riftflux.granted.without_desc", title);
        }
        if (player instanceof EntityPlayerMP && RFNetwork.CH != null) {
            RFNetwork.CH.sendTo(new MsgSyncBlessing(player), (EntityPlayerMP) player);
        }
        updatePillarActive(world, x, y, z, world.provider.dimensionId);
        if (hadOldSource) {
            World oldWorld = world;
            if (oldDim != world.provider.dimensionId) {
                MinecraftServer server = MinecraftServer.getServer();
                if (server != null) {
                    oldWorld = server.worldServerForDimension(oldDim);
                }
            }
            if (oldWorld != null) {
                updatePillarActive(oldWorld, oldX, oldY, oldZ, oldDim);
            }
        }
        return true;
    }

    private static void sendYellow(EntityPlayer player, String key, Object... args) {
        ChatComponentTranslation message = new ChatComponentTranslation(key, args);
        message.getChatStyle().setColor(EnumChatFormatting.YELLOW);
        player.addChatComponentMessage(message);
    }

    private boolean isPillarInUseByOther(World world, int x, int y, int z, int dim, EntityPlayer player) {
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null && server.getConfigurationManager() != null) {
            for (Object obj : server.getConfigurationManager().playerEntityList) {
                if (!(obj instanceof EntityPlayer)) {
                    continue;
                }
                EntityPlayer other = (EntityPlayer) obj;
                if (other == player) {
                    continue;
                }
                if (BlessingHelper.isBlessingSource(other, x, y, z, dim)) {
                    return true;
                }
            }
            return false;
        }
        for (Object obj : world.playerEntities) {
            if (!(obj instanceof EntityPlayer)) {
                continue;
            }
            EntityPlayer other = (EntityPlayer) obj;
            if (other == player) {
                continue;
            }
            if (BlessingHelper.isBlessingSource(other, x, y, z, dim)) {
                return true;
            }
        }
        return false;
    }

    static void updateSourceLight(World world, int x, int y, int z) {
        if (world == null) {
            return;
        }
        int level = getActiveLightLevel();
        world.setLightValue(EnumSkyBlock.Block, x, y, z, level);
        if (world.getBlock(x, y + 1, z) == BlessingContent.blessingPillar) {
            world.setLightValue(EnumSkyBlock.Block, x, y + 1, z, level);
        }
        world.func_147451_t(x, y, z);
        world.func_147451_t(x, y + 1, z);
        world.updateLightByType(EnumSkyBlock.Block, x, y, z);
        world.updateLightByType(EnumSkyBlock.Block, x, y + 1, z);
        world.markBlocksDirtyVertical(x, z, y, y + 1);
        world.markBlockForUpdate(x, y, z);
        world.markBlockForUpdate(x, y + 1, z);
        world.markBlockRangeForRenderUpdate(x - 1, y, z - 1, x + 1, y + 1, z + 1);
    }

    private static int getActiveLightLevel() {
        return Math.max(0, Math.min(15, ModConfig.artifactActiveLightLevel));
    }

    public static void updatePillarActive(World world, int x, int y, int z, int dim) {
        updatePillarActive(world, x, y, z, dim, null);
    }

    public static void updatePillarActive(World world, int x, int y, int z, int dim, EntityPlayer ignore) {
        if (world == null) {
            return;
        }
        TileEntity te = world.getTileEntity(x, y, z);
        if (!(te instanceof TileEntityBlessingPillar)) {
            return;
        }
        boolean active = false;
        MinecraftServer server = MinecraftServer.getServer();
        if (server != null && server.getConfigurationManager() != null) {
            for (Object obj : server.getConfigurationManager().playerEntityList) {
                if (!(obj instanceof EntityPlayer)) {
                    continue;
                }
                EntityPlayer player = (EntityPlayer) obj;
                if (player == ignore) {
                    continue;
                }
                if (BlessingHelper.isBlessingSource(player, x, y, z, dim)) {
                    active = true;
                    break;
                }
            }
        } else {
            for (Object obj : world.playerEntities) {
                if (!(obj instanceof EntityPlayer)) {
                    continue;
                }
                EntityPlayer player = (EntityPlayer) obj;
                if (player == ignore) {
                    continue;
                }
                if (BlessingHelper.isBlessingSource(player, x, y, z, dim)) {
                    active = true;
                    break;
                }
            }
        }
        ((TileEntityBlessingPillar) te).setActive(active);
        int baseMeta = active ? 2 : 0;
        int topMeta = active ? 3 : 1;
        if (world.getBlock(x, y, z) == BlessingContent.blessingPillar) {
            world.setBlockMetadataWithNotify(x, y, z, baseMeta, 3);
        }
        if (world.getBlock(x, y + 1, z) == BlessingContent.blessingPillar) {
            world.setBlockMetadataWithNotify(x, y + 1, z, topMeta, 3);
        } else if (world.isAirBlock(x, y + 1, z)) {
            world.setBlock(x, y + 1, z, BlessingContent.blessingPillar, topMeta, 3);
        }
        updateSourceLight(world, x, y, z);
        world.addBlockEvent(x, y, z, BlessingContent.blessingPillar, 1, active ? 1 : 0);
    }

    @Override
    public boolean onBlockEventReceived(World world, int x, int y, int z, int eventId, int eventParam) {
        if (eventId == 1) {
            int baseX = x;
            int baseY = y;
            int baseZ = z;
            int meta = world.getBlockMetadata(x, y, z);
            if (meta % 2 == 1) {
                baseY -= 1;
            }
            boolean active = eventParam > 0;
            TileEntity te = world.getTileEntity(baseX, baseY, baseZ);
            if (te instanceof TileEntityBlessingPillar) {
                ((TileEntityBlessingPillar) te).setActive(active);
            }
            int baseMeta = active ? 2 : 0;
            int topMeta = active ? 3 : 1;
            if (world.getBlock(baseX, baseY, baseZ) == this && world.getBlockMetadata(baseX, baseY, baseZ) != baseMeta) {
                world.setBlockMetadataWithNotify(baseX, baseY, baseZ, baseMeta, 2);
            }
            if (world.getBlock(baseX, baseY + 1, baseZ) == this && world.getBlockMetadata(baseX, baseY + 1, baseZ) != topMeta) {
                world.setBlockMetadataWithNotify(baseX, baseY + 1, baseZ, topMeta, 2);
            } else if (world.isAirBlock(baseX, baseY + 1, baseZ)) {
                world.setBlock(baseX, baseY + 1, baseZ, this, topMeta, 2);
            }
            updateSourceLight(world, baseX, baseY, baseZ);
            return true;
        }
        return super.onBlockEventReceived(world, x, y, z, eventId, eventParam);
    }

    private boolean hasNearbyMonsters(World world, int x, int y, int z) {
        AxisAlignedBB box = AxisAlignedBB.getBoundingBox(
                x - 14, y - 4, z - 14,
                x + 15, y + 5, z + 15
        );
        List<?> list = world.getEntitiesWithinAABB(EntityMob.class, box);
        return list != null && !list.isEmpty();
    }
}
