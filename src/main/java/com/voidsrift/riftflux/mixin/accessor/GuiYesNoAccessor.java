package com.voidsrift.riftflux.mixin.accessor;

import net.minecraft.client.gui.GuiYesNo;
import net.minecraft.client.gui.GuiYesNoCallback;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiYesNo.class)
public interface GuiYesNoAccessor {
    @Accessor("parentScreen")
    GuiYesNoCallback riftflux$getParentScreen();
}
