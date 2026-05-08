package com.voidsrift.riftflux.vortex.lib.helper;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL14;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public final class ArmoredArmsGlintHelper {
    private static boolean subtractiveBlend;
    private static boolean armorGlintPass;
    private static ItemStack currentArmorStack;

    private ArmoredArmsGlintHelper() {}

    public static void applyHeldItemGlintArgs(Args args) {
        Minecraft mc = Minecraft.getMinecraft();
        ItemStack held = mc.thePlayer == null ? null : mc.thePlayer.getHeldItem();
        applyGlintArgs(held, args);
    }

    public static void applyItemGlintArgs(ItemStack itemStack, Args args) {
        applyGlintArgs(itemStack, args);
    }

    public static void applyChestArmorGlintArgs(Args args) {
        applyGlintArgs(getArmorStackForGlint(), args);
    }

    public static void applyArmorModelGlintArgs(Args args) {
        if (!armorGlintPass) {
            return;
        }
        applyGlint3fArgs(getArmorStackForGlint(), args);
    }

    public static void applyArmorModelGlintColorIfActive() {
        if (armorGlintPass) {
            applyArmorModelGlintColor();
        }
    }

    public static boolean hasVisibleEffect(ItemStack itemStack, int pass) {
        return itemStack != null && !isNullGlint(itemStack) && itemStack.hasEffect(pass);
    }

    public static List newArmorTextureListOrNull(Object armModelManagerArmor) {
        ItemStack armorStack = getRenderedArmorStack(armModelManagerArmor);
        currentArmorStack = armorStack;
        if (isNullGlint(armorStack)) {
            return new ArrayList();
        }
        if (!hasVisibleCustomGlint(armorStack)) {
            return null;
        }

        try {
            Class<?> textureEnchant = Class.forName("com.artur114.armoredarms.client.util.TextureEnchant");
            Object first = textureEnchant.getField("FIRST").get(null);
            Object second = textureEnchant.getField("SECOND").get(null);
            return new ArrayList(Arrays.asList(first, second));
        } catch (Throwable ignored) {
            return null;
        }
    }

    public static void resetBlendEquation() {
        if (subtractiveBlend) {
            GL14.glBlendEquation(GL14.GL_FUNC_ADD);
            subtractiveBlend = false;
        }
    }

    public static void beginArmorGlintPass() {
        armorGlintPass = true;
    }

    public static void endArmorGlintPass() {
        armorGlintPass = false;
        resetBlendEquation();
    }

    public static boolean hasVisibleCustomGlint(ItemStack itemStack) {
        return EnchantHelper.hasCustomGlint(itemStack)
                && itemStack.getTagCompound().getInteger(EnchantHelper.CUSTOM_GLINT_TAG) != 16;
    }

    public static boolean isNullGlint(ItemStack itemStack) {
        return EnchantHelper.hasCustomGlint(itemStack)
                && itemStack.getTagCompound().getInteger(EnchantHelper.CUSTOM_GLINT_TAG) == 16;
    }

    private static ItemStack getChestArmor() {
        Minecraft mc = Minecraft.getMinecraft();
        return mc.thePlayer == null ? null : mc.thePlayer.getCurrentArmor(2);
    }

    private static ItemStack getArmorStackForGlint() {
        ItemStack liveChest = getChestArmor();
        if (EnchantHelper.hasCustomGlint(liveChest)) {
            return liveChest;
        }
        return currentArmorStack != null ? currentArmorStack : liveChest;
    }

    private static ItemStack getRenderedArmorStack(Object armModelManagerArmor) {
        ItemStack reflectedStack = reflectRenderedArmorStack(armModelManagerArmor);
        return reflectedStack != null ? reflectedStack : getChestArmor();
    }

    private static ItemStack reflectRenderedArmorStack(Object armModelManagerArmor) {
        if (armModelManagerArmor == null) {
            return null;
        }
        try {
            Object itemStackAA = armModelManagerArmor.getClass().getField("stack").get(armModelManagerArmor);
            if (itemStackAA == null) {
                return null;
            }
            Object stack = itemStackAA.getClass().getMethod("stack").invoke(itemStackAA);
            return stack instanceof ItemStack ? (ItemStack) stack : null;
        } catch (Throwable ignored) {
            return null;
        }
    }

    private static void applyGlintArgs(ItemStack itemStack, Args args) {
        subtractiveBlend = false;
        if (!EnchantHelper.hasCustomGlint(itemStack)) {
            return;
        }

        GlintColor color = resolveColor(itemStack.getTagCompound().getInteger(EnchantHelper.CUSTOM_GLINT_TAG));
        if (color.subtractive) {
            GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
            subtractiveBlend = true;
        }
        args.set(0, color.red);
        args.set(1, color.green);
        args.set(2, color.blue);
        args.set(3, 1.0F);
    }

    private static void applyGlint3fArgs(ItemStack itemStack, Args args) {
        subtractiveBlend = false;
        if (!EnchantHelper.hasCustomGlint(itemStack)) {
            return;
        }

        GlintColor color = resolveColor(itemStack.getTagCompound().getInteger(EnchantHelper.CUSTOM_GLINT_TAG));
        if (color.subtractive) {
            GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
            subtractiveBlend = true;
        }
        args.set(0, color.red);
        args.set(1, color.green);
        args.set(2, color.blue);
    }

    public static void applyArmorModelGlintColor() {
        subtractiveBlend = false;
        if (!armorGlintPass) {
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            return;
        }

        ItemStack armorStack = getArmorStackForGlint();
        if (!EnchantHelper.hasCustomGlint(armorStack)) {
            GL11.glColor3f(1.0F, 1.0F, 1.0F);
            return;
        }

        GlintColor color = resolveColor(armorStack.getTagCompound().getInteger(EnchantHelper.CUSTOM_GLINT_TAG));
        if (color.subtractive) {
            GL14.glBlendEquation(GL14.GL_FUNC_REVERSE_SUBTRACT);
            subtractiveBlend = true;
        }
        GL11.glColor3f(color.red, color.green, color.blue);
    }

    private static GlintColor resolveColor(int customGlint) {
        switch (customGlint) {
            case 7:
                return new GlintColor(0.36F, 0.36F, 0.36F, true);
            case 8:
                return new GlintColor(0.36F, 0.36F, 0.36F, false);
            case 11:
                return new GlintColor(0.72F, 0.39F, 0.02F, true);
            case 12:
                return new GlintColor(0.35F, 0.48F, 0.57F, true);
            case 13:
                return new GlintColor(0.54F, 0.22F, 0.57F, true);
            case 15:
                return new GlintColor(0.52F, 0.52F, 0.52F, true);
            case 16:
                return new GlintColor(0.0F, 0.0F, 0.0F, false);
            default:
                float[] colors = customGlint >= 0 && customGlint <= 15
                        ? EnchantHelper.generateColorsForGlint(ItemDye.field_150922_c[15 - customGlint])
                        : EnchantHelper.generateColorsForGlint(customGlint);
                return new GlintColor(colors[0], colors[1], colors[2], false);
        }
    }

    private static final class GlintColor {
        private final float red;
        private final float green;
        private final float blue;
        private final boolean subtractive;

        private GlintColor(float red, float green, float blue, boolean subtractive) {
            this.red = red;
            this.green = green;
            this.blue = blue;
            this.subtractive = subtractive;
        }
    }
}
