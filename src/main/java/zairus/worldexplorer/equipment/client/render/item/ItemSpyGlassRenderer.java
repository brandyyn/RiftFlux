/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.equipment.client.render.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.core.ClientProxy;
import zairus.worldexplorer.core.helpers.ColorHelper;
import zairus.worldexplorer.equipment.client.model.ModelSpyGlass;

@SideOnly(value=Side.CLIENT)
public class ItemSpyGlassRenderer
implements IItemRenderer {
    private static final ResourceLocation textures = new ResourceLocation("worldexplorer", "textures/model/spyglass.png");
    private ModelSpyGlass spyglassModel = new ModelSpyGlass();
    private static Minecraft mc = null;
    private static final RenderItem renderItem = new RenderItem();

    public boolean handleRenderType(ItemStack item, IItemRenderer.ItemRenderType type) {
        return type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED) || type.equals((Object)IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) || type.equals((Object)IItemRenderer.ItemRenderType.INVENTORY) || type.equals((Object)IItemRenderer.ItemRenderType.ENTITY);
    }

    public boolean shouldUseRenderHelper(IItemRenderer.ItemRenderType type, ItemStack item, IItemRenderer.ItemRendererHelper helper) {
        return helper.equals((Object)IItemRenderer.ItemRendererHelper.ENTITY_BOBBING) || helper.equals((Object)IItemRenderer.ItemRendererHelper.ENTITY_ROTATION);
    }

    public void renderItem(IItemRenderer.ItemRenderType type, ItemStack stack, Object ... data) {
        EntityLivingBase entity = null;
        if (data.length > 1 && data[1] instanceof EntityLivingBase) {
            entity = (EntityLivingBase)data[1];
        }
        if (mc == null) {
            mc = ClientProxy.mc;
        }
        switch (type) {
            case EQUIPPED: 
            case EQUIPPED_FIRST_PERSON: 
            case ENTITY: {
                float offsetX = -0.9f;
                float offsetY = -1.1f;
                float offsetZ = 0.0f;
                float angle = 210.0f;
                if (type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                    boolean flag = false;
                    if (entity instanceof EntityPlayer) {
                        EntityPlayer player = (EntityPlayer)entity;
                        if (player.getItemInUse() != null) {
                            flag = true;
                        }
                        if (flag && type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) {
                            return;
                        }
                    }
                    if (flag) {
                        angle -= 90.0f;
                        offsetY = (float)((double)offsetY - 0.5);
                        if (type == IItemRenderer.ItemRenderType.EQUIPPED) {
                            offsetX = (float)((double)offsetX + 1.05);
                            offsetZ = (float)((double)offsetZ + 0.12);
                        } else {
                            offsetX = (float)((double)offsetX + 0.8);
                            offsetZ = (float)((double)offsetZ - 0.4);
                        }
                    }
                }
                GL11.glPushMatrix();
                ItemSpyGlassRenderer.mc.renderEngine.bindTexture(textures);
                GL11.glRotatef((float)angle, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glTranslatef((float)offsetX, (float)offsetY, (float)offsetZ);
                this.spyglassModel.render((Entity)data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glTranslatef((float)(offsetX * -1.0f), (float)(offsetY * -1.0f), (float)(offsetZ * -1.0f));
                GL11.glRotatef((float)(angle * -1.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glPopMatrix();
                break;
            }
            case INVENTORY: {
                IIcon icon = stack.getItem().getIconFromDamage(0);
                int color = stack.getItem().getColorFromItemStack(stack, 0);
                ColorHelper.glSetColor(color, 1.0f);
                GL11.glDisable((int)2896);
                GL11.glEnable((int)3008);
                renderItem.renderIcon(0, 0, icon, 16, 16);
                GL11.glDisable((int)3008);
                GL11.glEnable((int)2896);
                break;
            }
        }
    }
}

