package com.voidsrift.riftflux.avatar.glider;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ItemGlider extends Item {
    private static final Map<String, Long> LAST_TOGGLE_TICK = new ConcurrentHashMap<String, Long>();
    private final int color;

    public ItemGlider(int color) {
        this.color = color;
        this.setCreativeTab(CreativeTabs.tabTransport);
        this.setTextureName(ItemGlider.getOpenTexture(this.color));
    }

    @Override
    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (player == null) {
            return stack;
        }
        String playerName = player.getDisplayName();
        long tick = player.worldObj != null ? player.worldObj.getTotalWorldTime() : 0L;
        Long last = LAST_TOGGLE_TICK.get(playerName);
        if (last != null && last.longValue() == tick) {
            return stack;
        }
        LAST_TOGGLE_TICK.put(playerName, tick);
        if (!GliderState.isPlayerGliding(playerName)) {
            GliderState.addGlidingPlayerName(playerName);
        } else {
            GliderState.removeGlidingPlayerName(playerName);
        }
        return stack;
    }

    public static String getOpenTexture(int color) {
        switch (color) {
            case 0:
                return "kieranvs_avatar:WhiteStaffIcon";
            case 1:
                return "kieranvs_avatar:OrangeStaffIcon";
            case 2:
                return "kieranvs_avatar:MagentaStaffIcon";
            case 3:
                return "kieranvs_avatar:LightBlueStaffIcon";
            case 4:
                return "kieranvs_avatar:YellowStaffIcon";
            case 5:
                return "kieranvs_avatar:LightGreenStaffIcon";
            case 6:
                return "kieranvs_avatar:PinkStaffIcon";
            case 7:
                return "kieranvs_avatar:GreyStaffIcon";
            case 8:
                return "kieranvs_avatar:LightGreyStaffIcon";
            case 9:
                return "kieranvs_avatar:CyanStaffIcon";
            case 10:
                return "kieranvs_avatar:PurpleStaffIcon";
            case 11:
                return "kieranvs_avatar:BlueStaffIcon";
            case 12:
                return "kieranvs_avatar:BrownStaffIcon";
            case 13:
                return "kieranvs_avatar:GreenStaffIcon";
            case 14:
                return "kieranvs_avatar:RedStaffIcon";
            case 15:
                return "kieranvs_avatar:BlackStaffIcon";
            default:
                return "kieranvs_avatar:OrangeStaffIcon";
        }
    }

    public int getColor() {
        return this.color;
    }
}
