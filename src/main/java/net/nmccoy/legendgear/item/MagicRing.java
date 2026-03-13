/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  baubles.api.BaubleType
 *  baubles.api.BaublesApi
 *  baubles.api.IBauble
 *  cpw.mods.fml.common.Loader
 *  cpw.mods.fml.common.Optional$Interface
 *  cpw.mods.fml.common.Optional$Method
 *  cpw.mods.fml.common.registry.GameRegistry
 *  net.minecraft.block.Block
 *  net.minecraft.block.material.Material
 *  net.minecraft.client.renderer.texture.IIconRegister
 *  net.minecraft.client.resources.I18n
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.init.Items
 *  net.minecraft.item.Item
 *  net.minecraft.item.ItemStack
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.IIcon
 */
package net.nmccoy.legendgear.item;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import cpw.mods.fml.common.Loader;
import cpw.mods.fml.common.Optional;
import cpw.mods.fml.common.registry.GameRegistry;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.IIcon;
import net.nmccoy.legendgear.LGUtil;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerStarstatsExtension;
import net.nmccoy.legendgear.item.ItemNucleus;
import net.nmccoy.legendgear.item.LGItem;

@Optional.Interface(iface="baubles.api.IBauble", modid="Baubles")
public class MagicRing
extends LGItem
implements IBauble {
    public static float SPRINT_RING_COST = 0.1f;
    public static float THIEF_RING_COST = 0.1f;
    public static float CONVECTION_RING_COST = 0.075f;
    public static float FALL_RING_COST = 1.5f;
    public static float RESONANCE_FACTOR = 0.5f;
    public static float MAGE_RING_FACTOR = 0.66f;
    public static float MAGE_AND_RESONANCE_FACTOR = 0.5f;
    public static float WARRIOR_DAMAGE_BOOST = 4.0f;
    public static float WARRIOR_MANA_COST = 4.0f;
    public static float COLDFEET_RING_COST = 0.1f;
    public IIcon goldRingIcon;
    public IIcon ironRingIcon;
    public IIcon woodRingIcon;
    public IIcon ringOrbBaseIcon;
    public IIcon ringOrbOverlayIcon;
    public IIcon ringOrbFillIcon;
    private static int STARGLASS = 2;
    private static int IRON = 1;
    private static int GOLD = 0;
    private static int WOOD = 4;
    private static final UUID ringSpeedBoostModifierUUID = UUID.fromString("9e93209c-4f07-4e26-a150-659bfc44feb1");
    private static final AttributeModifier ringSpeedBoostModifier = new AttributeModifier(ringSpeedBoostModifierUUID, "Sprinting ring speed boost", 0.5, 1).setSaved(false);
    private static final String DASH_AIR_JUMPS_KEY = "riftfluxDashAirJumps";
    private static final String DASH_LAST_GROUNDED_KEY = "riftfluxDashLastGrounded";
    private static final String DASH_PREV_MOTION_Y_KEY = "riftfluxDashPrevMotionY";

    public MagicRing() {
        this.setUnlocalizedName("magicRing");
        this.hasSubtypes = true;
        this.setMaxStackSize(1);
        this.setTextureName("legendgear:goldRing");
    }

    public static boolean PlayerWears(EntityPlayer player, RingType type) {
        if (Loader.isModLoaded((String)"Baubles")) {
            ItemStack stack1 = BaublesApi.getBaubles((EntityPlayer)player).getStackInSlot(1);
            ItemStack stack2 = BaublesApi.getBaubles((EntityPlayer)player).getStackInSlot(2);
            if (stack1 != null && stack1.getItem() == LegendGear2.magicRing && stack1.getItemDamage() == type.ordinal()) {
                return true;
            }
            if (stack2 != null && stack2.getItem() == LegendGear2.magicRing && stack2.getItemDamage() == type.ordinal()) {
                return true;
            }
        }
        return false;
    }

    public void registerIcons(IIconRegister ireg) {
        this.ironRingIcon = ireg.registerIcon("legendgear:ironRing");
        this.goldRingIcon = ireg.registerIcon("legendgear:goldRing");
        this.woodRingIcon = ireg.registerIcon("legendgear:woodenRing");
        this.ringOrbBaseIcon = ireg.registerIcon("legendgear:ringOrbBase");
        this.ringOrbOverlayIcon = ireg.registerIcon("legendgear:ringOrbOverlay");
        this.ringOrbFillIcon = ireg.registerIcon("legendgear:ringOrbFill");
    }

    @Optional.Method(modid="Baubles")
    public BaubleType getBaubleType(ItemStack itemstack) {
        return BaubleType.RING;
    }

    public static void spendRingMana(EntityPlayer player, float amount) {
        if (player.capabilities.isCreativeMode) {
            return;
        }
        float multiplier = 1.0f;
        if (MagicRing.PlayerWears(player, RingType.RESONANCE_RING)) {
            multiplier = RESONANCE_FACTOR;
        }
        float before = PlayerStarstatsExtension.availableMana(player);
        float after = before - amount * multiplier;
        PlayerStarstatsExtension.get(player).expendMana(null, amount * multiplier);
        if (after <= 0.0f) {
            LGUtil.soundForPlayer(player, "legendgear:ringOut", 0.5f, 1.0f);
        } else if ((int)(before / 2.0f) > (int)(after / 2.0f)) {
            LGUtil.soundForPlayer(player, "legendgear:ringTick", 0.5f, 1.0f);
        }
    }

    public boolean requiresMultipleRenderPasses() {
        return true;
    }

    @Optional.Method(modid="Baubles")
    public void onWornTick(ItemStack itemstack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            AxisAlignedBB playerbox;
            EntityPlayer player = (EntityPlayer)elb;
            RingType type = RingType.values()[itemstack.getItemDamage()];
            if (type == RingType.SPEED_RING) {
                IAttributeInstance iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed);
                if (iattributeinstance.getModifier(ringSpeedBoostModifierUUID) != null) {
                    iattributeinstance.removeModifier(ringSpeedBoostModifier);
                }
                boolean physicallyGrounded = player.onGround || player.isOnLadder() || player.isInWater();
                boolean useOriginalDashBehavior = LegendGear2.CONFIG_DASH_RING_USE_ORIGINAL_BEHAVIOR;
                if (!useOriginalDashBehavior) {
                    boolean wasGrounded = this.wasDashGrounded(player);
                    if (physicallyGrounded) {
                        this.setDashAirJumps(player, 0);
                    } else if (!wasGrounded && this.getDashAirJumps(player) < LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS && this.getDashPrevMotionY(player) <= 0.0 && player.motionY > 0.2) {
                        this.setDashAirJumps(player, this.getDashAirJumps(player) + 1);
                    }
                }
                if (player.isSprinting() && PlayerStarstatsExtension.get(player).getMana() < 20.0f) {
                    iattributeinstance.applyModifier(ringSpeedBoostModifier);
                    MagicRing.spendRingMana(player, SPRINT_RING_COST);
                    if (useOriginalDashBehavior) {
                        player.onGround = true;
                    } else if (!physicallyGrounded && LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS > 0 && this.getDashAirJumps(player) < LegendGear2.CONFIG_DASH_RING_MAX_AIR_JUMPS) {
                        player.onGround = true;
                    }
                }
                if (useOriginalDashBehavior) {
                    this.setDashAirJumps(player, 0);
                }
                this.setDashGrounded(player, physicallyGrounded);
                this.setDashPrevMotionY(player, player.motionY);
            }
            if (type == RingType.SOFT_FALL_RING && player.fallDistance > 3.0f && PlayerStarstatsExtension.get(player).getMana() < 20.0f) {
                float excess = player.fallDistance - 3.0f;
                MagicRing.spendRingMana(player, excess * FALL_RING_COST);
                player.fallDistance = 3.0f;
            }
            if (type == RingType.CONVECTION_RING && (playerbox = player.boundingBox) != null) {
                playerbox = playerbox.expand(0.25, 0.5, 0.25).offset(0.0, -2.5, 0.0);
                PlayerStarstatsExtension pse = PlayerStarstatsExtension.get(player);
                if (player.worldObj.isAABBInMaterial(playerbox, Material.lava) && pse.getMana() < 20.0f) {
                    float boost;
                    player.motionY *= (double)0.9f;
                    player.motionY += (double)0.09f;
                    player.fallDistance = 0.0f;
                    MagicRing.spendRingMana(player, CONVECTION_RING_COST);
                    if (pse.getGlide() > 0.0f && (boost = (float)player.posY + 16.0f) > pse.getGlide()) {
                        pse.setGlide(boost);
                    }
                }
            }
            if (type == RingType.COLDFEET_RING && !player.worldObj.isRemote && PlayerStarstatsExtension.availableMana(player) > 0.0f && (playerbox = player.boundingBox) != null) {
                playerbox = playerbox.expand(0.25, 0.25, 0.25);
                int y = (int)Math.floor(playerbox.minY);
                if (player.worldObj.isAABBInMaterial(playerbox, Material.water)) {
                    int x = (int)Math.floor(playerbox.minX);
                    while ((double)x <= Math.floor(playerbox.maxX)) {
                        int z = (int)Math.floor(playerbox.minZ);
                        while ((double)z <= Math.floor(playerbox.maxZ)) {
                            Block block = player.worldObj.getBlock(x, y, z);
                            int meta = player.worldObj.getBlockMetadata(x, y, z);
                            if ((block == Blocks.water || block == Blocks.flowing_water) && (meta & 7) == 0) {
                                player.worldObj.setBlock(x, y, z, (Block)LegendGear2.thawingIceBlock);
                                MagicRing.spendRingMana(player, COLDFEET_RING_COST);
                            }
                            ++z;
                        }
                        ++x;
                    }
                }
            }
            if (type == RingType.THIEF_RING && player.isSneaking() && PlayerStarstatsExtension.availableMana(player) > 0.0f) {
                player.addPotionEffect(new PotionEffect(Potion.invisibility.id, 4, 0, true));
                MagicRing.spendRingMana(player, THIEF_RING_COST);
            }
        }
    }

    @Optional.Method(modid="Baubles")
    public void onEquipped(ItemStack itemstack, EntityLivingBase player) {
    }

    @Optional.Method(modid="Baubles")
    public void onUnequipped(ItemStack itemstack, EntityLivingBase player) {
        IAttributeInstance iattributeinstance;
        RingType type = RingType.values()[itemstack.getItemDamage()];
        if (type == RingType.SPEED_RING && (iattributeinstance = player.getEntityAttribute(SharedMonsterAttributes.movementSpeed)).getModifier(ringSpeedBoostModifierUUID) != null) {
            iattributeinstance.removeModifier(ringSpeedBoostModifier);
        }
    }

    @Optional.Method(modid="Baubles")
    public boolean canEquip(ItemStack itemstack, EntityLivingBase elb) {
        if (elb instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)elb;
            return !MagicRing.PlayerWears(player, RingType.values()[itemstack.getItemDamage()]);
        }
        return false;
    }

    @Optional.Method(modid="Baubles")
    public boolean canUnequip(ItemStack itemstack, EntityLivingBase player) {
        return true;
    }

    public String getUnlocalizedName(ItemStack stack) {
        return "item." + (Object)((Object)RingType.values()[stack.getItemDamage()]);
    }

    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean bool) {
        String desc = I18n.format((String)("item." + (Object)((Object)RingType.values()[stack.getItemDamage()]) + ".desc"), (Object[])new Object[0]);
        list.add(desc);
    }

    public int getRenderPasses(int metadata) {
        if (RingType.values()[metadata].gemType == null) {
            return 1;
        }
        return 4;
    }

    public IIcon getIcon(ItemStack stack, int pass) {
        RingType type = RingType.values()[stack.getItemDamage()];
        if (pass == 0) {
            if (type.iconType == IRON) {
                return this.ironRingIcon;
            }
            if (type.iconType == GOLD) {
                return this.goldRingIcon;
            }
            if (type.iconType == WOOD) {
                return this.woodRingIcon;
            }
            return this.ironRingIcon;
        }
        if (pass == 2) {
            return this.ringOrbFillIcon;
        }
        if (pass == 1) {
            return this.ringOrbBaseIcon;
        }
        return this.ringOrbOverlayIcon;
    }

    @Override
    public boolean hasEffect(ItemStack par1ItemStack, int pass) {
        return par1ItemStack.getItemDamage() == RingType.PHOENIX_RING.ordinal();
    }

    public int getColorFromItemStack(ItemStack stack, int pass) {
        RingType type = RingType.values()[stack.getItemDamage()];
        if (pass == 1) {
            return type.gemType.color1;
        }
        if (pass == 2) {
            return type.gemType.color2;
        }
        if (pass == 0 && type.iconType == STARGLASS) {
            return 6488233;
        }
        if (pass == 0 && type == RingType.PHOENIX_RING) {
            return 0xFFDDDD;
        }
        return 0xFFFFFF;
    }

    public void getSubItems(Item item, CreativeTabs tabs, List list) {
        for (RingType type : RingType.values()) {
            list.add(new ItemStack(item, 1, type.ordinal()));
        }
    }

    public void addRecipes() {
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.SPEED_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Items.gold_ingot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.LIGHTNING), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.SOFT_FALL_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Items.gold_ingot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.SKY), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.CONVECTION_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Items.gold_ingot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.FIRE), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.COLDFEET_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Items.gold_ingot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.ICE), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.PLAIN_RING.ordinal()), (Object[])new Object[]{" M ", "MSM", " M ", Character.valueOf('M'), Items.gold_ingot, Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.RESONANCE_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), LegendGear2.starglassIngot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.SUN), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.WISH_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), LegendGear2.starglassIngot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.STAR), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 5)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.MAGE_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Items.iron_ingot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.STAR), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.WARRIOR_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Items.iron_ingot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.WEAPON), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.THIEF_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Items.iron_ingot, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.DARK), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.ARROWFIND_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Blocks.planks, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.WEAPON), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.AZUREFIND_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Blocks.planks, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.SKY), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
        GameRegistry.addRecipe((ItemStack)new ItemStack((Item)this, 1, RingType.FORTUNE_RING.ordinal()), (Object[])new Object[]{" MG", "MSM", " M ", Character.valueOf('M'), Blocks.planks, Character.valueOf('G'), ItemNucleus.gem(ItemNucleus.NucleusType.WEALTH), Character.valueOf('S'), new ItemStack((Item)LegendGear2.starDust, 1, 4)});
    }

    static /* synthetic */ int access$000() {
        return GOLD;
    }

    static /* synthetic */ int access$100() {
        return IRON;
    }

    static /* synthetic */ int access$200() {
        return WOOD;
    }

    static /* synthetic */ int access$300() {
        return STARGLASS;
    }

    private int getDashAirJumps(EntityPlayer player) {
        return player.getEntityData().getInteger(DASH_AIR_JUMPS_KEY);
    }

    private void setDashAirJumps(EntityPlayer player, int jumps) {
        player.getEntityData().setInteger(DASH_AIR_JUMPS_KEY, Math.max(0, jumps));
    }

    private boolean wasDashGrounded(EntityPlayer player) {
        return player.getEntityData().getBoolean(DASH_LAST_GROUNDED_KEY);
    }

    private void setDashGrounded(EntityPlayer player, boolean grounded) {
        player.getEntityData().setBoolean(DASH_LAST_GROUNDED_KEY, grounded);
    }

    private double getDashPrevMotionY(EntityPlayer player) {
        return player.getEntityData().getDouble(DASH_PREV_MOTION_Y_KEY);
    }

    private void setDashPrevMotionY(EntityPlayer player, double motionY) {
        player.getEntityData().setDouble(DASH_PREV_MOTION_Y_KEY, motionY);
    }

    public static enum RingType {
        SPEED_RING(ItemNucleus.NucleusType.LIGHTNING, MagicRing.access$000()),
        CONVECTION_RING(ItemNucleus.NucleusType.FIRE, MagicRing.access$000()),
        SOFT_FALL_RING(ItemNucleus.NucleusType.SKY, MagicRing.access$000()),
        COLDFEET_RING(ItemNucleus.NucleusType.ICE, MagicRing.access$000()),
        THIEF_RING(ItemNucleus.NucleusType.DARK, MagicRing.access$000()),
        MAGE_RING(ItemNucleus.NucleusType.STAR, MagicRing.access$100()),
        WARRIOR_RING(ItemNucleus.NucleusType.WEAPON, MagicRing.access$100()),
        FORTUNE_RING(ItemNucleus.NucleusType.WEALTH, MagicRing.access$200()),
        ARROWFIND_RING(ItemNucleus.NucleusType.WEAPON, MagicRing.access$200()),
        AZUREFIND_RING(ItemNucleus.NucleusType.SKY, MagicRing.access$200()),
        WISH_RING(ItemNucleus.NucleusType.STAR, MagicRing.access$300()),
        RESONANCE_RING(ItemNucleus.NucleusType.SUN, MagicRing.access$300()),
        PLAIN_RING(null, MagicRing.access$000()),
        PHOENIX_RING(null, MagicRing.access$000());

        public ItemNucleus.NucleusType gemType;
        public int iconType = 0;

        private RingType(ItemNucleus.NucleusType gem, int mat) {
            this.gemType = gem;
            this.iconType = mat;
        }
    }
}
