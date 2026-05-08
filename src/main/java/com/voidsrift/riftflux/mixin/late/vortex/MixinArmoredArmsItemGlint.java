package com.voidsrift.riftflux.mixin.late.vortex;

import com.voidsrift.riftflux.vortex.lib.helper.ArmoredArmsGlintHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.IItemRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Pseudo
@Mixin(targets = "com.artur114.armoredarms.client.engines.ArmRenderEngineForge", remap = false)
public abstract class MixinArmoredArmsItemGlint {
    @Unique
    private ItemStack riftflux$renderedItemStack;

    @Inject(method = "renderItem", at = @At("HEAD"), require = 0)
    private void riftflux$captureRenderedItem(EntityLivingBase entity, ItemStack itemStack, int pass,
            IItemRenderer.ItemRenderType type, CallbackInfo ci) {
        this.riftflux$renderedItemStack = itemStack;
    }

    @ModifyArgs(
            method = "renderItem",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/GL11;glColor4f(FFFF)V"),
            require = 0
    )
    private void riftflux$colorHeldItemGlint(Args args) {
        ArmoredArmsGlintHelper.applyItemGlintArgs(this.riftflux$renderedItemStack, args);
    }

    @Redirect(
            method = "renderItem",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;hasEffect(I)Z"),
            require = 0
    )
    private boolean riftflux$hideNullRuneGlint(ItemStack itemStack, int pass) {
        return ArmoredArmsGlintHelper.hasVisibleEffect(itemStack, pass);
    }

    @Inject(method = "renderItem", at = @At("RETURN"), require = 0)
    private void riftflux$resetHeldItemGlintBlend(EntityLivingBase entity, ItemStack itemStack, int pass,
            IItemRenderer.ItemRenderType type, CallbackInfo ci) {
        this.riftflux$renderedItemStack = null;
        ArmoredArmsGlintHelper.resetBlendEquation();
    }
}
