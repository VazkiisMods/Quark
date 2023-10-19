package vazkii.quark.base.module.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.text.WordUtils;
import org.jetbrains.annotations.Nullable;
import vazkii.quark.base.module.QuarkModule;
import vazkii.quark.base.module.config.type.IConfigType;
import vazkii.zeta.module.ZetaModule;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.function.Predicate;
import java.util.function.Supplier;

public final class ConfigObjectSerializer {

	public static void serialize(IConfigBuilder builder, ConfigFlagManager flagManager, List<Runnable> callbacks, Object object) throws ReflectiveOperationException {
		serialize(builder, flagManager, callbacks, object, object);
	}

	public static void serialize(IConfigBuilder builder, ConfigFlagManager flagManager, List<Runnable> callbacks, Object object, Object root) throws ReflectiveOperationException {
		List<Field> fields = recursivelyGetFields(object.getClass());
		for(Field f : fields) {
			Config config = f.getDeclaredAnnotation(Config.class);
			if(config != null)
				pushConfig(builder, flagManager, callbacks, object, root, f, config);
		}
	}

	public static List<Field> recursivelyGetFields(Class<?> clazz) {
		List<Field> list = new LinkedList<>();
		while(clazz != Object.class) {
			Field[] fields = clazz.getDeclaredFields();
			list.addAll(Arrays.asList(fields));

			clazz = clazz.getSuperclass();
		}

		return list;
	}

	private static void pushConfig(IConfigBuilder builder, ConfigFlagManager flagManager, List<Runnable> callbacks, Object object, Object root, Field field, Config config) throws ReflectiveOperationException {
		field.setAccessible(true);

		String name = config.name();
		if(name.isEmpty())
			name = WordUtils.capitalizeFully(field.getName().replaceAll("(?<=.)([A-Z])", " $1"));

		Config.Restriction restriction = field.getDeclaredAnnotation(Config.Restriction.class);
		Config.Min min = field.getDeclaredAnnotation(Config.Min.class);
		Config.Max max = field.getDeclaredAnnotation(Config.Max.class);
		Config.Condition condition = field.getDeclaredAnnotation(Config.Condition.class);
		ZetaModule rootModule = (root instanceof ZetaModule ? (ZetaModule) root : null);

		String nl = "";
		Class<?> type = field.getType();
		if(!config.description().isEmpty()) {
			builder.comment(config.description());
			nl = "\n";
		}

		if (restriction != null) {
			StringBuilder arrStr = new StringBuilder("[");

			String[] values = restriction.value();
			int lineLength = 0;

			arrStr.append('[');
			for (int i = 0; i < values.length; i++) {
				String value = String.valueOf(values[i]);
				arrStr.append(value);
				lineLength += value.length();
				if (i == values.length - 1)
					arrStr.append(']');
				else {
					if (lineLength > 50) {
						arrStr.append(",\n ");
						lineLength = 0;
					} else {
						arrStr.append(", ");
						lineLength += 2;
					}
				}
			}

			builder.comment(nl + "Allowed values: " + arrStr.toString());
		}

		if (min != null || max != null) {
			NumberFormat format = DecimalFormat.getNumberInstance(Locale.ROOT);
			String minPart = min == null ? "(" : ((min.exclusive() ? "(" : "[") + format.format(min.value()));
			String maxPart = max == null ? ")" : (format.format(max.value()) + (max.exclusive() ? ")" : "]"));
			builder.comment(nl + "Allowed values: " + minPart + "," + maxPart);
		}

		boolean isStatic = Modifier.isStatic(field.getModifiers());
		Supplier<Object> supplier = () -> {
			try {
				return isStatic ? field.get(null) : field.get(object);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		};

		Object defaultValue = supplier.get();
		if(type == float.class)
			throw new IllegalArgumentException("Floats can't be used in config, use double instead. Offender: " + field);

		if(defaultValue instanceof IConfigType configType) {
			name = name.toLowerCase(Locale.ROOT).replaceAll(" ", "_");

			builder.push(name, defaultValue);
			serialize(builder, flagManager, callbacks, defaultValue, root);
			//TODO, just needs to be pushed through tonnns of interfaces
			if(rootModule instanceof QuarkModule qm)
				callbacks.add(() -> configType.onReload(qm, flagManager));
			builder.pop();

			return;
		}

		String flag = config.flag();
		boolean useFlag = object instanceof ZetaModule && !flag.isEmpty();

		ForgeConfigSpec.ConfigValue<?> value;
		if (defaultValue instanceof List) {
			Supplier<List<?>> listSupplier = () -> (List<?>) supplier.get();
			value = builder.defineList(name, (List<?>) defaultValue, listSupplier, restrict(restriction, min, max, condition));
		} else
			value = builder.defineObj(name, defaultValue, supplier, restrict(restriction, min, max, condition));

		callbacks.add(() -> {
			try {
				Object setObj = value.get();
				if(isStatic)
					field.set(null, setObj);
				else field.set(object, setObj);

				if(useFlag)
					flagManager.putFlag((ZetaModule) object, flag, (boolean) setObj);
			} catch(IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		});
	}

	private static Predicate<Object> restrict(@Nullable Config.Restriction restriction, @Nullable Config.Min min,
											  @Nullable Config.Max max, @Nullable Config.Condition condition) {
		String[] restrictions = restriction == null ? null : restriction.value();
		double minVal = min == null ? -Double.MAX_VALUE : min.value();
		double maxVal = max == null ? Double.MAX_VALUE : max.value();
		boolean minExclusive = min != null && min.exclusive();
		boolean maxExclusive = max != null && max.exclusive();

		Predicate<Object> pred = (o) -> restrict(o, minVal, minExclusive, maxVal, maxExclusive, restrictions);
		if(condition != null){
			try {
				Constructor<? extends Predicate<Object>> constr = condition.value().getDeclaredConstructor();
				constr.setAccessible(true);
				Predicate<Object> additionalPredicate = constr.newInstance();
				pred = pred.and(additionalPredicate);
			} catch (Exception e) {
				throw new IllegalArgumentException("Failed to parse config Predicate annotation: ", e);
			}
		}
		return pred;
	}

	private static boolean restrict(Object o, double minVal, boolean minExclusive, double maxVal, boolean maxExclusive, String[] restrictions) {
		if (o == null)
			return false;

		if (o instanceof Number num) {
			double val = num.doubleValue();
			if (minExclusive) {
				if (minVal >= val)
					return false;
			} else if (minVal > val)
				return false;

			if (maxExclusive) {
				if (maxVal <= val)
					return false;
			} else if (maxVal < val)
				return false;
		}

		if (o instanceof String && restrictions != null) {
			for (String check : restrictions) {
				if (o.equals(check))
					return true;
			}

			return false;
		}

		return true;
	}
}
