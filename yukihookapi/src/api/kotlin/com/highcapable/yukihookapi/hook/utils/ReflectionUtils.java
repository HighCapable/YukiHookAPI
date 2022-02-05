/*
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * This file is Created by zpp0196 on 2019/1/24 0024.
 * This file is Modified by fankes on 2022/2/2 2240.
 */
package com.highcapable.yukihookapi.hook.utils;

import android.text.TextUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;

import de.robv.android.xposed.XposedHelpers;

@SuppressWarnings("ALL")
public class ReflectionUtils {

    private static final HashMap<String, Field> fieldCache = new HashMap<>();
    private static final HashMap<String, Method> methodCache = new HashMap<>();

    private static String getParametersString(Class<?>... clazzes) {
        StringBuilder sb = new StringBuilder("(");
        boolean first = true;
        for (Class<?> clazz : clazzes) {
            if (first)
                first = false;
            else
                sb.append(",");

            if (clazz != null)
                sb.append(clazz.getCanonicalName());
            else
                sb.append("null");
        }
        sb.append(")");
        return sb.toString();
    }

    @Deprecated
    public static void setStaticObjectField(Class<?> clazz, Class<?> fieldType, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        findFieldIfExists(clazz, fieldType, fieldName).set(null, value);
    }

    @Deprecated
    public static void setObjectField(Object obj, Class<?> fieldType, String fieldName, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        if (obj != null) {
            Field field = findFieldIfExists(obj.getClass(), fieldType, fieldName);
            if (field != null) {
                field.set(obj, value);
            }
        }
    }

    public static Field findFieldIfExists(Class<?> clazz, Class<?> fieldType, String fieldName)
            throws NoSuchFieldException {
        return findFieldIfExists(clazz, fieldType.getName(), fieldName);
    }

    public static boolean isCallingFrom(String className) {
        StackTraceElement[] stackTraceElements = Thread.currentThread()
                .getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            if (element.getClassName()
                    .contains(className)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isCallingFromEither(String... classname) {
        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
        for (StackTraceElement element : stackTraceElements) {
            for (String name : classname) {
                if (element.toString().contains(name)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static Field findFieldIfExists(Class<?> clazz, String typeName, String fieldName) throws NoSuchFieldException {
        String fullFieldName = "name:[" + fieldName + "] type:[" + typeName + "] in Class [" + clazz.getName() + "] by YukiHook#finder";
        if (!fieldCache.containsKey(fullFieldName)) {
            if (clazz != null && !TextUtils.isEmpty(typeName) && !TextUtils.isEmpty(fieldName)) {
                Class<?> clz = clazz;
                do {
                    for (Field field : clz.getDeclaredFields()) {
                        if (field.getType()
                                .getName()
                                .equals(typeName) && field.getName()
                                .equals(fieldName)) {
                            field.setAccessible(true);
                            fieldCache.put(fullFieldName, field);
                            return field;
                        }
                    }
                } while ((clz = clz.getSuperclass()) != null);
                throw new NoSuchFieldException("Can't find this field --> " + fullFieldName);
            }
            return null;
        } else {
            Field field = fieldCache.get(fullFieldName);
            if (field == null)
                throw new NoSuchFieldError(fullFieldName);
            return field;
        }
    }

    /**
     * 适用于查找混淆类型的 abcd 方法 - 无 param
     *
     * @param clazz      方法所在类
     * @param returnType 返回类型
     * @param methodName 方法名
     */
    public static Method findMethodNoParam(Class<?> clazz, Class<?> returnType, String methodName) {
        String fullMethodName = "name:[" + methodName + "] in Class [" + clazz.getName() + "] by YukiHook#finder";
        if (!methodCache.containsKey(fullMethodName)) {
            Method method = findMethodIfExists(clazz, returnType, methodName);
            methodCache.put(fullMethodName, method);
            return method;
        } else {
            return methodCache.get(fullMethodName);
        }
    }

    /**
     * 不区分 param 整个类搜索 - 适用于混淆方法 abcd
     *
     * @param clazz          方法所在类
     * @param returnType     返回类型
     * @param methodName     方法名
     * @param parameterTypes 方法参数类型数组
     */
    public static Method findMethodBestMatch(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) {
        String fullMethodName = "name:[" + methodName + "] paramType:[" + getParametersString(parameterTypes) + "] in Class [" + clazz.getName() + "] by YukiHook#finder";
        if (!methodCache.containsKey(fullMethodName)) {
            Method method = findMethodIfExists(clazz, returnType, methodName, parameterTypes);
            methodCache.put(fullMethodName, method);
            return method;
        } else {
            return methodCache.get(fullMethodName);
        }
    }

    /**
     * 查找构造方法
     *
     * @param clazz          构造类所在类
     * @param parameterTypes 构造类方法参数类型数组
     */
    public static Constructor<?> findConstructorExact(Class<?> clazz, Class<?>... parameterTypes) {
        String fullConstructorName = "paramType:[" + getParametersString(parameterTypes) + "in Class [" + clazz.getName() + "] by YukiHook#finder";
        try {
            Constructor<?> constructor = clazz.getDeclaredConstructor(parameterTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("Can't find this constructor --> " + fullConstructorName);
        }
    }

    private static Method findMethodExact(Class<?> clazz, String methodName, Class<?>... parameterTypes) {
        String fullMethodName = "name:[" + methodName + "] paramType:[" + getParametersString(parameterTypes) + "] in Class [" + clazz.getName() + "] by YukiHook#finder";
        try {
            Method method = clazz.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method;
        } catch (NoSuchMethodException e) {
            throw new NoSuchMethodError("Can't find this method --> " + fullMethodName);
        }
    }

    private static Method findMethodIfExists(Class<?> clazz, Class<?> returnType, String methodName, Class<?>... parameterTypes) {
        long l = System.currentTimeMillis();
        if (clazz != null && !TextUtils.isEmpty(methodName)) {
            Class<?> clz = clazz;
            if (returnType == null) return findMethodExact(clazz, methodName, parameterTypes);
            do {
                Method[] methods = XposedHelpers.findMethodsByExactParameters(clazz, returnType, parameterTypes);
                for (Method method : methods) if (method.getName().equals(methodName)) return method;
            } while ((clz = clz.getSuperclass()) != null);
        }
        throw new IllegalArgumentException("Can't find this method --> name:[" + methodName + "] returnType:[" + returnType.getName() + "] paramType:[" + getParametersString(parameterTypes) + "] in Class [" + clazz.getName() + "] by YukiHook#finder");
    }
}
