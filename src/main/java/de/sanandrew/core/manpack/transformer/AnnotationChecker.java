/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.launchwrapper.IClassTransformer
 *  org.apache.logging.log4j.Level
 *  org.objectweb.asm.tree.AnnotationNode
 *  org.objectweb.asm.tree.ClassNode
 *  org.objectweb.asm.tree.MethodNode
 */
package de.sanandrew.core.manpack.transformer;

import de.sanandrew.core.manpack.init.ManPackLoadingPlugin;
import de.sanandrew.core.manpack.transformer.ASMHelper;
import de.sanandrew.core.manpack.transformer.ASMNames;
import java.util.regex.Matcher;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.Level;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

public class AnnotationChecker
implements IClassTransformer {
    public byte[] transform(String name, String transformedName, byte[] bytes) {
        ClassNode cn = ASMHelper.createClassNode(bytes);
        for (MethodNode method : cn.methods) {
            if (method.visibleAnnotations == null) continue;
            for (AnnotationNode annotation : method.visibleAnnotations) {
                String err;
                if (!annotation.desc.equals("Lde/sanandrew/core/manpack/util/annotation/ASMOverride;")) continue;
                String asmMethodName = annotation.values.get(1).toString();
                MethodNode asmMethod = AnnotationChecker.getSignature(asmMethodName);
                if (!method.name.equals(asmMethod.name)) {
                    err = "Attempting to override Method %s in Class %s with incompatible name %s!";
                    ManPackLoadingPlugin.MOD_LOG.log(Level.FATAL, String.format(err, asmMethod.name, cn.name, method.name));
                    throw new OverrideException(String.format("Method name %s is not equal to %s!", method.name, asmMethod.name));
                }
                if (!method.desc.equals(asmMethod.desc)) {
                    err = "Attempting to override Method %s, description %s, in Class %s with incompatible description %s!";
                    ManPackLoadingPlugin.MOD_LOG.log(Level.FATAL, String.format(err, asmMethod.name, asmMethod.desc, cn.name, method.desc));
                    throw new OverrideException(String.format("Method desc %s is not equal to %s!", method.desc, asmMethod.desc));
                }
                if (AnnotationChecker.getAccessLevelInt(method.access) < AnnotationChecker.getAccessLevelInt(asmMethod.access)) {
                    err = "Attempting to assign weaker access privileges ('%s') on %s in %s; should be '%s'";
                    ManPackLoadingPlugin.MOD_LOG.log(Level.FATAL, String.format(err, AnnotationChecker.getAccessLevelName(method.access), asmMethod.name, cn.name, AnnotationChecker.getAccessLevelName(asmMethod.access)));
                    throw new OverrideException(String.format("Access level %s is weaker than %s!", AnnotationChecker.getAccessLevelName(method.access), AnnotationChecker.getAccessLevelName(asmMethod.access)));
                }
                if (AnnotationChecker.getAccessLevelInt(asmMethod.access) == 1) {
                    String ownerPkg;
                    String classPkg = cn.name.substring(0, cn.name.lastIndexOf(47));
                    if (classPkg.equals(ownerPkg = ASMHelper.getMethodInsnNode((int)asmMethod.access, (String)asmMethodName, (boolean)false).owner.substring(0, cn.name.lastIndexOf(47)))) continue;
                    String err2 = "Attempting to override packageLocal method %s outside of package %s in class %s, which is in package %s!";
                    ManPackLoadingPlugin.MOD_LOG.log(Level.FATAL, String.format(err2, method.name, ownerPkg, cn.name, classPkg));
                    throw new OverrideException(String.format("Class %s lies outside package %s for method %s to be overridden", cn.name, ownerPkg, classPkg));
                }
                if (!AnnotationChecker.checkBitwiseEqual(asmMethod.access, 131072)) continue;
                ManPackLoadingPlugin.MOD_LOG.log(Level.WARN, String.format("The Method %s is marked as deprecated! It is most likely that the method is not injected in any superclass anymore! Thus this may not be called!", asmMethod.name));
            }
        }
        return bytes;
    }

    private static MethodNode getSignature(String method) {
        String[] split = method.split(" ");
        int accLvl = AnnotationChecker.getAccessLevelOpcode(split[0]);
        Matcher mtch = ASMNames.OWNERNAME.matcher(split[1]);
        if (!mtch.find()) {
            throw new RuntimeException("SAP-Method signature invalid!");
        }
        String name = mtch.group(2);
        String desc = split[2];
        String sig = split.length > 3 ? split[3] : null;
        String[] throwing = split.length > 4 ? split[4].split(";") : null;
        return new MethodNode(accLvl, name, desc, sig, throwing);
    }

    private static int getAccessLevelInt(int access) {
        if (AnnotationChecker.checkBitwiseEqual(access, 2)) {
            return 0;
        }
        if (AnnotationChecker.checkBitwiseEqual(access, 4)) {
            return 2;
        }
        if (AnnotationChecker.checkBitwiseEqual(access, 1)) {
            return 3;
        }
        return 1;
    }

    private static String getAccessLevelName(int access) {
        if (AnnotationChecker.checkBitwiseEqual(access, 2)) {
            return "private";
        }
        if (AnnotationChecker.checkBitwiseEqual(access, 4)) {
            return "protected";
        }
        if (AnnotationChecker.checkBitwiseEqual(access, 1)) {
            return "public";
        }
        return "packageLocal";
    }

    private static int getAccessLevelOpcode(String accessName) {
        String[] accessSplit = accessName.split(";");
        int ret = 0;
        String[] stringArray = accessSplit;
        int n = stringArray.length;
        block12: for (int i = 0; i < n; ++i) {
            String acc;
            switch (acc = stringArray[i]) {
                case "private": {
                    ret |= 2;
                    continue block12;
                }
                case "protected": {
                    ret |= 4;
                    continue block12;
                }
                case "public": {
                    ret |= 1;
                    continue block12;
                }
                case "deprecated": {
                    ret |= 0x20000;
                }
            }
        }
        return ret;
    }

    private static boolean checkBitwiseEqual(int value, int flag) {
        return (value & flag) == flag;
    }

    public static class OverrideException
    extends RuntimeException {
        private static final long serialVersionUID = 7395488467026629355L;

        public OverrideException(String message) {
            super(message);
        }
    }
}

