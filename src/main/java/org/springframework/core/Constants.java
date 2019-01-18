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

package org.springframework.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class Constants {

    private final Map map = new HashMap();

    private final Class clazz;

    public Constants(Class clazz) {
        this.clazz = clazz;
        Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field f = fields[i];
            if (Modifier.isFinal(f.getModifiers()) && Modifier.isStatic(f.getModifiers()) &&
                    Modifier.isPublic(f.getModifiers())) {
                String name = f.getName();
                try {
                    Object value = f.get(null);
                    this.map.put(name, value);
                } catch (IllegalAccessException ex) {
                    // just leave this field and continue
                }
            }
        }
    }


    public int getSize() {
        return this.map.size();
    }


    public Number asNumber(String code) throws ConstantException {
        Object o = asObject(code);
        if (!(o instanceof Number))
            throw new ConstantException(this.clazz, code, "not a Number");
        return (Number) o;
    }


    public String asString(String code) throws ConstantException {
        return asObject(code).toString();
    }


    public Object asObject(String code) throws ConstantException {
        code = code.toUpperCase();
        Object val = this.map.get(code);
        if (val == null) {
            throw new ConstantException(this.clazz, code, "not found");
        }
        return val;
    }


    public Set getValues(String namePrefix) {
        namePrefix = namePrefix.toUpperCase();
        Set values = new HashSet();
        for (Iterator it = this.map.keySet().iterator(); it.hasNext(); ) {
            String code = (String) it.next();
            if (code.startsWith(namePrefix)) {
                values.add(this.map.get(code));
            }
        }
        return values;
    }

    public Set getValuesForProperty(String propertyName) {
        return getValues(propertyToConstantNamePrefix(propertyName));
    }

    public String toCode(Object value, String namePrefix) throws ConstantException {
        namePrefix = namePrefix.toUpperCase();
        for (Iterator it = this.map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry entry = (Map.Entry) it.next();
            String key = (String) entry.getKey();
            if (key.startsWith(namePrefix) && entry.getValue().equals(value)) {
                return key;
            }
        }
        throw new ConstantException(this.clazz, namePrefix, value);
    }


    public String toCodeForProperty(Object value, String propertyName) throws ConstantException {
        return toCode(value, propertyToConstantNamePrefix(propertyName));
    }


    public String propertyToConstantNamePrefix(String propertyName) {
        StringBuffer parsedPrefix = new StringBuffer();
        for (int i = 0; i < propertyName.length(); i++) {
            char c = propertyName.charAt(i);
            if (Character.isUpperCase(c)) {
                parsedPrefix.append("_");
                parsedPrefix.append(c);
            } else {
                parsedPrefix.append(Character.toUpperCase(c));
            }
        }
        return parsedPrefix.toString();
    }

}
