/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.objectweb.asm.tree.AbstractInsnNode
 *  org.objectweb.asm.tree.FieldInsnNode
 *  org.objectweb.asm.tree.IincInsnNode
 *  org.objectweb.asm.tree.InsnList
 *  org.objectweb.asm.tree.IntInsnNode
 *  org.objectweb.asm.tree.JumpInsnNode
 *  org.objectweb.asm.tree.LabelNode
 *  org.objectweb.asm.tree.LdcInsnNode
 *  org.objectweb.asm.tree.LineNumberNode
 *  org.objectweb.asm.tree.LookupSwitchInsnNode
 *  org.objectweb.asm.tree.MethodInsnNode
 *  org.objectweb.asm.tree.TableSwitchInsnNode
 *  org.objectweb.asm.tree.TypeInsnNode
 *  org.objectweb.asm.tree.VarInsnNode
 */
package de.sanandrew.core.manpack.transformer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.IincInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LookupSwitchInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.TableSwitchInsnNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;

public final class InstructionComparator {
    public static boolean fieldInsnEqual(FieldInsnNode insn1, FieldInsnNode insn2) {
        return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
    }

    public static InsnList getImportantList(InsnList list) {
        if (list.size() == 0) {
            return list;
        }
        HashMap<LabelNode, LabelNode> labels = new HashMap<LabelNode, LabelNode>();
        for (AbstractInsnNode insn = list.getFirst(); insn != null; insn = insn.getNext()) {
            if (!(insn instanceof LabelNode)) continue;
            labels.put((LabelNode)insn, (LabelNode)insn);
        }
        InsnList importantNodeList = new InsnList();
        for (AbstractInsnNode insn = list.getFirst(); insn != null; insn = insn.getNext()) {
            if (insn instanceof LabelNode || insn instanceof LineNumberNode) continue;
            importantNodeList.add(insn.clone(labels));
        }
        return importantNodeList;
    }

    public static boolean iincInsnEqual(IincInsnNode node1, IincInsnNode node2) {
        return node1.var == node2.var && node1.incr == node2.incr;
    }

    public static boolean insnEqual(AbstractInsnNode node1, AbstractInsnNode node2) {
        if (node1.getType() != node2.getType()) {
            return false;
        }
        if (node1.getOpcode() != node2.getOpcode()) {
            return false;
        }
        switch (node2.getType()) {
            case 2: {
                return InstructionComparator.varInsnEqual((VarInsnNode)node1, (VarInsnNode)node2);
            }
            case 3: {
                return InstructionComparator.typeInsnEqual((TypeInsnNode)node1, (TypeInsnNode)node2);
            }
            case 4: {
                return InstructionComparator.fieldInsnEqual((FieldInsnNode)node1, (FieldInsnNode)node2);
            }
            case 5: {
                return InstructionComparator.methodInsnEqual((MethodInsnNode)node1, (MethodInsnNode)node2);
            }
            case 9: {
                return InstructionComparator.ldcInsnEqual((LdcInsnNode)node1, (LdcInsnNode)node2);
            }
            case 10: {
                return InstructionComparator.iincInsnEqual((IincInsnNode)node1, (IincInsnNode)node2);
            }
            case 1: {
                return InstructionComparator.intInsnEqual((IntInsnNode)node1, (IntInsnNode)node2);
            }
        }
        return true;
    }

    public static List<Integer> insnListFind(InsnList haystack, InsnList needle) {
        LinkedList<Integer> list = new LinkedList<Integer>();
        for (int start = 0; start <= haystack.size() - needle.size(); ++start) {
            if (!InstructionComparator.insnListMatches(haystack, needle, start)) continue;
            list.add(start);
        }
        return list;
    }

    public static List<AbstractInsnNode> insnListFindEnd(InsnList haystack, InsnList needle) {
        LinkedList<AbstractInsnNode> callNodes = new LinkedList<AbstractInsnNode>();
        for (int callPoint : InstructionComparator.insnListFind(haystack, needle)) {
            callNodes.add(haystack.get(callPoint + needle.size() - 1));
        }
        return callNodes;
    }

    public static List<InsnListSection> insnListFindL(InsnList haystack, InsnList needle) {
        HashSet<LabelNode> controlFlowLabels = new HashSet<LabelNode>();
        block6: for (AbstractInsnNode insn = haystack.getFirst(); insn != null; insn = insn.getNext()) {
            switch (insn.getType()) {
                case 8: 
                case 15: {
                    continue block6;
                }
                case 7: {
                    JumpInsnNode jinsn = (JumpInsnNode)insn;
                    controlFlowLabels.add(jinsn.label);
                    continue block6;
                }
                case 11: {
                    TableSwitchInsnNode tsinsn = (TableSwitchInsnNode)insn;
                    for (LabelNode label : tsinsn.labels) {
                        controlFlowLabels.add(label);
                    }
                    continue block6;
                }
                case 12: {
                    LookupSwitchInsnNode lsinsn = (LookupSwitchInsnNode)insn;
                    Iterator<LabelNode> labels = lsinsn.labels.iterator();
                    while (labels.hasNext()) {
                        LabelNode label2 = labels.next();
                        controlFlowLabels.add(label2);
                    }
                    continue block6;
                }
            }
        }
        LinkedList<InsnListSection> list = new LinkedList<InsnListSection>();
        block9: for (int start = 0; start <= haystack.size() - needle.size(); ++start) {
            InsnListSection section = InstructionComparator.insnListMatchesL(haystack, needle, start, controlFlowLabels);
            if (section == null) continue;
            for (InsnListSection asection : list) {
                if (asection.last != section.last) continue;
                continue block9;
            }
            list.add(section);
        }
        return list;
    }

    public static List<AbstractInsnNode> insnListFindStart(InsnList haystack, InsnList needle) {
        LinkedList<AbstractInsnNode> callNodes = new LinkedList<AbstractInsnNode>();
        for (int callPoint : InstructionComparator.insnListFind(haystack, needle)) {
            callNodes.add(haystack.get(callPoint));
        }
        return callNodes;
    }

    public static boolean insnListMatches(InsnList haystack, InsnList needle, int start) {
        if (haystack.size() - start < needle.size()) {
            return false;
        }
        for (int i = 0; i < needle.size(); ++i) {
            if (InstructionComparator.insnEqual(haystack.get(i + start), needle.get(i))) continue;
            return false;
        }
        return true;
    }

    private static InsnListSection insnListMatchesL(InsnList haystack, InsnList needle, int start, HashSet<LabelNode> controlFlowLabels) {
        int h;
        int n = 0;
        for (h = start; h < haystack.size() && n < needle.size(); ++h) {
            AbstractInsnNode insn = haystack.get(h);
            if (insn.getType() == 15 || insn.getType() == 8 && !controlFlowLabels.contains(insn)) continue;
            if (!InstructionComparator.insnEqual(haystack.get(h), needle.get(n))) {
                return null;
            }
            ++n;
        }
        if (n != needle.size()) {
            return null;
        }
        return new InsnListSection(haystack, start, h - 1);
    }

    public static boolean intInsnEqual(IntInsnNode node1, IntInsnNode node2) {
        return node1.operand == -1 || node2.operand == -1 || node1.operand == node2.operand;
    }

    public static boolean ldcInsnEqual(LdcInsnNode insn1, LdcInsnNode insn2) {
        return insn1.cst.equals("~") || insn2.cst.equals("~") || insn1.cst.equals(insn2.cst);
    }

    public static boolean methodInsnEqual(MethodInsnNode insn1, MethodInsnNode insn2) {
        return insn1.owner.equals(insn2.owner) && insn1.name.equals(insn2.name) && insn1.desc.equals(insn2.desc);
    }

    public static boolean typeInsnEqual(TypeInsnNode insn1, TypeInsnNode insn2) {
        return insn1.desc.equals("~") || insn2.desc.equals("~") || insn1.desc.equals(insn2.desc);
    }

    public static boolean varInsnEqual(VarInsnNode insn1, VarInsnNode insn2) {
        return insn1.var == -1 || insn2.var == -1 || insn1.var == insn2.var;
    }

    public static class InsnListSection {
        public AbstractInsnNode first;
        public AbstractInsnNode last;

        public InsnListSection(InsnList haystack, int start, int end) {
            this.first = haystack.get(start);
            this.last = haystack.get(end);
        }
    }
}
