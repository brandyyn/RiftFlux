package com.excsi.riftfixes.core;

import cpw.mods.fml.relauncher.FMLLaunchHandler;
import cpw.mods.fml.relauncher.Side;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.*;

public class BasicTransformer implements IClassTransformer {

    public static final Logger LOG = LogManager.getLogger("RiftCore");

    public static Side side = FMLLaunchHandler.side();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if(transformedName.equals("Reika.DragonAPI.Auxiliary.DragonAPIEventWatcher"))
            return transformDragonAPI(transformedName,basicClass);
        return basicClass;
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
