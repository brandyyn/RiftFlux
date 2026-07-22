/*
 * Decompiled with CFR 0.152.
 */
package de.sanandrew.mods.claysoldiers.client.particle;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import de.sanandrew.core.manpack.util.helpers.SAPUtils;
import de.sanandrew.mods.claysoldiers.item.ItemTurtleDoll;
import de.sanandrew.mods.claysoldiers.util.RegistryItems;
import de.sanandrew.mods.claysoldiers.util.mount.EnumTurtleType;
import net.minecraft.client.particle.EntityBreakingFX;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

@SideOnly(value=Side.CLIENT)
public class ParticleTurtleDeath
extends EntityBreakingFX {
    public ParticleTurtleDeath(World world, double x, double y, double z, EnumTurtleType type) {
        super(world, x, y, z, RegistryItems.dollTurtleMount);
        ItemStack stack = ItemTurtleDoll.setType(new ItemStack(RegistryItems.dollTurtleMount), type);
        SAPUtils.RGBAValues splitClr = SAPUtils.getRgbaFromColorInt(RegistryItems.dollTurtleMount.getColorFromItemStack(stack, 0));
        this.setParticleIcon(RegistryItems.dollTurtleMount.getIcon(stack, 0));
        this.particleRed = (float)splitClr.getRed() / 255.0f;
        this.particleGreen = (float)splitClr.getGreen() / 255.0f;
        this.particleBlue = (float)splitClr.getBlue() / 255.0f;
    }
}

