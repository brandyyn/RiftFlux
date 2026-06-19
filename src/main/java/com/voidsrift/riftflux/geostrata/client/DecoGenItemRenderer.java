package com.voidsrift.riftflux.geostrata.client;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.event.TextureStitchEvent;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class DecoGenItemRenderer implements IItemRenderer {
    private static final int ORIENTATION_MASK = 14;
    private static final int SPRITE_SIZE = 16;
    private static final double ITEM_DEPTH = 0.0625D;
    private static final ResourceLocation CRYSTAL_SPIKES_TEXTURE =
            new ResourceLocation("riftflux", "textures/items/geostrata_crystal_spikes.png");
    private static final ResourceLocation ICICLES_TEXTURE =
            new ResourceLocation("riftflux", "textures/items/geostrata_icicles.png");

    private static IIcon blockCrystalSpikes;
    private static IIcon blockIcicles;

    @SubscribeEvent
    public void onTextureStitch(TextureStitchEvent.Pre event) {
        if (event.map.getTextureType() == 0) {
            registerBlockParticleIcons(event.map);
        }
    }

    public static void registerBlockParticleIcons(IIconRegister iconRegister) {
        blockCrystalSpikes = iconRegister.registerIcon("riftflux:geostrata_crystal_spikes");
        blockIcicles = iconRegister.registerIcon("riftflux:geostrata_icicles");
    }

    public static IIcon getParticleIcon(Block block, int meta) {
        if (block == null || !"Reika.GeoStrata.Blocks.BlockDecoGen".equals(block.getClass().getName())) {
            return null;
        }

        return getDecoGenIcon(meta);
    }

    public static IIcon getDecoGenIcon(int meta) {
        int baseMeta = meta & ~ORIENTATION_MASK;
        if (baseMeta == 0) {
            return blockCrystalSpikes != null ? blockCrystalSpikes : riftflux$getAtlasIcon("riftflux:geostrata_crystal_spikes");
        }
        if (baseMeta == 1) {
            return blockIcicles != null ? blockIcicles : riftflux$getAtlasIcon("riftflux:geostrata_icicles");
        }
        return null;
    }

    private static IIcon riftflux$getAtlasIcon(String iconName) {
        Minecraft minecraft = Minecraft.getMinecraft();
        return minecraft == null ? null : minecraft.getTextureMapBlocks().getAtlasSprite(iconName);
    }

    public static ResourceLocation getItemTexture(int meta) {
        int baseMeta = meta & ~ORIENTATION_MASK;
        if (baseMeta == 0) {
            return CRYSTAL_SPIKES_TEXTURE;
        }
        if (baseMeta == 1) {
            return ICICLES_TEXTURE;
        }
        return null;
    }

    public boolean handleRenderType(ItemStack stack, ItemRenderType type) {
        return this.riftflux$getItemTexture(stack) != null;
    }

    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack stack, ItemRendererHelper helper) {
        return helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION;
    }

    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        IIcon icon = this.riftflux$getRenderIcon(stack);
        if (icon != null) {
            this.riftflux$renderAtlasItem(type, icon);
            return;
        }

        ResourceLocation texture = this.riftflux$getItemTexture(stack);
        if (texture == null) {
            return;
        }
        this.riftflux$renderDirectTextureItem(type, texture);
    }

    private void riftflux$renderAtlasItem(ItemRenderType type, IIcon icon) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
        boolean wasBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean wasCullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        try {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (type == ItemRenderType.INVENTORY) {
                this.riftflux$renderInventoryIcon(icon);
            } else if (type == ItemRenderType.ENTITY) {
                this.riftflux$renderEntityIcon(icon);
            } else {
                this.riftflux$renderHeldIcon(icon);
            }
        } finally {
            if (!wasBlendEnabled) {
                GL11.glDisable(GL11.GL_BLEND);
            }
            if (wasCullEnabled) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            } else {
                GL11.glDisable(GL11.GL_CULL_FACE);
            }
            GL11.glPopMatrix();
        }
    }

    private void riftflux$renderDirectTextureItem(ItemRenderType type, ResourceLocation texture) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(texture);
        int oldWrapS = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S);
        int oldWrapT = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T);
        int oldMinFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER);
        int oldMagFilter = GL11.glGetTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER);
        boolean wasBlendEnabled = GL11.glIsEnabled(GL11.GL_BLEND);
        boolean wasCullEnabled = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        GL11.glPushMatrix();
        try {
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, GL12.GL_CLAMP_TO_EDGE);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, GL11.GL_NEAREST);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            if (type == ItemRenderType.INVENTORY) {
                this.riftflux$renderDirectInventoryIcon();
            } else if (type == ItemRenderType.ENTITY) {
                this.riftflux$renderDirectEntityIcon();
            } else {
                this.riftflux$renderDirectHeldIcon();
            }
        } finally {
            if (!wasBlendEnabled) {
                GL11.glDisable(GL11.GL_BLEND);
            }
            if (wasCullEnabled) {
                GL11.glEnable(GL11.GL_CULL_FACE);
            } else {
                GL11.glDisable(GL11.GL_CULL_FACE);
            }
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_S, oldWrapS);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_WRAP_T, oldWrapT);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MIN_FILTER, oldMinFilter);
            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, oldMagFilter);
            GL11.glPopMatrix();
        }
    }

    private void riftflux$renderInventoryIcon(IIcon icon) {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(0.0D, 16.0D, 0.0D, icon.getMinU(), icon.getMaxV());
        tessellator.addVertexWithUV(16.0D, 16.0D, 0.0D, icon.getMaxU(), icon.getMaxV());
        tessellator.addVertexWithUV(16.0D, 0.0D, 0.0D, icon.getMaxU(), icon.getMinV());
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, icon.getMinU(), icon.getMinV());
        tessellator.draw();
    }

    private void riftflux$renderEntityIcon(IIcon icon) {
        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
        this.riftflux$renderHeldIcon(icon);
    }

    private void riftflux$renderHeldIcon(IIcon icon) {
        ItemRenderer.renderItemIn2D(Tessellator.instance,
                icon.getMaxU(), icon.getMinV(), icon.getMinU(), icon.getMaxV(),
                icon.getIconWidth(), icon.getIconHeight(), (float)ITEM_DEPTH);
    }

    private void riftflux$renderDirectInventoryIcon() {
        Tessellator tessellator = Tessellator.instance;
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 0.0F, 1.0F);
        tessellator.addVertexWithUV(0.0D, 16.0D, 0.0D, 0.0D, 1.0D);
        tessellator.addVertexWithUV(16.0D, 16.0D, 0.0D, 1.0D, 1.0D);
        tessellator.addVertexWithUV(16.0D, 0.0D, 0.0D, 1.0D, 0.0D);
        tessellator.addVertexWithUV(0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
        tessellator.draw();
    }

    private void riftflux$renderDirectEntityIcon() {
        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
        this.riftflux$renderDirectHeldIcon();
    }

    private void riftflux$renderDirectHeldIcon() {
        ItemRenderer.renderItemIn2D(Tessellator.instance, 1.0F, 0.0F, 0.0F, 1.0F,
                SPRITE_SIZE, SPRITE_SIZE, (float)ITEM_DEPTH);
    }

    private IIcon riftflux$getRenderIcon(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        IIcon icon = getDecoGenIcon(stack.getItemDamage());
        return riftflux$isMissingIcon(icon) ? null : icon;
    }

    private static boolean riftflux$isMissingIcon(IIcon icon) {
        if (icon == null) {
            return true;
        }
        String iconName = icon.getIconName();
        return iconName == null || iconName.toLowerCase().contains("missing");
    }

    private ResourceLocation riftflux$getItemTexture(ItemStack stack) {
        if (stack == null) {
            return null;
        }
        return getItemTexture(stack.getItemDamage());
    }

}
