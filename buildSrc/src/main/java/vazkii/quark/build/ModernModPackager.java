package vazkii.quark.build;

import org.apache.commons.io.IOUtils;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.bundling.Jar;
import org.objectweb.asm.*;

import java.io.*;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.jar.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class ModernModPackager {

    public static final String LOAD_MODULE_DESC = "Lvazkii/quark/base/module/LoadModule;";

    private static JarOutputStream newJarOutputStream(File jarOutputDir, String name) throws IOException {
        return new JarOutputStream(new BufferedOutputStream(new FileOutputStream(new File(jarOutputDir, name))));
    }

    public static void runOn(Jar jar) {
        new ModernModPackager().runOnInst(jar);
    }

    private Manifest manifest;

    public void runOnInst(Jar jar) {
        Project project = jar.getProject();
        project.task("buildModernJars", task -> {
            task.dependsOn(jar);
            task.doLast(_task -> {
                String jarNameFmt = Pattern
                        .compile(Pattern.quote(jar.getArchiveBaseName().get()))
                        .matcher(jar.getArchiveFileName().get())
                        .replaceAll("$0-%s");
                File jarOutputDir = new File(jar.getDestinationDirectory().get().getAsFile(), "modern");
                FileCollection inputs = jar.getInputs().getFiles();
                List<QuarkModule> quarkModules = StreamSupport.stream(inputs.spliterator(), false)
                        .map(this::findModuleIn)
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .collect(Collectors.toList());
                Map<String, QuarkModule> moduleMap = quarkModules.stream()
                        .collect(Collectors.toMap(qm -> qm.classFullName + ".class", qm -> qm));
                try {
                    jarOutputDir.mkdirs();
                    try (JarInputStream jis = new JarInputStream(new BufferedInputStream(
                            new FileInputStream(jar.getOutputs().getFiles().getSingleFile())));
                         JarOutputStream jos = newJarOutputStream(jarOutputDir, String.format(jarNameFmt, "Core"))) {
                        JarEntry nextJarEntry;
                        while (true) {
                            nextJarEntry = jis.getNextJarEntry();
                            if (nextJarEntry == null) break;
                            QuarkModule quarkModule = moduleMap.get(nextJarEntry.getName());
                            if (quarkModule == null) {
                                jos.putNextEntry(nextJarEntry);
                                IOUtils.copy(jis, jos);
                            } else {
                                ClassReader classReader = new ClassReader(jis);
                                ClassWriter classWriter = new ClassWriter(classReader, 0);
                                classReader.accept(new ClassVisitor(Opcodes.ASM9, classWriter) {
                                    @Override
                                    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                        AnnotationVisitor av = super.visitAnnotation(descriptor, visible);
                                        if (LOAD_MODULE_DESC.equals(descriptor)) {
                                            AnnotationVisitor aav = av.visitArray("requiredMods");
                                            aav.visit("", quarkModule.getModId());
                                            aav.visitEnd();
                                        }
                                        return av;
                                    }
                                }, 0);
                                byte[] ba = classWriter.toByteArray();
                                nextJarEntry.setSize(ba.length);
                                nextJarEntry.setCompressedSize(-1);
                                jos.putNextEntry(nextJarEntry);
                                jos.write(ba);
                            }
                        }
                        Manifest inputManifest = jis.getManifest();
                        manifest = new Manifest();
                        for (String toCopy : new String[] {
                                "Manifest-Version",
                                "Implementation-Version",
                                "Specification-Vendor",
                                "Implementation-Timestamp",
                                "Specification-Version",
                                "Implementation-Vendor"
                        }) {
                            manifest.getMainAttributes().putValue(toCopy,
                                    inputManifest.getMainAttributes().getValue(toCopy));
                        }
                    }
                    for (QuarkModule module : quarkModules) {
                        try (JarOutputStream jos = newJarOutputStream(jarOutputDir, String.format(jarNameFmt, module.classSimpleName))) {
                            buildModuleJar(jos, module);
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
    }

    private void buildModuleJar(JarOutputStream jos, QuarkModule module) throws IOException {
        jos.putNextEntry(new JarEntry("pack.mcmeta"));
        OutputStreamWriter osw = new OutputStreamWriter(jos);
        osw.write(module.getMcmeta());
        osw.flush();
        jos.putNextEntry(new JarEntry("META-INF/MANIFEST.MF"));
        {
            Attributes attributes = manifest.getMainAttributes();
            attributes.putValue("Implementation-Title", module.getPrettyName());
            manifest.write(jos);
        }
        jos.putNextEntry(new JarEntry("META-INF/mods.toml"));
        osw.write(module.getModsToml());
        osw.flush();
        jos.putNextEntry(new JarEntry(module.getOutputClassName() + ".class"));
        jos.write(module.getClassBytes());
    }

    private static class QuarkModule {
        public String category = null;
        public String classFullName;
        public String classSimpleName;
        public File classFile;

        private String modId;
        private String prettyName;
        private static final Pattern CAMEL_PATTERN = Pattern.compile("[A-Z][a-z]+");

        public String getModId() {
            if (modId == null) {
                modId = "quark" + classSimpleName.toLowerCase(Locale.ROOT);
            }
            return modId;
        }

        public String getPrettyName() {
            if (prettyName == null) {
                StringBuilder sb = new StringBuilder("Quark");
                Matcher matcher = CAMEL_PATTERN.matcher(classSimpleName);
                while (matcher.find()) {
                    sb.append(' ');
                    sb.append(matcher.group());
                }
                prettyName = sb.toString();
            }
            return prettyName;
        }

        public String getModsToml() {
            return "modLoader=\"javafml\"\n" +
                    "loaderVersion=\"[38,)\"\n" +
                    "issueTrackerURL=\"https://github.com/Vazkii/Quark\"\n" +
                    "license=\"https://github.com/Vazkii/Quark/blob/master/LICENSE.md\"\n" +
                    "\n" +
                    "[[mods]]\n" +
                    "modId=\"" + getModId() + "\"\n" +
                    "displayName=\"" + getPrettyName() + "\"\n" +
                    "version=\"1.18\"\n" +
                    "authors=\"Vazkii, WireSegal, MCVinnyq, Sully\"\n" +
                    "description='''Enables the " + getPrettyName() + ".'''\n";
        }

        public String getMcmeta() {
            return "{\n" +
                    "    \"pack\": {\n" +
                    "        \"description\": \"" + getPrettyName() + " Resources\",\n" +
                    "        \"pack_format\": 5,\n" +
                    "        \"_comment\": \"A pack_format of 4 requires json lang files. Note: we require v4 pack meta for all mods.\"\n" +
                    "    }\n" +
                    "}\n";
        }

        public String getOutputClassName() {
            return "vazkii/quark/" + getModId() + "/" + classSimpleName;
        }

        public byte[] getClassBytes() {
            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            classWriter.visit(Opcodes.V1_8, Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER, getOutputClassName(), null, "java/lang/Object", null);
            {
                AnnotationVisitor mod = classWriter.visitAnnotation("Lnet/minecraftforge/fml/common/Mod;", true);
                mod.visit("value", getModId());
                mod.visitEnd();
            }
            {
                MethodVisitor ctor = classWriter.visitMethod(Opcodes.ACC_PUBLIC, "<init>", "()V", null, null);
                ctor.visitVarInsn(Opcodes.ALOAD, 0);
                ctor.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false);
                ctor.visitEnd();
            }
            classWriter.visitEnd();
            return classWriter.toByteArray();
        }

        @Override
        public String toString() {
            return "Module in " + category + " from " + classFile;
        }
    }

    private Optional<QuarkModule> findModuleIn(File file) {
        if (file.getName().endsWith(".class")) {
            try (InputStream is = new BufferedInputStream(new FileInputStream(file))) {
                ClassReader cr = new ClassReader(is);
                boolean[] hasModule = new boolean[]{false};
                QuarkModule module = new QuarkModule();
                module.classFile = file;
                cr.accept(new ClassVisitor(Opcodes.ASM9) {
                              @Override
                              public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                                  module.classFullName = name;
                                  int i = name.lastIndexOf('/');
                                  module.classSimpleName = name.substring(i + 1);
                              }

                              @Override
                              public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
                                  if (LOAD_MODULE_DESC.equals(descriptor)) {
                                      hasModule[0] = true;
                                      return new AnnotationVisitor(Opcodes.ASM9) {
                                          @Override
                                          public void visitEnum(String name, String descriptor, String value) {
                                              if ("category".equals(name)) {
                                                  module.category = value;
                                              }
                                          }
                                      };
                                  }
                                  return null;
                              }
                          },
                        ClassReader.SKIP_CODE |
                                ClassReader.SKIP_DEBUG |
                                ClassReader.SKIP_FRAMES);
                if (hasModule[0]) {
                    return Optional.of(module);
                }
            } catch (IOException e) {
                throw new RuntimeException("Failed to open class file", e);
            }
        }
        return Optional.empty();
    }
}
