package com.voidsrift.riftflux.client.worldtooltips;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;

@SideOnly(Side.CLIENT)
public final class WorldTooltip {
    private static final float SCALE = 0.016666668F;
    private static final int ATTRIB_MASK = GL11.GL_ENABLE_BIT
            | GL11.GL_COLOR_BUFFER_BIT
            | GL11.GL_CURRENT_BIT
            | GL11.GL_DEPTH_BUFFER_BIT
            | GL11.GL_LIGHTING_BIT
            | GL11.GL_TEXTURE_BIT;

    private final EntityItem entity;
    private final Item item;
    private final int itemDamage;
    private final int stackSize;
    private final String displayName;
    private final EnumChatFormatting rarityFormatting;
    private final String firstLine;
    private final String secondLine;
    private final int lineCount;
    private final int backgroundColor;
    private final int overrideOutlineColor;
    private final int alphaBits;
    private final boolean overrideOutline;
    private final boolean includeModName;
    private final int width;
    private final int height;

    public WorldTooltip(EntityItem entity) {
        this.entity = entity;
        this.overrideOutline = ModConfig.worldTooltipsOverrideOutline;
        this.alphaBits = ((int) (clamp(ModConfig.worldTooltipsTransparency, 0.0F, 1.0F) * 255.0F) & 0xFF) << 24;
        this.backgroundColor = ModConfig.worldTooltipsBackgroundColor & 0xFFFFFF;
        this.overrideOutlineColor = ModConfig.worldTooltipsOutlineColor & 0xFFFFFF;

        ItemStack stack = entity != null ? entity.getEntityItem() : null;
        if (stack == null || stack.getItem() == null) {
            this.item = null;
            this.itemDamage = 0;
            this.stackSize = 0;
            this.displayName = null;
            this.rarityFormatting = EnumChatFormatting.WHITE;
            this.firstLine = null;
            this.secondLine = null;
            this.lineCount = 0;
            this.includeModName = false;
            this.width = 0;
            this.height = 0;
            return;
        }

        this.item = stack.getItem();
        this.itemDamage = stack.getItemDamage();
        this.stackSize = stack.stackSize;
        this.displayName = stack.getDisplayName();
        this.rarityFormatting = resolveRarityFormatting(stack);
        this.includeModName = !ModConfig.worldTooltipsHideModName && !WorldTooltipClient.shouldHideModName();
        this.firstLine = stack.stackSize > 1 ? stack.stackSize + " x " + displayName : displayName;
        this.secondLine = includeModName
                ? EnumChatFormatting.BLUE.toString() + EnumChatFormatting.ITALIC + WorldTooltipClient.getModName(item) + EnumChatFormatting.RESET
                : null;
        this.lineCount = includeModName ? 2 : 1;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRenderer;
        int maxWidth = Math.max(4, fontRenderer.getStringWidth(firstLine));
        if (secondLine != null) {
            maxWidth = Math.max(maxWidth, fontRenderer.getStringWidth(secondLine));
        }
        this.width = maxWidth;
        this.height = computeHeight(lineCount);
    }

    public boolean isRenderable() {
        return entity != null && !entity.isDead && lineCount > 0 && width > 0 && height > 0;
    }

    public boolean isStillValid(EntityItem currentEntity) {
        if (currentEntity == null || currentEntity != entity || currentEntity.isDead) {
            return false;
        }

        ItemStack stack = currentEntity.getEntityItem();
        if (stack == null || stack.getItem() == null || stack.getItem() != item) {
            return false;
        }

        if (stack.getItemDamage() != itemDamage
                || stack.stackSize != stackSize
                || !safeEquals(displayName, stack.getDisplayName())
                || resolveRarityFormatting(stack) != rarityFormatting) {
            return false;
        }

        if (ModConfig.worldTooltipsOverrideOutline != overrideOutline
                || (((int) (clamp(ModConfig.worldTooltipsTransparency, 0.0F, 1.0F) * 255.0F) & 0xFF) << 24) != alphaBits
                || (ModConfig.worldTooltipsBackgroundColor & 0xFFFFFF) != backgroundColor
                || (ModConfig.worldTooltipsOutlineColor & 0xFFFFFF) != overrideOutlineColor) {
            return false;
        }

        return includeModName == (!ModConfig.worldTooltipsHideModName && !WorldTooltipClient.shouldHideModName());
    }

    public int size() {
        return lineCount;
    }

    public String getLine(int index) {
        if (index == 0) {
            return firstLine;
        }
        if (index == 1 && secondLine != null) {
            return secondLine;
        }
        throw new IndexOutOfBoundsException("Invalid world tooltip line index: " + index);
    }

    public EnumChatFormatting getRarityFormatting() {
        return rarityFormatting;
    }

    public void render(Minecraft mc, float partialTicks) {
        float textScale = clamp(ModConfig.worldTooltipsTextScale, 0.25F, 4.0F);
        int outlineRgb = overrideOutline
                ? overrideOutlineColor
                : WorldTooltipClient.getRarityColor(rarityFormatting, overrideOutlineColor);
        int outlinePrimary = withAlpha((outlineRgb & 0xFEFEFE) >> 1, alphaBits);
        int outlineSecondary = withAlpha(((outlinePrimary & 0xFFFFFF) & 0xFEFEFE) >> 1, alphaBits);

        double x = interpolate(entity.prevPosX, entity.posX, partialTicks) - RenderManager.renderPosX;
        double y = interpolate(entity.prevPosY, entity.posY, partialTicks) - RenderManager.renderPosY
                + ModConfig.worldTooltipsVerticalOffset;
        double z = interpolate(entity.prevPosZ, entity.posZ, partialTicks) - RenderManager.renderPosZ;
        int drawX = -width / 2;
        int drawY = -height;

        // Preserve only the state this renderer mutates so the tooltip is isolated without the overhead of GL_ALL_ATTRIB_BITS.
        GL11.glPushAttrib(ATTRIB_MASK);
        GL11.glPushMatrix();
        try {
            GL11.glTranslated(x, y, z);
            GL11.glNormal3f(0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-RenderManager.instance.playerViewY + 180.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef(-RenderManager.instance.playerViewX, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(SCALE * textScale, -SCALE * textScale, SCALE * textScale);
            GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glDisable(GL11.GL_CULL_FACE);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDepthMask(false);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            WorldTooltipRenderHelper.drawTooltipBackground(
                    drawX,
                    drawY,
                    width,
                    height,
                    withAlpha(backgroundColor, alphaBits),
                    outlinePrimary,
                    outlineSecondary
            );
            WorldTooltipRenderHelper.drawTooltipText(mc.fontRenderer, this, drawX, drawY, alphaBits);
        } finally {
            GL11.glPopMatrix();
            GL11.glPopAttrib();
        }
    }

    private static int computeHeight(int lineCount) {
        if (lineCount <= 0) {
            return 0;
        }
        return lineCount == 1 ? 8 : 8 + 2 + (lineCount - 1) * 10;
    }

    private static int withAlpha(int rgb, int alphaBits) {
        return (rgb & 0xFFFFFF) | alphaBits;
    }

    private static EnumChatFormatting resolveRarityFormatting(ItemStack stack) {
        if (stack == null || stack.getRarity() == null || stack.getRarity().rarityColor == null) {
            return EnumChatFormatting.WHITE;
        }
        return stack.getRarity().rarityColor;
    }

    private static boolean safeEquals(String left, String right) {
        return left == null ? right == null : left.equals(right);
    }

    private static double interpolate(double previous, double current, float partialTicks) {
        return previous + (current - previous) * partialTicks;
    }

    private static float clamp(float value, float min, float max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }
}
