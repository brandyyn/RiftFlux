package com.voidsrift.riftflux.mixin.accessor;

import net.minecraft.client.gui.GuiChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(GuiChat.class)
public interface GuiChatAccessor {
    @Invoker("mouseClicked")
    void riftflux$invokeMouseClicked(int mouseX, int mouseY, int button);
}
