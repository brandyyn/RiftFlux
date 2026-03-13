/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.world.World
 */
package inurosen.healaltar.common.items;

import com.voidsrift.riftflux.ModConfig;
import inurosen.healaltar.common.entity.ExtendedPlayer;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.world.World;

public class SoulHeart
extends Item {
    public SoulHeart() {
        this.setMaxStackSize(64);
        this.setUnlocalizedName("soulHeart");
        this.setTextureName("healingaltar:soulheart");
        this.setCreativeTab(CreativeTabs.tabMisc);
        this.setHasSubtypes(false);
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (!world.isRemote && !player.capabilities.isCreativeMode) {
            ExtendedPlayer props = ExtendedPlayer.get(player);
            if (props == null) {
                return stack;
            }

            float maxSoulHearts = 20.0f;
            if (player.getEntityAttribute(SharedMonsterAttributes.maxHealth) != null) {
                maxSoulHearts = (float)player.getEntityAttribute(SharedMonsterAttributes.maxHealth).getAttributeValue();
            }
            if (maxSoulHearts <= 0.0f) {
                maxSoulHearts = 20.0f;
            }

            float current = props.getSoulHearts();
            if (current >= maxSoulHearts) {
                props.setSoulHearts(maxSoulHearts);
                return stack;
            }

            float grantAmount = ModConfig.zeldaHeartsEnabled ? 4.0f : 2.0f;
            world.playSoundEffect(player.posX, player.posY, player.posZ, "random.orb", 1.0f, 0.5f);
            props.setSoulHearts(current + grantAmount);
            if (props.getSoulHearts() > current) {
                --stack.stackSize;
            }
        }
        return stack;
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean p4) {
        list.add(EnumChatFormatting.ITALIC + "Once used blocks all damage at the cost of one Soul Heart");
    }
}
