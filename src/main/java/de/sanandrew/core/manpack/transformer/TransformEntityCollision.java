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
 *  org.objectweb.asm.tree.TypeInsnNode
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
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformEntityCollision
implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if ("net.minecraft.world.World".equals(transformedName)) {
            return TransformEntityCollision.transformWorld(bytes);
        }
        if ("net.minecraft.entity.Entity".equals(transformedName)) {
            return TransformEntityCollision.transformEntity(bytes);
        }
        return bytes;
    }

    private static byte[] transformEntity(byte[] bytes) {
        ClassNode clazz = ASMHelper.createClassNode(bytes);
        MethodNode method = ASMHelper.getMethodNode(1, "net/minecraft/entity/Entity/_SAP_getBoundingBox (Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Lnet/minecraft/util/AxisAlignedBB;");
        method.visitCode();
        Label l0 = new Label();
        method.visitLabel(l0);
        method.visitVarInsn(25, 2);
        method.visitInsn(176);
        Label l1 = new Label();
        method.visitLabel(l1);
        method.visitLocalVariable("this", "Lnet/minecraft/entity/Entity;", null, l0, l1, 0);
        method.visitLocalVariable("entity", "Lnet/minecraft/entity/Entity;", null, l0, l1, 1);
        method.visitLocalVariable("oldAABB", "Lnet/minecraft/util/AxisAlignedBB;", null, l0, l1, 2);
        method.visitMaxs(1, 2);
        method.visitEnd();
        clazz.methods.add(method);
        bytes = ASMHelper.createBytes(clazz, 1);
        return bytes;
    }

    private static byte[] transformWorld(byte[] bytes) {
        ClassNode clazz = ASMHelper.createClassNode(bytes);
        MethodNode method = ASMHelper.findMethod(clazz, "net/minecraft/world/World/getCollidingBoundingBoxes (Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;");
        InsnList needle = new InsnList();
        LabelNode ln = new LabelNode();
        needle.add((AbstractInsnNode)ln);
        needle.add((AbstractInsnNode)new LineNumberNode(-1, ln));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 1));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 2));
        needle.add((AbstractInsnNode)new VarInsnNode(24, -1));
        needle.add((AbstractInsnNode)new VarInsnNode(24, -1));
        needle.add((AbstractInsnNode)new VarInsnNode(24, -1));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/util/AxisAlignedBB/expand (DDD)Lnet/minecraft/util/AxisAlignedBB;", false));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/world/World/getEntitiesWithinAABBExcludingEntity (Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Ljava/util/List;", false));
        needle.add((AbstractInsnNode)new VarInsnNode(58, -1));
        VarInsnNode insertPoint = (VarInsnNode)ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        InsnList injectList = new InsnList();
        injectList.add((AbstractInsnNode)new LabelNode());
        injectList.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(178, "de/sanandrew/core/manpack/util/helpers/SAPUtils/EVENT_BUS Lcpw/mods/fml/common/eventhandler/EventBus;"));
        injectList.add((AbstractInsnNode)new TypeInsnNode(187, "de/sanandrew/core/manpack/util/event/entity/CollidingEntityCheckEvent"));
        injectList.add((AbstractInsnNode)new InsnNode(89));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 0));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, insertPoint.var));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 1));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 2));
        injectList.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "de/sanandrew/core/manpack/util/event/entity/CollidingEntityCheckEvent/<init> (Lnet/minecraft/world/World;Ljava/util/List;Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)V", false));
        injectList.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "cpw/mods/fml/common/eventhandler/EventBus/post (Lcpw/mods/fml/common/eventhandler/Event;)Z", false));
        injectList.add((AbstractInsnNode)new InsnNode(87));
        method.instructions.insert((AbstractInsnNode)insertPoint, injectList);
        needle = new InsnList();
        ln = new LabelNode();
        needle.add((AbstractInsnNode)ln);
        needle.add((AbstractInsnNode)new LineNumberNode(-1, ln));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 11));
        needle.add((AbstractInsnNode)new VarInsnNode(21, 12));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(185, "java/util/List/get (I)Ljava/lang/Object;", true));
        needle.add((AbstractInsnNode)new TypeInsnNode(192, "net/minecraft/entity/Entity"));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/Entity/getBoundingBox ()Lnet/minecraft/util/AxisAlignedBB;", false));
        needle.add((AbstractInsnNode)new VarInsnNode(58, -1));
        insertPoint = (VarInsnNode)ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        injectList = new InsnList();
        injectList.add((AbstractInsnNode)new LabelNode());
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 11));
        injectList.add((AbstractInsnNode)new VarInsnNode(21, 12));
        injectList.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(185, "java/util/List/get (I)Ljava/lang/Object;", true));
        injectList.add((AbstractInsnNode)new TypeInsnNode(192, "net/minecraft/entity/Entity"));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, 1));
        injectList.add((AbstractInsnNode)new VarInsnNode(25, insertPoint.var));
        injectList.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/Entity/_SAP_getBoundingBox (Lnet/minecraft/entity/Entity;Lnet/minecraft/util/AxisAlignedBB;)Lnet/minecraft/util/AxisAlignedBB;", false));
        injectList.add((AbstractInsnNode)new VarInsnNode(58, insertPoint.var));
        method.instructions.insert((AbstractInsnNode)insertPoint, injectList);
        bytes = ASMHelper.createBytes(clazz, 1);
        return bytes;
    }
}

