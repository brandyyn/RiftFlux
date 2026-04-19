package zairus.worldexplorer.archery.client.renderer.item;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.archery.items.WEArcheryItems;
import zairus.worldexplorer.archery.items.WEItemRanged;
import zairus.worldexplorer.core.ClientProxy;
import zairus.worldexplorer.core.helpers.ColorHelper;

@SideOnly(Side.CLIENT)
public class ItemBowRenderer implements IItemRenderer {
    private static final ResourceLocation ITEM_GLINT = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    private static final Tessellator tessellator = Tessellator.instance;
    private static Minecraft mc = null;
    private static final RenderItem renderItem = new RenderItem();

    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type.equals(ItemRenderType.EQUIPPED)
                || type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON)
                || type.equals(ItemRenderType.INVENTORY)
                || type.equals(ItemRenderType.ENTITY);
    }

    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return helper.equals(ItemRendererHelper.ENTITY_BOBBING) || helper.equals(ItemRendererHelper.ENTITY_ROTATION);
    }

    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        EntityLivingBase entity = null;
        if (data.length > 1 && data[1] instanceof EntityLivingBase) {
            entity = (EntityLivingBase) data[1];
        }
        if (mc == null) {
            mc = ClientProxy.mc;
        }
        Item item = stack.getItem();
        if (type.equals(ItemRenderType.INVENTORY)) {
            GL11.glDisable(GL11.GL_LIGHTING);
            for (int pass = 0; pass < 2; ++pass) {
                EntityPlayer player;
                IIcon icon = entity != null && entity instanceof EntityPlayer
                        ? ((player = (EntityPlayer) entity).getItemInUse() != null
                                ? stack.getItem().getIcon(stack, pass, player, player.getItemInUse(), player.getItemInUseCount())
                                : stack.getItem().getIcon(stack, pass))
                        : stack.getItem().getIcon(stack, pass);
                if (icon != null) {
                    int color = item.getColorFromItemStack(stack, pass);
                    ColorHelper.glSetColor(color, 1.0F);
                    GL11.glDisable(GL11.GL_LIGHTING);
                    GL11.glEnable(GL11.GL_ALPHA_TEST);
                    renderItem.renderIcon(0, 0, icon, 16, 16);
                    GL11.glDisable(GL11.GL_ALPHA_TEST);
                    GL11.glEnable(GL11.GL_LIGHTING);
                }
                if (pass != 0 || !stack.hasEffect(pass)) {
                    continue;
                }
                GL11.glPushMatrix();
                GL11.glDepthFunc(GL11.GL_EQUAL);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDepthMask(false);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
                GL11.glColor4f(0.5F, 0.25F, 0.8F, 1.0F);
                mc.renderEngine.bindTexture(ITEM_GLINT);
                this.renderGlint(-2, -2, 20, 20);
                GL11.glDisable(GL11.GL_BLEND);
                GL11.glDepthMask(true);
                GL11.glEnable(GL11.GL_LIGHTING);
                GL11.glDepthFunc(GL11.GL_LEQUAL);
                GL11.glPopMatrix();
            }
            GL11.glEnable(GL11.GL_LIGHTING);
        } else if (type.equals(ItemRenderType.ENTITY)) {
            GL11.glTranslatef(-0.5F, -0.25F, 0.04F);
            GL11.glPushMatrix();
            for (int pass = 0; pass < 2; ++pass) {
                IIcon icon = item.getIcon(stack, pass);
                if (icon == null) {
                    continue;
                }
                int color = item.getColorFromItemStack(stack, pass);
                ColorHelper.glSetColor(color);
                float scale = pass == 0 ? 1.4F : 0.8F;
                float thickness = pass == 0 ? 0.08F : 0.06F;
                GL11.glPushMatrix();
                if (pass > 0) {
                    GL11.glTranslatef(-0.72F, -0.72F, 0.06F);
                }
                GL11.glScalef(scale, scale, scale);
                this.drawItem(icon, thickness);
                GL11.glPopMatrix();
            }
            GL11.glPopMatrix();
        } else if (type.equals(ItemRenderType.EQUIPPED) || type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON)) {
            GL11.glPushMatrix();
            int iconOffset = 0;
            int size = 1;
            float scale = 1.5F;
            int arrowStep = 1;
            float px = 1.0F / (float) (16 * size);
            float scaleOffsetX = 0.4F;
            float scaleOffsetY = 1.2F;
            boolean thirdPerson = !type.equals(ItemRenderType.EQUIPPED_FIRST_PERSON);
            boolean ifp = false;
            GL11.glPushMatrix();
            GL11.glTranslatef(scaleOffsetX, scaleOffsetY, 0.0F);
            GL11.glScalef(scale, scale, 1.0F);
            GL11.glTranslatef(-scaleOffsetX, -scaleOffsetY, 0.0F);
            IIcon arrowIcon = null;
            for (int pass = 0; pass < 2; ++pass) {
                IIcon icon;
                int useCount = 0;
                if (entity instanceof EntityPlayer) {
                    EntityPlayer player = (EntityPlayer) entity;
                    if (player.getItemInUse() != null) {
                        icon = stack.getItem().getIcon(stack, pass, player, player.getItemInUse(), player.getItemInUseCount());
                        ItemStack arrowStack = WEItemRanged.getAmmo(stack, player);
                        if (arrowStack == null) {
                            arrowStack = new ItemStack(WEArcheryItems.specialarrow, 1, 1);
                        }
                        arrowIcon = arrowStack.getItem().getIconFromDamage(arrowStack.getItemDamage());
                        useCount = stack.getItem().getMaxItemUseDuration(stack) - player.getItemInUseCount();
                    } else {
                        icon = stack.getItem().getIcon(stack, pass);
                    }
                } else {
                    icon = stack.getItem().getIcon(stack, pass);
                }
                this.drawItem(icon, 0.09375F);
                if (arrowIcon == null) {
                    continue;
                }
                float normalizedUse = (float) useCount / 20.0F;
                if (normalizedUse > 1.0F) {
                    normalizedUse = 1.0F;
                }
                float aX = 0.2F * (1.0F - normalizedUse);
                float aY = 1.1F + 0.2F * (1.0F - normalizedUse);
                float aZ = thirdPerson ? 0.04F : -0.04F;
                GL11.glTranslatef(aX, aY, aZ);
                GL11.glRotatef(-90.0F, 0.0F, 0.0F, 1.0F);
                this.drawItem(arrowIcon, 0.09375F);
                GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
                GL11.glTranslatef(-aX, -aY, -aZ);
            }
            GL11.glPopMatrix();
            GL11.glTranslatef(scaleOffsetX, scaleOffsetY, 0.0F);
            GL11.glScalef(scale, scale, 1.0F);
            GL11.glTranslatef(-scaleOffsetX, -scaleOffsetY, 0.0F);
            GL11.glPushMatrix();
            if (iconOffset > 0) {
                if (thirdPerson && ifp) {
                    GL11.glRotatef(5.0F, 1.0F, -1.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, -0.03F);
                } else {
                    GL11.glRotatef(-5.0F, 1.0F, -1.0F, 0.0F);
                    GL11.glTranslatef(0.0F, 0.0F, 0.03F);
                }
                float offset = (float) (-(iconOffset - 3) * arrowStep) * px;
                GL11.glTranslatef(offset, offset, 0.0F);
            }
            GL11.glMatrixMode(GL11.GL_MODELVIEW);
            GL11.glPopMatrix();
            GL11.glPopMatrix();
        }
    }

    private void drawItem(IIcon icon, float thickness) {
        float xStart = icon.getMinU();
        float xEnd = icon.getMaxU();
        float yStart = icon.getMinV();
        float yEnd = icon.getMaxV();
        int height = icon.getIconHeight();
        int width = icon.getIconWidth();
        ItemRenderer.renderItemIn2D(tessellator, xEnd, yStart, xStart, yEnd, width, height, thickness);
    }

    private void renderGlint(int x, int y, int width, int height) {
        for (int j1 = 0; j1 < 2; ++j1) {
            GL11.glBlendFunc(GL11.GL_SRC_COLOR, GL11.GL_ONE);
            float f = 0.0039063F;
            float f1 = 0.0039063F;
            float f2 = (float) (Minecraft.getGLMaximumTextureSize() % (3000 + j1 * 1873))
                    / (3000.0F + (float) (j1 * 1873)) * 256.0F;
            float f3 = 0.0F;
            Tessellator tessellator = Tessellator.instance;
            float f4 = j1 == 1 ? -1.0F : 4.0F;
            tessellator.startDrawingQuads();
            tessellator.addVertexWithUV(x, y + height, -50.0D, (f2 + (float) height * f4) * f, (f3 + (float) height) * f1);
            tessellator.addVertexWithUV(x + width, y + height, -50.0D, (f2 + (float) width + (float) height * f4) * f, (f3 + (float) height) * f1);
            tessellator.addVertexWithUV(x + width, y, -50.0D, (f2 + (float) width) * f, (f3 + 0.0F) * f1);
            tessellator.addVertexWithUV(x, y, -50.0D, (f2 + 0.0F) * f, (f3 + 0.0F) * f1);
            tessellator.draw();
        }
    }
}
