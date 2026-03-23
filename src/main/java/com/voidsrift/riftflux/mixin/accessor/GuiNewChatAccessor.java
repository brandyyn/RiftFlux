package com.voidsrift.riftflux.mixin.accessor;

import java.util.List;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GuiNewChat.class)
public interface GuiNewChatAccessor {
    @Accessor("field_146253_i")
    List getDrawnChatLines();

    @Accessor("field_146250_j")
    int getScrollPos();
}
