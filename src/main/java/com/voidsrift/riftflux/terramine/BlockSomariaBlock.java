package com.voidsrift.riftflux.terramine;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;

import java.util.Set;

public class BlockSomariaBlock extends BlockIceRodIce {
    private static final Block.SoundType SILENT_BLOCK_SOUND = new Block.SoundType("stone", 0.0F, 1.0F) {
        @Override
        public String getBreakSound() {
            return "dig.stone";
        }

        @Override
        public String getStepResourcePath() {
            return "step.stone";
        }

        @Override
        public String func_150496_b() {
            return "dig.stone";
        }
    };

    public BlockSomariaBlock() {
        super("somaria_block", "somaria_block");
        this.setStepSound(SILENT_BLOCK_SOUND);
        this.setLightOpacity(255);
        this.slipperiness = 0.6F;
    }

    @Override
    protected Block getDropBlock() {
        return TerrariaContent.somariaBlock;
    }

    @Override
    protected boolean requiresSilkTouchToDrop() {
        return ModConfig.somariaBlockRequireSilkTouch;
    }

    @Override
    public boolean canHarvestBlock(EntityPlayer player, int meta) {
        if (!ModConfig.somariaBlockRequirePickaxeToDrop) {
            return true;
        }
        if (player == null) {
            return false;
        }

        ItemStack equipped = player.getCurrentEquippedItem();
        if (equipped == null || equipped.getItem() == null) {
            return false;
        }

        Set<String> toolClasses = equipped.getItem().getToolClasses(equipped);
        return toolClasses != null && toolClasses.contains("pickaxe");
    }

    @Override
    protected Block getExpireEffectBlock() {
        return TerrariaContent.somariaBlock;
    }

    @Override
    protected String getBreakSound() {
        return "riftflux:somaria_break";
    }

    @Override
    protected int getLifetimeTicks() {
        return TerrariaContent.getSomariaBlockLifetimeTicks();
    }

    @Override
    public boolean isOpaqueCube() {
        return true;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return true;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public int getRenderBlockPass() {
        return 0;
    }
}
