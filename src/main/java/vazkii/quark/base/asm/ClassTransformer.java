/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * [ADD-LICENSE-HERE]
 *
 * File Created @ [26/03/2016, 21:31:04 (GMT)]
 */
package vazkii.quark.base.asm;

import java.util.Iterator;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;

public class ClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(transformedName.equals("net.minecraft.client.model.ModelBiped") || transformedName.equals("micdoodle8.mods.galacticraft.core.client.model.ModelPlayerGC")) {
			log("Starting on " + transformedName);
			String funcName = "setRotationAngles";
			String obfName = "a";
			String funcDesc = "(FFFFFFLnet/minecraft/entity/Entity;)V";
			String obfDesc = "(FFFFFFLrr;)V";

			if(LoadingPlugin.runtimeDeobfEnabled)
				funcName = "func_78087_a";
			log("Method is " + funcName + " or " + obfName + " for obf.");
			log("Descriptor is " + funcDesc + " or " + obfDesc + " dor obf.");

			ClassReader reader = new ClassReader(basicClass);
			ClassNode node = new ClassNode();
			reader.accept(node, 0);

			for(MethodNode method : node.methods)
				if((method.name.equals(funcName)|| method.name.equals(obfName)) && (method.desc.equals(funcDesc) || method.desc.equals(obfDesc))) {
					log("Found method: " + method.name + " " + method.desc);
					Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

					while(iterator.hasNext()) {
						AbstractInsnNode anode = iterator.next();
						if(anode.getOpcode() == Opcodes.RETURN) {
							log("RETURN Opcode found");
							InsnList newInstructions = new InsnList();

							newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 7));
							newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, "vazkii/quark/vanity/client/emotes/base/EmoteHandler", "updateEmotes", "(Lnet/minecraft/entity/Entity;)V"));

							method.instructions.insertBefore(anode, newInstructions);
							log("Patched!");
						}
					}

					ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
					node.accept(writer);
					return writer.toByteArray();
				}
		}

		return basicClass;
	}

	private static void log(String str) {
		FMLLog.info("[Quark Emotes ASM] %s", str);
	}
}