/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 *  org.objectweb.asm.Label
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.JumpInsnNode
 *  org.objectweb.asm.tree.LabelNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.VarInsnNode
 */
package de.sanandrew.core.manpack.transformer;

import de.sanandrew.core.manpack.transformer.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformPlayerDismountCtrl
implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if ("net.minecraft.entity.player.EntityPlayer".equals(transformedName)) {
            return TransformPlayerDismountCtrl.transformPlayer(bytes);
        }
        if ("net.minecraft.entity.Entity".equals(transformedName)) {
            return TransformPlayerDismountCtrl.transformEntity(bytes);
        }
        return bytes;
    }

    private static byte[] transformEntity(byte[] bytes) {
        ClassNode clazz = ASMHelper.createClassNode(bytes);
        MethodNode method = ASMHelper.getMethodNode(1, "net/minecraft/entity/Entity/_SAP_canDismountOnInput (Lnet/minecraft/entity/player/EntityPlayer;)Z");
        method.visitCode();
        Label l0 = new Label();
        method.visitLabel(l0);
        method.visitInsn(4);
        method.visitInsn(172);
        Label l1 = new Label();
        method.visitLabel(l1);
        method.visitLocalVariable("this", "Lnet/minecraft/entity/Entity;", null, l0, l1, 0);
        method.visitLocalVariable("player", "Lnet/minecraft/entity/player/EntityPlayer;", null, l0, l1, 1);
        method.visitMaxs(1, 2);
        method.visitEnd();
        clazz.methods.add(method);
        bytes = ASMHelper.createBytes(clazz, 1);
        return bytes;
    }

    private static byte[] transformPlayer(byte[] bytes) {
        ClassNode clazz = ASMHelper.createClassNode(bytes);
        MethodNode method = ASMHelper.findMethod(clazz, "net/minecraft/entity/player/EntityPlayer/updateRidden ()V");
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/player/EntityPlayer/worldObj Lnet/minecraft/world/World;"));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/world/World/isRemote Z"));
        LabelNode ln1 = new LabelNode();
        needle.add((AbstractInsnNode)new JumpInsnNode(154, ln1));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/player/EntityPlayer/isSneaking ()Z", false));
        needle.add((AbstractInsnNode)new JumpInsnNode(153, ln1));
        AbstractInsnNode insertPoint = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        InsnList injectList = new InsnList();
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 0));
        injectList.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/player/EntityPlayer/ridingEntity Lnet/minecraft/entity/Entity;"));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 0));
        injectList.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/Entity/_SAP_canDismountOnInput (Lnet/minecraft/entity/player/EntityPlayer;)Z", false));
        injectList.add((AbstractInsnNode)new JumpInsnNode(153, ((JumpInsnNode)insertPoint).label));
        method.instructions.insert(insertPoint, injectList);
        bytes = ASMHelper.createBytes(clazz, 1);
        return bytes;
    }
}

