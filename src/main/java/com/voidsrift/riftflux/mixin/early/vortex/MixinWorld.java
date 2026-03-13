package com.voidsrift.riftflux.mixin.early.vortex;

import java.util.concurrent.Callable;
import net.minecraft.block.Block;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.util.ReportedException;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin({World.class})
public abstract class MixinWorld {
   @Overwrite
   public void notifyBlockOfNeighborChange(int p_147460_1_, int p_147460_2_, int p_147460_3_, final Block p_147460_4_) {
      World world = (World)(Object)this;
      if (!world.isRemote) {
         Block block = world.getBlock(p_147460_1_, p_147460_2_, p_147460_3_);

         try {
            block.onNeighborBlockChange(world, p_147460_1_, p_147460_2_, p_147460_3_, p_147460_4_);
         } catch (Throwable var13) {
            CrashReport crashreport = CrashReport.makeCrashReport(var13, "Exception while updating neighbours");
            CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being updated");

            int l;
            try {
               l = world.getBlockMetadata(p_147460_1_, p_147460_2_, p_147460_3_);
            } catch (Throwable var12) {
               l = -1;
            }

            crashreportcategory.addCrashSectionCallable("Source block type", new Callable() {
               public String call() {
                  try {
                     return String.format("ID #%d (%s // %s)", Block.getIdFromBlock(p_147460_4_), p_147460_4_.getUnlocalizedName(), p_147460_4_.getClass().getCanonicalName());
                  } catch (Throwable var2) {
                     return "ID #" + Block.getIdFromBlock(p_147460_4_);
                  }
               }
            });
            CrashReportCategory.func_147153_a(crashreportcategory, p_147460_1_, p_147460_2_, p_147460_3_, block, l);
            throw new ReportedException(crashreport);
         }
      }

   }
}
