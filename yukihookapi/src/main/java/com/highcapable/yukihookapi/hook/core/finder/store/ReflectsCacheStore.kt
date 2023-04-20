/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
 * https://github.com/fankes/YukiHookAPI
 *
 * MIT License
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
 * This file is Created by fankes on 2022/3/29.
 */
package com.highcapable.yukihookapi.hook.core.finder.store

import android.util.ArrayMap
import com.highcapable.yukihookapi.YukiHookAPI
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是一个全局静态的 [Class]、[Member] 缓存实例
 *
 * 为防止 [Class]、[Member] 复用过高造成的系统 GC 问题
 *
 * 查找后的 [Class] 自动进入缓存 - 不受任何控制
 *
 * 查找后的 [Member] 在 [YukiHookAPI.Configs.isEnableMemberCache] 启用后自动进入缓存
 */
internal object ReflectsCacheStore {

    /** 缓存的 [Class] 列表 */
    private val dexClassListData = ArrayMap<Int, List<String>>()

    /** 缓存的 [Class] 对象 */
    private val classData = ArrayMap<Int, Class<*>?>()

    /** 缓存的 [Class] 数组 */
    private val classesData = ArrayMap<Int, HashSet<Class<*>>>()

    /** 缓存的 [Method] 数组 */
    private val methodsData = ArrayMap<Int, HashSet<Method>>()

    /** 缓存的 [Constructor] 数组 */
    private val constructorsData = ArrayMap<Int, HashSet<Constructor<*>>>()

    /** 缓存的 [Field] 数组 */
    private val fieldsData = ArrayMap<Int, HashSet<Field>>()

    /**
     * 查找缓存中的 [Class] 列表
     * @param hashCode 标识符
     * @return [List]<[Class]>
     */
    internal fun findDexClassList(hashCode: Int) = dexClassListData[hashCode]

    /**
     * 查找缓存中的 [Class]
     * @param hashCode 标识符
     * @return [Class] or null
     */
    internal fun findClass(hashCode: Int) = classData[hashCode]

    /**
     * 查找缓存中的 [Class] 数组
     * @param hashCode 标识符
     * @return [HashSet]<[Class]> or null
     */
    internal fun findClasses(hashCode: Int) = classesData[hashCode]

    /**
     * 查找缓存中的 [Method] 数组
     * @param hashCode 标识符
     * @return [HashSet]<[Method]>
     */
    internal fun findMethods(hashCode: Int) = methodsData[hashCode]

    /**
     * 查找缓存中的 [Constructor] 数组
     * @param hashCode 标识符
     * @return [HashSet]<[Constructor]>
     */
    internal fun findConstructors(hashCode: Int) = constructorsData[hashCode]

    /**
     * 查找缓存中的 [Field] 数组
     * @param hashCode 标识符
     * @return [HashSet]<[Field]>
     */
    internal fun findFields(hashCode: Int) = fieldsData[hashCode]

    /**
     * 写入 [Class] 列表到缓存
     * @param hashCode 标识符
     * @param instance 实例
     */
    internal fun putDexClassList(hashCode: Int, instance: List<String>) {
        dexClassListData[hashCode] = instance
    }

    /**
     * 写入 [Class] 到缓存
     * @param hashCode 标识符
     * @param instance 实例
     */
    internal fun putClass(hashCode: Int, instance: Class<*>?) {
        classData[hashCode] = instance
    }

    /**
     * 写入 [Class] 数组到缓存
     * @param hashCode 标识符
     * @param instance 实例
     */
    internal fun putClasses(hashCode: Int, instance: HashSet<Class<*>>) {
        classesData[hashCode] = instance
    }

    /**
     * 写入 [Method] 数组到缓存
     * @param hashCode 标识符
     * @param instances 实例数组
     */
    internal fun putMethods(hashCode: Int, instances: HashSet<Method>) {
        if (YukiHookAPI.Configs.isEnableMemberCache.not()) return
        methodsData[hashCode] = instances
    }

    /**
     * 写入 [Constructor] 数组到缓存
     * @param hashCode 标识符
     * @param instances 实例数组
     */
    internal fun putConstructors(hashCode: Int, instances: HashSet<Constructor<*>>) {
        if (YukiHookAPI.Configs.isEnableMemberCache.not()) return
        constructorsData[hashCode] = instances
    }

    /**
     * 写入 [Field] 数组到缓存
     * @param hashCode 标识符
     * @param instances 实例数组
     */
    internal fun putFields(hashCode: Int, instances: HashSet<Field>) {
        if (YukiHookAPI.Configs.isEnableMemberCache.not()) return
        fieldsData[hashCode] = instances
    }
}