/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.entity.RenderItem
 *  net.minecraft.client.renderer.entity.RenderManager
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.ResourceLocation
 *  net.minecraft.world.World
 *  net.minecraftforge.client.IItemRenderer
 *  net.minecraftforge.client.IItemRenderer$ItemRenderType
 *  net.minecraftforge.client.IItemRenderer$ItemRendererHelper
 *  org.lwjgl.opengl.GL11
 */
package zairus.worldexplorer.archery.client.renderer.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.archery.client.model.ModelCrossBow;
import zairus.worldexplorer.archery.items.CrossBow;
import zairus.worldexplorer.archery.items.WEArcheryItems;
import zairus.worldexplorer.archery.items.WEItemRanged;
import zairus.worldexplorer.core.ClientProxy;
import zairus.worldexplorer.core.helpers.ColorHelper;

@SideOnly(value=Side.CLIENT)
public class ItemCrossBowRenderer
implements IItemRenderer {
    private static final ResourceLocation blowPipeTextures = new ResourceLocation("worldexplorer", "textures/model/crossbow_textures.png");
    private ModelCrossBow crossBowModel = new ModelCrossBow();
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
                float angleY = 0.0f;
                float angleZ = 210.0f;
                boolean flag = false;
                float stagePercent = 0.0f;
                ItemStack arrowStack = null;
                boolean pulling = false;
                boolean hasArrow = false;
                if (entity != null && entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer)entity;
                    arrowStack = WEItemRanged.getAmmo(stack, player);
                    hasArrow = ((CrossBow)stack.getItem()).getBowHasAwwor(stack);
                    boolean bl = pulling = player.getItemInUse() != null || hasArrow;
                    if (player.getItemInUse() != null) {
                        flag = true;
                        float maxUse = 20.0f;
                        int useCount = stack.getItem().getMaxItemUseDuration(stack) - player.getItemInUseCount();
                        stagePercent = (float)useCount / maxUse;
                        if (stagePercent > 1.0f) {
                            stagePercent = 1.0f;
                        }
                    }
                    if (hasArrow) {
                        stagePercent = 1.0f;
                    }
                }
                if ((type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) && flag) {
                    angleY -= 90.0f;
                    angleZ -= 90.0f;
                    offsetX += 1.0f;
                    offsetY -= 0.6f;
                }
                GL11.glPushMatrix();
                ItemCrossBowRenderer.mc.renderEngine.bindTexture(blowPipeTextures);
                GL11.glRotatef((float)angleZ, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glRotatef((float)angleY, (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glTranslatef((float)offsetX, (float)offsetY, (float)offsetZ);
                this.crossBowModel.setStage(stagePercent);
                this.crossBowModel.render((Entity)data[1], 0.0f, 0.0f, 0.0f, 0.0f, 0.0f, 0.0625f);
                GL11.glTranslatef((float)(offsetX * -1.0f), (float)(offsetY * -1.0f), (float)(offsetZ * -1.0f));
                GL11.glRotatef((float)(angleY * -1.0f), (float)0.0f, (float)1.0f, (float)0.0f);
                GL11.glRotatef((float)(angleZ * -1.0f), (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glPopMatrix();
                if (!pulling) break;
                arrowStack = arrowStack == null ? new ItemStack((Item)WEArcheryItems.specialarrow, 1, 1) : new ItemStack(arrowStack.getItem(), 1, arrowStack.getItemDamage());
                GL11.glPushMatrix();
                float arrowScale = 2.0f;
                float arrowAngleZ = 75.0f;
                offsetX = 0.42f;
                offsetY = -0.43f;
                offsetZ = 0.0f;
                if ((type == IItemRenderer.ItemRenderType.EQUIPPED || type == IItemRenderer.ItemRenderType.EQUIPPED_FIRST_PERSON) && flag) {
                    arrowAngleZ -= 90.0f;
                    offsetX -= 0.31f;
                    offsetY += 0.44f;
                }
                EntityItem arrow = new EntityItem((World)ItemCrossBowRenderer.mc.theWorld, 0.0, 0.0, 0.0, arrowStack);
                arrow.hoverStart = 0.0f;
                RenderItem.renderInFrame = true;
                GL11.glRotatef((float)arrowAngleZ, (float)0.0f, (float)0.0f, (float)1.0f);
                GL11.glScalef((float)arrowScale, (float)arrowScale, (float)arrowScale);
                GL11.glTranslatef((float)offsetX, (float)offsetY, (float)offsetZ);
                RenderManager.instance.renderEntityWithPosYaw((Entity)arrow, 0.0, 0.0, 0.0, 0.0f, 0.0f);
                GL11.glTranslatef((float)(-offsetX), (float)(-offsetY), (float)(-offsetZ));
                GL11.glScalef((float)(1.0f / arrowScale), (float)(1.0f / arrowScale), (float)(1.0f / arrowScale));
                GL11.glRotatef((float)(-arrowAngleZ), (float)0.0f, (float)0.0f, (float)1.0f);
                RenderItem.renderInFrame = false;
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

