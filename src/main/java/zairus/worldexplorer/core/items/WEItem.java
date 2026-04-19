/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.relauncher.Side
 *  cpw.mods.fml.relauncher.SideOnly
 *  net.minecraft.creativetab.CreativeTabs
 *  net.minecraft.item.Item
 */
package zairus.worldexplorer.core.items;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import zairus.worldexplorer.core.gui.IGuiOverlay;

public class WEItem
extends Item {
    protected List<Improvement> itemImprovements = new ArrayList<Improvement>();

    public WEItem() {
        this.addImprovements();
    }

    public WEItem setUnlocalizedName(String name) {
        super.setUnlocalizedName(name);
        return this;
    }

    public WEItem setTextureName(String name) {
        this.iconString = name;
        return this;
    }

    public String getUnlocalizedName() {
        return super.getUnlocalizedName();
    }

    public WEItem setCreativeTab(CreativeTabs tab) {
        super.setCreativeTab(tab);
        return this;
    }

    public WEItem setMaxStackSize(int size) {
        super.setMaxStackSize(size);
        return this;
    }

    public boolean holdsAmmo() {
        return false;
    }

    public boolean updatesFOV() {
        return false;
    }

    public float getFOVValue() {
        return 0.0f;
    }

    public float getFOVSpeedFactor() {
        return 0.0f;
    }

    @SideOnly(value=Side.CLIENT)
    public IGuiOverlay getUseOverlay() {
        return null;
    }

    protected void addImprovements() {
    }

    public List<Improvement> getItemImprovements() {
        return this.itemImprovements;
    }

    public boolean hasImprovements() {
        return this.itemImprovements.size() > 0;
    }

    public Improvement getImprovementFromMaterial(Item material) {
        Improvement imp = null;
        for (int i = 0; i < this.itemImprovements.size(); ++i) {
            if (this.itemImprovements.get((int)i).material != material) continue;
            imp = this.itemImprovements.get(i);
            break;
        }
        return imp;
    }

    public class Improvement {
        public final Item material;
        public final ImprovementType improvementType;
        public final float valuePerUnit;

        public Improvement(Item material, ImprovementType type, float value) {
            this.material = material;
            this.improvementType = type;
            this.valuePerUnit = value;
        }
    }

    public static enum ImprovementType {
        IMPACT("Gunpowder Modifier", 3.0f),
        ADHERENCE("Slime Modifier", 8.0f),
        POWER("Glowstone Modifier", 3.0f),
        ENERGETIC("Redstone Modifier", 48.0f),
        ENDER("Ender Modifier", 100.0f),
        BLAZE("Fire Modifier", 3.0f);

        private String improvementKey;
        private float maxValue;

        private ImprovementType(String key, float maxValue) {
            this.improvementKey = key;
            this.maxValue = maxValue;
        }

        public String getKey() {
            return this.improvementKey;
        }

        public float getMaxValue() {
            return this.maxValue;
        }

        public String toString() {
            return super.toString();
        }
    }
}

