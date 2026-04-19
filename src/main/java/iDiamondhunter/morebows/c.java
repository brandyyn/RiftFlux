/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.entity.Render
 *  net.minecraft.client.renderer.entity.RenderEntity
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.monster.EntityWitch
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.entity.projectile.EntityArrow
 *  net.minecraft.entity.projectile.EntitySnowball
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.MathHelper
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package iDiamondhunter.morebows;

import iDiamondhunter.morebows.Client;
import iDiamondhunter.morebows.MoreBows;
import iDiamondhunter.morebows.b;
import iDiamondhunter.morebows.e;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEntity;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityWitch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.item.ItemStack;
import net.minecraft.util.MathHelper;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;

public final class c
extends RenderEntity
implements IItemRenderer {
    private static final Render a = RenderManager.instance.getEntityClassRenderObject(EntityArrow.class);
    private static final Render b = RenderManager.instance.getEntityClassRenderObject(EntitySnowball.class);

    public final void doRender(Entity entity, double d2, double d3, double d4, float f, float f2) {
        if (((e)entity).var_byte_a == 3) {
            if (!MoreBows.var_boolean_b) {
                b.doRender(entity, d2, d3, d4, f, f2);
                return;
            }
            super.doRender(entity, d2, d3, d4, f, f2);
            return;
        }
        a.doRender(entity, d2, d3, d4, f, f2);
    }

    public final boolean handleRenderType(ItemStack itemStack, IItemRenderer.ItemRenderType itemRenderType) {
        return itemRenderType == IItemRenderer.ItemRenderType.EQUIPPED || itemRenderType == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON;
    }

    public final void renderItem(IItemRenderer.ItemRenderType itemRenderType, ItemStack itemStack, Object ... data) {
        if (data == null || data.length < 2 || !(data[1] instanceof EntityLivingBase)) {
            return;
        }
        EntityLivingBase entityLivingBase = (EntityLivingBase)data[1];
        GL11.glPopMatrix();
        if (itemRenderType == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
            int n = ((EntityPlayer)entityLivingBase).getItemInUseCount();
            if (n > 0) {
                GL11.glRotatef((float)-18.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)-12.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)-8.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glTranslatef((float)-0.9f, (float)0.2f, (float)0.0f);
                float f2 = 72000.0f - ((float)n - Client.a + 1.0f);
                float f3 = f2 / ((float)((b)itemStack.getItem()).var_byte_arr_a[0] * 1.1f);
                f3 = (f3 * f3 + f3 * 2.0f) / 3.0f;
                if (f3 > 1.0f) {
                    f3 = 1.0f;
                }
                if (f3 > 0.1f) {
                    GL11.glTranslatef((float)0.0f, (float)(MathHelper.sin((float)((f2 - 0.1f) * 1.3f)) * 0.01f * (f3 - 0.1f)), (float)0.0f);
                }
                GL11.glTranslatef((float)0.0f, (float)0.0f, (float)(f3 * 0.1f));
                GL11.glRotatef((float)-335.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)-50.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)0.0f, (float)0.5f, (float)0.0f);
                GL11.glScalef((float)1.0f, (float)1.0f, (float)(1.0f + f3 * 0.2f));
                GL11.glTranslatef((float)0.0f, (float)-0.5f, (float)0.0f);
                GL11.glRotatef((float)50.0f, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)335.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
        } else {
            boolean bl = entityLivingBase instanceof EntityWitch;
            if (bl) {
                GL11.glRotatef((float)-40.0f, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)15.0f, (float)-1.0f, (float)0.0f, (float)0.0f);
            }
            GL11.glRotatef((float)-20.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glRotatef((float)90.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)-60.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            GL11.glScalef((float)2.6666667f, (float)2.6666667f, (float)2.6666667f);
            GL11.glTranslatef((float)-0.25f, (float)-0.1875f, (float)0.1875f);
            GL11.glTranslatef((float)0.0f, (float)0.125f, (float)0.3125f);
            GL11.glRotatef((float)-20.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            GL11.glScalef((float)0.625f, (float)-0.625f, (float)0.625f);
            GL11.glRotatef((float)-100.0f, (float)1.0f, (float)0.0f, (float)0.0f);
            GL11.glRotatef((float)45.0f, (float)0.0f, (float)1.0f, (float)0.0f);
            if (bl) {
                GL11.glRotatef((float)-15.0f, (float)1.0f, (float)0.0f, (float)0.0f);
                GL11.glRotatef((float)40.0f, (float)0.0f, (float)0.0f, (float)1.0f);
            }
        }
        RenderManager.instance.itemRenderer.renderItem(entityLivingBase, itemStack, 0, IItemRenderer.ItemRenderType.ENTITY);
        GL11.glPushMatrix();
    }

    public final boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType itemRenderType, ItemStack itemStack, IItemRenderer.ItemRendererHelper itemRendererHelper) {
        return false;
    }
}
