/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.InsnNode
 *  org.objectweb.asm.tree.JumpInsnNode
 *  org.objectweb.asm.tree.LabelNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.TypeInsnNode
 *  org.objectweb.asm.tree.VarInsnNode
 */
package de.sanandrew.core.manpack.transformer;

import de.sanandrew.core.manpack.transformer.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformEnderman
implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if ("net.minecraft.entity.monster.EntityEnderman".equals(transformedName)) {
            return TransformEnderman.transformEnderman(bytes);
        }
        return bytes;
    }

    private static byte[] transformEnderman(byte[] bytes) {
        ClassNode clazz = ASMHelper.createClassNode(bytes);
        MethodNode method = ASMHelper.findMethod(clazz, "net/minecraft/entity/monster/EntityEnderman/shouldAttackPlayer (Lnet/minecraft/entity/player/EntityPlayer;)Z");
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new VarInsnNode(25, 1));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/player/EntityPlayer/inventory Lnet/minecraft/entity/player/InventoryPlayer;"));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/player/InventoryPlayer/armorInventory [Lnet/minecraft/item/ItemStack;"));
        needle.add((AbstractInsnNode)new InsnNode(6));
        needle.add((AbstractInsnNode)new InsnNode(50));
        needle.add((AbstractInsnNode)new VarInsnNode(58, 2));
        AbstractInsnNode insertPt = ASMHelper.findFirstNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(178, "de/sanandrew/core/manpack/util/helpers/SAPUtils/EVENT_BUS Lcpw/mods/fml/common/eventhandler/EventBus;"));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(187, "de/sanandrew/core/manpack/util/event/entity/EnderFacingEvent"));
        newInstr.add((AbstractInsnNode)new InsnNode(89));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 1));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "de/sanandrew/core/manpack/util/event/entity/EnderFacingEvent/<init> (Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/monster/EntityEnderman;)V", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "cpw/mods/fml/common/eventhandler/EventBus/post (Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
        LabelNode l1 = new LabelNode();
        newInstr.add((AbstractInsnNode)new JumpInsnNode(153, l1));
        newInstr.add((AbstractInsnNode)new InsnNode(3));
        newInstr.add((AbstractInsnNode)new InsnNode(172));
        newInstr.add((AbstractInsnNode)l1);
        method.instructions.insertBefore(insertPt, newInstr);
        return ASMHelper.createBytes(clazz, 1);
    }
}

