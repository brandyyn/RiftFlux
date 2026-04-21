package zairus.worldexplorer.archery.client.renderer.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityEnderChest;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

@SideOnly(value=Side.CLIENT)
public class ItemCapturedEnderChestRenderer implements IItemRenderer {
    private final TileEntityEnderChest chest = new TileEntityEnderChest();

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return true;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        if (type == ItemRenderType.INVENTORY) {
            return helper == ItemRendererHelper.INVENTORY_BLOCK;
        }
        return helper == ItemRendererHelper.ENTITY_BOBBING || helper == ItemRendererHelper.ENTITY_ROTATION || helper == ItemRendererHelper.BLOCK_3D;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack item, Object... data) {
        this.chest.field_145975_i = 0.85f;
        this.chest.field_145972_a = 0.85f;

        GL11.glPushMatrix();
        GL11.glPushAttrib(GL11.GL_ALL_ATTRIB_BITS);
        try {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            if (type == ItemRenderType.INVENTORY) {
                GL11.glDisable(GL11.GL_LIGHTING);
            } else {
                RenderHelper.enableStandardItemLighting();
            }
            OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0f, 240.0f);

            if (type == ItemRenderType.INVENTORY) {
                GL11.glTranslatef(0.0f, -0.2375f, 0.0f);
                GL11.glScalef(0.8f, 0.8f, 0.8f);
                GL11.glRotatef(90.0f, 0.0f, 1.0f, 0.0f);
                GL11.glTranslatef(-0.5f, -0.5f, -0.5f);
            } else if (type == ItemRenderType.ENTITY) {
                GL11.glTranslatef(0.0f, 0.05f, 0.0f);
                GL11.glScalef(0.44f, 0.44f, 0.44f);
                GL11.glTranslatef(-0.5f, 0.0f, -0.5f);
            } else if (type == ItemRenderType.EQUIPPED_FIRST_PERSON) {
                GL11.glTranslatef(0.18f, 0.44f, -0.1f);
                GL11.glScalef(0.33f, 0.33f, 0.33f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
                GL11.glRotatef(15.5f, 1.0f, 0.0f, 0.0f);
            } else {
                GL11.glTranslatef(0.0f, 0.35f, 0.0f);
                GL11.glScalef(0.6375f, 0.6375f, 0.6375f);
                GL11.glRotatef(180.0f, 0.0f, 1.0f, 0.0f);
            }
            TileEntityRendererDispatcher.instance.renderTileEntityAt(this.chest, 0.0D, 0.0D, 0.0D, 0.0F);
        } finally {
            RenderHelper.disableStandardItemLighting();
            GL11.glPopAttrib();
            GL11.glPopMatrix();
        }
    }
}
