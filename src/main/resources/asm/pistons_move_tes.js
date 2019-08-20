function injectForEachMethod(method, targetType, clazz, targetName, sig, callback) {
    var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');

    var target = ASM.findFirstMethodCall(method,
        targetType,
        clazz,
        targetName,
        sig);

    while (target !== null) {
        var index = method.instructions.indexOf(target);
        index += callback(target, index);

        target = ASM.findFirstMethodCallAfter(method,
            targetType,
            clazz,
            targetName,
            sig,
            index);
    }

    return method;
}

function remapBlockState(method) {
    var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');

    return injectForEachMethod(method,
        ASM.MethodType.VIRTUAL,
        "net/minecraft/world/World",
        ASM.mapMethod("setBlockState"),
        "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
        function (target) {
            method.instructions.insert(target, ASM.buildMethodCall(
                "vazkii/quark/base/handler/AsmHooks",
                "setPistonBlock",
                "(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)Z",
                ASM.MethodType.STATIC
            ));
            method.instructions.remove(target);
            return 0;
        });
}

function initializeCoreMod() {
    return {
        'piston-update-hook': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.tileentity.PistonTileEntity',
                'methodName': 'tick',
                'methodDesc': '()V'
            },
            'transformer': remapBlockState
        },
        'clear-piston-data': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.PistonBlock',
                'methodName': 'clearPistonTileEntity',
                'methodDesc': '()V'
            },
            'transformer': remapBlockState
        },
        'piston-render-hook': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.client.renderer.tileentity.PistonTileEntityRenderer',
                'methodName': 'render',
                'methodDesc': '(Lnet/minecraft/tileentity/PistonTileEntity;DDDFI)V'
            },
            'transformer': function(method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
                var JumpInsnNode = Java.type('org.objectweb.asm.tree.JumpInsnNode');
                var InsnNode = Java.type('org.objectweb.asm.tree.InsnNode');
                var LabelNode = Java.type('org.objectweb.asm.tree.LabelNode');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');

                var newInstructions = new InsnList();

                var escape = new LabelNode();

                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new VarInsnNode(Opcodes.DLOAD, 2));
                newInstructions.add(new VarInsnNode(Opcodes.DLOAD, 4));
                newInstructions.add(new VarInsnNode(Opcodes.DLOAD, 6));
                newInstructions.add(new VarInsnNode(Opcodes.FLOAD, 8));
                newInstructions.add(ASM.buildMethodCall(
                    "vazkii/quark/base/handler/AsmHooks",
                    "renderPistonBlock",
                    "(Lnet/minecraft/tileentity/PistonTileEntity;DDDF)Z",
                    ASM.MethodType.STATIC));
                newInstructions.add(new JumpInsnNode(Opcodes.IFEQ, escape));
                newInstructions.add(new InsnNode(Opcodes.RETURN));
                newInstructions.add(escape);

                method.instructions.insertBefore(method.instructions.getFirst(), newInstructions);
                return method;
            }
        },
        'allow-tile-pushing': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.PistonBlock',
                'methodName': 'canPush',
                'methodDesc': '(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;ZLnet/minecraft/util/Direction;)Z'
            },
            'transformer': function(method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var target = ASM.findFirstMethodCall(method,
                    ASM.MethodType.VIRTUAL,
                    "net/minecraft/block/BlockState",
                    ASM.mapMethod("hasTileEntity"),
                    "()Z");

                var newInstructions = new InsnList();

                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 0));
                newInstructions.add(ASM.buildMethodCall(
                    "vazkii/quark/base/handler/AsmHooks",
                    "shouldPistonMoveTE",
                    "(ZLnet/minecraft/block/BlockState;)Z",
                    ASM.MethodType.STATIC));

                method.instructions.insert(target, newInstructions);
                return method;
            }
        },
        'reinstate-tile-entity': {
            'target': {
                'type': 'METHOD',
                'class': 'net.minecraft.block.PistonBlock',
                'methodName': 'doMove',
                'methodDesc': '(Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/Direction;Z)Z'
            },
            'transformer': function(method) {
                var ASM = Java.type('net.minecraftforge.coremod.api.ASMAPI');
                var Opcodes = Java.type('org.objectweb.asm.Opcodes');
                var InsnList = Java.type('org.objectweb.asm.tree.InsnList');
                var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');

                var target = ASM.findFirstMethodCall(method,
                    ASM.MethodType.VIRTUAL,
                    "net/minecraft/block/state/PistonBlockStructureHelper",
                    ASM.mapMethod("getBlocksToMove"),
                    "()Ljava/util/List;");

                var newInstructions = new InsnList();

                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 6));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 1));
                newInstructions.add(new VarInsnNode(Opcodes.ALOAD, 3));
                newInstructions.add(new VarInsnNode(Opcodes.ILOAD, 4));

                newInstructions.add(ASM.buildMethodCall(
                    "vazkii/quark/base/handler/AsmHooks",
                    "postPistonPush",
                    "(Lnet/minecraft/block/state/PistonBlockStructureHelper;Lnet/minecraft/world/World;Lnet/minecraft/util/Direction;Z)V",
                    ASM.MethodType.STATIC));

                method.instructions.insertBefore(target, newInstructions);

                return method;
            }
        }
    }
}
