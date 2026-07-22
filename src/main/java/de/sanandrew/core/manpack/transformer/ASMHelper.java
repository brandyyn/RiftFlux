/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Level
 *  org.objectweb.asm.ClassReader
 *  org.objectweb.asm.ClassVisitor
 *  org.objectweb.asm.ClassWriter
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.FieldInsnNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.MethodInsnNode
 *  org.objectweb.asm.tree.MethodNode
 */
package de.sanandrew.core.manpack.transformer;

import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.transformer.ASMNames;
import de.sanandrew.core.manpack.transformer.InstructionComparator;
import de.sanandrew.core.manpack.util.javatuples.Triplet;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

public final class ASMHelper {
    public static boolean isMCP = false;

    public static byte[] createBytes(ClassNode cnode, int cwFlags) {
        ClassWriter cw = new ClassWriter(cwFlags);
        cnode.accept((ClassVisitor)cw);
        byte[] bArr = cw.toByteArray();
        ManPackLoadingPlugin.MOD_LOG.log(Level.INFO, String.format("Class %s successfully transformed!", cnode.name));
        return bArr;
    }

    public static ClassNode createClassNode(byte[] bytes) {
        ClassNode cnode = new ClassNode();
        ClassReader reader = new ClassReader(bytes);
        reader.accept((ClassVisitor)cnode, 8);
        return cnode;
    }

    public static AbstractInsnNode findFirstNodeFromNeedle(InsnList haystack, InsnList needle) {
        List<AbstractInsnNode> ret = InstructionComparator.insnListFindStart(haystack, needle);
        if (ret.size() != 1) {
            throw new InvalidNeedleException(ret.size());
        }
        return ret.get(0);
    }

    public static AbstractInsnNode findLastNodeFromNeedle(InsnList haystack, InsnList needle) {
        List<AbstractInsnNode> ret = InstructionComparator.insnListFindEnd(haystack, needle);
        if (ret.size() != 1) {
            throw new InvalidNeedleException(ret.size());
        }
        return ret.get(0);
    }

    public static boolean hasClassMethod(ClassNode cn, String method) {
        Triplet<String, String, String[]> methodDesc = ASMNames.getSrgNameMd(method);
        for (MethodNode methodNd : cn.methods) {
            if (!methodNd.name.equals(methodDesc.getValue1()) || !methodNd.desc.equals(methodDesc.getValue2()[0])) continue;
            return true;
        }
        return false;
    }

    private static MethodNode findMethodNode(ClassNode cnode, String name, String desc) {
        for (MethodNode mnode : cnode.methods) {
            if (!name.equals(mnode.name) || !desc.equals(mnode.desc)) continue;
            return mnode;
        }
        throw new MethodNotFoundException(name, desc);
    }

    public static String getRemappedMF(String mcp, String srg) {
        if (isMCP) {
            return mcp;
        }
        return srg;
    }

    public static void removeNeedleFromHaystack(InsnList haystack, InsnList needle) {
        int firstInd = haystack.indexOf(ASMHelper.findFirstNodeFromNeedle(haystack, needle));
        int lastInd = haystack.indexOf(ASMHelper.findLastNodeFromNeedle(haystack, needle));
        ArrayList<AbstractInsnNode> realNeedle = new ArrayList<AbstractInsnNode>();
        for (int i = firstInd; i <= lastInd; ++i) {
            realNeedle.add(haystack.get(i));
        }
        for (AbstractInsnNode node : realNeedle) {
            haystack.remove(node);
        }
    }

    public static void writeClassToFile(byte[] classBytes, String file) {
        try (FileOutputStream out = new FileOutputStream(file);){
            out.write(classBytes);
        }
        catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public static MethodNode getMethodNode(int access, String method) {
        Triplet<String, String, String[]> methodDesc = ASMNames.getSrgNameMd(method);
        String sig = methodDesc.getValue2().length > 1 ? methodDesc.getValue2()[1] : null;
        String[] throwing = methodDesc.getValue2().length > 2 ? methodDesc.getValue2()[2].split(";") : null;
        return new MethodNode(access, methodDesc.getValue1(), methodDesc.getValue2()[0], sig, throwing);
    }

    public static MethodInsnNode getMethodInsnNode(int opcode, String method, boolean intf) {
        Triplet<String, String, String[]> methodDesc = ASMNames.getSrgNameMd(method);
        return new MethodInsnNode(opcode, methodDesc.getValue0(), methodDesc.getValue1(), methodDesc.getValue2()[0], intf);
    }

    public static void visitMethodInsn(MethodNode node, int opcode, String method, boolean intf) {
        MethodInsnNode mdiNode = ASMHelper.getMethodInsnNode(opcode, method, intf);
        node.visitMethodInsn(opcode, mdiNode.owner, mdiNode.name, mdiNode.desc, intf);
    }

    public static MethodNode findMethod(ClassNode clazz, String method) {
        Triplet<String, String, String[]> methodDesc = ASMNames.getSrgNameMd(method);
        return ASMHelper.findMethodNode(clazz, methodDesc.getValue1(), methodDesc.getValue2()[0]);
    }

    public static FieldInsnNode getFieldInsnNode(int opcode, String field) {
        Triplet<String, String, String> fieldDesc = ASMNames.getSrgNameFd(field);
        return new FieldInsnNode(opcode, fieldDesc.getValue0(), fieldDesc.getValue1(), fieldDesc.getValue2());
    }

    public static void visitFieldInsn(MethodNode node, int opcode, String field) {
        FieldInsnNode fdiNode = ASMHelper.getFieldInsnNode(opcode, field);
        node.visitFieldInsn(opcode, fdiNode.owner, fdiNode.name, fdiNode.desc);
    }

    public static class MethodNotFoundException
    extends RuntimeException {
        private static final long serialVersionUID = 7439846361566319105L;

        public MethodNotFoundException(String methodName, String methodDesc) {
            super(String.format("Could not find any method matching the name < %s > and description < %s >", methodName, methodDesc));
        }
    }

    public static class InvalidNeedleException
    extends RuntimeException {
        private static final long serialVersionUID = -913530798954926801L;

        public InvalidNeedleException(int count) {
            super(count > 1 ? "Multiple Needles found in Haystack!" : (count < 1 ? "Needle not found in Haystack!" : "Wait, Needle was found!? o.O"));
        }
    }
}

