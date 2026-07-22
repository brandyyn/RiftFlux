/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.mount.EnumBunnyType;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.world.World;

@SideOnly(value=Side.CLIENT)
public class ParticleBunnyDeath
extends EntityBreakingFX {
    public ParticleBunnyDeath(World world, double x, double y, double z, EnumBunnyType type) {
        super(world, x, y, z, RegistryItems.dollBunnyMount);
        SAPUtils.RGBAValues splitClr = SAPUtils.getRgbaFromColorInt(type.typeColor);
        this.setParticleIcon(RegistryItems.dollBunnyMount.getIconFromDamage(0));
        this.particleRed = (float)splitClr.getRed() / 255.0f;
        this.particleGreen = (float)splitClr.getGreen() / 255.0f;
        this.particleBlue = (float)splitClr.getBlue() / 255.0f;
    }
}

