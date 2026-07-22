/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.soldier.ClaymanTeam;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.world.World;

@SideOnly(value=Side.CLIENT)
public class ParticleSoldierDeath
extends EntityBreakingFX {
    public ParticleSoldierDeath(World world, double x, double y, double z, ClaymanTeam team) {
        super(world, x, y, z, RegistryItems.dollSoldier);
        SAPUtils.RGBAValues splitClr = SAPUtils.getRgbaFromColorInt(team.getIconColor());
        this.setParticleIcon(team.getIconInstance());
        this.particleRed = (float)splitClr.getRed() / 255.0f;
        this.particleGreen = (float)splitClr.getGreen() / 255.0f;
        this.particleBlue = (float)splitClr.getBlue() / 255.0f;
    }
}

