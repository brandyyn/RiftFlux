/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 *  org.objectweb.asm.Label
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.InsnNode
 *  org.objectweb.asm.tree.LabelNode
 *  org.objectweb.asm.tree.LineNumberNode
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
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformEntityThrowable
implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if ("net.minecraft.entity.projectile.EntityThrowable".equals(transformedName)) {
            return TransformEntityThrowable.transformLqThrowable(bytes);
        }
        return bytes;
    }

    private static byte[] transformLqThrowable(byte[] bytes) {
        ClassNode classNode = ASMHelper.createClassNode(bytes);
        MethodNode method = ASMHelper.getMethodNode(1, "net/minecraft/entity/projectile/EntityThrowable/_SAP_canImpactOnLiquid ()Z");
        method.visitCode();
        Label label1 = new Label();
        method.visitLabel(label1);
        method.visitInsn(3);
        method.visitInsn(172);
        Label label2 = new Label();
        method.visitLabel(label2);
        method.visitLocalVariable("this", "Lnet/minecraft/entity/projectile/EntityThrowable;", null, label1, label2, 0);
        method.visitMaxs(0, 0);
        method.visitEnd();
        classNode.methods.add(method);
        method = ASMHelper.findMethod(classNode, "net/minecraft/entity/projectile/EntityThrowable/onUpdate ()V");
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/projectile/EntityThrowable/motionZ D"));
        needle.add((AbstractInsnNode)new InsnNode(99));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(184, "net/minecraft/util/Vec3/createVectorHelper (DDD)Lnet/minecraft/util/Vec3;", false));
        needle.add((AbstractInsnNode)new VarInsnNode(58, 2));
        needle.add((AbstractInsnNode)new LabelNode());
        needle.add((AbstractInsnNode)new LineNumberNode(-1, new LabelNode()));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/projectile/EntityThrowable/worldObj Lnet/minecraft/world/World;"));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 1));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 2));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/world/World/rayTraceBlocks (Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;)Lnet/minecraft/util/MovingObjectPosition;", false));
        needle.add((AbstractInsnNode)new VarInsnNode(58, -1));
        VarInsnNode insertPoint = (VarInsnNode)ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        InsnList injectList = new InsnList();
        injectList.add((AbstractInsnNode)new LabelNode());
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 0));
        injectList.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/projectile/EntityThrowable/worldObj Lnet/minecraft/world/World;"));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 1));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 2));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 0));
        injectList.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/projectile/EntityThrowable/_SAP_canImpactOnLiquid ()Z", false));
        injectList.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/world/World/rayTraceBlocks (Lnet/minecraft/util/Vec3;Lnet/minecraft/util/Vec3;Z)Lnet/minecraft/util/MovingObjectPosition;", false));
        injectList.add((AbstractInsnNode)new VarInsnNode(58, insertPoint.var));
        method.instructions.insert((AbstractInsnNode)insertPoint, injectList);
        return ASMHelper.createBytes(classNode, 1);
    }
}

