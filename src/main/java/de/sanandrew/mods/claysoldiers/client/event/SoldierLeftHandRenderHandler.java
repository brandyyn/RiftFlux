/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.eventhandler.SubscribeEvent
 *  org.lwjgl.opengl.GL11
 */
package de.sanandrew.mods.claysoldiers.client.event;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.client.helpers.ItemRenderHelper;
import de.sanandrew.mods.claysoldiers.client.event.SoldierRenderEvent;
import de.sanandrew.mods.claysoldiers.client.render.entity.RenderClayMan;
import de.sanandrew.mods.claysoldiers.client.util.Textures;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class SoldierLeftHandRenderHandler {
    private final ItemStack p_itemShearBlade = new ItemStack(RegistryItems.shearBlade);
    private final ItemStack p_blockGravel = new ItemStack(Blocks.gravel);
    private final ItemStack p_blockSnow = new ItemStack(Blocks.snow);
    private final ItemStack p_blockObsidian = new ItemStack(Blocks.obsidian);
    private final ItemStack p_blockEmerald = new ItemStack(Blocks.emerald_block);

    @SubscribeEvent
    public void onSoldierRender(SoldierRenderEvent event) {
        if (event.stage == SoldierRenderEvent.EnumRenderStage.EQUIPPED) {
            if (event.clayMan.hasUpgrade("shear_l")) {
                SoldierLeftHandRenderHandler.renderLeftHandItem(event.clayMan, event.clayManRender, this.p_itemShearBlade);
            } else if (event.clayMan.hasUpgrade("gravel")) {
                SoldierLeftHandRenderHandler.renderThrowableBlock(event.clayMan, event.clayManRender, this.p_blockGravel);
            } else if (event.clayMan.hasUpgrade("snow")) {
                SoldierLeftHandRenderHandler.renderThrowableBlock(event.clayMan, event.clayManRender, this.p_blockSnow);
            } else if (event.clayMan.hasUpgrade("firecharge")) {
                SoldierLeftHandRenderHandler.renderThrowableBlock(event.clayMan, event.clayManRender, this.p_blockObsidian);
            } else if (event.clayMan.hasUpgrade("emerald")) {
                SoldierLeftHandRenderHandler.renderThrowableBlock(event.clayMan, event.clayManRender, this.p_blockEmerald);
            } else if (event.clayMan.hasUpgrade("bowl")) {
                SoldierLeftHandRenderHandler.renderShield(event.clayMan, event.clayManRender);
            }
        }
    }

    private static void renderLeftHandItem(EntityClayMan clayMan, RenderClayMan renderer, ItemStack stack) {
        GL11.glPushMatrix();
        renderer.modelBipedMain.bipedLeftArm.postRender(0.0625f);
        GL11.glTranslatef((float)-0.1f, (float)0.6f, (float)0.0f);
        float itemScale = 0.6f;
        GL11.glScalef((float)itemScale, (float)itemScale, (float)itemScale);
        GL11.glRotatef((float)140.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        renderer.getItemRenderer().renderItem(clayMan, stack, 0);
        GL11.glPopMatrix();
    }

    private static void renderThrowableBlock(EntityClayMan clayMan, RenderClayMan renderer, ItemStack stack) {
        GL11.glPushMatrix();
        renderer.modelBipedMain.bipedLeftArm.postRender(0.0625f);
        GL11.glTranslatef((float)0.05f, (float)0.55f, (float)0.0f);
        GL11.glScalef((float)0.3f, (float)0.3f, (float)0.3f);
        renderer.getItemRenderer().renderItem(clayMan, stack, 0);
        GL11.glPopMatrix();
    }

    private static void renderShield(EntityClayMan clayMan, RenderClayMan renderer) {
        IIcon icon = clayMan.hasUpgrade("iron_block") ? Textures.s_shieldStudIcon : Textures.s_shieldIcon;
        GL11.glPushMatrix();
        renderer.modelBipedMain.bipedLeftArm.postRender(0.0625f);
        GL11.glTranslatef((float)-0.4f, (float)0.15f, (float)-0.2f);
        GL11.glScalef((float)0.75f, (float)0.75f, (float)0.75f);
        ItemRenderHelper.renderIconIn3D(icon, false, false, 0xFFFFFF);
        GL11.glPopMatrix();
    }
}

