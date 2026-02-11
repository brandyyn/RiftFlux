package com.voidsrift.riftflux.avatar.glider;

import com.voidsrift.riftflux.avatar.util.FileLocation;
import com.voidsrift.riftflux.avatar.glider.GliderState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class RenderGliderInHand implements IItemRenderer {
    private final ModelAirStaffClosed model = new ModelAirStaffClosed();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.EQUIPPED || type == ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType t, ItemStack i, ItemRendererHelper h) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        if (data.length > 1 && data[1] instanceof Entity) {
            Entity entity = (Entity) data[1];
            if (entity instanceof net.minecraft.entity.player.EntityPlayer) {
                net.minecraft.entity.player.EntityPlayer player = (net.minecraft.entity.player.EntityPlayer) entity;
                boolean gliderActive = GliderState.isPlayerGliding(player.getDisplayName());
                boolean shouldGlide = gliderActive && !player.onGround && !player.isInWater();
                if (gliderActive || shouldGlide) {
                    return;
                }
            }
        }
        GL11.glRotatef(-15.0f, 2.0f, 3.0f, -5.0f);
        GL11.glTranslatef(0.3f, -0.3f, -0.1f);
        GL11.glScalef(2.0f, 2.0f, 1.0f);
        Minecraft.getMinecraft().renderEngine.bindTexture(
                new ResourceLocation(FileLocation.ENTITYTEXTURE + "Airbending staff closed.png"));
        this.model.render((Entity) data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
    }
}
