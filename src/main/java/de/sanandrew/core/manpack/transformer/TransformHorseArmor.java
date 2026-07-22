/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 *  org.apache.logging.log4j.Level
 *  org.objectweb.asm.Label
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FrameNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.InsnNode
 *  org.objectweb.asm.tree.IntInsnNode
 *  org.objectweb.asm.tree.JumpInsnNode
 *  org.objectweb.asm.tree.LabelNode
 *  org.objectweb.asm.tree.LdcInsnNode
 *  org.objectweb.asm.tree.LineNumberNode
 *  org.objectweb.asm.tree.MethodNode
 *  org.objectweb.asm.tree.TypeInsnNode
 *  org.objectweb.asm.tree.VarInsnNode
 */
package de.sanandrew.core.manpack.transformer;

import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.transformer.ASMHelper;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public class TransformHorseArmor
implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        if (transformedName.equals("net.minecraft.entity.passive.EntityHorse")) {
            return TransformHorseArmor.transformHorse(bytes);
        }
        return bytes;
    }

    private static byte[] transformHorse(byte[] bytes) {
        ClassNode clazz = ASMHelper.createClassNode(bytes);
        MethodNode method = TransformHorseArmor.injectMethodSetCustomArmorItem();
        clazz.methods.add(method);
        method = TransformHorseArmor.injectMethodGetCustomArmorItem();
        clazz.methods.add(method);
        TransformHorseArmor.transformInteract(ASMHelper.findMethod(clazz, "net/minecraft/entity/passive/EntityHorse/interact (Lnet/minecraft/entity/player/EntityPlayer;)Z"));
        TransformHorseArmor.transformIsValidArmor(ASMHelper.findMethod(clazz, "net/minecraft/entity/passive/EntityHorse/func_146085_a (Lnet/minecraft/item/Item;)Z"));
        TransformHorseArmor.transformEntityInit(ASMHelper.findMethod(clazz, "net/minecraft/entity/passive/EntityHorse/entityInit ()V"));
        TransformHorseArmor.transformUpdateHorseSlots(ASMHelper.findMethod(clazz, "net/minecraft/entity/passive/EntityHorse/func_110232_cE ()V"));
        TransformHorseArmor.transformOnInvChanged(ASMHelper.findMethod(clazz, "net/minecraft/entity/passive/EntityHorse/onInventoryChanged (Lnet/minecraft/inventory/InventoryBasic;)V"));
        TransformHorseArmor.transformGetTotalArmorValue(ASMHelper.findMethod(clazz, "net/minecraft/entity/passive/EntityHorse/getTotalArmorValue ()I"));
        if (ASMHelper.hasClassMethod(clazz, "net/minecraft/entity/passive/EntityHorse/setHorseTexturePaths ()V")) {
            TransformHorseArmor.transformArmorTexture(ASMHelper.findMethod(clazz, "net/minecraft/entity/passive/EntityHorse/setHorseTexturePaths ()V"));
        } else {
            ManPackLoadingPlugin.MOD_LOG.log(Level.INFO, "Running on dedicated server, no need to transform Method >setHorseTexturePaths< in EntityHorse!");
        }
        bytes = ASMHelper.createBytes(clazz, 1);
        return bytes;
    }

    private static MethodNode injectMethodGetCustomArmorItem() {
        MethodNode method = ASMHelper.getMethodNode(2, "net/minecraft/entity/passive/EntityHorse/_SAP_getCustomArmorItem ()Lnet/minecraft/item/ItemStack;");
        method.visitCode();
        Label l0 = new Label();
        method.visitLabel(l0);
        method.visitVarInsn(25, 0);
        ASMHelper.visitFieldInsn(method, 180, "net/minecraft/entity/passive/EntityHorse/dataWatcher Lnet/minecraft/entity/DataWatcher;");
        method.visitIntInsn(16, 23);
        ASMHelper.visitMethodInsn(method, 182, "net/minecraft/entity/DataWatcher/getWatchableObjectItemStack (I)Lnet/minecraft/item/ItemStack;", false);
        method.visitInsn(176);
        Label l1 = new Label();
        method.visitLabel(l1);
        method.visitLocalVariable("this", "Lnet/minecraft/entity/passive/EntityHorse;", null, l0, l1, 0);
        method.visitMaxs(2, 1);
        method.visitEnd();
        return method;
    }

    private static MethodNode injectMethodSetCustomArmorItem() {
        MethodNode method = ASMHelper.getMethodNode(2, "net/minecraft/entity/passive/EntityHorse/_SAP_setCustomArmorItem (Lnet/minecraft/item/ItemStack;)V");
        method.visitCode();
        Label l0 = new Label();
        method.visitLabel(l0);
        method.visitVarInsn(25, 1);
        Label l1 = new Label();
        method.visitJumpInsn(199, l1);
        method.visitTypeInsn(187, "net/minecraft/item/ItemStack");
        method.visitInsn(89);
        ASMHelper.visitFieldInsn(method, 178, "net/minecraft/init/Items/iron_shovel Lnet/minecraft/item/Item;");
        method.visitInsn(3);
        ASMHelper.visitMethodInsn(method, 183, "net/minecraft/item/ItemStack/<init> (Lnet/minecraft/item/Item;I)V", false);
        method.visitVarInsn(58, 1);
        method.visitLabel(l1);
        method.visitFrame(3, 0, null, 0, null);
        method.visitVarInsn(25, 0);
        ASMHelper.visitFieldInsn(method, 180, "net/minecraft/entity/passive/EntityHorse/dataWatcher Lnet/minecraft/entity/DataWatcher;");
        method.visitIntInsn(16, 23);
        method.visitVarInsn(25, 1);
        ASMHelper.visitMethodInsn(method, 182, "net/minecraft/entity/DataWatcher/updateObject (ILjava/lang/Object;)V", false);
        Label l3 = new Label();
        method.visitLabel(l3);
        method.visitInsn(177);
        Label l4 = new Label();
        method.visitLabel(l4);
        method.visitLocalVariable("this", "Lnet/minecraft/entity/passive/EntityHorse;", null, l0, l3, 0);
        method.visitLocalVariable("stack", "Lnet/minecraft/item/ItemStack;", null, l0, l3, 1);
        method.visitMaxs(5, 2);
        method.visitEnd();
        return method;
    }

    private static void transformGetTotalArmorValue(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new LabelNode());
        needle.add((AbstractInsnNode)new LineNumberNode(-1, new LabelNode()));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(178, "net/minecraft/entity/passive/EntityHorse/armorValues [I"));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/passive/EntityHorse/func_110241_cb ()I", false));
        AbstractInsnNode pointer = ASMHelper.findFirstNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "net/minecraft/entity/passive/EntityHorse/_SAP_getCustomArmorItem ()Lnet/minecraft/item/ItemStack;", false));
        newInstr.add((AbstractInsnNode)new VarInsnNode(58, 1));
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 1));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/getItem ()Lnet/minecraft/item/Item;", false));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(193, "de/sanandrew/core/manpack/item/AItemHorseArmor"));
        LabelNode l2 = new LabelNode();
        newInstr.add((AbstractInsnNode)new JumpInsnNode(153, l2));
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 1));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/getItem ()Lnet/minecraft/item/Item;", false));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(192, "de/sanandrew/core/manpack/item/AItemHorseArmor"));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 1));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "de/sanandrew/core/manpack/item/AItemHorseArmor/getArmorValue (Lnet/minecraft/entity/passive/EntityHorse;Lnet/minecraft/item/ItemStack;)I", false));
        newInstr.add((AbstractInsnNode)new InsnNode(172));
        newInstr.add((AbstractInsnNode)l2);
        method.instructions.insertBefore(pointer, newInstr);
    }

    private static void transformOnInvChanged(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/passive/EntityHorse/isHorseSaddled ()Z", false));
        needle.add((AbstractInsnNode)new VarInsnNode(54, 3));
        AbstractInsnNode pointer = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "net/minecraft/entity/passive/EntityHorse/_SAP_getCustomArmorItem ()Lnet/minecraft/item/ItemStack;", false));
        newInstr.add((AbstractInsnNode)new VarInsnNode(58, 4));
        method.instructions.insert(pointer, newInstr);
        needle = new InsnList();
        needle.add((AbstractInsnNode)new LdcInsnNode((Object)"mob.horse.armor"));
        needle.add((AbstractInsnNode)new LdcInsnNode((Object)Float.valueOf(0.5f)));
        needle.add((AbstractInsnNode)new InsnNode(12));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/passive/EntityHorse/playSound (Ljava/lang/String;FF)V", false));
        needle.add((AbstractInsnNode)new LabelNode());
        pointer = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 4));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/getItem ()Lnet/minecraft/item/Item;", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(178, "net/minecraft/init/Items/iron_shovel Lnet/minecraft/item/Item;"));
        LabelNode l9 = new LabelNode();
        newInstr.add((AbstractInsnNode)new JumpInsnNode(166, l9));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 4));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "net/minecraft/entity/passive/EntityHorse/_SAP_getCustomArmorItem ()Lnet/minecraft/item/ItemStack;", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/isItemEqual (Lnet/minecraft/item/ItemStack;)Z", false));
        newInstr.add((AbstractInsnNode)new JumpInsnNode(154, l9));
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)new LdcInsnNode((Object)"mob.horse.armor"));
        newInstr.add((AbstractInsnNode)new LdcInsnNode((Object)Float.valueOf(0.5f)));
        newInstr.add((AbstractInsnNode)new InsnNode(12));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/passive/EntityHorse/playSound (Ljava/lang/String;FF)V", false));
        newInstr.add((AbstractInsnNode)l9);
        method.instructions.insert(pointer, newInstr);
    }

    private static void transformArmorTexture(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/passive/EntityHorse/func_110241_cb ()I", false));
        needle.add((AbstractInsnNode)new VarInsnNode(54, 3));
        AbstractInsnNode node = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        LabelNode l17 = new LabelNode();
        newInstr.add((AbstractInsnNode)l17);
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "net/minecraft/entity/passive/EntityHorse/_SAP_getCustomArmorItem ()Lnet/minecraft/item/ItemStack;", false));
        newInstr.add((AbstractInsnNode)new VarInsnNode(58, 4));
        LabelNode l18 = new LabelNode();
        newInstr.add((AbstractInsnNode)l18);
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 4));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/getItem ()Lnet/minecraft/item/Item;", false));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(193, "de/sanandrew/core/manpack/item/AItemHorseArmor"));
        LabelNode l19 = new LabelNode();
        newInstr.add((AbstractInsnNode)new JumpInsnNode(153, l19));
        LabelNode l20 = new LabelNode();
        newInstr.add((AbstractInsnNode)l20);
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/passive/EntityHorse/field_110280_bR [Ljava/lang/String;"));
        newInstr.add((AbstractInsnNode)new InsnNode(5));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 4));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/getItem ()Lnet/minecraft/item/Item;", false));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(192, "de/sanandrew/core/manpack/item/AItemHorseArmor"));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 4));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "de/sanandrew/core/manpack/item/AItemHorseArmor/getArmorTexture (Lnet/minecraft/entity/passive/EntityHorse;Lnet/minecraft/item/ItemStack;)Ljava/lang/String;", false));
        newInstr.add((AbstractInsnNode)new InsnNode(83));
        LabelNode l21 = new LabelNode();
        newInstr.add((AbstractInsnNode)l21);
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(187, "java/lang/StringBuilder"));
        newInstr.add((AbstractInsnNode)new InsnNode(89));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/passive/EntityHorse/field_110286_bQ Ljava/lang/String;"));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(184, "java/lang/String/valueOf (Ljava/lang/Object;)Ljava/lang/String;", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "java/lang/StringBuilder/<init> (Ljava/lang/String;)V", false));
        newInstr.add((AbstractInsnNode)new LdcInsnNode((Object)"cst-"));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "java/lang/StringBuilder/append (Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 4));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/getUnlocalizedName ()Ljava/lang/String;", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "java/lang/StringBuilder/append (Ljava/lang/String;)Ljava/lang/StringBuilder;", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "java/lang/StringBuilder/toString ()Ljava/lang/String;", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(181, "net/minecraft/entity/passive/EntityHorse/field_110286_bQ Ljava/lang/String;"));
        newInstr.add((AbstractInsnNode)new InsnNode(177));
        newInstr.add((AbstractInsnNode)l19);
        method.instructions.insert(node, newInstr);
    }

    private static void transformUpdateHorseSlots(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/passive/EntityHorse/horseChest Lnet/minecraft/inventory/AnimalChest;"));
        needle.add((AbstractInsnNode)new InsnNode(4));
        AbstractInsnNode pointer = ASMHelper.findFirstNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/passive/EntityHorse/horseChest Lnet/minecraft/inventory/AnimalChest;"));
        newInstr.add((AbstractInsnNode)new InsnNode(4));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/inventory/AnimalChest/getStackInSlot (I)Lnet/minecraft/item/ItemStack;", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "net/minecraft/entity/passive/EntityHorse/_SAP_setCustomArmorItem (Lnet/minecraft/item/ItemStack;)V", false));
        newInstr.add((AbstractInsnNode)new LabelNode());
        method.instructions.insertBefore(pointer, newInstr);
    }

    private static void transformEntityInit(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/passive/EntityHorse/dataWatcher Lnet/minecraft/entity/DataWatcher;"));
        needle.add((AbstractInsnNode)new IntInsnNode(16, 22));
        needle.add((AbstractInsnNode)new InsnNode(3));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(184, "java/lang/Integer/valueOf (I)Ljava/lang/Integer;", false));
        needle.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/DataWatcher/addObject (ILjava/lang/Object;)V", false));
        AbstractInsnNode pointer = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(180, "net/minecraft/entity/passive/EntityHorse/dataWatcher Lnet/minecraft/entity/DataWatcher;"));
        newInstr.add((AbstractInsnNode)new IntInsnNode(16, 23));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(187, "net/minecraft/item/ItemStack"));
        newInstr.add((AbstractInsnNode)new InsnNode(89));
        newInstr.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(178, "net/minecraft/init/Items/iron_shovel Lnet/minecraft/item/Item;"));
        newInstr.add((AbstractInsnNode)new InsnNode(3));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(183, "net/minecraft/item/ItemStack/<init> (Lnet/minecraft/item/Item;I)V", false));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/entity/DataWatcher/addObject (ILjava/lang/Object;)V", false));
        method.instructions.insert(pointer, newInstr);
    }

    private static void transformIsValidArmor(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)new LabelNode());
        needle.add((AbstractInsnNode)new LineNumberNode(-1, new LabelNode()));
        needle.add((AbstractInsnNode)new VarInsnNode(25, 0));
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(178, "net/minecraft/init/Items/iron_horse_armor Lnet/minecraft/item/Item;"));
        needle.add((AbstractInsnNode)new JumpInsnNode(165, new LabelNode()));
        AbstractInsnNode node = ASMHelper.findFirstNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 0));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(193, "de/sanandrew/core/manpack/item/AItemHorseArmor"));
        LabelNode ln = new LabelNode();
        newInstr.add((AbstractInsnNode)new JumpInsnNode(153, ln));
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new InsnNode(4));
        newInstr.add((AbstractInsnNode)new InsnNode(172));
        newInstr.add((AbstractInsnNode)ln);
        method.instructions.insertBefore(node, newInstr);
    }

    private static void transformInteract(MethodNode method) {
        InsnList needle = new InsnList();
        needle.add((AbstractInsnNode)ASMHelper.getFieldInsnNode(178, "net/minecraft/init/Items/diamond_horse_armor Lnet/minecraft/item/Item;"));
        needle.add((AbstractInsnNode)new JumpInsnNode(166, new LabelNode()));
        needle.add((AbstractInsnNode)new LabelNode());
        needle.add((AbstractInsnNode)new LineNumberNode(-1, new LabelNode()));
        needle.add((AbstractInsnNode)new InsnNode(6));
        needle.add((AbstractInsnNode)new VarInsnNode(54, 4));
        needle.add((AbstractInsnNode)new LabelNode());
        needle.add((AbstractInsnNode)new LineNumberNode(-1, new LabelNode()));
        needle.add((AbstractInsnNode)new FrameNode(3, 0, null, 0, null));
        AbstractInsnNode node = ASMHelper.findLastNodeFromNeedle(method.instructions, needle);
        InsnList newInstr = new InsnList();
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new VarInsnNode(25, 2));
        newInstr.add((AbstractInsnNode)ASMHelper.getMethodInsnNode(182, "net/minecraft/item/ItemStack/getItem ()Lnet/minecraft/item/Item;", false));
        newInstr.add((AbstractInsnNode)new TypeInsnNode(193, "de/sanandrew/core/manpack/item/AItemHorseArmor"));
        LabelNode l8 = new LabelNode();
        newInstr.add((AbstractInsnNode)new JumpInsnNode(153, l8));
        newInstr.add((AbstractInsnNode)new LabelNode());
        newInstr.add((AbstractInsnNode)new InsnNode(7));
        newInstr.add((AbstractInsnNode)new VarInsnNode(54, 4));
        newInstr.add((AbstractInsnNode)l8);
        newInstr.add((AbstractInsnNode)new FrameNode(3, 0, null, 0, null));
        method.instructions.insert(node, newInstr);
    }
}

