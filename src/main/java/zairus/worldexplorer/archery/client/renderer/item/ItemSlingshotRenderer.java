package zairus.worldexplorer.archery.client.renderer.item;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.IItemRenderer;
import org.lwjgl.opengl.GL11;
import zairus.worldexplorer.archery.items.SlingshotAmmoHelper;
import zairus.worldexplorer.core.helpers.ColorHelper;

@SideOnly(value=Side.CLIENT)
public class ItemSlingshotRenderer implements IItemRenderer {
    private static final RenderItem RENDER_ITEM = new RenderItem();
    private static boolean suppressAmmoOverlay;

    public static void setSuppressAmmoOverlay(boolean suppress) {
        suppressAmmoOverlay = suppress;
    }

    @Override
    public boolean handleRenderType(ItemStack item, ItemRenderType type) {
        return type == ItemRenderType.INVENTORY;
    }

    @Override
    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper) {
        return false;
    }

    @Override
    public void renderItem(ItemRenderType type, ItemStack stack, Object... data) {
        if (stack == null || stack.getItem() == null) {
            return;
        }

        renderIcon(stack, 0, 0, 16, 16);

        if (suppressAmmoOverlay || isNeiRenderCall()) {
            return;
        }

        Minecraft mc = Minecraft.getMinecraft();
        EntityPlayer player = mc == null ? null : mc.thePlayer;
        if (player == null) {
            return;
        }
        if (!shouldRenderAmmoOverlay(player, stack)) {
            return;
        }

        SlingshotAmmoHelper.AmmoSelection selection = SlingshotAmmoHelper.peekAmmo(player);
        ItemStack ammoStack = selection == null ? null : selection.getAmmoStack();
        if (ammoStack == null || ammoStack.getItem() == null) {
            return;
        }

        int[] corner = getOverlayCorner();
        GL11.glPushMatrix();
        GL11.glTranslatef((float)corner[0], (float)corner[1], 0.0f);
        GL11.glScalef(0.5f, 0.5f, 1.0f);
        renderStack(ammoStack, 0, 0);
        GL11.glPopMatrix();
    }

    private static int[] getOverlayCorner() {
        String corner = ModConfig.riftExplorerSlingshotAmmoIconCorner;
        if (corner == null) {
            corner = "top_right";
        }
        String normalized = corner.trim().toLowerCase(Locale.ROOT).replace(' ', '_').replace('-', '_');
        if ("top_left".equals(normalized) || "left_top".equals(normalized)) {
            return new int[]{0, 0};
        }
        if ("bottom_left".equals(normalized) || "left_bottom".equals(normalized)) {
            return new int[]{0, 8};
        }
        if ("bottom_right".equals(normalized) || "right_bottom".equals(normalized)) {
            return new int[]{8, 8};
        }
        return new int[]{8, 0};
    }

    private static boolean isNeiRenderCall() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for (int i = 0; i < stackTrace.length; i++) {
            String className = stackTrace[i].getClassName();
            if (className != null && className.toLowerCase(Locale.ROOT).contains("codechicken.nei")) {
                return true;
            }
        }
        return false;
    }

    private static boolean shouldRenderAmmoOverlay(EntityPlayer player, ItemStack stack) {
        if (player == null || stack == null) {
            return false;
        }
        ItemStack heldStack = player.getCurrentEquippedItem();
        if (heldStack == null || heldStack.getItem() != stack.getItem()) {
            return false;
        }
        return heldStack == stack;
    }

    private static void renderIcon(ItemStack stack, int x, int y, int width, int height) {
        IIcon icon = stack.getItem().getIcon(stack, 0);
        if (icon == null) {
            return;
        }
        int color = stack.getItem().getColorFromItemStack(stack, 0);
        ColorHelper.glSetColor(color, 1.0f);
        GL11.glDisable((int)2896);
        GL11.glEnable((int)3008);
        RENDER_ITEM.renderIcon(x, y, icon, width, height);
        GL11.glDisable((int)3008);
        GL11.glEnable((int)2896);
    }

    private static void renderStack(ItemStack stack, int x, int y) {
        if (stack == null || stack.getItem() == null) {
            return;
        }
        Minecraft mc = Minecraft.getMinecraft();
        if (mc == null) {
            return;
        }
        RenderItem renderItem = RenderItem.getInstance();
        float oldZ = renderItem.zLevel;
        renderItem.zLevel = 300.0f;
        renderItem.renderItemAndEffectIntoGUI(mc.fontRenderer, mc.getTextureManager(), stack, x, y);
        renderItem.zLevel = oldZ;
        GL11.glDisable((int)2896);
    }
}
