package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.placeditem.BlockPlacedItem;
import com.voidsrift.riftflux.placeditem.TilePlacedItem;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase_NoPlacedItemRunParticles {

    @Inject(method = "spawnRunningParticles", at = @At("HEAD"), cancellable = true)
    private void rf$cancelRunningParticlesForPlacedItems(CallbackInfo ci) {
        EntityLivingBase self = (EntityLivingBase) (Object) this;
        World world = self.worldObj;
        if (world == null) {
            return;
        }
        int minX = MathHelper.floor_double(self.boundingBox.minX);
        int maxX = MathHelper.floor_double(self.boundingBox.maxX);
        int minZ = MathHelper.floor_double(self.boundingBox.minZ);
        int maxZ = MathHelper.floor_double(self.boundingBox.maxZ);
        int y = MathHelper.floor_double(self.boundingBox.minY - 0.1D);

        for (int x = minX; x <= maxX; x++) {
            for (int z = minZ; z <= maxZ; z++) {
                Block block = world.getBlock(x, y, z);
                if (block instanceof BlockPlacedItem) {
                ci.cancel();
                return;
            }
        }
        }
    }
}
