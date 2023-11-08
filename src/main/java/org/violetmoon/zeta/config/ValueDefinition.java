package org.violetmoon.zeta.config;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

//TODO: maybe we need "boolean equals(T thing1, T thing2)"
public class ValueDefinition<T> extends Definition {
	public final T defaultValue;
	public final @Nullable Predicate<Object> validator;

	public ValueDefinition(String name, List<String> comment, T defaultValue, @Nullable Predicate<Object> validator, @Nullable SectionDefinition parent) {
		super(name, comment, parent);
		this.defaultValue = defaultValue;
		this.validator = validator;
	}

	public ValueDefinition(String name, List<String> comment, T defaultValue, Predicate<Object> validator) {
		this(name, comment, defaultValue, validator, null);
	}

	public ValueDefinition(String name, List<String> comment, T defaultValue, SectionDefinition parent) {
		this(name, comment, defaultValue, null, parent);
	}

	public ValueDefinition(String name, List<String> comment, T defaultValue) {
		this(name, comment, defaultValue, null, null);
	}

	public boolean isOfType(Class<?> clazz) {
		//TODO: have "type" as a field, so that defaultValue can be null
		return clazz.isAssignableFrom(defaultValue.getClass());
	}

	@SuppressWarnings("unchecked")
	public <X> @Nullable ValueDefinition<X> downcast(Class<X> newType) {
		if(isOfType(newType))
			return (ValueDefinition<X>) this;
		else
			return null;
	}

	public boolean validate(Object underTest) {
		//you HAVE to start with these two conditions lest forge's config api explode into a million pieces
		if(underTest == null)
			return false;

		//TODO: forge's defineList passes each *element* to the validator predicate, not the list itself :/
		// so i need an exemption from isSubtype for lists
		boolean isList = List.class.isAssignableFrom(defaultValue.getClass());
		boolean isSubtype = defaultValue.getClass().isAssignableFrom(underTest.getClass());
		if(!isList && !isSubtype)
			return false;

		if(validator == null)
			return true;
		else
			return validator.test(underTest);
	}

	@Override
	public String toString() {
		return "ValueDefinition{" + name + "}";
	}
}
