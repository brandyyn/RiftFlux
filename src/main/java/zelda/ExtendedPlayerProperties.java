/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.entity.Entity
 *  net.minecraft.entity.SharedMonsterAttributes
 *  net.minecraft.entity.ai.attributes.AttributeModifier
 *  net.minecraft.entity.ai.attributes.IAttributeInstance
 *  net.minecraft.entity.player.EntityPlayer
 *  net.minecraft.nbt.NBTBase
 *  net.minecraft.nbt.NBTTagCompound
 *  net.minecraft.world.World
 *  net.minecraftforge.common.IExtendedEntityProperties
 */
package zelda;

import com.voidsrift.riftflux.util.ConfigResolver;
import java.util.LinkedHashSet;
import java.util.Set;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.IExtendedEntityProperties;
import zelda.Config;

public class ExtendedPlayerProperties
implements IExtendedEntityProperties {
    public static final String EXT_PROP_NAME = "RiftFluxZeldaExtendedPlayer";
    private static final String LEGACY_EXT_PROP_NAME = "ExtendedPlayer";
    private static final String PERSIST_TAG = "ForgeData";
    private static final String HEARTS_TAG = "RiftFluxZeldaHearts";
    private static final String FRESH_TAG = "RiftFluxZeldaFresh";
    private static final String HEART_CONTAINER_KILLS_TAG = "RiftFluxZeldaHeartContainerKills";
    private final EntityPlayer player;
    private double hearts;
    private boolean fresh;
    private final Set<String> heartContainerKills;

    public ExtendedPlayerProperties(EntityPlayer player) {
        this.player = player;
        this.hearts = Config.STARTING_HEARTS;
        this.fresh = true;
        this.heartContainerKills = new LinkedHashSet<String>();
    }

    public static final void register(EntityPlayer player) {
        player.registerExtendedProperties(EXT_PROP_NAME, (IExtendedEntityProperties)new ExtendedPlayerProperties(player));
    }

    public static final ExtendedPlayerProperties get(EntityPlayer player) {
        IExtendedEntityProperties props = player.getExtendedProperties(EXT_PROP_NAME);
        if (props instanceof ExtendedPlayerProperties) {
            return (ExtendedPlayerProperties)props;
        }
        return null;
    }

    public void saveNBTData(NBTTagCompound compound) {
        NBTTagCompound props = new NBTTagCompound();
        props.setDouble("Hearts", this.hearts);
        props.setBoolean("Fresh", this.fresh);
        props.setString("HeartContainerKills", this.serializeHeartContainerKills());
        compound.setTag(EXT_PROP_NAME, (NBTBase)props);
        this.saveToPersistedData();
    }

    public void loadNBTData(NBTTagCompound compound) {
        NBTTagCompound props = (NBTTagCompound)compound.getTag(EXT_PROP_NAME);
        if (props == null || !props.hasKey("Hearts")) {
            props = (NBTTagCompound)compound.getTag(LEGACY_EXT_PROP_NAME);
        }
        if (props != null && props.hasKey("Hearts")) {
            this.hearts = props.getDouble("Hearts");
            this.fresh = props.getBoolean("Fresh");
            this.deserializeHeartContainerKills(props.getString("HeartContainerKills"));
            if (this.heartContainerKills.isEmpty()) {
                this.loadHeartContainerKillsFromPersistedData();
            }
        } else {
            this.loadFromPersistedData();
        }
        this.applyHearts(false);
    }

    public void init(Entity entity, World world) {
    }

    public void addHeart() {
        int maxHearts = Math.max(Config.STARTING_HEARTS, Config.MAXIMUM_HEARTS);
        this.hearts = this.clampHearts();
        if (this.hearts >= (double)maxHearts) {
            return;
        }
        this.hearts += 1.0;
        this.applyHearts(true);
    }

    public void setBaseHearts(double hearts) {
        this.hearts = hearts;
        this.applyHearts(false);
        if (this.fresh) {
            this.fresh = false;
        }
    }

    public void setBaseHeartsMax() {
        this.applyHearts(true);
    }

    public double getMaxHearts() {
        return this.clampHearts();
    }

    public boolean isFresh() {
        return this.fresh;
    }

    public boolean hasKilledHeartContainerMob(String mobId) {
        String normalizedMobId = this.normalizeHeartContainerMobId(mobId);
        return !normalizedMobId.isEmpty() && this.heartContainerKills.contains(normalizedMobId);
    }

    public boolean addKilledHeartContainerMob(String mobId) {
        String normalizedMobId = this.normalizeHeartContainerMobId(mobId);
        if (normalizedMobId.isEmpty() || !this.heartContainerKills.add(normalizedMobId)) {
            return false;
        }
        this.saveToPersistedData();
        return true;
    }

    private double clampHearts() {
        int maxHearts = Math.max(Config.STARTING_HEARTS, Config.MAXIMUM_HEARTS);
        if (this.hearts < (double)Config.STARTING_HEARTS) {
            this.hearts = Config.STARTING_HEARTS;
        } else if (this.hearts > (double)maxHearts) {
            this.hearts = maxHearts;
        }
        return this.hearts;
    }

    private void applyHearts(boolean setToFull) {
        int maxHearts = Math.max(Config.STARTING_HEARTS, Config.MAXIMUM_HEARTS);
        if (this.hearts < (double)Config.STARTING_HEARTS) {
            this.hearts = Config.STARTING_HEARTS;
        } else if (this.hearts > (double)maxHearts) {
            this.hearts = maxHearts;
        }
        IAttributeInstance attributeInstance = this.player.getEntityAttribute(SharedMonsterAttributes.maxHealth);
        if (attributeInstance == null) {
            return;
        }
        double desiredBase = this.hearts * 4.0;
        double currentBase = attributeInstance.getBaseValue();
        if (this.fresh) {
            if (Math.abs(currentBase - 20.0) < 0.01) {
                attributeInstance.setBaseValue(desiredBase);
            } else if (currentBase < desiredBase) {
                attributeInstance.setBaseValue(desiredBase);
            }
            this.fresh = false;
        } else if (currentBase < desiredBase) {
            attributeInstance.setBaseValue(desiredBase);
        }
        float maxHealth = (float)attributeInstance.getAttributeValue();
        if (setToFull) {
            this.player.setHealth(maxHealth);
        } else if (this.player.getHealth() > maxHealth) {
            this.player.setHealth(maxHealth);
        }
        this.saveToPersistedData();
    }

    private void saveToPersistedData() {
        if (this.player == null) {
            return;
        }
        NBTTagCompound root = this.player.getEntityData();
        NBTTagCompound persisted = root.getCompoundTag(PERSIST_TAG);
        persisted.setDouble(HEARTS_TAG, this.hearts);
        persisted.setBoolean(FRESH_TAG, this.fresh);
        persisted.setString(HEART_CONTAINER_KILLS_TAG, this.serializeHeartContainerKills());
        root.setTag(PERSIST_TAG, persisted);
    }

    private void loadFromPersistedData() {
        if (this.player == null) {
            return;
        }
        NBTTagCompound root = this.player.getEntityData();
        NBTTagCompound persisted = root.getCompoundTag(PERSIST_TAG);
        if (persisted.hasKey(HEARTS_TAG)) {
            this.hearts = persisted.getDouble(HEARTS_TAG);
        }
        if (persisted.hasKey(FRESH_TAG)) {
            this.fresh = persisted.getBoolean(FRESH_TAG);
        }
        if (persisted.hasKey(HEART_CONTAINER_KILLS_TAG)) {
            this.deserializeHeartContainerKills(persisted.getString(HEART_CONTAINER_KILLS_TAG));
        }
    }

    private void loadHeartContainerKillsFromPersistedData() {
        if (this.player == null) {
            return;
        }
        NBTTagCompound root = this.player.getEntityData();
        NBTTagCompound persisted = root.getCompoundTag(PERSIST_TAG);
        if (persisted.hasKey(HEART_CONTAINER_KILLS_TAG)) {
            this.deserializeHeartContainerKills(persisted.getString(HEART_CONTAINER_KILLS_TAG));
        }
    }

    private String normalizeHeartContainerMobId(String mobId) {
        return ConfigResolver.stripKnownEntityPrefixes(ConfigResolver.normalizeToken(mobId));
    }

    private String serializeHeartContainerKills() {
        if (this.heartContainerKills.isEmpty()) {
            return "";
        }

        StringBuilder out = new StringBuilder();
        for (String mobId : this.heartContainerKills) {
            if (mobId == null || mobId.isEmpty()) {
                continue;
            }
            if (out.length() > 0) {
                out.append(',');
            }
            out.append(mobId);
        }
        return out.toString();
    }

    private void deserializeHeartContainerKills(String serialized) {
        this.heartContainerKills.clear();
        if (serialized == null || serialized.isEmpty()) {
            return;
        }

        String[] entries = serialized.split(",");
        for (String entry : entries) {
            String normalizedEntry = this.normalizeHeartContainerMobId(entry);
            if (!normalizedEntry.isEmpty()) {
                this.heartContainerKills.add(normalizedEntry);
            }
        }
    }
}
