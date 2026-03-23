/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.GameRegistry
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Items
 *  net.minecraft.item.EnumAction
 *  net.minecraft.item.EnumRarity
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.IIcon
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.World
 */
package net.nmccoy.legendgear.item;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.List;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.EnumAction;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.StarSpirit;
import net.nmccoy.legendgear.entity.EntitySpellEffect;
import net.nmccoy.legendgear.item.LGItem;
import net.nmccoy.legendgear.magic.IMana;

public class SpiritEmblem
extends LGItem
implements IMana {
    public SpiritEmblem() {
        this.setHasSubtypes(true);
        this.tabs.add(CreativeTabs.tabMisc);
        this.setMaxStackSize(1);
    }

    public EnumAction getItemUseAction(ItemStack p_77661_1_) {
        return EnumAction.block;
    }

    public int getMaxItemUseDuration(ItemStack stack) {
        if (stack.getItemDamage() != 0) {
            return 65535;
        }
        return 0;
    }

    @SideOnly(value=Side.CLIENT)
    public void registerIcons(IIconRegister par1IconRegister) {
        for (EmblemStates state : EmblemStates.values()) {
            state.icon = par1IconRegister.registerIcon("legendgear:" + state.resourceName);
        }
    }

    public String getUnlocalizedName(ItemStack stack) {
        int damage = stack.getItemDamage();
        return "item." + EmblemStates.values()[damage].unlocalizedName;
    }

    public IIcon getIconFromDamage(int damage) {
        return EmblemStates.values()[damage].icon;
    }

    public ItemStack onItemRightClick(ItemStack stack, World world, EntityPlayer player) {
        if (stack.getItemDamage() != 0) {
            player.setItemInUse(stack, this.getMaxItemUseDuration(stack));
        }
        return stack;
    }

    public void onUsingTick(ItemStack stack, EntityPlayer player, int count) {
        if (stack.getItemDamage() != 1) {
            return;
        }
        if (player.worldObj.isRemote) {
            return;
        }
        int clicks = 0;
        if (count % 10 == 0) {
            clicks = count / 10;
            float investment = (float)clicks / 20.0f + 0.5f;
            PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
            pse.expendMana(stack, 1.0f);
            if (player.getHealth() <= 5.0f && StarSpirit.attemptIntervention(0, player, 100, investment)) {
                this.phoenixInterveneEffect(player);
                player.setHealth(player.getMaxHealth());
            } else if (player.getFoodStats().getFoodLevel() <= 5 && StarSpirit.attemptIntervention(0, player, 100, investment)) {
                this.phoenixInterveneEffect(player);
                player.getFoodStats().setFoodLevel(20);
            } else {
                boolean intervened;
                float radius = 8.0f;
                AxisAlignedBB searchBox = AxisAlignedBB.getBoundingBox((double)player.posX, (double)player.posY, (double)player.posZ, (double)player.posX, (double)player.posY, (double)player.posZ);
                searchBox = searchBox.expand((double)radius, (double)radius, (double)radius);
                List list = player.worldObj.getEntitiesWithinAABB(EntityLivingBase.class, searchBox);
                int undead = 0;
                int players = 0;
                for (Object o : list) {
                    EntityLivingBase elb = (EntityLivingBase)o;
                    float distance = elb.getDistanceToEntity((Entity)player);
                    if (!(distance <= 8.0f) || elb == player) continue;
                    if (elb.isEntityUndead()) {
                        ++undead;
                    }
                    if (!(elb instanceof EntityPlayer)) continue;
                    ++players;
                }
                if (undead >= 3 && players == 0 && (intervened = StarSpirit.attemptIntervention(0, player, 200, investment))) {
                    player.worldObj.spawnEntityInWorld((Entity)new EntitySpellEffect(player.worldObj, EntitySpellEffect.SpellType.Rayfire, player, Vec3.createVectorHelper((double)player.posX, (double)player.posY, (double)player.posZ), radius, 20.0, true));
                }
            }
        }
    }

    private void phoenixInterveneEffect(EntityPlayer player) {
        net.nmccoy.legendgear.LegendGear2.addConfiguredPotionEffect(player, net.nmccoy.legendgear.LegendGear2.CONFIG_PHOENIX_EMBLEM_FIRE_RESISTANCE_POTION_ID, Potion.fireResistance, 20, 0, false);
        player.setFire(1);
        player.worldObj.playSoundAtEntity((Entity)player, "legendgear:revive", 1.0f, 1.0f);
    }

    public IIcon getIcon(ItemStack stack, int renderPass, EntityPlayer player, ItemStack usingItem, int useRemaining) {
        if (stack.getItemDamage() == 1) {
            if (usingItem == stack) {
                return EmblemStates.PHOENIX_CHARGED.icon;
            }
            return EmblemStates.PHOENIX.icon;
        }
        return EmblemStates.BLANK.icon;
    }

    public void getSubItems(Item p_150895_1_, CreativeTabs p_150895_2_, List list) {
        list.add(new ItemStack((Item)this, 1, 0));
        list.add(new ItemStack((Item)this, 1, 1));
    }

    @Override
    public float getManaCost() {
        return 1.0f;
    }

    public EnumRarity getRarity(ItemStack stack) {
        return stack.getItemDamage() > 0 ? EnumRarity.uncommon : EnumRarity.common;
    }

    public void addRecipes() {
        GameRegistry.addShapedRecipe((ItemStack)new ItemStack((Item)this), (Object[])new Object[]{" G ", "GGG", " G ", Items.gold_ingot});
    }

    public static enum EmblemStates {
        BLANK("blankEmblem", "blankEmblem"),
        PHOENIX("phoenixEmblem", "phoenixEmblem"),
        PHOENIX_CHARGED("phoenixEmblem", "phoenixEmblemFlicker");

        public IIcon icon;
        public String unlocalizedName;
        public String resourceName;

        private EmblemStates(String name, String resource) {
            this.unlocalizedName = name;
            this.resourceName = resource;
        }
    }
}
