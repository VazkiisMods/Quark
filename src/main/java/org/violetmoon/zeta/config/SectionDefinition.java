package org.violetmoon.zeta.config;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class SectionDefinition extends Definition {
	public final Map<String, SectionDefinition> subsections = new LinkedHashMap<>();
	public final Map<String, ValueDefinition<?>> values = new LinkedHashMap<>();

	public SectionDefinition(String name, List<String> comment, @Nullable SectionDefinition parent) {
		super(name, comment, parent);
	}

	public SectionDefinition(String name, List<String> comment) {
		this(name, comment, null);
	}

	public SectionDefinition getOrCreateSubsection(String name, List<String> comment) {
		return subsections.computeIfAbsent(name, __ -> new SectionDefinition(name, comment, this));
	}

	public <T> ValueDefinition<T> addValue(String name, List<String> comment, T defaultValue) {
		ValueDefinition<T> val = new ValueDefinition<>(name, comment, defaultValue, this);
		values.put(val.name, val);
		return val;
	}

	public <T> ValueDefinition<T> addValue(String name, List<String> comment, T defaultValue, Predicate<Object> validator) {
		ValueDefinition<T> val = new ValueDefinition<>(name, comment, defaultValue, validator, this);
		values.put(val.name, val);
		return val;
	}

	public @Nullable ValueDefinition<?> getValue(String name) {
		return values.get(name);
	}

	public <T> @Nullable ValueDefinition<T> getValue(String name, Class<T> type) {
		ValueDefinition<?> value = getValue(name);
		return value == null ? null : value.downcast(type);
	}

	//ugh
	@SuppressWarnings("unchecked")
	public <T> @Nullable ValueDefinition<T> getValueErased(String name, Class<?> type) {
		return (ValueDefinition<T>) getValue(name, type);
	}

	public Collection<SectionDefinition> getSubsections() {
		return subsections.values();
	}

	public Collection<ValueDefinition<?>> getValues() {
		return values.values();
	}

	public void trimEmptySubsections() {
		getSubsections().removeIf(sub -> sub.getSubsections().isEmpty() && sub.getValues().isEmpty());
		getSubsections().forEach(SectionDefinition::trimEmptySubsections);
	}

	@Override
	public String toString() {
		return "SectionDefinition{" + name + " (" + subsections.size() + " subsections, " + values.size() + " values)}";
	}
}
