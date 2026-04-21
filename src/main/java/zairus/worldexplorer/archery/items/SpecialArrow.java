/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.MathHelper
 */
package zairus.worldexplorer.archery.items;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.Locale;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MathHelper;
import zairus.worldexplorer.core.items.WEItem;

public class SpecialArrow
extends WEItem {
    public static final String[] arrow_types = new String[]{"sharpened_stick", "stone_arrow", "flint_arrow", "iron_arrow", "diamond_arrow", "obsidian_arrow"};
    private static final float SPEED_STEP_PERCENT = 10.0F;
    private IIcon[] arrowIcons;

    public SpecialArrow() {
        this.setUnlocalizedName("specialarrow");
        this.setTextureName("worldexplorer:specialarrow");
        this.setCreativeTab(net.minecraft.creativetab.CreativeTabs.tabCombat);
        this.setHasSubtypes(true);
    }

    @SideOnly(value=Side.CLIENT)
    public IIcon getIconFromDamage(int damage) {
        int j = MathHelper.clamp_int((int)damage, (int)0, (int)(arrow_types.length - 1));
        return this.arrowIcons[j];
    }

    public String getUnlocalizedName(ItemStack stack) {
        int i = MathHelper.clamp_int((int)stack.getItemDamage(), (int)0, (int)(arrow_types.length - 1));
        return super.getUnlocalizedName() + "." + arrow_types[i];
    }

    @SideOnly(value=Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean advanced) {
        int type = normalizeArrowType(stack == null ? 0 : stack.getItemDamage());
        float speedPercent = (getSpeedMultiplier(type) - 1.0F) * 100.0F;
        int damageBonus = getDamageBonus(type);
        list.add(EnumChatFormatting.GRAY + "Arrow Speed: " + (speedPercent <= 0.0F ? "Normal" : "+" + formatPercent(speedPercent) + "%"));
        list.add(EnumChatFormatting.GRAY + "Arrow Damage: " + (damageBonus <= 0 ? "Normal" : "+" + damageBonus));
    }

    @SideOnly(value=Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTab, List list) {
        for (int i = 0; i < arrow_types.length; ++i) {
            if (isArrowTypeEnabled(i)) {
                list.add(new ItemStack(item, 1, i));
            }
        }
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister) {
        this.arrowIcons = new IIcon[arrow_types.length];
        for (int i = 0; i < arrow_types.length; ++i) {
            this.arrowIcons[i] = iconRegister.registerIcon(this.getIconString() + (i > 0 ? "_" + arrow_types[i] : ""));
        }
    }

    public static int normalizeArrowType(int type) {
        return MathHelper.clamp_int(type, 0, arrow_types.length - 1);
    }

    public static int getDamageBonus(int type) {
        return normalizeArrowType(type);
    }

    public static float getSpeedMultiplier(int type) {
        return 1.0F + normalizeArrowType(type) * (SPEED_STEP_PERCENT / 100.0F);
    }

    public static int getFirstEnabledArrowType() {
        for (int i = 0; i < arrow_types.length; i++) {
            if (isArrowTypeEnabled(i)) {
                return i;
            }
        }
        return -1;
    }

    public static boolean isArrowTypeEnabled(int type) {
        int normalized = normalizeArrowType(type);
        String[] disabled = ModConfig.riftExplorerDisabledLongbowArrows;
        if (disabled == null || disabled.length == 0) {
            return true;
        }
        for (int i = 0; i < disabled.length; i++) {
            if (matchesDisabledArrow(disabled[i], normalized)) {
                return false;
            }
        }
        return true;
    }

    private static boolean matchesDisabledArrow(String raw, int type) {
        String entry = normalizeConfigEntry(raw);
        if (entry == null) {
            return false;
        }
        String name = arrow_types[type];
        String shortName = name.endsWith("_arrow") ? name.substring(0, name.length() - "_arrow".length()) : name;
        return entry.equals(Integer.toString(type)) || entry.equals(name) || entry.equals(shortName);
    }

    private static String normalizeConfigEntry(String raw) {
        if (raw == null) {
            return null;
        }
        String normalized = raw.trim().toLowerCase(Locale.ROOT).replace('-', '_').replace(' ', '_');
        return normalized.length() == 0 ? null : normalized;
    }

    private static String formatPercent(float value) {
        float rounded = Math.round(value * 10.0F) / 10.0F;
        if (Math.abs(rounded - Math.round(rounded)) < 0.0001F) {
            return Integer.toString(Math.round(rounded));
        }
        return String.format(Locale.ROOT, "%.1f", rounded).replaceAll("0+$", "").replaceAll("\\.$", "");
    }
}
