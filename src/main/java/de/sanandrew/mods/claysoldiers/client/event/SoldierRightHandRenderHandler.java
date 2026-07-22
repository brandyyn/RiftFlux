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
import de.sanandrew.mods.claysoldiers.client.event.SoldierRenderEvent;
import de.sanandrew.mods.claysoldiers.client.render.entity.RenderClayMan;
import de.sanandrew.mods.claysoldiers.entity.EntityClayMan;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

@SideOnly(value=Side.CLIENT)
public class SoldierRightHandRenderHandler {
    private final ItemStack p_upgStick = new ItemStack(Items.stick);
    private final ItemStack p_upgStickArrow = new ItemStack(Items.arrow);
    private final ItemStack p_upgBlazeRod = new ItemStack(Items.blaze_rod);
    private final ItemStack p_upgWoodButton = new ItemStack(Blocks.planks);
    private final ItemStack p_upgStoneButton = new ItemStack(Blocks.stone);
    private final ItemStack p_upgShearBlade = new ItemStack(RegistryItems.shearBlade);
    private final ItemStack p_upgGoldMelon = new ItemStack(Items.speckled_melon);
    private final ItemStack p_upgBone = new ItemStack(Items.bone);

    @SubscribeEvent
    public void onSoldierRender(SoldierRenderEvent event) {
        if (event.stage == SoldierRenderEvent.EnumRenderStage.EQUIPPED) {
            if (event.clayMan.hasUpgrade("stick")) {
                if (event.clayMan.hasUpgrade("flint")) {
                    SoldierRightHandRenderHandler.renderRightHandItem(event.clayMan, event.clayManRender, this.p_upgStickArrow);
                } else {
                    SoldierRightHandRenderHandler.renderRightHandItem(event.clayMan, event.clayManRender, this.p_upgStick);
                }
            } else if (event.clayMan.hasUpgrade("blazerod")) {
                SoldierRightHandRenderHandler.renderRightHandItem(event.clayMan, event.clayManRender, this.p_upgBlazeRod);
            } else if (event.clayMan.hasUpgrade("shear_r")) {
                SoldierRightHandRenderHandler.renderRightHandItem(event.clayMan, event.clayManRender, this.p_upgShearBlade);
            } else if (event.clayMan.hasUpgrade("goldmelon")) {
                SoldierRightHandRenderHandler.renderRightHandItem(event.clayMan, event.clayManRender, this.p_upgGoldMelon);
            } else if (event.clayMan.hasUpgrade("bone")) {
                SoldierRightHandRenderHandler.renderRightHandItem(event.clayMan, event.clayManRender, this.p_upgBone);
            }
            if (event.clayMan.hasUpgrade("woodbutton")) {
                SoldierRightHandRenderHandler.renderKnuckle(event.clayMan, event.clayManRender, this.p_upgWoodButton);
            } else if (event.clayMan.hasUpgrade("stonebutton")) {
                SoldierRightHandRenderHandler.renderKnuckle(event.clayMan, event.clayManRender, this.p_upgStoneButton);
            }
        }
    }

    private static void renderRightHandItem(EntityClayMan clayMan, RenderClayMan renderer, ItemStack stack) {
        GL11.glPushMatrix();
        renderer.modelBipedMain.bipedRightArm.postRender(0.0625f);
        GL11.glTranslatef((float)-0.1f, (float)0.6f, (float)0.0f);
        float itemScale = 0.6f;
        GL11.glScalef((float)itemScale, (float)itemScale, (float)itemScale);
        GL11.glRotatef((float)140.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        GL11.glRotatef((float)-90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
        GL11.glRotatef((float)0.0f, (float)0.0f, (float)0.0f, (float)1.0f);
        renderer.getItemRenderer().renderItem(clayMan, stack, 0);
        GL11.glPopMatrix();
    }

    private static void renderKnuckle(EntityClayMan clayMan, RenderClayMan renderer, ItemStack stack) {
        GL11.glPushMatrix();
        renderer.modelBipedMain.bipedRightArm.postRender(0.0625f);
        GL11.glTranslatef((float)-0.05f, (float)0.55f, (float)0.0f);
        GL11.glScalef((float)0.3f, (float)0.3f, (float)0.3f);
        renderer.getItemRenderer().renderItem(clayMan, stack, 0);
        GL11.glPopMatrix();
    }
}

