package tk.nukeduck.hearts.renderer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.nmccoy.legendgear.entity.EntityFallingStar;
import net.nmccoy.legendgear.render.RenderFallingStar;
import org.lwjgl.opengl.GL11;

public class StarLanternRenderer
extends LanternRenderer {
    private static final float STAR_Y_OFFSET = -1.27f;
    private static final float STAR_SCALE = 0.336f;
    private EntityFallingStar displayStar;
    private World displayStarWorld;
    private final RenderFallingStar starRenderer = new RenderFallingStar();

    public StarLanternRenderer() {
        this.starRenderer.setRenderManager(RenderManager.instance);
    }

    @Override
    protected ResourceLocation getLanternTexture() {
        return new ResourceLocation("hearts", "textures/models/star_lantern.png");
    }

    @Override
    protected void renderLanternCore(TileEntity tileEntity, float partialTicks) {
        EntityFallingStar star = this.getDisplayStar(tileEntity);
        if (star == null) {
            return;
        }
        GL11.glPushMatrix();
        GL11.glTranslatef((float)0.0f, (float)STAR_Y_OFFSET, (float)0.0f);
        GL11.glScalef((float)STAR_SCALE, (float)STAR_SCALE, (float)STAR_SCALE);
        this.starRenderer.doRender(star, 0.0, 0.0, 0.0, 0.0f, partialTicks);
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
