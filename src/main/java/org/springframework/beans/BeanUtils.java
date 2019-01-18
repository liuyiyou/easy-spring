/*
 * Copyright 2002-2004 the original author or authors.
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

package org.springframework.beans;

import org.springframework.util.Assert;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


public abstract class BeanUtils {

    public static Object instantiateClass(Class clazz) throws BeansException {
        try {
            return clazz.newInstance();
        } catch (InstantiationException ex) {
            throw new FatalBeanException("Could not instantiate class [" + clazz.getName() +
                    "]; Is it an interface or an abstract class? Does it have a no-arg constructor?", ex);
        } catch (IllegalAccessException ex) {
            throw new FatalBeanException("Could not instantiate class [" + clazz.getName() +
                    "]; has class definition changed? Is there a public no-arg constructor?", ex);
        }
    }


    public static Object instantiateClass(Constructor constructor, Object[] arguments) throws BeansException {
        try {
            return constructor.newInstance(arguments);
        } catch (IllegalArgumentException ex) {
            throw new FatalBeanException("Illegal arguments when trying to instantiate constructor: " + constructor, ex);
        } catch (InstantiationException ex) {
            throw new FatalBeanException("Could not instantiate class [" + constructor.getDeclaringClass().getName() +
                    "]; is it an interface or an abstract class?", ex);
        } catch (IllegalAccessException ex) {
            throw new FatalBeanException("Could not instantiate class [" + constructor.getDeclaringClass().getName() +
                    "]; has class definition changed? Is there a public constructor?", ex);
        } catch (InvocationTargetException ex) {
            throw new FatalBeanException("Could not instantiate class [" + constructor.getDeclaringClass().getName() +
                    "]; constructor threw exception", ex.getTargetException());
        }
    }


    public static boolean isAssignable(Class type, Object value) {
        return (type.isInstance(value) ||
                (!type.isPrimitive() && value == null) ||
                (type.equals(boolean.class) && value instanceof Boolean) ||
                (type.equals(byte.class) && value instanceof Byte) ||
                (type.equals(char.class) && value instanceof Character) ||
                (type.equals(short.class) && value instanceof Short) ||
                (type.equals(int.class) && value instanceof Integer) ||
                (type.equals(long.class) && value instanceof Long) ||
                (type.equals(float.class) && value instanceof Float) ||
                (type.equals(double.class) && value instanceof Double));
    }


    public static boolean isSimpleProperty(Class clazz) {
        return clazz.isPrimitive() || isPrimitiveArray(clazz) || isPrimitiveWrapperArray(clazz) ||
                clazz.equals(String.class) || clazz.equals(String[].class) ||
                clazz.equals(Class.class) || clazz.equals(Class[].class);
    }


    public static boolean isPrimitiveArray(Class clazz) {
        return boolean[].class.equals(clazz) || byte[].class.equals(clazz) || char[].class.equals(clazz) ||
                short[].class.equals(clazz) || int[].class.equals(clazz) || long[].class.equals(clazz) ||
                float[].class.equals(clazz) || double[].class.equals(clazz);
    }


    public static boolean isPrimitiveWrapperArray(Class clazz) {
        return Boolean[].class.equals(clazz) || Byte[].class.equals(clazz) || Character[].class.equals(clazz) ||
                Short[].class.equals(clazz) || Integer[].class.equals(clazz) || Long[].class.equals(clazz) ||
                Float[].class.equals(clazz) || Double[].class.equals(clazz);
    }


    public static void copyProperties(Object source, Object target)
            throws IllegalArgumentException, BeansException {
        copyProperties(source, target, null);
    }


    public static void copyProperties(Object source, Object target, String[] ignoreProperties)
            throws IllegalArgumentException, BeansException {
        if (source == null || target == null || !source.getClass().isInstance(target)) {
            throw new IllegalArgumentException("Target must an instance of source");
        }
        List ignoreList = (ignoreProperties != null) ? Arrays.asList(ignoreProperties) : null;
        BeanWrapper sourceBw = new BeanWrapperImpl(source);
        BeanWrapper targetBw = new BeanWrapperImpl(target);
        MutablePropertyValues values = new MutablePropertyValues();
        for (int i = 0; i < sourceBw.getPropertyDescriptors().length; i++) {
            PropertyDescriptor sourceDesc = sourceBw.getPropertyDescriptors()[i];
            String name = sourceDesc.getName();
            PropertyDescriptor targetDesc = targetBw.getPropertyDescriptor(name);
            if (targetDesc.getWriteMethod() != null && targetDesc.getReadMethod() != null &&
                    (ignoreProperties == null || (!ignoreList.contains(name)))) {
                values.addPropertyValue(new PropertyValue(name, sourceBw.getPropertyValue(name)));
            }
        }
        targetBw.setPropertyValues(values);
    }


    public static PropertyDescriptor findPropertyForMethod(Method method) throws BeansException {
        Assert.notNull(method, "Method must not be null");
        PropertyDescriptor[] pds = getPropertyDescriptors(method.getDeclaringClass());
        for (int i = 0; i < pds.length; i++) {
            PropertyDescriptor pd = pds[i];
            if (method.equals(pd.getReadMethod()) || method.equals(pd.getWriteMethod())) {
                return pd;
            }
        }
        return null;
    }


    public static PropertyDescriptor[] getPropertyDescriptors(Class clazz) throws BeansException {
        CachedIntrospectionResults cr = CachedIntrospectionResults.forClass(clazz);
        return cr.getBeanInfo().getPropertyDescriptors();
    }
}
