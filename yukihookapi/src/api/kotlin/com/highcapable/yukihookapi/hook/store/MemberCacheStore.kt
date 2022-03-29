/*
 * YukiHookAPI - An efficient Kotlin version of the Xposed Hook API.
 * Copyright (C) 2019-2022 HighCapable
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
package com.highcapable.yukihookapi.hook.store

import com.highcapable.yukihookapi.YukiHookAPI
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method

/**
 * 这是一个全局静态的 [Member] 缓存实例
 *
 * 为防止 [Member] 复用过高造成的系统 GC 问题
 *
 * 查询后的 [Member] 在 [YukiHookAPI.Configs.isEnableMemberCache] 启用后自动进入缓存
 */
internal object MemberCacheStore {

    /** 缓存的 [Class] */
    private val classCacheDatas = HashMap<Int, Class<*>?>()

    /** 缓存的 [Method] */
    private val methodCacheDatas = HashMap<Int, Method?>()

    /** 缓存的 [Constructor] */
    private val constructorCacheDatas = HashMap<Int, Constructor<*>?>()

    /** 缓存的 [Field] */
    private val fieldCacheDatas = HashMap<Int, Field?>()

    /**
     * 查找缓存中的 [Class]
     * @param hashCode 标识符
     * @return [Class] or null
     */
    internal fun findClass(hashCode: Int) = classCacheDatas[hashCode]

    /**
     * 查找缓存中的 [Method]
     * @param hashCode 标识符
     * @return [Method] or null
     */
    internal fun findMethod(hashCode: Int) = methodCacheDatas[hashCode]

    /**
     * 查找缓存中的 [Constructor]
     * @param hashCode 标识符
     * @return [Constructor] or null
     */
    internal fun findConstructor(hashCode: Int) = constructorCacheDatas[hashCode]

    /**
     * 查找缓存中的 [Field]
     * @param hashCode 标识符
     * @return [Field] or null
     */
    internal fun findField(hashCode: Int) = fieldCacheDatas[hashCode]

    /**
     * 写入 [Class] 到缓存
     * @param hashCode 标识符
     * @param instance 实例
     */
    internal fun putClass(hashCode: Int, instance: Class<*>?) {
        if (YukiHookAPI.Configs.isEnableMemberCache.not()) return
        classCacheDatas[hashCode] = instance
    }

    /**
     * 写入 [Method] 到缓存
     * @param hashCode 标识符
     * @param instance 实例
     */
    internal fun putMethod(hashCode: Int, instance: Method?) {
        if (YukiHookAPI.Configs.isEnableMemberCache.not()) return
        methodCacheDatas[hashCode] = instance
    }

    /**
     * 写入 [Constructor] 到缓存
     * @param hashCode 标识符
     * @param instance 实例
     */
    internal fun putConstructor(hashCode: Int, instance: Constructor<*>?) {
        if (YukiHookAPI.Configs.isEnableMemberCache.not()) return
        constructorCacheDatas[hashCode] = instance
    }

    /**
     * 写入 [Field] 到缓存
     * @param hashCode 标识符
     * @param instance 实例
     */
    internal fun putField(hashCode: Int, instance: Field?) {
        if (YukiHookAPI.Configs.isEnableMemberCache.not()) return
        fieldCacheDatas[hashCode] = instance
    }
}