package zairus.worldexplorer.archery.client.renderer.entity;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.RenderBlocks;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;
import zairus.worldexplorer.archery.entity.EntityPebble;

@SideOnly(Side.CLIENT)
public class RenderEntityPebble extends Render {
    private static final ResourceLocation PEBBLE_TEXTURE = new ResourceLocation("worldexplorer", "textures/entity/entity_pebble.png");
    private static final RenderBlocks BLOCK_RENDERER = new RenderBlocks();

    public void doRender(EntityPebble entity, double x, double y, double z, float yaw, float partialTicks) {
        GL11.glPushMatrix();
        GL11.glTranslatef((float) x, (float) y, (float) z);

        if (ModConfig.riftExplorerSlingshotUsesCobblestoneAmmo) {
            this.bindTexture(TextureMap.locationBlocksTexture);
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glEnable(GL12.GL_RESCALE_NORMAL);
            RenderHelper.enableStandardItemLighting();
            GL11.glRotatef((entity.ticksExisted + partialTicks) * 18.0F, 0.0F, 1.0F, 0.0F);
            GL11.glRotatef((entity.ticksExisted + partialTicks) * 12.0F, 1.0F, 0.0F, 0.0F);
            GL11.glScalef(0.35F, 0.35F, 0.35F);
            BLOCK_RENDERER.renderBlockAsItem(Blocks.cobblestone, 0, entity.getBrightness(partialTicks));
            RenderHelper.disableStandardItemLighting();
            GL11.glDisable(GL12.GL_RESCALE_NORMAL);
            GL11.glPopMatrix();
            return;
        }

        this.bindTexture(PEBBLE_TEXTURE);
        GL11.glEnable(32826);
        float scale = 0.5F;
        GL11.glScalef(scale, scale, scale);
        Tessellator tessellator = Tessellator.instance;
        float u0 = 0.0F;
        float u1 = 1.0F;
        float v0 = 0.0F;
        float v1 = 1.0F;
        float width = 1.0F;
        float height = 1.0F;
        float yOffset = 0.25F;
        GL11.glRotatef(180.0F - this.renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
        GL11.glRotatef(-this.renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
        tessellator.startDrawingQuads();
        tessellator.setNormal(0.0F, 1.0F, 0.0F);
        tessellator.addVertexWithUV(0.0F - height, 0.0F - yOffset, 0.0D, u0, v1);
        tessellator.addVertexWithUV(width - height, 0.0F - yOffset, 0.0D, u1, v1);
        tessellator.addVertexWithUV(width - height, 1.0F - yOffset, 0.0D, u1, v0);
        tessellator.addVertexWithUV(0.0F - height, 1.0F - yOffset, 0.0D, u0, v0);
        tessellator.draw();
        GL11.glDisable(32826);
        GL11.glPopMatrix();
    }

    protected ResourceLocation getEntityTexture(EntityPebble entity) {
        return PEBBLE_TEXTURE;
    }

    protected ResourceLocation getEntityTexture(Entity entity) {
        return this.getEntityTexture((EntityPebble) entity);
    }

    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTicks) {
        this.doRender((EntityPebble) entity, x, y, z, yaw, partialTicks);
    }
}
