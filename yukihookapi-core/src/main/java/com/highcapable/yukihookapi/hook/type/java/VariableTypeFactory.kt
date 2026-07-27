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
 * Gets an array of any type.
 *
 * It is represented as `([type])[]` in Java.
 * @param type the component type.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
fun ArrayClass(type: Class<*>) = JavaArray.newInstance(type, 0).javaClass as Class<JavaArray>

/**
 * Gets the [Any] type.
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [AnyClass].
 * @return [Class]<[Any]>
 */
@Deprecated(message = "Use the new naming method", ReplaceWith("AnyClass"))
val AnyType get() = AnyClass

/**
 * Gets the [Boolean] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `boolean`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanType get() = Boolean::class.javaPrimitiveType ?: "boolean".toClass()

/**
 * Gets the [Char] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `char`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharType get() = Char::class.javaPrimitiveType ?: "char".toClass()

/**
 * Gets the [Byte] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `byte`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteType get() = Byte::class.javaPrimitiveType ?: "byte".toClass()

/**
 * Gets the [Short] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `short`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortType get() = Short::class.javaPrimitiveType ?: "short".toClass()

/**
 * Gets the [Int] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `int`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntType get() = Int::class.javaPrimitiveType ?: "int".toClass()

/**
 * Gets the [Float] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `float`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatType get() = Float::class.javaPrimitiveType ?: "float".toClass()

/**
 * Gets the [Long] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `long`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongType get() = Long::class.javaPrimitiveType ?: "long".toClass()

/**
 * Gets the [Double] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `double`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleType get() = Double::class.javaPrimitiveType ?: "double".toClass()

/**
 * Gets the [Unit] type.
 *
 * This is a Java primitive type. Its bytecode keyword is `void`.
 * @return [Class]
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val UnitType get() = Void.TYPE ?: "void".toClass()

/**
 * Gets the [Any] type.
 *
 * It is equivalent to [java.lang.Object] in Java.
 * @return [Class]<[Any]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnyClass get() = classOf<Any>()

/**
 * Gets the [Boolean] type.
 *
 * It is equivalent to [java.lang.Boolean] in Java.
 * @return [Class]<[Boolean]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanClass get() = classOf<Boolean>()

/**
 * Gets the [Char] type.
 *
 * It is equivalent to [java.lang.Character] in Java.
 * @return [Class]<[Char]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharClass get() = classOf<Char>()

/**
 * Gets the [Byte] type.
 *
 * It is equivalent to [java.lang.Byte] in Java.
 * @return [Class]<[Byte]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteClass get() = classOf<Byte>()

/**
 * Gets the [Short] type.
 *
 * It is equivalent to [java.lang.Short] in Java.
 * @return [Class]<[Short]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortClass get() = classOf<Short>()

/**
 * Gets the [Int] type.
 *
 * It is equivalent to [java.lang.Integer] in Java.
 * @return [Class]<[Int]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntClass get() = classOf<Int>()

/**
 * Gets the [Float] type.
 *
 * It is equivalent to [java.lang.Float] in Java.
 * @return [Class]<[Float]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatClass get() = classOf<Float>()

/**
 * Gets the [Long] type.
 *
 * It is equivalent to [java.lang.Long] in Java.
 * @return [Class]<[Long]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongClass get() = classOf<Long>()

/**
 * Gets the [Double] type.
 *
 * It is equivalent to [java.lang.Double] in Java.
 * @return [Class]<[Double]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleClass get() = classOf<Double>()

/**
 * Gets the [Number] type.
 *
 * It is equivalent to [java.lang.Number] in Java.
 * @return [Class]<[Number]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NumberClass get() = classOf<Number>()

/**
 * Gets the [Unit] type.
 *
 * It is equivalent to [java.lang.Void] in Java.
 * @return [Class]<[Void]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val UnitClass get() = classOf<Void>()

/**
 * Gets the [String] type.
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [StringClass].
 * @return [Class]<[String]>
 */
@Deprecated(message = "Use the new naming method", ReplaceWith("StringClass"))
val StringType get() = StringClass

/**
 * Gets the [CharSequence] type.
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [CharSequenceClass].
 * @return [Class]<[CharSequence]>
 */
@Deprecated(message = "Use the new naming method", ReplaceWith("CharSequenceClass"))
val CharSequenceType get() = CharSequenceClass

/**
 * Gets the [String] type.
 *
 * It is equivalent to [java.lang.String] in Java.
 * @return [Class]<[String]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringClass get() = classOf<String>()

/**
 * Gets the [CharSequence] type.
 *
 * It is equivalent to [java.lang.CharSequence] in Java.
 * @return [Class]<[CharSequence]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharSequenceClass get() = classOf<CharSequence>()

/**
 * Gets the [Serializable] type.
 * @return [Class]<[Serializable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SerializableClass get() = classOf<Serializable>()

/**
 * Gets the [Array] type.
 *
 * It is equivalent to [java.lang.reflect.Array] in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArrayClass get() = classOf<JavaArray>()

/**
 * Gets the [Boolean] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `boolean[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanArrayType get() = ArrayClass(BooleanType)

/**
 * Gets the [Char] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `char[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharArrayType get() = ArrayClass(CharType)

/**
 * Gets the [Byte] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `byte[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteArrayType get() = ArrayClass(ByteType)

/**
 * Gets the [Short] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `short[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortArrayType get() = ArrayClass(ShortType)

/**
 * Gets the [Short] - [Array] type.
 *
 * - This API is deprecated and will be removed in a future version.
 *
 * - Migrate to [ShortArrayType].
 * @return [Class]<[JavaArray]>
 */
@Deprecated(message = "Use the corrected naming method", ReplaceWith("ShortArrayType"))
val ShortArraytType get() = ShortArrayType

/**
 * Gets the [Int] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `int[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntArrayType get() = ArrayClass(IntType)

/**
 * Gets the [Float] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `float[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatArrayType get() = ArrayClass(FloatType)

/**
 * Gets the [Long] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `long[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongArrayType get() = ArrayClass(LongType)

/**
 * Gets the [Double] - [Array] type.
 *
 * This is a Java primitive type array. Its bytecode keyword is `double[]`.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleArrayType get() = ArrayClass(DoubleType)

/**
 * Gets the [Any] - [Array] type.
 *
 * It is represented as `Object[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AnyArrayClass get() = ArrayClass(AnyClass)

/**
 * Gets the [Boolean] - [Array] type.
 *
 * It is represented as `Boolean[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BooleanArrayClass get() = ArrayClass(BooleanClass)

/**
 * Gets the [Char] - [Array] type.
 *
 * It is represented as `Character[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharArrayClass get() = ArrayClass(CharClass)

/**
 * Gets the [Byte] - [Array] type.
 *
 * It is represented as `Byte[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ByteArrayClass get() = ArrayClass(ByteClass)

/**
 * Gets the [Short] - [Array] type.
 *
 * It is represented as `Short[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ShortArrayClass get() = ArrayClass(ShortClass)

/**
 * Gets the [Int] - [Array] type.
 *
 * It is represented as `Integer[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IntArrayClass get() = ArrayClass(IntClass)

/**
 * Gets the [Float] - [Array] type.
 *
 * It is represented as `Float[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FloatArrayClass get() = ArrayClass(FloatClass)

/**
 * Gets the [Long] - [Array] type.
 *
 * It is represented as `Long[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val LongArrayClass get() = ArrayClass(LongClass)

/**
 * Gets the [Double] - [Array] type.
 *
 * It is represented as `Double[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DoubleArrayClass get() = ArrayClass(DoubleClass)

/**
 * Gets the [Number] - [Array] type.
 *
 * It is represented as `Number[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NumberArrayClass get() = ArrayClass(NumberClass)

/**
 * Gets the [String] - [Array] type.
 *
 * It is represented as `String[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringArrayClass get() = ArrayClass(StringClass)

/**
 * Gets the [CharSequence] - [Array] type.
 *
 * It is represented as `CharSequence[]` in Java.
 * @return [Class]<[JavaArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CharSequenceArrayClass get() = ArrayClass(CharSequenceClass)

/**
 * Gets the [Cloneable] type.
 * @return [Class]<[Cloneable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CloneableClass get() = classOf<Cloneable>()

/**
 * Gets the [List] type.
 * @return [Class]<[List]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ListClass get() = classOf<List<*>>()

/**
 * Gets the [ArrayList] type.
 * @return [Class]<[ArrayList]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ArrayListClass get() = classOf<ArrayList<*>>()

/**
 * Gets the [HashMap] type.
 * @return [Class]<[HashMap]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HashMapClass get() = classOf<HashMap<*, *>>()

/**
 * Gets the [HashSet] type.
 * @return [Class]<[HashSet]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HashSetClass get() = classOf<HashSet<*>>()

/**
 * Gets the [WeakHashMap] type.
 * @return [Class]<[WeakHashMap]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WeakHashMapClass get() = classOf<WeakHashMap<*, *>>()

/**
 * Gets the [WeakReference] type.
 * @return [Class]<[WeakReference]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val WeakReferenceClass get() = classOf<WeakReference<*>>()

/**
 * Gets the [Enum] type.
 * @return [Class]<[Enum]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val EnumClass get() = classOf<Enum<*>>()

/**
 * Gets the [Map] type.
 * @return [Class]<[Map]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MapClass get() = classOf<Map<*, *>>()

/**
 * Gets the [Map.Entry] type.
 * @return [Class]<[Map.Entry]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Map_EntryClass get() = classOf<Map.Entry<*, *>>()

/**
 * Gets the [Reference] type.
 * @return [Class]<[Reference]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ReferenceClass get() = classOf<Reference<*>>()

/**
 * Gets the [Vector] type.
 * @return [Class]<[Vector]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val VectorClass get() = classOf<Vector<*>>()

/**
 * Gets the [File] type.
 * @return [Class]<[File]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FileClass get() = classOf<File>()

/**
 * Gets the [InputStream] type.
 * @return [Class]<[InputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val InputStreamClass get() = classOf<InputStream>()

/**
 * Gets the [OutputStream] type.
 * @return [Class]<[OutputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OutputStreamClass get() = classOf<OutputStream>()

/**
 * Gets the [BufferedReader] type.
 * @return [Class]<[BufferedReader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BufferedReaderClass get() = classOf<BufferedReader>()

/**
 * Gets the [Date] type.
 * @return [Class]<[Date]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DateClass get() = classOf<Date>()

/**
 * Gets the [TimeZone] type.
 * @return [Class]<[TimeZone]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TimeZoneClass get() = classOf<TimeZone>()

/**
 * Gets the [SimpleDateFormat] type.
 * @return [Class]<[SimpleDateFormat]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SimpleDateFormatClass_Java get() = classOf<SimpleDateFormat>()

/**
 * Gets the [Timer] type.
 * @return [Class]<[Timer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TimerClass get() = classOf<Timer>()

/**
 * Gets the [TimerTask] type.
 * @return [Class]<[TimerTask]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TimerTaskClass get() = classOf<TimerTask>()

/**
 * Gets the [Thread] type.
 * @return [Class]<[Thread]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ThreadClass get() = classOf<Thread>()

/**
 * Gets the [Base64] type.
 *
 * - Available on Android O (26) and later.
 * @return [Class]<[Base64]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Base64Class_Java get() = if (Build.VERSION.SDK_INT >= 26) classOf<Base64>() else null

/**
 * Gets the [Observer] type.
 * @return [Class]<[Observer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ObserverClass get() = classOf<Observer>()

/**
 * Gets the [Set] type.
 * @return [Class]<[Set]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SetClass get() = classOf<Set<*>>()

/**
 * Gets the [JSONObject] type.
 * @return [Class]<[JSONObject]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JSONObjectClass get() = classOf<JSONObject>()

/**
 * Gets the [JSONArray] type.
 * @return [Class]<[JSONArray]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JSONArrayClass get() = classOf<JSONArray>()

/**
 * Gets the [StringBuilder] type.
 * @return [Class]<[StringBuilder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringBuilderClass get() = classOf<StringBuilder>()

/**
 * Gets the [StringBuffer] type.
 * @return [Class]<[StringBuffer]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val StringBufferClass get() = classOf<StringBuffer>()

/**
 * Gets the [ZipEntry] type.
 * @return [Class]<[ZipEntry]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipEntryClass get() = classOf<ZipEntry>()

/**
 * Gets the [ZipFile] type.
 * @return [Class]<[ZipFile]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipFileClass get() = classOf<ZipFile>()

/**
 * Gets the [ZipInputStream] type.
 * @return [Class]<[ZipInputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipInputStreamClass get() = classOf<ZipInputStream>()

/**
 * Gets the [ZipOutputStream] type.
 * @return [Class]<[ZipOutputStream]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ZipOutputStreamClass get() = classOf<ZipOutputStream>()

/**
 * Gets the [HttpURLConnection] type.
 * @return [Class]<[HttpURLConnection]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HttpURLConnectionClass get() = classOf<HttpURLConnection>()

/**
 * Gets the [HttpCookie] type.
 * @return [Class]<[HttpCookie]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HttpCookieClass get() = classOf<HttpCookie>()

/**
 * Gets the [HttpClient] type.
 * @return [Class] or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val HttpClientClass get() = "java.net.http.HttpClient".toClassOrNull()

/**
 * Gets the [AtomicBoolean] type.
 * @return [Class]<[AtomicBoolean]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val AtomicBooleanClass get() = classOf<AtomicBoolean>()

/**
 * Gets the [Supplier] type.
 * @return [Class]<[Supplier]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SupplierClass get() = classOf<Supplier<*>>()

/**
 * Gets the [Class] type.
 * @return [Class]<[Class]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaClass get() = classOf<Class<*>>()

/**
 * Gets the [ClassLoader] type.
 * @return [Class]<[ClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaClassLoader get() = classOf<ClassLoader>()

/**
 * Gets the [BaseDexClassLoader] type.
 * @return [Class]<[BaseDexClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikBaseDexClassLoader get() = classOf<BaseDexClassLoader>()

/**
 * Gets the [DexClassLoader] type.
 * @return [Class]<[DexClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikDexClassLoader get() = classOf<DexClassLoader>()

/**
 * Gets the [PathClassLoader] type.
 * @return [Class]<[PathClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikPathClassLoader get() = classOf<PathClassLoader>()

/**
 * Gets the [InMemoryDexClassLoader] type.
 * @return [Class]<[InMemoryDexClassLoader]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DalvikInMemoryDexClassLoader get() = classOf<InMemoryDexClassLoader>()

/**
 * Gets the [Method] type.
 * @return [Class]<[Method]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaMethodClass get() = classOf<Method>()

/**
 * Gets the [Field] type.
 * @return [Class]<[Field]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaFieldClass get() = classOf<Field>()

/**
 * Gets the [Constructor] type.
 * @return [Class]<[Constructor]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaConstructorClass get() = classOf<Constructor<*>>()

/**
 * Gets the [Member] type.
 * @return [Class]<[Member]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaMemberClass get() = classOf<Member>()

/**
 * Gets the [Annotation] type.
 * @return [Class]<[Annotation]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val JavaAnnotationClass get() = classOf<Annotation>()

/**
 * Gets the [java.util.function.Function] type.
 * @return [Class]<[JavaFunction]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val FunctionClass get() = classOf<JavaFunction<*, *>>()

/**
 * Gets the [Optional] type.
 * @return [Class]<[Optional]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalClass get() = classOf<Optional<*>>()

/**
 * Gets the [OptionalInt] type.
 * @return [Class]<[OptionalInt]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalIntClass get() = classOf<OptionalInt>()

/**
 * Gets the [OptionalLong] type.
 * @return [Class]<[OptionalLong]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalLongClass get() = classOf<OptionalLong>()

/**
 * Gets the [OptionalDouble] type.
 * @return [Class]<[OptionalDouble]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OptionalDoubleClass get() = classOf<OptionalDouble>()

/**
 * Gets the [Objects] type.
 * @return [Class]<[Objects]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ObjectsClass get() = classOf<Objects>()

/**
 * Gets the [Runtime] type.
 * @return [Class]<[Runtime]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RuntimeClass get() = classOf<Runtime>()

/**
 * Gets the [NullPointerException] type.
 * @return [Class]<[NullPointerException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NullPointerExceptionClass get() = classOf<NullPointerException>()

/**
 * Gets the [NumberFormatException] type.
 * @return [Class]<[NumberFormatException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NumberFormatExceptionClass get() = classOf<NumberFormatException>()

/**
 * Gets the [IllegalStateException] type.
 * @return [Class]<[IllegalStateException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IllegalStateExceptionClass get() = classOf<IllegalStateException>()

/**
 * Gets the [RuntimeException] type.
 * @return [Class]<[RuntimeException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RuntimeExceptionClass get() = classOf<RuntimeException>()

/**
 * Gets the [ClassNotFoundException] type.
 * @return [Class]<[ClassNotFoundException]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ClassNotFoundExceptionClass get() = classOf<ClassNotFoundException>()

/**
 * Gets the [NoClassDefFoundError] type.
 * @return [Class]<[NoClassDefFoundError]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NoClassDefFoundErrorClass get() = classOf<NoClassDefFoundError>()

/**
 * Gets the [NoSuchMethodError] type.
 * @return [Class]<[NoSuchMethodError]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NoSuchMethodErrorClass get() = classOf<NoSuchMethodError>()

/**
 * Gets the [NoSuchFieldError] type.
 * @return [Class]<[NoSuchFieldError]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NoSuchFieldErrorClass get() = classOf<NoSuchFieldError>()

/**
 * Gets the [Error] type.
 * @return [Class]<[Error]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ErrorClass get() = classOf<Error>()

/**
 * Gets the [Exception] type.
 * @return [Class]<[Exception]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ExceptionClass get() = classOf<Exception>()

/**
 * Gets the [Throwable] type.
 * @return [Class]<[Throwable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ThrowableClass get() = classOf<Throwable>()