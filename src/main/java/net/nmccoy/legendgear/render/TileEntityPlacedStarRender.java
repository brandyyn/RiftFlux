package net.nmccoy.legendgear.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import org.lwjgl.opengl.GL11;

public class TileEntityPlacedStarRender extends TileEntitySpecialRenderer {
    private static final float STAR_SCALE = 0.336F;
    private static final float STAR_Y_OFFSET = 0.35F;

    private final RenderFallingStar starRenderer = new RenderFallingStar();
    private EntityFallingStar displayStar;
    private World displayStarWorld;

    public TileEntityPlacedStarRender() {
        this.starRenderer.setRenderManager(RenderManager.instance);
    }

    @Override
    public void renderTileEntityAt(TileEntity tileEntity, double x, double y, double z, float partialTicks) {
        EntityFallingStar star = this.getDisplayStar(tileEntity);
        if (star == null) {
            return;
        }

        GL11.glPushMatrix();
        GL11.glTranslatef((float) x + 0.5F, (float) y + STAR_Y_OFFSET, (float) z + 0.5F);
        GL11.glScalef(STAR_SCALE, STAR_SCALE, STAR_SCALE);
        this.starRenderer.doRender(star, 0.0, 0.0, 0.0, 0.0F, partialTicks);
        GL11.glPopMatrix();
    }

    private EntityFallingStar getDisplayStar(TileEntity tileEntity) {
        World world = Minecraft.getMinecraft().theWorld;
        if (world == null && tileEntity != null) {
            world = tileEntity.getWorldObj();
        }
        if (world == null) {
            return null;
        }
        if (this.displayStar == null || this.displayStarWorld != world) {
            this.displayStarWorld = world;
            this.displayStar = new EntityFallingStar(world);
        }
        this.displayStar.impact = true;
        this.displayStar.dwindle_timer = Math.max(1, EntityFallingStar.DWINDLE_TIME / 3);
        return this.displayStar;
    }
}
