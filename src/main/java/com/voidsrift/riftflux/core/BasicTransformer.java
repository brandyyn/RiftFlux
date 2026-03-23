package com.voidsrift.riftflux.core;

import com.voidsrift.riftflux.ModConfig;
import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.*;

import java.io.File;

public class BasicTransformer implements IClassTransformer {

    public static final Logger LOG = LogManager.getLogger("RiftCore");

    public static Side side = FMLLaunchHandler.side();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (basicClass == null) {
            return null;
        }
        ensureConfigLoaded();
        byte[] transformed = basicClass;
        if (ModConfig.disableFalseCrashImprover
                && transformedName.equals("net.minecraft.crash.CrashReport")) {
            transformed = transformCrashReport(transformedName, transformed);
        }
        if (ModConfig.disableDragonAPILogging) {
            if (transformedName.equals("Reika.DragonAPI.DragonAPICore")) {
                transformed = transformDragonAPICoreLogging(transformedName, transformed);
            }
            if (transformedName.equals("Reika.DragonAPI.Instantiable.IO.ModLogger")) {
                transformed = transformDragonAPIModLogger(transformedName, transformed);
            }
            if (transformedName.equals("Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary")) {
                transformed = transformReikaJavaLibraryLogging(transformedName, transformed);
            }
            if (transformedName.startsWith("Reika.DragonAPI.")) {
                transformed = stripDragonAPIDirectDebugOutput(transformedName, transformed);
            }
        }
        if(transformedName.equals("Reika.DragonAPI.Auxiliary.DragonAPIEventWatcher"))
            transformed = transformDragonAPI(transformedName, transformed);
        return transformed;
    }

    private static void ensureConfigLoaded() {
        if (ModConfig.config == null) {
            ModConfig.init(new File("config/riftflux.cfg"));
        }
    }

    public static byte[] transformCrashReport(String name, byte[] classBytes) {
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(classBytes);
        reader.accept(classNode, 0);

        boolean modified = false;

        for (MethodNode method : classNode.methods) {
            if (!method.name.equals("saveToFile") && !method.name.equals("func_147149_a")) {
                continue;
            }

            for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; ) {
                AbstractInsnNode next = insn.getNext();
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode methodInsn = (MethodInsnNode) insn;
                    if (methodInsn.getOpcode() == Opcodes.INVOKESTATIC
                            && methodInsn.owner.equals("com/falsepattern/lib/internal/logging/CrashImprover")
                            && methodInsn.name.equals("injectLatest")
                            && methodInsn.desc.equals("(Ljava/io/FileWriter;)V")) {
                        AbstractInsnNode previous = insn.getPrevious();
                        if (previous != null && previous.getOpcode() == Opcodes.DUP) {
                            method.instructions.remove(previous);
                        }
                        method.instructions.remove(insn);
                        modified = true;
                    }
                }
                insn = next;
            }
        }

        if (!modified) {
            return classBytes;
        }

        LOG.info("Removed FalsePatternLib crash log append hook from {}", name);
        ClassWriter writer = new ClassWriter(0);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static byte[] transformDragonAPI(String name, byte[] classBytes) {
        if (side == Side.SERVER)
            return classBytes;
        LOG.info("Patching class with name " + name);
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(classBytes);
        reader.accept(classNode, 0);
        String methodName = "onGameLoaded";
        MethodNode mn = null;
        for (MethodNode node : classNode.methods) {
            if (node.name.equals(methodName)) {
                mn = node;
                break;
            }
        }
        if (mn == null) {
            throw new RuntimeException("Could not find method with name " + methodName);
        }
        AbstractInsnNode min = getMethodCall(mn,
                "Reika/DragonAPI/Auxiliary/Trackers/ReflectiveFailureTracker",
                "print",
                "print",
                "()V");
        mn.instructions.insert(min,new InsnNode(Opcodes.RETURN));
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    public static byte[] transformDragonAPICoreLogging(String name, byte[] classBytes) {
        ClassNode classNode = readClass(classBytes);
        boolean modified = false;
        modified |= replaceVoidMethodBody(classNode, "debugPrint", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "debug", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "log", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "logError", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "logError", "(Ljava/lang/Object;Lcpw/mods/fml/relauncher/Side;)V");
        if (!modified) {
            return classBytes;
        }
        LOG.info("Disabled DragonAPI core log entry points in {}", name);
        return writeClass(classNode);
    }

    public static byte[] transformDragonAPIModLogger(String name, byte[] classBytes) {
        ClassNode classNode = readClass(classBytes);
        boolean modified = false;
        modified |= replaceVoidMethodBody(classNode, "debug", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "log", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "logError", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "warn", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "write", "(Lorg/apache/logging/log4j/Level;Ljava/lang/String;)V");
        if (!modified) {
            return classBytes;
        }
        LOG.info("Disabled DragonAPI shared mod logger output in {}", name);
        return writeClass(classNode);
    }

    public static byte[] transformReikaJavaLibraryLogging(String name, byte[] classBytes) {
        ClassNode classNode = readClass(classBytes);
        boolean modified = false;
        modified |= replaceVoidMethodBody(classNode, "pConsole", "(Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "pConsole", "(Lorg/apache/logging/log4j/Level;Ljava/lang/Object;)V");
        modified |= replaceVoidMethodBody(classNode, "dumpStack", "()V");
        modified |= replaceVoidMethodBody(classNode, "writeLineToConsoleAndLogs", "(Lorg/apache/logging/log4j/Level;Ljava/lang/String;)V");
        if (!modified) {
            return classBytes;
        }
        LOG.info("Disabled DragonAPI console/log sink in {}", name);
        return writeClass(classNode);
    }

    public static byte[] stripDragonAPIDirectDebugOutput(String name, byte[] classBytes) {
        ClassNode classNode = readClass(classBytes);
        boolean modified = false;
        for (MethodNode method : classNode.methods) {
            for (AbstractInsnNode insn = method.instructions.getFirst(); insn != null; ) {
                AbstractInsnNode next = insn.getNext();
                if (insn instanceof MethodInsnNode) {
                    MethodInsnNode methodInsn = (MethodInsnNode)insn;
                    if (isThreadDumpStack(methodInsn) || isThrowablePrintStackTrace(methodInsn)) {
                        replaceInvocationWithPops(method, methodInsn);
                        modified = true;
                    }
                }
                insn = next;
            }
        }
        if (!modified) {
            return classBytes;
        }
        LOG.info("Disabled DragonAPI direct stack dump/printStackTrace calls in {}", name);
        return writeClass(classNode);
    }

    private static ClassNode readClass(byte[] classBytes) {
        ClassNode classNode = new ClassNode();
        ClassReader reader = new ClassReader(classBytes);
        reader.accept(classNode, 0);
        return classNode;
    }

    private static byte[] writeClass(ClassNode classNode) {
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        classNode.accept(writer);
        return writer.toByteArray();
    }

    private static boolean replaceVoidMethodBody(ClassNode classNode, String methodName, String desc) {
        for (MethodNode method : classNode.methods) {
            if (!method.name.equals(methodName) || !method.desc.equals(desc)) {
                continue;
            }
            method.instructions.clear();
            method.tryCatchBlocks.clear();
            method.instructions.add(new InsnNode(Opcodes.RETURN));
            method.maxStack = 0;
            return true;
        }
        return false;
    }

    private static boolean isThreadDumpStack(MethodInsnNode methodInsn) {
        return methodInsn.getOpcode() == Opcodes.INVOKESTATIC
                && methodInsn.owner.equals("java/lang/Thread")
                && methodInsn.name.equals("dumpStack")
                && methodInsn.desc.equals("()V");
    }

    private static boolean isThrowablePrintStackTrace(MethodInsnNode methodInsn) {
        return methodInsn.getOpcode() != Opcodes.INVOKESTATIC
                && methodInsn.name.equals("printStackTrace")
                && (methodInsn.desc.equals("()V")
                || methodInsn.desc.equals("(Ljava/io/PrintStream;)V")
                || methodInsn.desc.equals("(Ljava/io/PrintWriter;)V"));
    }

    private static void replaceInvocationWithPops(MethodNode method, MethodInsnNode methodInsn) {
        InsnList replacement = new InsnList();
        Type[] args = Type.getArgumentTypes(methodInsn.desc);
        for (int i = args.length - 1; i >= 0; i--) {
            replacement.add(new InsnNode(args[i].getSize() == 2 ? Opcodes.POP2 : Opcodes.POP));
        }
        if (methodInsn.getOpcode() != Opcodes.INVOKESTATIC) {
            replacement.add(new InsnNode(Opcodes.POP));
        }
        method.instructions.insertBefore(methodInsn, replacement);
        method.instructions.remove(methodInsn);
    }

    public static MethodInsnNode getMethodCall(MethodNode mn, String owner, String nameDeobf, String nameObf, String sig) {
        for (int i = 0; i < mn.instructions.size(); i++) {
            AbstractInsnNode ain = mn.instructions.get(i);
            if (ain instanceof MethodInsnNode) {
                MethodInsnNode min = (MethodInsnNode)ain;
                if (min.owner.equals(owner) && (min.name.equals(nameDeobf) || min.name.equals(nameObf)) && min.desc.equals(sig)) {
                    return min;
                }
            }
        }
        throw new RuntimeException("Could not find method call " +nameDeobf);
    }
}
