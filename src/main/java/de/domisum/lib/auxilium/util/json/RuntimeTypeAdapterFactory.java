/*
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.domisum.lib.auxilium.util.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.internal.Streams;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import de.domisum.lib.auxilium.util.java.annotations.API;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Adapts values whose runtime type may differ from their declaration type. This
 * is necessary when a field's type is not the same type that GSON should create
 * when deserializing that field. For example, consider these types:
 * <pre>   {@code
 *   abstract class Shape {
 *     int x;
 *     int y;
 *   }
 *   class Circle extends Shape {
 *     int radius;
 *   }
 *   class Rectangle extends Shape {
 *     int width;
 *     int height;
 *   }
 *   class Diamond extends Shape {
 *     int width;
 *     int height;
 *   }
 *   class Drawing {
 *     Shape bottomShape;
 *     Shape topShape;
 *   }
 * }</pre>
 * <p>Without additional type information, the serialized JSON is ambiguous. Is
 * the bottom shape in this drawing a rectangle or a diamond? <pre>   {@code
 *   {
 *     "bottomShape": {
 *       "width": 10,
 *       "height": 5,
 *       "x": 0,
 *       "y": 0
 *     },
 *     "topShape": {
 *       "radius": 2,
 *       "x": 4,
 *       "y": 1
 *     }
 *   }}</pre>
 * This class addresses this problem by adding type information to the
 * serialized JSON and honoring that type information when the JSON is
 * deserialized: <pre>   {@code
 *   {
 *     "bottomShape": {
 *       "type": "Diamond",
 *       "width": 10,
 *       "height": 5,
 *       "x": 0,
 *       "y": 0
 *     },
 *     "topShape": {
 *       "type": "Circle",
 *       "radius": 2,
 *       "x": 4,
 *       "y": 1
 *     }
 *   }}</pre>
 * Both the type field name ({@code "type"}) and the type labels ({@code
 * "Rectangle"}) are configurable.
 * <p>
 * <h3>Registering Types</h3>
 * Create a {@code RuntimeTypeAdapterFactory} by passing the base type and type field
 * name to the {@link #of} factory method. If you don't supply an explicit type
 * field name, {@code "type"} will be used. <pre>   {@code
 *   RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory
 *       = RuntimeTypeAdapterFactory.of(Shape.class, "type");
 * }</pre>
 * Next register all of your subtypes. Every subtype must be explicitly
 * registered. This protects your application from injection attacks. If you
 * don't supply an explicit type label, the type's simple name will be used.
 * <pre>   {@code
 *   shapeAdapter.registerSubtype(Rectangle.class, "Rectangle");
 *   shapeAdapter.registerSubtype(Circle.class, "Circle");
 *   shapeAdapter.registerSubtype(Diamond.class, "Diamond");
 * }</pre>
 * Finally, register the type adapter factory in your application's GSON builder:
 * <pre>   {@code
 *   Gson gson = new GsonBuilder()
 *       .registerTypeAdapterFactory(shapeAdapterFactory)
 *       .create();
 * }</pre>
 * Like {@code GsonBuilder}, this API supports chaining: <pre>   {@code
 *   RuntimeTypeAdapterFactory<Shape> shapeAdapterFactory = RuntimeTypeAdapterFactory.of(Shape.class)
 *       .registerSubtype(Rectangle.class)
 *       .registerSubtype(Circle.class)
 *       .registerSubtype(Diamond.class);
 * }</pre>
 */
@API
public final class RuntimeTypeAdapterFactory<T> implements TypeAdapterFactory
{

	private final Class<?> baseType;
	private final String typeFieldName;
	private final Map<String, Class<?>> labelToSubtype = new LinkedHashMap<>();
	private final Map<Class<?>, String> subtypeToLabel = new LinkedHashMap<>();


	// INIT
	private RuntimeTypeAdapterFactory(Class<?> baseType, String typeFieldName)
	{
		if(typeFieldName == null || baseType == null)
			throw new IllegalArgumentException("baseType and typeFieldName can't be null");

		this.baseType = baseType;
		this.typeFieldName = typeFieldName;
	}


	/**
	 * Creates a new runtime type adapter using for {@code baseType} using {@code
	 * typeFieldName} as the type field name. Type field names are case sensitive.
	 */
	@API public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType, String typeFieldName)
	{
		return new RuntimeTypeAdapterFactory<>(baseType, typeFieldName);
	}

	/**
	 * Creates a new runtime type adapter for {@code baseType} using {@code "type"} as
	 * the type field name.
	 */
	@API public static <T> RuntimeTypeAdapterFactory<T> of(Class<T> baseType)
	{
		return new RuntimeTypeAdapterFactory<>(baseType, "type");
	}

	/**
	 * Registers {@code type} identified by {@code label}. Labels are case
	 * sensitive.
	 *
	 * @throws IllegalArgumentException if either {@code type} or {@code label}
	 *                                  have already been registered on this type adapter.
	 */
	@API public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type, String label)
	{
		if(type == null || label == null)
			throw new IllegalArgumentException("type and label can't be null");

		if(this.subtypeToLabel.containsKey(type) || this.labelToSubtype.containsKey(label))
			throw new IllegalArgumentException("types and labels must be unique");

		this.labelToSubtype.put(label, type);
		this.subtypeToLabel.put(type, label);
		return this;
	}

	/**
	 * Registers {@code type} identified by its {@link Class#getSimpleName simple
	 * name}. Labels are case sensitive.
	 *
	 * @throws IllegalArgumentException if either {@code type} or its simple name
	 *                                  have already been registered on this type adapter.
	 */
	@API public RuntimeTypeAdapterFactory<T> registerSubtype(Class<? extends T> type)
	{
		return registerSubtype(type, type.getSimpleName());
	}

	@Override public <R> TypeAdapter<R> create(Gson gson, TypeToken<R> type)
	{
		if(type.getRawType() != this.baseType)
			return null;

		final Map<String, TypeAdapter<?>> labelToDelegate = new LinkedHashMap<>();
		final Map<Class<?>, TypeAdapter<?>> subtypeToDelegate = new LinkedHashMap<>();
		for(Map.Entry<String, Class<?>> entry : this.labelToSubtype.entrySet())
		{
			TypeAdapter<?> delegate = gson.getDelegateAdapter(this, TypeToken.get(entry.getValue()));
			labelToDelegate.put(entry.getKey(), delegate);
			subtypeToDelegate.put(entry.getValue(), delegate);
		}

		return new TypeAdapter<R>()
		{
			@Override public R read(JsonReader in)
			{
				JsonElement jsonElement = Streams.parse(in);
				JsonElement labelJsonElement = jsonElement.getAsJsonObject().remove(RuntimeTypeAdapterFactory.this.typeFieldName);
				if(labelJsonElement == null)
					throw new JsonParseException("cannot deserialize "+RuntimeTypeAdapterFactory.this.baseType
							+" because it does not define a field named "+RuntimeTypeAdapterFactory.this.typeFieldName);

				String label = labelJsonElement.getAsString();
				// registration requires that subtype extends T
				@SuppressWarnings("unchecked") TypeAdapter<R> delegate = (TypeAdapter<R>) labelToDelegate.get(label);
				if(delegate == null)
					throw new JsonParseException(
							"cannot deserialize "+RuntimeTypeAdapterFactory.this.baseType+" subtype named "+label
									+"; did you forget to register a subtype?");

				return delegate.fromJsonTree(jsonElement);
			}

			@Override public void write(JsonWriter out, R value) throws IOException
			{
				Class<?> srcType = value.getClass();
				String label = RuntimeTypeAdapterFactory.this.subtypeToLabel.get(srcType);

				// registration requires that subtype extends T
				@SuppressWarnings("unchecked") TypeAdapter<R> delegate = (TypeAdapter<R>) subtypeToDelegate.get(srcType);
				if(delegate == null)
					throw new JsonParseException("cannot serialize "+srcType.getName()+"; did you forget to register a subtype?");

				JsonObject jsonObject = delegate.toJsonTree(value).getAsJsonObject();
				if(jsonObject.has(RuntimeTypeAdapterFactory.this.typeFieldName))
					throw new JsonParseException(
							"cannot serialize "+srcType.getName()+" because it already defines a field named "
									+RuntimeTypeAdapterFactory.this.typeFieldName);

				JsonObject clone = new JsonObject();
				clone.add(RuntimeTypeAdapterFactory.this.typeFieldName, new JsonPrimitive(label));
				for(Map.Entry<String, JsonElement> e : jsonObject.entrySet())
					clone.add(e.getKey(), e.getValue());

				Streams.write(clone, out);
			}

		}.nullSafe();
	}

}
