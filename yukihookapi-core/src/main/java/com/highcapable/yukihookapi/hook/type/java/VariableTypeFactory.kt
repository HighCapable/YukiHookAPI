/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019 HighCapable
 * https://github.com/HighCapable/YukiHookAPI
 *
 * Apache License Version 2.0
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * This file is created by fankes on 2022/2/2.
 */
@file:Suppress(
    "DEPRECATION", "FunctionName", "KDocUnresolvedReference", "UNCHECKED_CAST", "ktlint:standard:no-wildcard-imports", "unused",
    "DeprecatedCallableAddReplaceWith"
)

package com.highcapable.yukihookapi.hook.type.java

import android.os.Build
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.factory.classOf
import com.highcapable.yukihookapi.hook.factory.toClass
import com.highcapable.yukihookapi.hook.factory.toClassOrNull
import dalvik.system.BaseDexClassLoader
import dalvik.system.DexClassLoader
import dalvik.system.InMemoryDexClassLoader
import dalvik.system.PathClassLoader
import org.json.JSONArray
import org.json.JSONObject
import java.io.*
import java.lang.ref.Reference
import java.lang.ref.WeakReference
import java.lang.reflect.Constructor
import java.lang.reflect.Field
import java.lang.reflect.Member
import java.lang.reflect.Method
import java.net.HttpCookie
import java.net.HttpURLConnection
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream
import java.lang.reflect.Array as JavaArray
import java.util.function.Function as JavaFunction

/**
 * 获得任意类型的数组
 *
 * 它在 Java 中表示为：([type])[]
 * @param type 类型
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun ArrayClass(type: Class<*>) = JavaArray.newInstance(type, 0).javaClass as Class<JavaArray>

/**
 * 获得 [Any] 类型
 *
 * - 此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在迁移到 [AnyClass]
 * @return [Class]<[Any]>
 */
@Deprecated(message = "请使用新的命名方法", ReplaceWith("AnyClass"))
val AnyType get() = AnyClass

/**
 * 获得 [Boolean] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "boolean"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanType get() = Boolean::class.javaPrimitiveType ?: "boolean".toClass()

/**
 * 获得 [Char] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "char"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharType get() = Char::class.javaPrimitiveType ?: "char".toClass()

/**
 * 获得 [Byte] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "byte"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteType get() = Byte::class.javaPrimitiveType ?: "byte".toClass()

/**
 * 获得 [Short] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "short"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortType get() = Short::class.javaPrimitiveType ?: "short".toClass()

/**
 * 获得 [Int] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "int"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntType get() = Int::class.javaPrimitiveType ?: "int".toClass()

/**
 * 获得 [Float] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "float"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatType get() = Float::class.javaPrimitiveType ?: "float".toClass()

/**
 * 获得 [Long] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "long"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongType get() = Long::class.javaPrimitiveType ?: "long".toClass()

/**
 * 获得 [Double] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "double"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleType get() = Double::class.javaPrimitiveType ?: "double".toClass()

/**
 * 获得 [Unit] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) - 它在字节码中的关键字为 "void"
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val UnitType get() = Void.TYPE ?: "void".toClass()

/**
 * 获得 [Any] 类型
 *
 * 它等价于 Java 中的 [java.lang.Object]
 * @return [Class]<[Any]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnyClass get() = classOf<Any>()

/**
 * 获得 [Boolean] 类型
 *
 * 它等价于 Java 中的 [java.lang.Boolean]
 * @return [Class]<[Boolean]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanClass get() = classOf<Boolean>()

/**
 * 获得 [Char] 类型
 *
 * 它等价于 Java 中的 [java.lang.Character]
 * @return [Class]<[Char]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharClass get() = classOf<Char>()

/**
 * 获得 [Byte] 类型
 *
 * 它等价于 Java 中的 [java.lang.Byte]
 * @return [Class]<[Byte]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteClass get() = classOf<Byte>()

/**
 * 获得 [Short] 类型
 *
 * 它等价于 Java 中的 [java.lang.Short]
 * @return [Class]<[Short]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortClass get() = classOf<Short>()

/**
 * 获得 [Int] 类型
 *
 * 它等价于 Java 中的 [java.lang.Integer]
 * @return [Class]<[Int]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntClass get() = classOf<Int>()

/**
 * 获得 [Float] 类型
 *
 * 它等价于 Java 中的 [java.lang.Float]
 * @return [Class]<[Float]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatClass get() = classOf<Float>()

/**
 * 获得 [Long] 类型
 *
 * 它等价于 Java 中的 [java.lang.Long]
 * @return [Class]<[Long]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongClass get() = classOf<Long>()

/**
 * 获得 [Double] 类型
 *
 * 它等价于 Java 中的 [java.lang.Double]
 * @return [Class]<[Double]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleClass get() = classOf<Double>()

/**
 * 获得 [Number] 类型
 *
 * 它等价于 Java 中的 [java.lang.Number]
 * @return [Class]<[Number]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NumberClass get() = classOf<Number>()

/**
 * 获得 [Unit] 类型
 *
 * 它等价于 Java 中的 [java.lang.Void]
 * @return [Class]<[Void]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val UnitClass get() = classOf<Void>()

/**
 * 获得 [String] 类型
 *
 * - 此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在迁移到 [StringClass]
 * @return [Class]<[String]>
 */
@Deprecated(message = "请使用新的命名方法", ReplaceWith("StringClass"))
val StringType get() = StringClass

/**
 * 获得 [CharSequence] 类型
 *
 * - 此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在迁移到 [CharSequenceClass]
 * @return [Class]<[CharSequence]>
 */
@Deprecated(message = "请使用新的命名方法", ReplaceWith("CharSequenceClass"))
val CharSequenceType get() = CharSequenceClass

/**
 * 获得 [String] 类型
 *
 * 它等价于 Java 中的 [java.lang.String]
 * @return [Class]<[String]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringClass get() = classOf<String>()

/**
 * 获得 [CharSequence] 类型
 *
 * 它等价于 Java 中的 [java.lang.CharSequence]
 * @return [Class]<[CharSequence]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharSequenceClass get() = classOf<CharSequence>()

/**
 * 获得 [Serializable] 类型
 * @return [Class]<[Serializable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SerializableClass get() = classOf<Serializable>()

/**
 * 获得 [Array] 类型
 *
 * 它等价于 Java 中的 [java.lang.reflect.Array]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArrayClass get() = classOf<JavaArray>()

/**
 * 获得 [Boolean] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "boolean[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanArrayType get() = ArrayClass(BooleanType)

/**
 * 获得 [Char] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "char[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharArrayType get() = ArrayClass(CharType)

/**
 * 获得 [Byte] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "byte[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteArrayType get() = ArrayClass(ByteType)

/**
 * 获得 [Short] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "short[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortArrayType get() = ArrayClass(ShortType)

/**
 * 获得 [Short] - [Array] 类型
 *
 * - 此方法已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在迁移到 [ShortArrayType]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(message = "请使用修复后的命名方法", ReplaceWith("ShortArrayType"))
val ShortArraytType get() = ShortArrayType

/**
 * 获得 [Int] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "int[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntArrayType get() = ArrayClass(IntType)

/**
 * 获得 [Float] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "float[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatArrayType get() = ArrayClass(FloatType)

/**
 * 获得 [Long] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "long[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongArrayType get() = ArrayClass(LongType)

/**
 * 获得 [Double] - [Array] 类型
 *
 * 这是 Java 原始类型 (Primitive Type) 数组 - 它在字节码中的关键字为 "double[]"
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleArrayType get() = ArrayClass(DoubleType)

/**
 * 获得 [Any] - [Array] 类型
 *
 * 它在 Java 中表示为：Object[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnyArrayClass get() = ArrayClass(AnyClass)

/**
 * 获得 [Boolean] - [Array] 类型
 *
 * 它在 Java 中表示为：Boolean[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanArrayClass get() = ArrayClass(BooleanClass)

/**
 * 获得 [Char] - [Array] 类型
 *
 * 它在 Java 中表示为：Character[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharArrayClass get() = ArrayClass(CharClass)

/**
 * 获得 [Byte] - [Array] 类型
 *
 * 它在 Java 中表示为：Byte[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteArrayClass get() = ArrayClass(ByteClass)

/**
 * 获得 [Short] - [Array] 类型
 *
 * 它在 Java 中表示为：Short[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortArrayClass get() = ArrayClass(ShortClass)

/**
 * 获得 [Int] - [Array] 类型
 *
 * 它在 Java 中表示为：Integer[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntArrayClass get() = ArrayClass(IntClass)

/**
 * 获得 [Float] - [Array] 类型
 *
 * 它在 Java 中表示为：Float[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatArrayClass get() = ArrayClass(FloatClass)

/**
 * 获得 [Long] - [Array] 类型
 *
 * 它在 Java 中表示为：Long[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongArrayClass get() = ArrayClass(LongClass)

/**
 * 获得 [Double] - [Array] 类型
 *
 * 它在 Java 中表示为：Double[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleArrayClass get() = ArrayClass(DoubleClass)

/**
 * 获得 [Number] - [Array] 类型
 *
 * 它在 Java 中表示为：Number[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NumberArrayClass get() = ArrayClass(NumberClass)

/**
 * 获得 [String] - [Array] 类型
 *
 * 它在 Java 中表示为：String[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringArrayClass get() = ArrayClass(StringClass)

/**
 * 获得 [CharSequence] - [Array] 类型
 *
 * 它在 Java 中表示为：CharSequence[]
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharSequenceArrayClass get() = ArrayClass(CharSequenceClass)

/**
 * 获得 [Cloneable] 类型
 * @return [Class]<[Cloneable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CloneableClass get() = classOf<Cloneable>()

/**
 * 获得 [List] 类型
 * @return [Class]<[List]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ListClass get() = classOf<List<*>>()

/**
 * 获得 [ArrayList] 类型
 * @return [Class]<[ArrayList]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArrayListClass get() = classOf<ArrayList<*>>()

/**
 * 获得 [HashMap] 类型
 * @return [Class]<[HashMap]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HashMapClass get() = classOf<HashMap<*, *>>()

/**
 * 获得 [HashSet] 类型
 * @return [Class]<[HashSet]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HashSetClass get() = classOf<HashSet<*>>()

/**
 * 获得 [WeakHashMap] 类型
 * @return [Class]<[WeakHashMap]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WeakHashMapClass get() = classOf<WeakHashMap<*, *>>()

/**
 * 获得 [WeakReference] 类型
 * @return [Class]<[WeakReference]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WeakReferenceClass get() = classOf<WeakReference<*>>()

/**
 * 获得 [Enum] 类型
 * @return [Class]<[Enum]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val EnumClass get() = classOf<Enum<*>>()

/**
 * 获得 [Map] 类型
 * @return [Class]<[Map]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MapClass get() = classOf<Map<*, *>>()

/**
 * 获得 [Map.Entry] 类型
 * @return [Class]<[Map.Entry]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Map_EntryClass get() = classOf<Map.Entry<*, *>>()

/**
 * 获得 [Reference] 类型
 * @return [Class]<[Reference]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ReferenceClass get() = classOf<Reference<*>>()

/**
 * 获得 [Vector] 类型
 * @return [Class]<[Vector]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VectorClass get() = classOf<Vector<*>>()

/**
 * 获得 [File] 类型
 * @return [Class]<[File]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FileClass get() = classOf<File>()

/**
 * 获得 [InputStream] 类型
 * @return [Class]<[InputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val InputStreamClass get() = classOf<InputStream>()

/**
 * 获得 [OutputStream] 类型
 * @return [Class]<[OutputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OutputStreamClass get() = classOf<OutputStream>()

/**
 * 获得 [BufferedReader] 类型
 * @return [Class]<[BufferedReader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BufferedReaderClass get() = classOf<BufferedReader>()

/**
 * 获得 [Date] 类型
 * @return [Class]<[Date]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DateClass get() = classOf<Date>()

/**
 * 获得 [TimeZone] 类型
 * @return [Class]<[TimeZone]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TimeZoneClass get() = classOf<TimeZone>()

/**
 * 获得 [SimpleDateFormat] 类型
 * @return [Class]<[SimpleDateFormat]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SimpleDateFormatClass_Java get() = classOf<SimpleDateFormat>()

/**
 * 获得 [Timer] 类型
 * @return [Class]<[Timer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TimerClass get() = classOf<Timer>()

/**
 * 获得 [TimerTask] 类型
 * @return [Class]<[TimerTask]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TimerTaskClass get() = classOf<TimerTask>()

/**
 * 获得 [Thread] 类型
 * @return [Class]<[Thread]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ThreadClass get() = classOf<Thread>()

/**
 * 获得 [Base64] 类型
 *
 * - 在 Android O (26) 及以上系统加入
 * @return [Class]<[Base64]> or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Base64Class_Java get() = if (Build.VERSION.SDK_INT >= 26) classOf<Base64>() else null

/**
 * 获得 [Observer] 类型
 * @return [Class]<[Observer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ObserverClass get() = classOf<Observer>()

/**
 * 获得 [Set] 类型
 * @return [Class]<[Set]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SetClass get() = classOf<Set<*>>()

/**
 * 获得 [JSONObject] 类型
 * @return [Class]<[JSONObject]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JSONObjectClass get() = classOf<JSONObject>()

/**
 * 获得 [JSONArray] 类型
 * @return [Class]<[JSONArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JSONArrayClass get() = classOf<JSONArray>()

/**
 * 获得 [StringBuilder] 类型
 * @return [Class]<[StringBuilder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringBuilderClass get() = classOf<StringBuilder>()

/**
 * 获得 [StringBuffer] 类型
 * @return [Class]<[StringBuffer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringBufferClass get() = classOf<StringBuffer>()

/**
 * 获得 [ZipEntry] 类型
 * @return [Class]<[ZipEntry]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipEntryClass get() = classOf<ZipEntry>()

/**
 * 获得 [ZipFile] 类型
 * @return [Class]<[ZipFile]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipFileClass get() = classOf<ZipFile>()

/**
 * 获得 [ZipInputStream] 类型
 * @return [Class]<[ZipInputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipInputStreamClass get() = classOf<ZipInputStream>()

/**
 * 获得 [ZipOutputStream] 类型
 * @return [Class]<[ZipOutputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipOutputStreamClass get() = classOf<ZipOutputStream>()

/**
 * 获得 [HttpURLConnection] 类型
 * @return [Class]<[HttpURLConnection]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HttpURLConnectionClass get() = classOf<HttpURLConnection>()

/**
 * 获得 [HttpCookie] 类型
 * @return [Class]<[HttpCookie]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HttpCookieClass get() = classOf<HttpCookie>()

/**
 * 获得 [HttpClient] 类型
 * @return [Class] or null
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HttpClientClass get() = "java.net.http.HttpClient".toClassOrNull()

/**
 * 获得 [AtomicBoolean] 类型
 * @return [Class]<[AtomicBoolean]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AtomicBooleanClass get() = classOf<AtomicBoolean>()

/**
 * 获得 [Supplier] 类型
 * @return [Class]<[Supplier]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SupplierClass get() = classOf<Supplier<*>>()

/**
 * 获得 [Class] 类型
 * @return [Class]<[Class]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaClass get() = classOf<Class<*>>()

/**
 * 获得 [ClassLoader] 类型
 * @return [Class]<[ClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaClassLoader get() = classOf<ClassLoader>()

/**
 * 获得 [BaseDexClassLoader] 类型
 * @return [Class]<[BaseDexClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikBaseDexClassLoader get() = classOf<BaseDexClassLoader>()

/**
 * 获得 [DexClassLoader] 类型
 * @return [Class]<[DexClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikDexClassLoader get() = classOf<DexClassLoader>()

/**
 * 获得 [PathClassLoader] 类型
 * @return [Class]<[PathClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikPathClassLoader get() = classOf<PathClassLoader>()

/**
 * 获得 [InMemoryDexClassLoader] 类型
 * @return [Class]<[InMemoryDexClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikInMemoryDexClassLoader get() = classOf<InMemoryDexClassLoader>()

/**
 * 获得 [Method] 类型
 * @return [Class]<[Method]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaMethodClass get() = classOf<Method>()

/**
 * 获得 [Field] 类型
 * @return [Class]<[Field]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaFieldClass get() = classOf<Field>()

/**
 * 获得 [Constructor] 类型
 * @return [Class]<[Constructor]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaConstructorClass get() = classOf<Constructor<*>>()

/**
 * 获得 [Member] 类型
 * @return [Class]<[Member]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaMemberClass get() = classOf<Member>()

/**
 * 获得 [Annotation] 类型
 * @return [Class]<[Annotation]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaAnnotationClass get() = classOf<Annotation>()

/**
 * 获得 [java.util.function.Function] 类型
 * @return [Class]<[JavaFunction]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FunctionClass get() = classOf<JavaFunction<*, *>>()

/**
 * 获得 [Optional] 类型
 * @return [Class]<[Optional]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalClass get() = classOf<Optional<*>>()

/**
 * 获得 [OptionalInt] 类型
 * @return [Class]<[OptionalInt]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalIntClass get() = classOf<OptionalInt>()

/**
 * 获得 [OptionalLong] 类型
 * @return [Class]<[OptionalLong]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalLongClass get() = classOf<OptionalLong>()

/**
 * 获得 [OptionalDouble] 类型
 * @return [Class]<[OptionalDouble]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalDoubleClass get() = classOf<OptionalDouble>()

/**
 * 获得 [Objects] 类型
 * @return [Class]<[Objects]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ObjectsClass get() = classOf<Objects>()

/**
 * 获得 [Runtime] 类型
 * @return [Class]<[Runtime]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RuntimeClass get() = classOf<Runtime>()

/**
 * 获得 [NullPointerException] 类型
 * @return [Class]<[NullPointerException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NullPointerExceptionClass get() = classOf<NullPointerException>()

/**
 * 获得 [NumberFormatException] 类型
 * @return [Class]<[NumberFormatException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NumberFormatExceptionClass get() = classOf<NumberFormatException>()

/**
 * 获得 [IllegalStateException] 类型
 * @return [Class]<[IllegalStateException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IllegalStateExceptionClass get() = classOf<IllegalStateException>()

/**
 * 获得 [RuntimeException] 类型
 * @return [Class]<[RuntimeException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RuntimeExceptionClass get() = classOf<RuntimeException>()

/**
 * 获得 [ClassNotFoundException] 类型
 * @return [Class]<[ClassNotFoundException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ClassNotFoundExceptionClass get() = classOf<ClassNotFoundException>()

/**
 * 获得 [NoClassDefFoundError] 类型
 * @return [Class]<[NoClassDefFoundError]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NoClassDefFoundErrorClass get() = classOf<NoClassDefFoundError>()

/**
 * 获得 [NoSuchMethodError] 类型
 * @return [Class]<[NoSuchMethodError]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NoSuchMethodErrorClass get() = classOf<NoSuchMethodError>()

/**
 * 获得 [NoSuchFieldError] 类型
 * @return [Class]<[NoSuchFieldError]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NoSuchFieldErrorClass get() = classOf<NoSuchFieldError>()

/**
 * 获得 [Error] 类型
 * @return [Class]<[Error]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ErrorClass get() = classOf<Error>()

/**
 * 获得 [Exception] 类型
 * @return [Class]<[Exception]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ExceptionClass get() = classOf<Exception>()

/**
 * 获得 [Throwable] 类型
 * @return [Class]<[Throwable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ThrowableClass get() = classOf<Throwable>()