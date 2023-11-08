package org.violetmoon.zeta.client.config.definition;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.annotation.Nonnull;

import org.violetmoon.quark.base.config.type.inputtable.ConvulsionMatrixConfig;
import org.violetmoon.zeta.client.ZetaClient;
import org.violetmoon.zeta.client.config.screen.AbstractSectionInputScreen;
import org.violetmoon.zeta.client.config.widget.PencilButton;
import org.violetmoon.zeta.config.ChangeSet;
import org.violetmoon.zeta.config.SectionDefinition;
import org.violetmoon.zeta.config.ValueDefinition;

import com.google.common.base.Preconditions;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.gui.widget.ForgeSlider;

public class ConvulsionMatrixClientDefinition implements ClientDefinitionExt<SectionDefinition> {
	public ConvulsionMatrixClientDefinition(ConvulsionMatrixConfig cfg, SectionDefinition def) {
		this.params = cfg.params;

		r = def.getValueErased("R", List.class);
		g = def.getValueErased("G", List.class);
		b = def.getValueErased("B", List.class);

		Preconditions.checkNotNull(r, "need an 'R' value in this section");
		Preconditions.checkNotNull(g, "need an 'G' value in this section");
		Preconditions.checkNotNull(b, "need an 'B' value in this section");
	}

	private final ConvulsionMatrixConfig.Params params;

	private final ValueDefinition<List<Double>> r;
	private final ValueDefinition<List<Double>> g;
	private final ValueDefinition<List<Double>> b;

	@Override
	public String getSubtitle(ChangeSet changes, SectionDefinition def) {
		List<Double> r_ = changes.getExactSizeCopy(r, 3, 0d);
		List<Double> g_ = changes.getExactSizeCopy(g, 3, 0d);
		List<Double> b_ = changes.getExactSizeCopy(b, 3, 0d);

		return Stream.of(r_, g_, b_)
			.flatMap(List::stream)
			.map(d -> String.format("%.1f", d))
			.collect(Collectors.joining(", ", "[", "]"));
	}

	@Override
	public void addWidgets(ZetaClient zc, Screen parent, ChangeSet changes, SectionDefinition def, Consumer<AbstractWidget> widgets) {
		Screen newScreen = new ConvulsionMatrixInputScreen(zc, parent, changes, def);
		widgets.accept(new PencilButton(zc, 230, 3, b1 -> Minecraft.getInstance().setScreen(newScreen)));
	}

	class ConvulsionMatrixInputScreen extends AbstractSectionInputScreen {
		ForgeSlider[] sliders = new ForgeSlider[9];

		public ConvulsionMatrixInputScreen(ZetaClient zc, Screen parent, ChangeSet changes, SectionDefinition def) {
			super(zc, parent, changes, def);
		}

		protected List<Double> with(ValueDefinition<List<Double>> def, int index, double value) {
			List<Double> copy = changes.getExactSizeCopy(def, 3, 0d);
			copy.set(index, value);
			return copy;
		}

		@SuppressWarnings({"PointlessArithmeticExpression", "ConstantValue"}) //table :(
		@Override
		protected void init() {
			super.init();

			int w = 70;
			int p = 12;
			int x = width / 2 - 33;
			int y = 55;

			sliders[0] = addRenderableWidget(makeSliderPlease(x + w * 0, y + 25 * 0, w - p, 20, r, 0));
			sliders[1] = addRenderableWidget(makeSliderPlease(x + w * 1, y + 25 * 0, w - p, 20, r, 1));
			sliders[2] = addRenderableWidget(makeSliderPlease(x + w * 2, y + 25 * 0, w - p, 20, r, 2));
			sliders[3] = addRenderableWidget(makeSliderPlease(x + w * 0, y + 25 * 1, w - p, 20, g, 0));
			sliders[4] = addRenderableWidget(makeSliderPlease(x + w * 1, y + 25 * 1, w - p, 20, g, 1));
			sliders[5] = addRenderableWidget(makeSliderPlease(x + w * 2, y + 25 * 1, w - p, 20, g, 2));
			sliders[6] = addRenderableWidget(makeSliderPlease(x + w * 0, y + 25 * 2, w - p, 20, b, 0));
			sliders[7] = addRenderableWidget(makeSliderPlease(x + w * 1, y + 25 * 2, w - p, 20, b, 1));
			sliders[8] = addRenderableWidget(makeSliderPlease(x + w * 2, y + 25 * 2, w - p, 20, b, 2));

			int i = 0;
			for(Map.Entry<String, double[]> entry : params.presetMap.entrySet()) {
				String name = entry.getKey();
				double[] preset = entry.getValue();
				addRenderableWidget(new Button(x + (w * i), y + 115, w - p, 20, Component.literal(name), __ -> setFromArray(preset)));
				i++;
			}

			forceUpdateWidgets();
		}

		@Override
		protected void forceUpdateWidgets() {
			setFromList(Stream.of(changes.getExactSizeCopy(r, 3, 0d), changes.getExactSizeCopy(g, 3, 0d), changes.getExactSizeCopy(b, 3, 0d))
				.flatMap(List<Double>::stream)
				.toList());
		}

		private static final Component EMPTY = Component.empty();

		private ForgeSlider makeSliderPlease(int x, int y, int width, int height, ValueDefinition<List<Double>> binding, int bindingIndex) {
			return new ForgeSlider(x, y, width, height, EMPTY, EMPTY, 0d, 2d, 0d, 0, 1, false) {
				@Override
				public void setValue(double value) {
					super.setValue(value);
					changes.set(binding, with(binding, bindingIndex, value));
				}

				@Override
				protected void applyValue() {
					setValue(snap(this));
				}

				@Override
				public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
					super.render(mstack, mouseX, mouseY, partialTicks);

					String displayVal = String.format("%.2f", getValue());
					int valueColor = changes.isDirty(binding) ? ChatFormatting.GOLD.getColor() : 0xFFFFFF;
					font.drawShadow(mstack, displayVal, x + (float) (getWidth() / 2 - font.width(displayVal) / 2), y + 6, valueColor);
				}
			};
		}

		@Override
		public void tick() {
			updateButtonStatus(true); //always valid
		}

		@Override
		public void render(@Nonnull PoseStack mstack, int mouseX, int mouseY, float partialTicks) {
			renderBackground(mstack);

			super.render(mstack, mouseX, mouseY, partialTicks);

			int x = width / 2 - 203;
			int y = 10;
			int size = 60;

			int titleLeft = width / 2 + 66;
			drawCenteredString(mstack, font, Component.literal(params.name).withStyle(ChatFormatting.BOLD), titleLeft, 20, 0xFFFFFF);
			drawCenteredString(mstack, font, Component.literal("Presets"), titleLeft, 155, 0xFFFFFF);

			//copied from the original
			int sliders = 0;
			for(Widget w : renderables) {
				if(w instanceof ForgeSlider s) {
					switch (sliders) {
						case 0 -> {
							font.drawShadow(mstack, "R =", s.x - 20, s.y + 5, 0xFF0000);
							font.drawShadow(mstack, "R", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0xFF0000);
						}
						case 1 -> font.drawShadow(mstack, "G", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0x00FF00);
						case 2 -> font.drawShadow(mstack, "B", s.x + (float) (s.getWidth() / 2 - 2), s.y - 12, 0x0077FF);
						case 3 -> font.drawShadow(mstack, "G =", s.x - 20, s.y + 5, 0x00FF00);
						case 6 -> font.drawShadow(mstack, "B =", s.x - 20, s.y + 5, 0x0077FF);
					}
					if((sliders % 3) != 0)
						font.drawShadow(mstack, "+", s.x - 9, s.y + 5, 0xFFFFFF);

					sliders++;
				}
			}

			//color preview
			String[] biomes = params.biomeNames;
			int[] colors = params.testColors;
			int[] folliageColors =  params.folliageTestColors;
			boolean renderFolliage = params.shouldDisplayFolliage();
			double[] matrix = getToDoubleArray();

			for(int i = 0; i < biomes.length; i++) {
				String name = biomes[i];
				int color = colors[i];

				int convolved = ConvulsionMatrixConfig.convolve(matrix, color);

				int folliage, convolvedFolliage = 0;
				if(renderFolliage) {
					folliage = folliageColors[i];
					convolvedFolliage = ConvulsionMatrixConfig.convolve(matrix, folliage);
				}

				int cx = x + (i % 2) * (size + 5);
				int cy = y + (i / 2) * (size + 5);

				fill(mstack, cx - 1, cy - 1, cx + size + 1, cy + size + 1, 0xFF000000);
				fill(mstack, cx, cy, cx + size, cy + size, convolved);
				fill(mstack, cx + size / 2 - 1, cy + size / 2 - 1, cx + size, cy + size, 0x22000000);

				if(renderFolliage)
					fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage);

				font.draw(mstack, name, cx + 2, cy + 2, 0x55000000);

				if(renderFolliage) {
					minecraft.getItemRenderer().renderGuiItem(new ItemStack(Items.OAK_SAPLING), cx + size - 18, cy + size - 16);
					mstack.pushPose();
					mstack.translate(0, 0, 999);
					fill(mstack, cx + size / 2, cy + size / 2, cx + size, cy + size, convolvedFolliage & 0x55FFFFFF);
					mstack.popPose();
				}
			}
		}

		protected void setFromArray(double[] values) {
			for(int i = 0; i < 9; i++) {
				sliders[i].setValue(values[i]);
			}
		}

		protected void setFromList(List<Double> values) {
			for(int i = 0; i < 9; i++) {
				sliders[i].setValue(values.get(i));
			}
		}

		protected double[] getToDoubleArray() {
			double[] values = new double[9];
			for(int i = 0; i < 9; i++) {
				values[i] = sliders[i].getValue();
			}
			return values;
		}

		private double snap(ForgeSlider s) {
			double val = s.getValue();
			val = snap(val, 0.5, s);
			val = snap(val, 1.0, s);
			val = snap(val, 1.5, s);
			return val;
		}

		private double snap(double val, double correct, ForgeSlider s) {
			if(Math.abs(val - correct) < 0.02) {
				s.setValue(correct);
				return correct;
			}
			return val;
		}
	}
}
