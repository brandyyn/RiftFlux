package zairus.worldexplorer.archery.client.renderer.entity;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import zairus.worldexplorer.archery.client.renderer.item.ItemCapturedEnderChestRenderer;
import zairus.worldexplorer.archery.entity.EntityPebble;
import zairus.worldexplorer.archery.items.WEArcheryItems;

@SideOnly(Side.CLIENT)
public class RenderEntityPebble extends Render {
    private static final ResourceLocation FALLBACK_TEXTURE = TextureMap.locationItemsTexture;
    private static final RenderBlocks BLOCK_RENDERER = new RenderBlocks();
    private static final ItemCapturedEnderChestRenderer CAPTURED_CHEST_RENDERER = new ItemCapturedEnderChestRenderer();

    public void doRender(EntityPebble entity, double x, double y, double z, float yaw, float partialTicks) {
        ItemStack stack = entity.getRenderStack();
        if (stack == null || stack.getItem() == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        Item item = stack.getItem();
        Block block = Block.getBlockFromItem(item);
        if (item == WEArcheryItems.captured_ender_chest) {
            this.renderCapturedChestAmmo(entity, stack, partialTicks);
        } else if (block != null && block != Blocks.air) {
            this.renderBlockAmmo(entity, stack, block, partialTicks);
        } else {
            this.renderItemAmmo(stack);
        }

        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glPopMatrix();
    }

    private void renderBlockAmmo(EntityPebble entity, ItemStack stack, Block block, float partialTicks) {
        this.bindTexture(TextureMap.locationBlocksTexture);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        RenderHelper.enableStandardItemLighting();
        GL11.glRotatef((entity.ticksExisted + partialTicks) * 18.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((entity.ticksExisted + partialTicks) * 12.0F, 1.0F, 0.0F, 0.0F);
        GL11.glScalef(0.35F, 0.35F, 0.35F);
        BLOCK_RENDERER.renderBlockAsItem(block, stack.getItemDamage(), entity.getBrightness(partialTicks));
        RenderHelper.disableStandardItemLighting();
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    private void renderCapturedChestAmmo(EntityPebble entity, ItemStack stack, float partialTicks) {
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GL11.glRotatef((entity.ticksExisted + partialTicks) * 18.0F, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef((entity.ticksExisted + partialTicks) * 12.0F, 1.0F, 0.0F, 0.0F);
        CAPTURED_CHEST_RENDERER.renderItem(IItemRenderer.ItemRenderType.ENTITY, stack);
    }

    private void renderItemAmmo(ItemStack stack) {
        Item item = stack.getItem();
        IIcon icon = item.getIconIndex(stack);
        if (icon == null) {
            return;
        }

        this.bindTexture(item.getSpriteNumber() == 0 ? TextureMap.locationBlocksTexture : TextureMap.locationItemsTexture);
        int color = item.getColorFromItemStack(stack, 0);
        float red = (float)(color >> 16 & 255) / 255.0F;
        float green = (float)(color >> 8 & 255) / 255.0F;
        float blue = (float)(color & 255) / 255.0F;
        GL11.glColor4f(red, green, blue, 1.0F);
        GL11.glEnable(GL12.GL_RESCALE_NORMAL);
        GL11.glScalef(0.5F, 0.5F, 0.5F);
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        GL11.glTranslatef(-0.5F, -0.5F, 0.0F);
        ItemRenderer.renderItemIn2D(
                net.minecraft.client.renderer.Tessellator.instance,
                icon.getMaxU(),
                icon.getMinV(),
                icon.getMinU(),
                icon.getMaxV(),
                icon.getIconWidth(),
                icon.getIconHeight(),
                0.0625F
        );
        GL11.glDisable(GL12.GL_RESCALE_NORMAL);
    }

    protected ResourceLocation getEntityTexture(EntityPebble entity) {
        return FALLBACK_TEXTURE;
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityPebble) entity);
    }

    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRender((EntityPebble) entity, x, y, z, yaw, partialTicks);
    }
}
