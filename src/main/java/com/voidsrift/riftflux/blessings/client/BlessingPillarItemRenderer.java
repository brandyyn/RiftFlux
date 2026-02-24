package com.voidsrift.riftflux.blessings.client;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public class BlessingPillarItemRenderer implements IItemRenderer {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation("riftflux", "textures/blocks/blessing_pillar.png");

    private final ModelBlessingPillar model = new ModelBlessingPillar();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        switch (type) {
            case ENTITY:
            case EQUIPPED:
            case EQUIPPED_FIRST_PERSON:
            case INVENTORY:
                return true;
            default:
                return false;
        }
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return true;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        GL11.glPushMatrix();

        if (type == ItemRenderType.INVENTORY) {
            GL11.glTranslatef(0.0f, -0.35f, 0.0f);
            GL11.glScalef(0.65f, 0.65f, 0.65f);
        } else if (type == ItemRenderType.ENTITY) {
            GL11.glTranslatef(0.0f, 0.5f, 0.0f);
            GL11.glScalef(0.6f, 0.6f, 0.6f);
        } else {
            GL11.glTranslatef(0.5f, 0.55f, 0.5f);
            GL11.glScalef(0.6f, 0.6f, 0.6f);
        }

        GL11.glRotatef(180.0f, 1.0f, 0.0f, 0.0f);
        GL11.glTranslatef(0.5f, -1.5f, -0.5f);

        Minecraft.getMinecraft().getTextureManager().bindTexture(TEXTURE);
        model.render(null, 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);

        GL11.glPopMatrix();
    }
}
