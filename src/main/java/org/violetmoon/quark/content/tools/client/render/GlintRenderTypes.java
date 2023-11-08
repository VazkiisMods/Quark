package org.violetmoon.quark.content.tools.client.render;

import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.violetmoon.quark.base.Quark;
import org.violetmoon.quark.content.tools.module.ColorRunesModule;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@OnlyIn(Dist.CLIENT)
public class GlintRenderTypes extends RenderType {
	private GlintRenderTypes(String name, VertexFormat vf, VertexFormat.Mode mode, int bufSize, boolean affectsCrumbling, boolean sortOnUpload, Runnable setup, Runnable clean) {
		super(name, vf, mode, bufSize, affectsCrumbling, sortOnUpload, setup, clean);
		throw new UnsupportedOperationException("Don't instantiate this");
	}

	public static List<RenderType> glint = newRenderList(GlintRenderTypes::buildGlintRenderType);
	public static List<RenderType> glintTranslucent = newRenderList(GlintRenderTypes::buildGlintTranslucentRenderType);
	public static List<RenderType> entityGlint = newRenderList(GlintRenderTypes::buildEntityGlintRenderType);
	public static List<RenderType> glintDirect = newRenderList(GlintRenderTypes::buildGlintDirectRenderType);
	public static List<RenderType> entityGlintDirect = newRenderList(GlintRenderTypes::buildEntityGlintDriectRenderType);
	public static List<RenderType> armorGlint = newRenderList(GlintRenderTypes::buildArmorGlintRenderType);
	public static List<RenderType> armorEntityGlint = newRenderList(GlintRenderTypes::buildArmorEntityGlintRenderType);

	public static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map) {
		addGlintTypes(map, glint);
		addGlintTypes(map, glintTranslucent);
		addGlintTypes(map, entityGlint);
		addGlintTypes(map, glintDirect);
		addGlintTypes(map, entityGlintDirect);
		addGlintTypes(map, armorGlint);
		addGlintTypes(map, armorEntityGlint);
	}

	private static List<RenderType> newRenderList(Function<String, RenderType> func) {
		ArrayList<RenderType> list = new ArrayList<>(ColorRunesModule.RUNE_TYPES + 1);

		for (DyeColor color : DyeColor.values())
			list.add(func.apply(color.getName()));
		list.add(func.apply("rainbow"));
		list.add(func.apply("blank"));

		return list;
	}

	private static void addGlintTypes(Object2ObjectLinkedOpenHashMap<RenderType, BufferBuilder> map, List<RenderType> typeList) {
		for(RenderType renderType : typeList)
			if (!map.containsKey(renderType))
				map.put(renderType, new BufferBuilder(renderType.bufferSize()));
	}

	private static RenderType buildGlintRenderType(String name) {
		return RenderType.create("glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
			.setShaderState(RenderStateShard.RENDERTYPE_GLINT_SHADER)
			.setTextureState(new TextureStateShard(texture(name), true, false))
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
			.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
			.setTexturingState(RenderStateShard.GLINT_TEXTURING)
			.createCompositeState(false));
	}

	private static RenderType buildGlintTranslucentRenderType(String name) {
		return RenderType.create("glint_translucent_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
			.setShaderState(RenderStateShard.RENDERTYPE_GLINT_TRANSLUCENT_SHADER)
			.setTextureState(new TextureStateShard(texture(name), true, false))
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
			.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
			.setTexturingState(RenderStateShard.GLINT_TEXTURING)
			.createCompositeState(false));
	}

	private static RenderType buildEntityGlintRenderType(String name) {
		return RenderType.create("entity_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
			.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_SHADER)
			.setTextureState(new TextureStateShard(texture(name), true, false))
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
			.setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
			.setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
			.createCompositeState(false));
	}


	private static RenderType buildGlintDirectRenderType(String name) {
		return RenderType.create("glint_direct_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
			.setShaderState(RenderStateShard.RENDERTYPE_GLINT_DIRECT_SHADER)
			.setTextureState(new TextureStateShard(texture(name), true, false))
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
			.setTexturingState(RenderStateShard.GLINT_TEXTURING)
			.createCompositeState(false));
	}


	private static RenderType buildEntityGlintDriectRenderType(String name) {
		return RenderType.create("entity_glint_direct_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
			.setShaderState(RenderStateShard.RENDERTYPE_ENTITY_GLINT_DIRECT_SHADER)
			.setTextureState(new TextureStateShard(texture(name), true, false))
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
			.setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
			.createCompositeState(false));
	}

	private static RenderType buildArmorGlintRenderType(String name) {
		return RenderType.create("armor_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
			.setShaderState(RenderStateShard.RENDERTYPE_ARMOR_GLINT_SHADER)
			.setTextureState(new TextureStateShard(texture(name), true, false))
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
			.setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false));
	}

	private static RenderType buildArmorEntityGlintRenderType(String name) {
		return RenderType.create("armor_entity_glint_" + name, DefaultVertexFormat.POSITION_TEX, VertexFormat.Mode.QUADS, 256, false, false, CompositeState.builder()
			.setShaderState(RenderStateShard.RENDERTYPE_ARMOR_ENTITY_GLINT_SHADER)
			.setTextureState(new TextureStateShard(texture(name), true, false))
			.setWriteMaskState(RenderStateShard.COLOR_WRITE)
			.setCullState(RenderStateShard.NO_CULL)
			.setDepthTestState(RenderStateShard.EQUAL_DEPTH_TEST)
			.setTransparencyState(RenderStateShard.GLINT_TRANSPARENCY)
			.setTexturingState(RenderStateShard.ENTITY_GLINT_TEXTURING)
			.setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
			.createCompositeState(false));
	}

	private static ResourceLocation texture(String name) {
		return new ResourceLocation(Quark.MOD_ID, "textures/glint/enchanted_item_glint_" + name + ".png");
	}
}
