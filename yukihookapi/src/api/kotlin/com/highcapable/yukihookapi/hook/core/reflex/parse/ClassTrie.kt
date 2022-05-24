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
 * This file is Created by fankes on 2022/5/24.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.core.reflex.parse

import java.util.concurrent.ConcurrentHashMap

/**
 * 用来储存一个 APK 的 package 结构
 *
 * 出于性能考虑 - 这个类不支持读线程和写线程同时操作 - 但支持同类型的线程同时操作
 *
 * Contributed from [conan](https://github.com/meowool-catnip/conan)
 */
internal class ClassTrie internal constructor() {

    private companion object {

        /**
         * 用来将 JVM 格式的类型标识符转换为类名
         *
         * Example: String 的类型标识符为 "Ljava/lang/String;"
         *
         * [Refer](https://docs.oracle.com/javase/specs/jvms/se7/html/jvms-4.html#jvms-4.3)
         * @return [String]
         */
        private fun convertJVMTypeToClassName(type: String) = type.substring(1, type.length - 1).replace(oldChar = '/', newChar = '.')
    }

    /**
     * 读写开关 - 用于增强线程间的安全性
     *
     * 只有开关设为 true 的时候 - 写操作才会被执行
     *
     * 只有开关设为 false 的时候 - 读操作才会返回有效数据
     */
    @Volatile
    internal var mutable = true

    /**
     * package 结构的根结点
     * @return [TrieNode]
     */
    private val root = TrieNode()

    /**
     * 插入一个单独的 JVM 格式的类型标识符
     * @param type 类型
     */
    internal operator fun plusAssign(type: String) {
        if (mutable) root.add(convertJVMTypeToClassName(type))
    }

    /**
     * 插入一组 JVM 格式的类型标识符
     * @param types 类型数组
     */
    internal operator fun plusAssign(types: Array<String>) = types.forEach { this += it }

    /**
     * 查找指定包里指定深度的所有类
     *
     * 出于性能方面的考虑 - 只有深度相等的类才会被返回 - 比如查找深度为 0 的时候 - 就只返回这个包自己拥有的类 - 不包括它里面其他包拥有的类
     * @param packageName 包名 - 默认为根包名
     * @param depth 深度
     * @return [List] 查找到的类名数组
     */
    internal fun search(packageName: String = "root", depth: Int): List<String> {
        if (mutable) return emptyList()
        if (packageName == "root") return root.classes
        return root.search(packageName, depth)
    }

    /**
     * 私有的节点结构
     */
    inner class TrieNode {

        /** 当前的 Class 实例数组 */
        internal val classes: MutableList<String> = ArrayList(50)

        /** 当前的 Class 子实例数组 */
        private val children: MutableMap<String, TrieNode> = ConcurrentHashMap()

        /**
         * 添加节点
         * @param className 类名
         */
        internal fun add(className: String) = add(className, pos = 0)

        /**
         * 获取节点下的类名数组
         * @param depth 深度
         * @return [List]
         */
        internal fun get(depth: Int = 0): List<String> {
            if (depth == 0) return classes
            return children.flatMap { it.value.get(depth - 1) }
        }

        /**
         * 查找当前包里指定深度的所有类
         * @param packageName 包名 - 默认为根包名
         * @param depth 深度
         * @return [List] 查找到的类名数组
         */
        internal fun search(packageName: String, depth: Int) = search(packageName, depth, pos = 0)

        /**
         * 添加节点
         * @param className 类名
         * @param pos 下标
         */
        private fun add(className: String, pos: Int) {
            val delimiterAt = className.indexOf(char = '.', pos)
            if (delimiterAt == -1) {
                synchronized(this) { classes.add(className) }
                return
            }
            val pkg = className.substring(pos, delimiterAt)
            if (pkg !in children) children[pkg] = TrieNode()
            children[pkg]?.add(className, pos = delimiterAt + 1)
        }

        /**
         * 查找当前包里指定深度的所有类
         * @param packageName 包名 - 默认为根包名
         * @param depth 深度
         * @param pos 下标
         * @return [List] 查找到的类名数组
         */
        private fun search(packageName: String, depth: Int, pos: Int): List<String> {
            val delimiterAt = packageName.indexOf(char = '.', pos)
            if (delimiterAt == -1) {
                val pkg = packageName.substring(pos)
                return children[pkg]?.get(depth) ?: emptyList()
            }
            val pkg = packageName.substring(pos, delimiterAt)
            val next = children[pkg] ?: return emptyList()
            return next.search(packageName, depth, pos = delimiterAt + 1)
        }
    }
}