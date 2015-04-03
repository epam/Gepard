package com.epam.gepard.util;

/*==========================================================================
 Copyright 2004-2015 EPAM Systems

 This file is part of Gepard.

 Gepard is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 Gepard is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with Gepard.  If not, see <http://www.gnu.org/licenses/>.
===========================================================================*/

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.epam.gepard.annotations.TestParameter;
import com.epam.gepard.util.helper.BooleanValuer;
import com.epam.gepard.util.helper.DoubleValuer;
import com.epam.gepard.util.helper.FloatValuer;
import com.epam.gepard.util.helper.IntegerValuer;
import com.epam.gepard.util.helper.LongValuer;
import com.epam.gepard.util.helper.ShortValuer;
import com.epam.gepard.util.helper.Valuer;

/**
 * Utility class for extending reflection utils.
 * @author Lajos_Kesztyus
 */
public final class ReflectionUtilsExtension {

    private static Map<Class<?>, Valuer> valuerMap;
    static {
        valuerMap = new HashMap<>();
        putIntegerValuer(valuerMap);
        putLongValuer(valuerMap);
        putShortValuer(valuerMap);
        putFloatValuer(valuerMap);
        putDoubleValuer(valuerMap);
        putBooleanValuer(valuerMap);
    }

    private ReflectionUtilsExtension() {
    }

    private static void putBooleanValuer(final Map<Class<?>, Valuer> valuerMap) {
        Valuer booleanValuer = new BooleanValuer();
        valuerMap.put(Boolean.class, booleanValuer);
        valuerMap.put(boolean.class, booleanValuer);
    }

    private static void putDoubleValuer(final Map<Class<?>, Valuer> valuerMap) {
        Valuer doubleValuer = new DoubleValuer();
        valuerMap.put(Double.class, doubleValuer);
        valuerMap.put(double.class, doubleValuer);
    }

    private static void putFloatValuer(final Map<Class<?>, Valuer> valuerMap) {
        Valuer floatValuer = new FloatValuer();
        valuerMap.put(Float.class, floatValuer);
        valuerMap.put(float.class, floatValuer);
    }

    private static void putShortValuer(final Map<Class<?>, Valuer> valuerMap) {
        Valuer shortValuer = new ShortValuer();
        valuerMap.put(Short.class, shortValuer);
        valuerMap.put(short.class, shortValuer);
    }

    private static void putLongValuer(final Map<Class<?>, Valuer> valuerMap) {
        Valuer longValuer = new LongValuer();
        valuerMap.put(Long.class, longValuer);
        valuerMap.put(long.class, longValuer);
    }

    private static void putIntegerValuer(final Map<Class<?>, Valuer> valuerMap) {
        Valuer integerValuer = new IntegerValuer();
        valuerMap.put(int.class, integerValuer);
        valuerMap.put(Integer.class, integerValuer);
    }

    /**
     * Returns an object holding the value of the specified String.
     * The object must implement the valueOf method or should be
     * a primitive type.
     * @param clazz the class of the object
     * @param value the value of the object
     * @return the object hoding the data
     * @throws Exception in case no valueOf method found
     */
    public static Object valueOf(final Class<?> clazz, final String value) throws Exception {
        Object object = value;
        Valuer valuer = valuerMap.get(clazz);
        if (valuer != null) {
            object = valuer.valueOf(value);
        } else {
            //Byte, BigInteger, BigDecimal, and the wrapper classes
            //(Integer, Long, Short, Float, Double, Boolean) have valueOf method that can be invoked
            object = tryToInvokeValueOf(clazz, value, object);
        }

        return object;
    }

    private static Object tryToInvokeValueOf(final Class<?> clazz, final String value, final Object object) throws Exception {
        Object result = object;
        try {
            Method m;
            try {
                m = clazz.getDeclaredMethod("valueOf", String.class);
            } catch (NoSuchMethodException e) {
                m = clazz.getDeclaredMethod("valueOf", Object.class);
            }
            if (m != null) {
                result = m.invoke(null, value);
            }
        } catch (Exception e) {
            throw new Exception("Unsupported input param type" + clazz.getName());
        }
        return result;
    }

    /**
     * Returns an array, a set or a list holding the values of the specified String.
     * If it is a collection the contained object must implement the valueOf method or should be
     * a primitive type.
     * @param field the field to get the type from
     * @param value the value to be set
     * @param separator the separator of the values
     * @return the array or collection holding the data
     * @throws Exception in case no valueOf method found
     */
    @SuppressWarnings("unchecked")
    public static Object valueOf(final Field field, final String value, final String separator) throws Exception {
        Object ret = null;
        Collection collection = null;
        Type genericType = null;

        if (Set.class.isAssignableFrom(field.getType())) {
            collection = new HashSet<Object>();
        } else if (List.class.isAssignableFrom(field.getType())) {
            collection = new ArrayList<Object>();
        }

        if (collection != null) {
            //Fill the collection
            genericType = getGenericType(field);
            for (String s : value.split(separator)) {
                collection.add(valueOf((Class) genericType, s));
            }
            ret = collection;
        } else {
            //Fill the array
            String[] contents = value.split(field.getAnnotation(TestParameter.class).separator());
            ret = Array.newInstance(field.getType().getComponentType(), contents.length);

            for (int i = 0; i < contents.length; i++) {
                Array.set(ret, i, contents[i]);
            }
        }

        return ret;
    }

    /**
     * Returns the generic type for a field if exists else null.
     * @param field the field
     * @return the generic type
     */
    private static Type getGenericType(final Field field) {
        Type genericType = null;

        if (field.getGenericType() instanceof ParameterizedType) {
            Type[] fieldArgTypes = ((ParameterizedType) field.getGenericType()).getActualTypeArguments();
            if (fieldArgTypes.length > 0) {
                genericType = fieldArgTypes[0];
            }
        }

        return genericType;
    }
}
