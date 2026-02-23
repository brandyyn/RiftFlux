/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.fml.common.network.simpleimpl.MessageContext
 *  net.minecraft.client.Minecraft
 *  net.minecraft.entity.player.EntityPlayer
 */
package de.rinonline.korinrpg;

import cpw.mods.fml.common.network.simpleimpl.MessageContext;
import de.rinonline.korinrpg.BasisCommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class BasisClientProxy
extends BasisCommonProxy {
    @Override
    public EntityPlayer getPlayerEntity(MessageContext ctx) {
        return ctx.side.isClient() ? Minecraft.getMinecraft().thePlayer : super.getPlayerEntity(ctx);
    }
}

