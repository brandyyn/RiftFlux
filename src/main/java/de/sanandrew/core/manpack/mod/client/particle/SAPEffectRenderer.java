/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 *  com.google.common.collect.ArrayListMultimap
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.Multimap
 *  cpw.mods.fml.common.eventhandler.Event
 *  javax.annotation.Nullable
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.core.manpack.mod.client.particle;

import com.google.common.base.Predicate;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Collections2;
import com.google.common.collect.Multimap;
import cpw.mods.fml.common.eventhandler.Event;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.client.EntityParticle;
import de.sanandrew.core.manpack.util.client.event.SAPFxLayerRenderEvent;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.core.manpack.util.javatuples.Pair;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.particle.EntityFX;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class SAPEffectRenderer {
    private static final ResourceLocation PARTICLE_TEXTURES = new ResourceLocation("textures/particle/particles.png");
    private int defaultFxLayer = 0;
    private Map<Integer, Pair<ResourceLocation, Boolean>> fxLayers = new HashMap<Integer, Pair<ResourceLocation, Boolean>>();
    private Multimap<Integer, EntityParticle> particles = ArrayListMultimap.create();
    private TextureManager textureManager;
    private static boolean isInitialized = false;
    public static final SAPEffectRenderer INSTANCE = new SAPEffectRenderer();

    public int getDefaultFxLayer() {
        return this.defaultFxLayer;
    }

    public void addEffect(EntityParticle particle) {
        this.particles.put(particle.getFXLayer(), particle);
    }

    public int registerFxLayer(ResourceLocation resource, boolean hasAlpha) {
        Pair<ResourceLocation, Boolean> newEntry = Pair.with(resource, hasAlpha);
        int newIndex = this.fxLayers.size();
        this.fxLayers.put(newIndex, newEntry);
        return newIndex;
    }

    public void updateEffects() {
        Iterator particleIt = this.particles.values().iterator();
        while (particleIt.hasNext()) {
            EntityParticle particle = (EntityParticle)particleIt.next();
            try {
                if (particle != null) {
                    particle.onUpdate();
                }
            }
            catch (Throwable throwable) {
                throw new RuntimeException("Error in ticking particle!");
            }
            if (particle != null && !particle.isDead) continue;
            particleIt.remove();
        }
    }

    public void renderParticles(Entity viewingEntity, float partTicks, boolean alpha) {
        float rotX = ActiveRenderInfo.rotationX;
        float rotZ = ActiveRenderInfo.rotationZ;
        float rotYZ = ActiveRenderInfo.rotationYZ;
        float rotXY = ActiveRenderInfo.rotationXY;
        float rotXZ = ActiveRenderInfo.rotationXZ;
        EntityFX.interpPosX = viewingEntity.lastTickPosX + (viewingEntity.posX - viewingEntity.lastTickPosX) * (double)partTicks;
        EntityFX.interpPosY = viewingEntity.lastTickPosY + (viewingEntity.posY - viewingEntity.lastTickPosY) * (double)partTicks;
        EntityFX.interpPosZ = viewingEntity.lastTickPosZ + (viewingEntity.posZ - viewingEntity.lastTickPosZ) * (double)partTicks;
        Collection<Map.Entry<Integer, Pair<ResourceLocation, Boolean>>> currLayers = Collections2.filter(this.fxLayers.entrySet(), new SortingFilter(alpha));
        for (Map.Entry<Integer, Pair<ResourceLocation, Boolean>> layer : currLayers) {
            Pair<ResourceLocation, Boolean> layerData = layer.getValue();
            Collection<EntityParticle> particles = this.particles.get(layer.getKey());
            if (particles.isEmpty()) continue;
            Tessellator tessellator = Tessellator.instance;
            tessellator.startDrawingQuads();
            for (EntityParticle particle : particles) {
                if (particle == null) continue;
                tessellator.setBrightness(particle.getBrightnessForRender(partTicks));
                try {
                    particle.renderParticle(tessellator, partTicks, rotX, rotXZ, rotZ, rotYZ, rotXY);
                }
                catch (Throwable throwable) {
                    throw new RuntimeException("Couldn't render particle!", throwable);
                }
            }
            this.textureManager.bindTexture(layerData.getValue0());
            GL11.glColor4f((float)1.0f, (float)1.0f, (float)1.0f, (float)1.0f);
            if (layerData.getValue1()) {
                GL11.glEnable((int)3042);
                OpenGlHelper.glBlendFunc(770, 771, 1, 0);
            }
            SAPUtils.EVENT_BUS.post((Event)new SAPFxLayerRenderEvent.Pre(layer.getKey(), tessellator));
            tessellator.draw();
            SAPUtils.EVENT_BUS.post((Event)new SAPFxLayerRenderEvent.Post(layer.getKey()));
            if (!layerData.getValue1()) continue;
            GL11.glDisable((int)3042);
        }
    }

    public static void initialize(TextureManager texManager) {
        if (isInitialized) {
            return;
        }
        isInitialized = true;
        SAPEffectRenderer.INSTANCE.textureManager = texManager;
        SAPEffectRenderer.INSTANCE.defaultFxLayer = INSTANCE.registerFxLayer(PARTICLE_TEXTURES, false);
    }

    private static class SortingFilter
    implements Predicate<Map.Entry<Integer, Pair<ResourceLocation, Boolean>>> {
        private final boolean hasAlpha;

        public SortingFilter(boolean alpha) {
            this.hasAlpha = alpha;
        }

        public boolean apply(@Nullable Map.Entry<Integer, Pair<ResourceLocation, Boolean>> input) {
            return input != null && input.getValue().getValue1() == this.hasAlpha;
        }
    }
}
