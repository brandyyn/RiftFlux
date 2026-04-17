/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.registry.IEntityAdditionalSpawnData
 *  io.netty.buffer.ByteBuf
 *  net.minecraft.block.Block
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.EntityLivingBase
 *  net.minecraft.entity.item.EntityItem
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.init.Blocks
 *  net.minecraft.item.ItemStack
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.potion.Potion
 *  net.minecraft.potion.PotionEffect
 *  net.minecraft.tileentity.TileEntity
 *  net.minecraft.util.AxisAlignedBB
 *  net.minecraft.util.DamageSource
 *  net.minecraft.util.EntityDamageSourceIndirect
 *  net.minecraft.util.Vec3
 *  net.minecraft.world.ChunkPosition
 *  net.minecraft.world.IBlockAccess
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IShearable
 */
package net.nmccoy.legendgear.entity;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.common.registry.IEntityAdditionalSpawnData;
import io.netty.buffer.ByteBuf;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;
import net.minecraft.world.ChunkPosition;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.IShearable;
import net.nmccoy.legendgear.LegendGear2;
import net.nmccoy.legendgear.PlayerEventHandler;
import net.nmccoy.legendgear.block.TileEntityRitual;
import net.nmccoy.legendgear.block.TileEntityStarwell;
import net.nmccoy.legendgear.entity.SpellDecorator;
import net.nmccoy.legendgear.magic.Spell;

public class EntitySpellEffect
extends Entity
implements IEntityAdditionalSpawnData {
    private static boolean AFFECTS_BLOCKS = true;
    private static boolean DOESNT_AFFECT_BLOCKS = false;
    public EntityPlayer caster;
    private Entity attackSource;
    public double radius;
    public double power;
    public boolean isCrit;
    public SpellType spellType;
    public int lifeTicks;
    public int maxLife = 10;
    public HashSet hits;
    public ArrayList<ChunkPosition> blocksAffected;
    private int overrideFireSeconds = -1;
    private static float blockRadiusFudge = 0.25f;

    public EntitySpellEffect(World world) {
        super(world);
        this.noClip = true;
        this.isImmuneToFire = true;
        this.hits = new HashSet();
        this.height = 0.0f;
        this.width = 0.0f;
    }

    private static int getConfiguredFireSeconds() {
        return Math.max(0, (int)Math.ceil((double)Math.max(0.0F, ModConfig.legendGearEmberStaffFireSeconds)));
    }

    private static void applyConfiguredFire(EntitySpellEffect spell, EntityLivingBase living) {
        if (living == null || living.isImmuneToFire()) {
            return;
        }

        int fireSeconds = spell == null ? getConfiguredFireSeconds() : spell.getEffectiveFireSeconds();
        if (fireSeconds > 0) {
            living.setFire(fireSeconds);
        }
    }

    public EntitySpellEffect withOverrideFireSeconds(int fireSeconds) {
        this.overrideFireSeconds = fireSeconds < 0 ? -1 : fireSeconds;
        return this;
    }

    private int getEffectiveFireSeconds() {
        return this.overrideFireSeconds >= 0 ? this.overrideFireSeconds : getConfiguredFireSeconds();
    }

    private static boolean hasBedrockAbove(World world, int x, int y, int z) {
        int startY = Math.max(0, y + 1);
        int maxY = world.getActualHeight();
        for (int scanY = startY; scanY < maxY; ++scanY) {
            if (world.getBlock(x, scanY, z) == Blocks.bedrock) {
                return true;
            }
        }
        return false;
    }

    private static int findSurfaceTeleportY(World world, int x, int z) {
        int y = Math.max(1, world.getTopSolidOrLiquidBlock(x, z));
        int maxY = Math.max(1, world.getActualHeight() - 1);
        while (y < maxY && (!world.isAirBlock(x, y, z) || !world.isAirBlock(x, y + 1, z))) {
            ++y;
        }
        if (y >= maxY) {
            return -1;
        }
        return y;
    }

    public EntitySpellEffect(World world, SpellType id, EntityPlayer caster, Vec3 location, double radius, double power, boolean critical) {
        this(world);
        this.posX = location.xCoord;
        this.posY = location.yCoord;
        this.posZ = location.zCoord;
        this.caster = caster;
        this.radius = radius;
        this.power = power;
        this.isCrit = critical;
        this.spellType = id;
        this.maxLife = id.maxLife;
    }

    public void knockAwayFrom(Entity target, Entity source, float force, float up) {
        Vec3 away = Vec3.createVectorHelper((double)(target.posX - source.posX), (double)(target.posY - source.posY), (double)(target.posZ - source.posZ));
        away = away.normalize();
        target.addVelocity(away.xCoord * (double)force, (double)up, away.zCoord * (double)force);
    }

    public void knockRadialOutward(Entity target, float force, float extraUp) {
        Vec3 away = Vec3.createVectorHelper((double)(target.posX - this.posX), (double)(target.posY - this.posY), (double)(target.posZ - this.posZ));
        away = away.normalize();
        target.addVelocity(away.xCoord * (double)force, away.yCoord * (double)force + (double)extraUp, away.zCoord * (double)force);
    }

    private void affectBlock(int x, int y, int z) {
        this.spellType.affectBlock(this, x, y, z);
    }

    private void affectBlocks() {
        this.blocksAffected = new ArrayList();
        double adjR = this.radius + (double)blockRadiusFudge;
        AxisAlignedBB boxBounds = AxisAlignedBB.getBoundingBox((double)Math.floor(this.posX - adjR), (double)Math.floor(this.posY - adjR), (double)Math.floor(this.posZ - adjR), (double)Math.ceil(this.posX + adjR), (double)Math.ceil(this.posY + adjR), (double)Math.ceil(this.posZ + adjR));
        Vec3 burstCenter = Vec3.createVectorHelper((double)this.posX, (double)this.posY, (double)this.posZ);
        for (int x = (int)boxBounds.minX; x < (int)boxBounds.maxX; ++x) {
            for (int y = (int)boxBounds.minY; y < (int)boxBounds.maxY; ++y) {
                for (int z = (int)boxBounds.minZ; z < (int)boxBounds.maxZ; ++z) {
                    Vec3 blockCenter = Vec3.createVectorHelper((double)((double)x + 0.5), (double)((double)y + 0.5), (double)((double)z + 0.5));
                    if (!(blockCenter.squareDistanceTo(burstCenter) <= adjR * adjR)) continue;
                    this.spellType.affectBlock(this, x, y, z);
                }
            }
        }
    }

    public void executeHit(Entity target) {
        this.attackSource = this.caster == null ? this : this.caster;
        if (target instanceof EntityLivingBase) {
            this.spellType.affectLiving(this, (EntityLivingBase)target);
        } else {
            this.spellType.affectInanimate(this, target);
        }
    }

    public boolean confirmHit(Entity e) {
        if (!this.hits.contains(e)) {
            this.executeHit(e);
            this.hits.add(e);
            return true;
        }
        return false;
    }

    public void tryHittingEntities() {
        AxisAlignedBB firstHitPass = AxisAlignedBB.getBoundingBox((double)(this.posX - this.radius), (double)(this.posY - this.radius), (double)(this.posZ - this.radius), (double)(this.posX + this.radius), (double)(this.posY + this.radius), (double)(this.posZ + this.radius));
        List entities = this.worldObj.getEntitiesWithinAABBExcludingEntity((Entity)this, firstHitPass);
        for (Object obj : entities) {
            double cz;
            double cy;
            double cx;
            double dsq;
            Entity ent = (Entity)obj;
            AxisAlignedBB entbox = ent.boundingBox;
            if (entbox == null || !((dsq = ((cx = Math.min(entbox.maxX, Math.max(this.posX, entbox.minX))) - this.posX) * (cx - this.posX) + ((cy = Math.min(entbox.maxY, Math.max(this.posY, entbox.minY))) - this.posY) * (cy - this.posY) + ((cz = Math.min(entbox.maxZ, Math.max(this.posZ, entbox.minZ))) - this.posZ) * (cz - this.posZ)) <= this.radius * this.radius)) continue;
            this.confirmHit(ent);
        }
    }

    public void onUpdate() {
        super.onUpdate();
        if (this.lifeTicks == 0) {
            if (!this.worldObj.isRemote) {
                this.worldObj.spawnEntityInWorld((Entity)new SpellDecorator(this));
            }
            if (!this.worldObj.isRemote) {
                this.worldObj.playSoundAtEntity((Entity)this, "legendgear:spell." + this.spellType.toString(), 2.5f, 1.0f);
            }
        }
        if (this.lifeTicks == this.spellType.sleepTime && !this.worldObj.isRemote && this.spellType.affectsBlocks) {
            this.affectBlocks();
        }
        if (this.lifeTicks >= this.spellType.sleepTime && !this.worldObj.isRemote) {
            this.tryHittingEntities();
        }
        ++this.lifeTicks;
        if (this.lifeTicks >= this.maxLife) {
            this.setDead();
        }
    }

    protected void entityInit() {
    }

    protected void readEntityFromNBT(NBTTagCompound tag) {
        this.spellType = SpellType.values()[tag.getInteger("spellType")];
        this.radius = tag.getDouble("radius");
        this.power = tag.getDouble("power");
        this.isCrit = tag.getBoolean("isCrit");
        this.maxLife = tag.getInteger("maxLife");
        this.lifeTicks = tag.getInteger("lifeTicks");
        this.overrideFireSeconds = tag.hasKey("overrideFireSeconds") ? tag.getInteger("overrideFireSeconds") : -1;
    }

    protected void writeEntityToNBT(NBTTagCompound tag) {
        tag.setInteger("spellType", this.spellType.ordinal());
        tag.setDouble("radius", this.radius);
        tag.setDouble("power", this.power);
        tag.setBoolean("isCrit", this.isCrit);
        tag.setInteger("maxLife", this.maxLife);
        tag.setInteger("lifeTicks", this.lifeTicks);
        tag.setInteger("overrideFireSeconds", this.overrideFireSeconds);
    }

    public void writeSpawnData(ByteBuf buffer) {
        buffer.writeInt(this.spellType.ordinal());
        buffer.writeDouble(this.radius);
        buffer.writeDouble(this.power);
        buffer.writeBoolean(this.isCrit);
        buffer.writeInt(this.maxLife);
        buffer.writeInt(this.lifeTicks);
        buffer.writeInt(this.overrideFireSeconds);
    }

    public void readSpawnData(ByteBuf additionalData) {
        this.spellType = SpellType.values()[additionalData.readInt()];
        this.radius = additionalData.readDouble();
        this.power = additionalData.readDouble();
        this.isCrit = additionalData.readBoolean();
        this.maxLife = additionalData.readInt();
        this.lifeTicks = additionalData.readInt();
        this.overrideFireSeconds = additionalData.readInt();
    }

    static /* synthetic */ boolean access$000() {
        return AFFECTS_BLOCKS;
    }

    static /* synthetic */ boolean access$200() {
        return DOESNT_AFFECT_BLOCKS;
    }

    public static enum SpellType {
        Twinkle(Spell.Element.Star),
        OrbExplosion(Spell.Element.Explosion, 2, EntitySpellEffect.access$000()){

            @Override
            public boolean affectBlock(EntitySpellEffect spell, int x, int y, int z) {
                Block block = spell.worldObj.getBlock(x, y, z);
                int meta = spell.worldObj.getBlockMetadata(x, y, z);
                boolean breakable = false;
                if (block == Blocks.cobblestone) {
                    breakable = true;
                }
                if (!block.hasTileEntity(meta) && block.getBlockHardness(spell.worldObj, x, y, z) == 0.0f) {
                    breakable = true;
                }
                if (breakable) {
                    block.dropBlockAsItemWithChance(spell.worldObj, x, y, z, spell.worldObj.getBlockMetadata(x, y, z), 1.0f, 0);
                    spell.worldObj.playAuxSFX(2001, x, y, z, Block.getIdFromBlock((Block)block) + (meta << 12));
                    spell.worldObj.setBlockToAir(x, y, z);
                    spell.blocksAffected.add(new ChunkPosition(x, y, z));
                    return true;
                }
                return false;
            }

            @Override
            public boolean affectLiving(EntitySpellEffect spell, EntityLivingBase living) {
                float damage = (float)spell.power;
                if (living.equals((Object)spell.caster)) {
                    damage = 4.0f;
                }
                boolean hit = living.attackEntityFrom(this.getDamageSource(spell), damage);
                spell.knockRadialOutward((Entity)living, 0.5f, 0.0f);
                return hit;
            }
        }
        ,
        Fire1(Spell.Element.Fire, 15, EntitySpellEffect.access$000()),
        Lightning1(Spell.Element.Lightning),
        Ice1(Spell.Element.Ice, 10, EntitySpellEffect.access$000()),
        StarImpact(Spell.Element.Star, 10, EntitySpellEffect.access$000()),
        WaterFlood(Spell.Element.Harmless, 1, EntitySpellEffect.access$000()){

            @Override
            public boolean affectBlock(EntitySpellEffect spell, int x, int y, int z) {
                Block block = spell.worldObj.getBlock(x, y, z);
                int meta = spell.worldObj.getBlockMetadata(x, y, z);
                if (block.isAir((IBlockAccess)spell.worldObj, x, y, z)) {
                    spell.worldObj.setBlock(x, y, z, (Block)Blocks.flowing_water, 1, 3);
                    return true;
                }
                return false;
            }
        }
        ,
        LavaFlood(Spell.Element.Harmless, 1, EntitySpellEffect.access$000()){

            @Override
            public boolean affectBlock(EntitySpellEffect spell, int x, int y, int z) {
                Block block = spell.worldObj.getBlock(x, y, z);
                int meta = spell.worldObj.getBlockMetadata(x, y, z);
                if (block.isAir((IBlockAccess)spell.worldObj, x, y, z)) {
                    spell.worldObj.setBlock(x, y, z, (Block)Blocks.flowing_lava, 1, 3);
                    return true;
                }
                return false;
            }
        }
        ,
        Exit(Spell.Element.Teleport, 15, EntitySpellEffect.access$200(), 10){

            @Override
            public boolean affectLiving(EntitySpellEffect spell, EntityLivingBase elb) {
                if (spell.caster == null || elb.dimension != 0) {
                    return false;
                }
                int destX = MathHelper.floor_double(elb.posX);
                int destZ = MathHelper.floor_double(elb.posZ);
                int currentY = MathHelper.floor_double(elb.boundingBox.maxY);
                if (EntitySpellEffect.hasBedrockAbove(elb.worldObj, destX, currentY, destZ)) {
                    return false;
                }
                int destY = EntitySpellEffect.findSurfaceTeleportY(elb.worldObj, destX, destZ);
                if (destY < 0) {
                    return false;
                }
                elb.worldObj.playSoundEffect(elb.posX, elb.posY, elb.posZ, "mob.endermen.portal", 1.0f, 1.0f);
                elb.setPositionAndUpdate((double)destX + 0.5, (double)destY, (double)destZ + 0.5);
                elb.fallDistance = 0.0f;
                if (!spell.isCrit) {
                    LegendGear2.addConfiguredPotionEffect(elb, LegendGear2.CONFIG_EXIT_CONFUSION_POTION_ID, Potion.confusion, 300);
                }
                return true;
            }
        }
        ,
        SprinkleStardust(Spell.Element.Harmless, 5, EntitySpellEffect.access$000()){

            @Override
            public boolean affectBlock(EntitySpellEffect spell, int x, int y, int z) {
                TileEntityRitual ritual;
                if (spell.worldObj.getBlock(x, y, z) == LegendGear2.ritualBlock && (ritual = (TileEntityRitual)spell.worldObj.getTileEntity(x, y, z)) != null && spell.caster != null) {
                    ritual.tryInvoke(spell.caster);
                    return true;
                }
                TileEntityStarwell starwell;
                if (spell.worldObj.getBlock(x, y, z) == LegendGear2.skylensBlock && spell.worldObj.getBlock(x, y - 1, z) == LegendGear2.starwellBlock && (starwell = (TileEntityStarwell)spell.worldObj.getTileEntity(x, y - 1, z)) != null && spell.caster != null) {
                    starwell.flightCharge = TileEntityStarwell.FLIGHT_CHARGE_DURATION;
                    spell.worldObj.markBlockForUpdate(x, y - 1, z);
                    spell.worldObj.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "legendgear:ritualReady", 1.0f, 1.0f);
                    return true;
                }
                if (spell.worldObj.getBlock(x, y, z) == Blocks.bookshelf) {
                    spell.worldObj.setBlockToAir(x, y, z);
                    ItemStack book = LegendGear2.fortunes.makeRitualNotebook();
                    spell.worldObj.spawnEntityInWorld((Entity)new EntityItem(spell.worldObj, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, book));
                    spell.worldObj.playSoundEffect((double)x + 0.5, (double)y + 0.5, (double)z + 0.5, "legendgear:transform", 1.0f, 1.0f);
                }
                return false;
            }
        }
        ,
        ScytheWind(Spell.Element.Wind, 15, EntitySpellEffect.access$000()){
            float inScale = 0.06f;

            @Override
            public boolean affectBlock(EntitySpellEffect spell, int x, int y, int z) {
                if (spell.worldObj.isRemote) {
                    return false;
                }
                Block target = spell.worldObj.getBlock(x, y, z);
                int meta = spell.worldObj.getBlockMetadata(x, y, z);
                if (target instanceof IShearable) {
                    IShearable shearable = (IShearable)target;
                    ArrayList<ItemStack> items = shearable.onSheared(null, (IBlockAccess)spell.worldObj, x, y, z, 0);
                    for (ItemStack stack : items) {
                        EntityItem ei = new EntityItem(spell.worldObj, (double)x + 0.5, (double)y + 0.5, (double)z + 0.5, stack);
                        double vx = (spell.posX - (double)x + 0.5) * (double)this.inScale;
                        double vz = (spell.posZ - (double)z + 0.5) * (double)this.inScale;
                        double vy = 0.5;
                        if (spell.isCrit) {
                            ei.motionX = vx;
                            ei.motionY = vy;
                            ei.motionZ = vz;
                        }
                        spell.worldObj.spawnEntityInWorld((Entity)ei);
                    }
                    if (spell.worldObj.setBlockToAir(x, y, z)) {
                        target.onBlockDestroyedByPlayer(spell.worldObj, x, y, z, meta);
                    }
                    return true;
                }
                if (target.getBlockHardness(spell.worldObj, x, y, z) == 0.0f && target.getHarvestTool(meta) == null) {
                    target.dropBlockAsItem(spell.worldObj, x, y, z, meta, 0);
                    if (spell.worldObj.setBlockToAir(x, y, z)) {
                        target.onBlockDestroyedByPlayer(spell.worldObj, x, y, z, meta);
                    }
                }
                return false;
            }

            @Override
            public boolean affectLiving(EntitySpellEffect spell, EntityLivingBase living) {
                IShearable shearable;
                if (living instanceof IShearable && !living.worldObj.isRemote && (shearable = (IShearable)living).isShearable(null, (IBlockAccess)living.worldObj, (int)living.posX, (int)living.posY, (int)living.posZ)) {
                    ArrayList<ItemStack> stacks = shearable.onSheared(null, (IBlockAccess)living.worldObj, (int)living.posX, (int)living.posY, (int)living.posZ, 0);
                    for (ItemStack stack : stacks) {
                        EntityItem ei = new EntityItem(spell.worldObj, living.posX, living.posY, living.posZ, stack);
                        double vx = (spell.posX - living.posX) * (double)this.inScale;
                        double vz = (spell.posZ - living.posZ) * (double)this.inScale;
                        double vy = 0.5;
                        if (spell.isCrit) {
                            ei.motionX = vx;
                            ei.motionY = vy;
                            ei.motionZ = vz;
                        }
                        spell.worldObj.spawnEntityInWorld((Entity)ei);
                    }
                    return true;
                }
                return super.affectLiving(spell, living);
            }

            @Override
            public boolean affectInanimate(EntitySpellEffect spell, Entity target) {
                if (target instanceof EntityItem) {
                    double vx = (spell.posX - target.posX) * (double)this.inScale;
                    double vz = (spell.posZ - target.posZ) * (double)this.inScale;
                    double vy = 0.5;
                    if (spell.isCrit) {
                        target.motionX = vx;
                        target.motionY = vy;
                        target.motionZ = vz;
                    }
                    return true;
                }
                return super.affectInanimate(spell, target);
            }
        }
        ,
        Rayfire(Spell.Element.Radiant, 7){

            @Override
            public boolean affectLiving(EntitySpellEffect spell, EntityLivingBase living) {
                if (spell.isCrit && living == spell.caster) {
                    return false;
                }
                if (living.isEntityUndead()) {
                    living.setFire((int)spell.power);
                }
                return super.affectLiving(spell, living);
            }
        };

        public int maxLife = 10;
        public int sleepTime = 0;
        public boolean affectsBlocks = false;
        public Spell.Element element;

        private SpellType(Spell.Element ele) {
            this.element = ele;
        }

        private SpellType(Spell.Element ele, int life) {
            this.element = ele;
            this.maxLife = life;
        }

        private SpellType(Spell.Element ele, int life, boolean blocks) {
            this.element = ele;
            this.maxLife = life;
            this.affectsBlocks = blocks;
        }

        private SpellType(Spell.Element ele, int life, boolean blocks, int sleep) {
            this.element = ele;
            this.maxLife = life;
            this.affectsBlocks = blocks;
            this.sleepTime = sleep;
        }

        public boolean affectBlock(EntitySpellEffect spell, int x, int y, int z) {
            Block block = spell.worldObj.getBlock(x, y, z);
            int meta = spell.worldObj.getBlockMetadata(x, y, z);
            if (this.element == Spell.Element.Ice && (block == Blocks.water || block == Blocks.flowing_water) && (meta & 7) == 0) {
                spell.worldObj.setBlock(x, y, z, Blocks.ice);
            }
            if (this.element == Spell.Element.Fire && (block == Blocks.ice || block == LegendGear2.thawingIceBlock)) {
                spell.worldObj.setBlock(x, y, z, Blocks.water);
            }
            if (this.element == Spell.Element.Star && block == Blocks.sand) {
                spell.worldObj.setBlock(x, y, z, (Block)LegendGear2.starSandBlock);
            }
            return false;
        }

        public boolean affectLiving(EntitySpellEffect spell, EntityLivingBase living) {
            if (this.element != Spell.Element.Harmless) {
                boolean hit;
                float damage = (float)spell.power;
                if ((damage *= this.element.getDamageMultiplier(living)) == 0.0f) {
                    return false;
                }
                float knockback = 0.15f;
                if (spell.isCrit) {
                    knockback = 0.2f;
                    if (this.element == Spell.Element.Ice) {
                        LegendGear2.addConfiguredPotionEffect(living, LegendGear2.CONFIG_ICE_SPELL_SLOWNESS_POTION_ID, Potion.moveSlowdown, (int)spell.power * 10, 5, false);
                        LegendGear2.applyJumpPenalty(living, (int)spell.power * 10, 4, false);
                    }
                    if (this.element == Spell.Element.Lightning) {
                        knockback = 0.5f;
                    }
                }
                hit = living.attackEntityFrom(this.getDamageSource(spell), damage);
                if (this.element == Spell.Element.Fire) {
                    EntitySpellEffect.applyConfiguredFire(spell, living);
                }
                if (hit) {
                    spell.knockAwayFrom((Entity)living, spell.attackSource, knockback, knockback);
                }
                return hit;
            }
            return false;
        }

        public boolean affectInanimate(EntitySpellEffect spell, Entity target) {
            return false;
        }

        public DamageSource getDamageSource(EntitySpellEffect spell) {
            EntityDamageSourceIndirect source = new EntityDamageSourceIndirect("magic." + this.element.toString(), (Entity)spell, spell.attackSource);
            if (this.element == Spell.Element.Explosion) {
                source.setExplosion();
            } else {
                source.setMagicDamage().setDamageBypassesArmor();
            }
            if (this.element == Spell.Element.Fire) {
                source.setFireDamage();
            }
            return source;
        }
    }
}
