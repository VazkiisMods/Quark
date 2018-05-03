/**
 * This class was created by <Vazkii>. It's distributed as
 * part of the Quark Mod. Get the Source Code in github:
 * https://github.com/Vazkii/Quark
 *
 * Quark is Open Source and distributed under the
 * CC-BY-NC-SA 3.0 License: https://creativecommons.org/licenses/by-nc-sa/3.0/deed.en_GB
 *
 * File Created @ [26/03/2016, 21:31:04 (GMT)]
 */
package vazkii.quark.base.asm;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.apache.commons.lang3.tuple.Pair;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.IntInsnNode;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.TypeInsnNode;
import org.objectweb.asm.tree.VarInsnNode;
import org.objectweb.asm.util.Printer;
import org.objectweb.asm.util.Textifier;
import org.objectweb.asm.util.TraceMethodVisitor;

import net.minecraft.launchwrapper.IClassTransformer;

public class ClassTransformer implements IClassTransformer {

	private static final String ASM_HOOKS = "vazkii/quark/base/asm/ASMHooks";

	private static final Map<String, Transformer> transformers = new HashMap<>();

	static {
		// For Emotes
		transformers.put("net.minecraft.client.model.ModelBiped", ClassTransformer::transformModelBiped);

		// For Color Runes
		transformers.put("net.minecraft.client.renderer.RenderItem", ClassTransformer::transformRenderItem);
		transformers.put("net.minecraft.client.renderer.entity.layers.LayerArmorBase", ClassTransformer::transformLayerArmorBase);

		// For Boat Sails
		transformers.put("net.minecraft.client.renderer.entity.RenderBoat", ClassTransformer::transformRenderBoat);
		transformers.put("net.minecraft.entity.item.EntityBoat", ClassTransformer::transformEntityBoat);

		// For Piston Block Breakers and Pistons Move TEs
		transformers.put("net.minecraft.block.BlockPistonBase", ClassTransformer::transformBlockPistonBase);

		// For Better Craft Shifting
		transformers.put("net.minecraft.inventory.ContainerWorkbench", ClassTransformer::transformContainerWorkbench);

		// For Pistons Move TEs
		transformers.put("net.minecraft.tileentity.TileEntityPiston", ClassTransformer::transformTileEntityPiston);
		transformers.put("net.minecraft.client.renderer.tileentity.TileEntityPistonRenderer", ClassTransformer::transformTileEntityPistonRenderer);

		// For Imrpoved Sleeping
		transformers.put("net.minecraft.world.WorldServer", ClassTransformer::transformWorldServer);

		// For Colored Lights
		transformers.put("net.minecraft.client.renderer.BlockModelRenderer", ClassTransformer::transformBlockModelRenderer);

	}

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if(transformers.containsKey(transformedName))
			return transformers.get(transformedName).apply(basicClass);

		return basicClass;
	}

	private static byte[] transformModelBiped(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming ModelBiped");
		MethodSignature sig = new MethodSignature("setRotationAngles", "func_78087_a", "(FFFFFFLnet/minecraft/entity/Entity;)V");

		return transform(basicClass, Pair.of(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.RETURN;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 7));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "updateEmotes", "(Lnet/minecraft/entity/Entity;)V"));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformRenderItem(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming RenderItem");
		MethodSignature sig1 = new MethodSignature("renderItem", "func_180454_a", "(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");
		MethodSignature sig2 = new MethodSignature("renderEffect", "func_191966_a", "(Lnet/minecraft/client/renderer/block/model/IBakedModel;)V");

		byte[] transClass = basicClass;

		transClass = transform(transClass, Pair.of(sig1, combine(
				(AbstractInsnNode node) -> { // Filter
					return true;
				}, (MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "setColorRuneTargetStack", "(Lnet/minecraft/item/ItemStack;)V"));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));

		transClass = transform(transClass, Pair.of(sig2, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.LDC && ((LdcInsnNode) node).cst.equals(-8372020);
				}, (MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "getRuneColor", "(I)I"));

					method.instructions.insert(node, newInstructions);
					return false;
				})));

		return transClass;
	}

	static int invokestaticCount = 0;
	private static byte[] transformLayerArmorBase(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming LayerArmorBase");
		MethodSignature sig1 = new MethodSignature("renderArmorLayer", "func_188361_a", "(Lnet/minecraft/entity/EntityLivingBase;FFFFFFFLnet/minecraft/inventory/EntityEquipmentSlot;)V");
		MethodSignature sig2 = new MethodSignature("renderEnchantedGlint", "func_188364_a", "(Lnet/minecraft/client/renderer/entity/RenderLivingBase;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/model/ModelBase;FFFFFFF)V");

		byte[] transClass = basicClass;

		transClass = transform(transClass, Pair.of(sig1, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.ASTORE;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 10));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "setColorRuneTargetStack", "(Lnet/minecraft/item/ItemStack;)V"));

					method.instructions.insert(node, newInstructions);
					return true;
				})));

		if(!hasOptifine(sig2.toString())) {
			invokestaticCount = 0;
			transClass = transform(transClass, Pair.of(sig2, combine(
					(AbstractInsnNode node) -> { // Filter
						return node.getOpcode() == Opcodes.INVOKESTATIC && ((MethodInsnNode) node).desc.equals("(FFFF)V");
					},
					(MethodNode method, AbstractInsnNode node) -> { // Action
						invokestaticCount++;

						InsnList newInstructions = new InsnList();

						newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "applyRuneColor", "()V"));

						method.instructions.insert(node, newInstructions);
						return invokestaticCount == 2;
					})));
		}

		return transClass;
	}

	private static byte[] transformEntityBoat(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming EntityBoat");
		MethodSignature sig1 = new MethodSignature("attackEntityFrom", "func_70097_a", "(Lnet/minecraft/util/DamageSource;F)Z");
		MethodSignature sig2 = new MethodSignature("onUpdate", "func_70071_h_", "()V");

		byte[] transClass = transform(basicClass, Pair.of(sig1, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.POP;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "dropBoatBanner", "(Lnet/minecraft/entity/item/EntityBoat;)V"));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));

		transClass = transform(transClass, Pair.of(sig2, combine(
				(AbstractInsnNode node) -> { // Filter
					return true;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "onBoatUpdate", "(Lnet/minecraft/entity/item/EntityBoat;)V"));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));

		return transClass;
	}

	private static byte[] transformRenderBoat(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming RenderBoat");
		MethodSignature sig = new MethodSignature("doRender", "func_188300_b", "(Lnet/minecraft/entity/item/EntityBoat;DDDFF)V");

		return transform(basicClass, Pair.of(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return (node.getOpcode() == Opcodes.INVOKEVIRTUAL || node.getOpcode() == Opcodes.INVOKEINTERFACE)
							&& checkDesc(((MethodInsnNode) node).desc, "(Lnet/minecraft/entity/Entity;FFFFFF)V");
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					newInstructions.add(new VarInsnNode(Opcodes.FLOAD, 9));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "renderBannerOnBoat", "(Lnet/minecraft/entity/item/EntityBoat;F)V"));

					method.instructions.insert(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformBlockPistonBase(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming BlockPistonBase");
		MethodSignature sig1 = new MethodSignature("doMove", "func_176319_a", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Z)Z");
		MethodSignature sig2 = new MethodSignature("canPush", "func_185646_a", "(Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;ZLnet/minecraft/util/EnumFacing;)Z");


		byte[] transClass = transform(basicClass, Pair.of(sig1, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.ASTORE && ((VarInsnNode) node).var == 11;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 8));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 11));
					newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "breakStuffWithSpikes", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Ljava/util/List;Ljava/util/List;Lnet/minecraft/util/EnumFacing;Z)Z"));

					// recalculate the list and array sizes
					LabelNode label = new LabelNode();
					newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I"));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 8));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKEINTERFACE, "java/util/List", "size", "()I"));
					newInstructions.add(new InsnNode(Opcodes.IADD));
					newInstructions.add(new VarInsnNode(Opcodes.ISTORE, 9));
					newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 9));

					AbstractInsnNode newNode = node.getPrevious();
					while(true) {
						if(newNode.getOpcode() == Opcodes.ANEWARRAY) {
							newInstructions.add(new TypeInsnNode(Opcodes.ANEWARRAY, ((TypeInsnNode) newNode).desc));
							break;
						}
						newNode = newNode.getPrevious();
					}

					newInstructions.add(new VarInsnNode(Opcodes.ASTORE, 10));
					newInstructions.add(label);

					method.instructions.insert(node, newInstructions);
					return true;
				})));

		transClass = transform(transClass, Pair.of(sig2, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.INVOKEVIRTUAL && ((MethodInsnNode) node).name.equals("hasTileEntity");
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "shouldPistonMoveTE", "(ZLnet/minecraft/block/state/IBlockState;)Z"));

					method.instructions.insert(node, newInstructions);
					return true;
				})));

		return transClass;
	}

	static int bipushCount = 0;
	private static byte[] transformContainerWorkbench(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming ContainerWorkbench");
		MethodSignature sig = new MethodSignature("transferStackInSlot", "func_82846_b", "(Lnet/minecraft/entity/player/EntityPlayer;I)Lnet/minecraft/item/ItemStack;");

		bipushCount = 0;
		return transform(basicClass, Pair.of(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.BIPUSH;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();
					bipushCount++;
					if(bipushCount != 5 && bipushCount != 6)
						return false;

					LoadingPlugin.LOGGER.debug("Adding invokestatic to " + ((IntInsnNode) node).operand + "/" + bipushCount);
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "getInventoryBoundary", "(I)I"));

					method.instructions.insert(node, newInstructions);
					return bipushCount == 6;
				})));
	}

	private static byte[] transformTileEntityPiston(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming TileEntityPiston");
		MethodSignature sig1 = new MethodSignature("clearPistonTileEntity", "func_145866_f", "()V");
		MethodSignature sig2 = new MethodSignature("update", "func_73660_a", "()V");

		MethodAction action = combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.INVOKEVIRTUAL && checkDesc(((MethodInsnNode) node).desc, "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z");
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "setPistonBlock", "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;I)Z"));

					method.instructions.insert(node, newInstructions);
					method.instructions.remove(node);

					return true;
				});

		byte[] transClass = transform(basicClass, Pair.of(sig1, action));
		return transform(transClass, Pair.of(sig2, action));
	}

	private static byte[] transformTileEntityPistonRenderer(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming TileEntityPistonRenderer");
		MethodSignature sig = new MethodSignature("renderStateModel", "func_188186_a", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/world/World;Z)Z");

		return transform(basicClass, Pair.of(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return true;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					for(int i = 1; i <= 4; i++)
						newInstructions.add(new VarInsnNode(Opcodes.ALOAD, i));
					newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 5));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "renderPistonBlock", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/world/World;Z)Z"));
					newInstructions.add(new InsnNode(Opcodes.IRETURN));

					method.instructions = newInstructions;
					return true;
				})));
	}

	private static byte[] transformWorldServer(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming WorldServer");
		MethodSignature sig = new MethodSignature("areAllPlayersAsleep", "func_73056_e", "()Z");

		return transform(basicClass, Pair.of(sig, combine(
				(AbstractInsnNode node) -> { // Filter
					return true;
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "isEveryoneAsleep", "(Lnet/minecraft/world/World;)I"));
					newInstructions.add(new InsnNode(Opcodes.DUP));
					LabelNode label = new LabelNode();
					newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, label));
					newInstructions.add(new InsnNode(Opcodes.ICONST_1));
					newInstructions.add(new InsnNode(Opcodes.ISUB));
					newInstructions.add(new InsnNode(Opcodes.IRETURN));
					newInstructions.add(label);

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));
	}

	private static byte[] transformBlockModelRenderer(byte[] basicClass) {
		LoadingPlugin.LOGGER.info("Transforming BlockModelRenderer");
		MethodSignature sig1 = new MethodSignature("renderQuadsFlat", "func_187496_a", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;IZLnet/minecraft/client/renderer/BufferBuilder;Ljava/util/List;Ljava/util/BitSet;)V");

		if(hasOptifine(sig1.toString()))
			return basicClass;

		return transform(basicClass, Pair.of(sig1, combine(
				(AbstractInsnNode node) -> { // Filter
					return node.getOpcode() == Opcodes.INVOKEVIRTUAL && checkDesc(((MethodInsnNode) node).desc, "(DDD)V");
				},
				(MethodNode method, AbstractInsnNode node) -> { // Action
					InsnList newInstructions = new InsnList();

					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 2));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
					newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 18));
					newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 4));
					newInstructions.add(new MethodInsnNode(Opcodes.INVOKESTATIC, ASM_HOOKS, "putColorsFlat", "(Lnet/minecraft/world/IBlockAccess;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/client/renderer/BufferBuilder;Lnet/minecraft/client/renderer/block/model/BakedQuad;I)V"));

					method.instructions.insertBefore(node, newInstructions);
					return true;
				})));
	}

	// BOILERPLATE BELOW ==========================================================================================================================================

	private static byte[] transform(byte[] basicClass, Pair<MethodSignature, MethodAction>... methods) {
		ClassReader reader = new ClassReader(basicClass);
		ClassNode node = new ClassNode();
		reader.accept(node, 0);

		boolean didAnything = false;

		for(Pair<MethodSignature, MethodAction> pair : methods) {
			LoadingPlugin.LOGGER.info("Applying Transformation to method (" + pair.getLeft() + ")");
			didAnything |= findMethodAndTransform(node, pair.getLeft(), pair.getRight());
		}

		if(didAnything) {
			ClassWriter writer = new ClassWriterPatch(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
			node.accept(writer);
			return writer.toByteArray();
		}

		return basicClass;
	}

	public static boolean findMethodAndTransform(ClassNode node, MethodSignature sig, MethodAction pred) {
		String funcName = sig.funcName;
		if(LoadingPlugin.runtimeDeobfEnabled)
			funcName = sig.srgName;

		for(MethodNode method : node.methods) {
			if((method.name.equals(funcName) || method.name.equals(sig.srgName)) && (method.desc.equals(sig.funcDesc))) {
				LoadingPlugin.LOGGER.debug("Located Method, patching...");

				boolean finish = pred.test(method);
				LoadingPlugin.LOGGER.info("Patch result: " + finish);

				return finish;
			}
		}

		LoadingPlugin.LOGGER.error("Failed to locate the method!");
		return false;
	}

	public static MethodAction combine(NodeFilter filter, NodeAction action) {
		return (MethodNode mnode) -> applyOnNode(mnode, filter, action);
	}

	public static boolean applyOnNode(MethodNode method, NodeFilter filter, NodeAction action) {
		Iterator<AbstractInsnNode> iterator = method.instructions.iterator();

		boolean didAny = false;
		while(iterator.hasNext()) {
			AbstractInsnNode anode = iterator.next();
			if(filter.test(anode)) {
				LoadingPlugin.LOGGER.debug("Located patch target node " + getNodeString(anode));
				didAny = true;
				if(action.test(method, anode))
					break;
			}
		}

		return didAny;
	}

	private static void prettyPrint(AbstractInsnNode node) {
		LoadingPlugin.LOGGER.info(getNodeString(node));
	}

	private static String getNodeString(AbstractInsnNode node) {
		Printer printer = new Textifier();

		TraceMethodVisitor visitor = new TraceMethodVisitor(printer);
		node.accept(visitor);

		StringWriter sw = new StringWriter();
		printer.print(new PrintWriter(sw));
		printer.getText().clear();

		return sw.toString().replaceAll("\n", "").trim();
	}

	private static boolean checkDesc(String desc, String expected) {
		return desc.equals(expected);
	}

	private static boolean hasOptifine(String msg) {
		try {
			if(Class.forName("optifine.OptiFineTweaker") != null) {
				LoadingPlugin.LOGGER.info("Optifine Detected. Disabling Patch for " + msg);
				return true;
			}
		} catch (ClassNotFoundException e) { }
		return false;
	}

	private static class MethodSignature {
		String funcName, srgName, funcDesc;

		public MethodSignature(String funcName, String srgName, String funcDesc) {
			this.funcName = funcName;
			this.srgName = srgName;
			this.funcDesc = funcDesc;
		}

		@Override
		public String toString() {
			return "Names [" + funcName + ", " + srgName +"] Descriptor " + funcDesc;
		}

	}

	// Basic interface aliases to not have to clutter up the code with generics over and over again
	private static interface Transformer extends Function<byte[], byte[]> { }
	private static interface MethodAction extends Predicate<MethodNode> { }
	private static interface NodeFilter extends Predicate<AbstractInsnNode> { }
	private static interface NodeAction extends BiPredicate<MethodNode, AbstractInsnNode> { }

}