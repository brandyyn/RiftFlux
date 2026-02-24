package com.voidsrift.riftflux.mixin.accessor;

import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.client.gui.GuiScreen;

@Mixin(GuiScreen.class)
public interface GuiScreenAccessor {
    @Accessor("buttonList")
    List getButtonList();

    @Invoker("func_146283_a")
    void callDrawHoveringText(List list, int x, int y);
}
