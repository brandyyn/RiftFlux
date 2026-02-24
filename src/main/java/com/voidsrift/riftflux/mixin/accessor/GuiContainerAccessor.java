package com.voidsrift.riftflux.mixin.accessor;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.client.gui.inventory.GuiContainer;

@Mixin(GuiContainer.class)
public interface GuiContainerAccessor {
    @Accessor("guiLeft")
    int getGuiLeft();

    @Accessor("guiTop")
    int getGuiTop();

    @Accessor("xSize")
    int getXSize();

    @Accessor("ySize")
    int getYSize();
}
