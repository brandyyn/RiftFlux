package com.voidsrift.riftflux.mixin.early;

import com.voidsrift.riftflux.client.WorldSelectionCache;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiErrorScreen;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSelectWorld;
import net.minecraft.world.storage.ISaveFormat;
import net.minecraft.world.storage.SaveFormatComparator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiSelectWorld.class)
public abstract class MixinGuiSelectWorld_AsyncLoad extends GuiScreen {
    @Shadow
    private List field_146639_s;

    @Shadow
    private int field_146640_r;

    @Shadow
    private GuiButton field_146642_y;

    @Shadow
    private GuiButton field_146641_z;

    @Shadow
    private GuiButton field_146630_A;

    @Shadow
    private GuiButton field_146631_B;

    @Redirect(
            method = "func_146627_h",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/storage/ISaveFormat;getSaveList()Ljava/util/List;"
            )
    )
    private List riftflux$loadWorldListWithoutBlockingUi(ISaveFormat saveFormat) {
        return WorldSelectionCache.getCachedSaveListAndRefresh(saveFormat);
    }

    @Inject(method = "confirmClicked", at = @At("HEAD"))
    private void riftflux$invalidateWorldListAfterDelete(boolean confirmed, int selectedWorld, CallbackInfo ci) {
        if (confirmed) {
            WorldSelectionCache.invalidate();
        }
    }

    @Inject(method = "func_146615_e", at = @At("HEAD"))
    private void riftflux$finishWorldListRefreshBeforeLoad(int selectedWorld, CallbackInfo ci) {
        WorldSelectionCache.cancelRefresh();
    }

    @Inject(method = "drawScreen", at = @At("HEAD"))
    private void riftflux$applyLoadedWorldList(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        AnvilConverterException error = WorldSelectionCache.consumePendingError();
        if (error != null && (this.field_146639_s == null || this.field_146639_s.isEmpty())) {
            this.mc.displayGuiScreen(new GuiErrorScreen("Unable to load worlds", error.getMessage()));
            return;
        }

        List loadedSaveList = WorldSelectionCache.consumePendingSaveList();
        if (loadedSaveList == null) {
            return;
        }

        String selectedFileName = this.riftflux$getSelectedFileName();
        this.field_146639_s = loadedSaveList;
        this.field_146640_r = this.riftflux$findWorldIndex(loadedSaveList, selectedFileName);
        this.riftflux$setSelectionButtonsEnabled(this.field_146640_r >= 0);
    }

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void riftflux$drawLoadingWorldsMessage(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (WorldSelectionCache.isLoading() && (this.field_146639_s == null || this.field_146639_s.isEmpty())) {
            this.drawCenteredString(this.fontRendererObj, "Loading worlds...", this.width / 2, this.height / 2, 0xA0A0A0);
        }
    }

    @Inject(method = "onGuiClosed", at = @At("HEAD"))
    private void riftflux$releaseWorldSelectionCache(CallbackInfo ci) {
        WorldSelectionCache.releaseScreenCache();
    }

    private void riftflux$setSelectionButtonsEnabled(boolean enabled) {
        if (this.field_146641_z != null) {
            this.field_146641_z.enabled = enabled;
        }
        if (this.field_146642_y != null) {
            this.field_146642_y.enabled = enabled;
        }
        if (this.field_146630_A != null) {
            this.field_146630_A.enabled = enabled;
        }
        if (this.field_146631_B != null) {
            this.field_146631_B.enabled = enabled;
        }
    }

    private String riftflux$getSelectedFileName() {
        if (this.field_146639_s == null || this.field_146640_r < 0 || this.field_146640_r >= this.field_146639_s.size()) {
            return null;
        }
        Object selectedWorld = this.field_146639_s.get(this.field_146640_r);
        if (selectedWorld instanceof SaveFormatComparator) {
            return ((SaveFormatComparator)selectedWorld).getFileName();
        }
        return null;
    }

    private int riftflux$findWorldIndex(List saveList, String fileName) {
        if (saveList == null || fileName == null) {
            return -1;
        }
        for (int i = 0; i < saveList.size(); i++) {
            Object world = saveList.get(i);
            if (world instanceof SaveFormatComparator && fileName.equals(((SaveFormatComparator)world).getFileName())) {
                return i;
            }
        }
        return -1;
    }
}
