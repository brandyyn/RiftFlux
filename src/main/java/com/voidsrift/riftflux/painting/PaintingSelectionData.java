package com.voidsrift.riftflux.painting;

import net.minecraft.entity.item.EntityPainting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public final class PaintingSelectionData {

    private static final String ROOT_TAG = "RiftFluxPainting";
    private static final String KEY_SELECTED_MOTIVE = "SelectedMotive";

    private PaintingSelectionData() {}

    public static String getSelectedMotive(EntityPlayer player) {
        NBTTagCompound root = getRootTag(player);
        if (root == null || !root.hasKey(KEY_SELECTED_MOTIVE)) {
            return null;
        }
        String motive = root.getString(KEY_SELECTED_MOTIVE);
        if (motive == null) {
            return null;
        }
        motive = motive.trim();
        return motive.isEmpty() ? null : motive;
    }

    public static void setSelectedMotive(EntityPlayer player, String motive) {
        NBTTagCompound root = getOrCreateRootTag(player);
        if (root == null) {
            return;
        }
        if (motive == null || motive.trim().isEmpty()) {
            root.removeTag(KEY_SELECTED_MOTIVE);
        } else {
            root.setString(KEY_SELECTED_MOTIVE, motive.trim());
        }
    }

    public static EntityPainting.EnumArt resolveSelectedArt(EntityPlayer player) {
        String motive = getSelectedMotive(player);
        if (motive == null) {
            return null;
        }
        for (EntityPainting.EnumArt art : EntityPainting.EnumArt.values()) {
            if (art.title.equals(motive)) {
                return art;
            }
        }
        return null;
    }

    private static NBTTagCompound getRootTag(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        NBTTagCompound persisted = getPersistedTag(player, false);
        if (persisted == null || !persisted.hasKey(ROOT_TAG)) {
            return null;
        }
        return persisted.getCompoundTag(ROOT_TAG);
    }

    private static NBTTagCompound getOrCreateRootTag(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        NBTTagCompound persisted = getPersistedTag(player, true);
        if (persisted == null) {
            return null;
        }
        if (!persisted.hasKey(ROOT_TAG)) {
            persisted.setTag(ROOT_TAG, new NBTTagCompound());
        }
        return persisted.getCompoundTag(ROOT_TAG);
    }

    private static NBTTagCompound getPersistedTag(EntityPlayer player, boolean create) {
        if (player == null) {
            return null;
        }
        NBTTagCompound data = player.getEntityData();
        if (data == null) {
            return null;
        }
        if (!data.hasKey(EntityPlayer.PERSISTED_NBT_TAG)) {
            if (!create) {
                return null;
            }
            data.setTag(EntityPlayer.PERSISTED_NBT_TAG, new NBTTagCompound());
        }
        return data.getCompoundTag(EntityPlayer.PERSISTED_NBT_TAG);
    }
}
