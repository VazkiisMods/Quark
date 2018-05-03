package vazkii.quark.base.asm;

import net.minecraft.launchwrapper.Launch;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class ClassWriterPatch extends ClassWriter {
    public ClassWriterPatch(int i) {
        super(i);
    }

    public ClassWriterPatch(ClassReader classReader, int i) {
        super(classReader, i);
    }

    /**
     * Basically a copy of super that tries to use the LaunchClassLoader if the SystemClassLoader fails
     * Fixes some random crashes when other mods try to transform the same file, or when COMPUTE_FRAMES decides to be stupid
     */
    @Override
    protected String getCommonSuperClass(final String type1, final String type2) {
        Class<?> c, d;
        ClassLoader classLoader = getClass().getClassLoader();
        try {
            String type1Name = type1.replace('/', '.');
            String type2Name = type2.replace('/', '.');
            try {
                c = Class.forName(type1Name, false, classLoader);
            } catch (ClassNotFoundException e1) {
                c = Class.forName(type1Name, false, Launch.classLoader);
            }
            try {
                d = Class.forName(type2Name, false, classLoader);
            } catch (ClassNotFoundException e1) {
                d = Class.forName(type2Name, false, Launch.classLoader);
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e.toString());
        }
        if (c.isAssignableFrom(d)) {
            return type1;
        }
        if (d.isAssignableFrom(c)) {
            return type2;
        }
        if (c.isInterface() || d.isInterface()) {
            return "java/lang/Object";
        } else {
            do {
                c = c.getSuperclass();
            } while (!c.isAssignableFrom(d));
            return c.getName().replace('.', '/');
        }
    }
}
