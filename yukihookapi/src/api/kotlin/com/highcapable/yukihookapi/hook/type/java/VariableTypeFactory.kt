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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("unused", "KDocUnresolvedReference", "DEPRECATION", "FunctionName", "NewApi")

package com.highcapable.yukihookapi.hook.type.java

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
import java.net.http.HttpClient
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean
import java.util.function.Supplier
import java.util.zip.ZipEntry
import java.util.zip.ZipFile
import java.util.zip.ZipInputStream
import java.util.zip.ZipOutputStream

/**
 * 任意类型的数组
 *
 * Java 中的表示：array[]
 * @param type 类型
 * @return [Class]
 */
fun ArrayClass(type: Class<*>) = java.lang.reflect.Array.newInstance(type, 0).javaClass

/**
 * 获得 [Any] 类型
 * @return [Class]
 */
val AnyType get() = Any::class.java

/**
 * 获得 [Unit] 类型
 * @return [Class]
 */
val UnitType get() = Void.TYPE ?: Unit::class.java

/**
 * 获得 [Boolean] 类型
 * @return [Class]
 */
val BooleanType get() = Boolean::class.java

/**
 * 获得 [Int] 类型
 * @return [Class]
 */
val IntType get() = Int::class.javaPrimitiveType ?: UnitType

/**
 * 获得 [Long] 类型
 * @return [Class]
 */
val LongType get() = Long::class.javaPrimitiveType ?: UnitType

/**
 * 获得 [Short] 类型
 * @return [Class]
 */
val ShortType get() = Short::class.javaPrimitiveType ?: UnitType

/**
 * 获得 [Float] 类型
 * @return [Class]
 */
val FloatType get() = Float::class.javaPrimitiveType ?: UnitType

/**
 * 获得 [Double] 类型
 * @return [Class]
 */
val DoubleType get() = Double::class.javaPrimitiveType ?: UnitType

/**
 * 获得 [String] 类型
 * @return [Class]
 */
val StringType get() = String::class.java

/**
 * 获得 [Char] 类型
 * @return [Class]
 */
val CharType get() = Char::class.java

/**
 * 获得 [CharSequence] 类型
 * @return [Class]
 */
val CharSequenceType get() = CharSequence::class.java

/**
 * 获得 [Serializable] 类型
 * @return [Class]
 */
val SerializableClass get() = Serializable::class.java

/**
 * 获得 [Byte] 类型
 * @return [Class]
 */
val ByteClass get() = Byte::class.java

/**
 * 获得 [Array] 类型
 * @return [Class]
 */
val ArrayClass get() = Array::class.java

/**
 * 获得 [Any] - [Array] 类型
 *
 * Java 中表示：Object[]
 * @return [Class]
 */
val AnyArrayClass get() = ArrayClass(AnyType)

/**
 * 获得 [Byte] - [Array] 类型
 *
 * Java 中表示：byte[]
 * @return [Class]
 */
val ByteArrayClass get() = ArrayClass(ByteClass)

/**
 * 获得 [Int] - [Array] 类型
 *
 * Java 中表示：int[]
 * @return [Class]
 */
val IntArrayClass get() = ArrayClass(IntType)

/**
 * 获得 [String] - [Array] 类型
 *
 * Java 中表示：String[]
 * @return [Class]
 */
val StringArrayClass get() = ArrayClass(StringType)

/**
 * 获得 [Long] - [Array] 类型
 *
 * Java 中表示：long[]
 * @return [Class]
 */
val LongArrayClass get() = ArrayClass(LongType)

/**
 * 获得 [Short] - [Array] 类型
 *
 * Java 中表示：short[]
 * @return [Class]
 */
val ShortArrayClass get() = ArrayClass(ShortType)

/**
 * 获得 [Float] - [Array] 类型
 *
 * Java 中表示：float[]
 * @return [Class]
 */
val FloatArrayClass get() = ArrayClass(FloatType)

/**
 * 获得 [Double] - [Array] 类型
 *
 * Java 中表示：double[]
 * @return [Class]
 */
val DoubleArrayClass get() = ArrayClass(DoubleType)

/**
 * 获得 [Cloneable] 类型
 * @return [Class]
 */
val CloneableClass get() = Cloneable::class.java

/**
 * 获得 [List] 类型
 * @return [Class]
 */
val ListClass get() = List::class.java

/**
 * 获得 [ArrayList] 类型
 * @return [Class]
 */
val ArrayListClass get() = ArrayList::class.java

/**
 * 获得 [HashMap] 类型
 * @return [Class]
 */
val HashMapClass get() = HashMap::class.java

/**
 * 获得 [HashSet] 类型
 * @return [Class]
 */
val HashSetClass get() = HashSet::class.java

/**
 * 获得 [WeakHashMap] 类型
 * @return [Class]
 */
val WeakHashMapClass get() = WeakHashMap::class.java

/**
 * 获得 [WeakReference] 类型
 * @return [Class]
 */
val WeakReferenceClass get() = WeakReference::class.java

/**
 * 获得 [Enum] 类型
 * @return [Class]
 */
val EnumClass get() = Enum::class.java

/**
 * 获得 [Map] 类型
 * @return [Class]
 */
val MapClass get() = Map::class.java

/**
 * 获得 [Map.Entry] 类型
 * @return [Class]
 */
val Map_EntryClass get() = Map.Entry::class.java

/**
 * 获得 [Reference] 类型
 * @return [Class]
 */
val ReferenceClass get() = Reference::class.java

/**
 * 获得 [Vector] 类型
 * @return [Class]
 */
val VectorClass get() = Vector::class.java

/**
 * 获得 [File] 类型
 * @return [Class]
 */
val FileClass get() = File::class.java

/**
 * 获得 [InputStream] 类型
 * @return [Class]
 */
val InputStreamClass get() = InputStream::class.java

/**
 * 获得 [OutputStream] 类型
 * @return [Class]
 */
val OutputStreamClass get() = OutputStream::class.java

/**
 * 获得 [BufferedReader] 类型
 * @return [Class]
 */
val BufferedReaderClass get() = BufferedReader::class.java

/**
 * 获得 [Date] 类型
 * @return [Class]
 */
val DateClass get() = Date::class.java

/**
 * 获得 [TimeZone] 类型
 * @return [Class]
 */
val TimeZoneClass get() = TimeZone::class.java

/**
 * 获得 [SimpleDateFormat] 类型
 * @return [Class]
 */
val SimpleDateFormatClass_Java get() = SimpleDateFormat::class.java

/**
 * 获得 [Timer] 类型
 * @return [Class]
 */
val TimerClass get() = Timer::class.java

/**
 * 获得 [TimerTask] 类型
 * @return [Class]
 */
val TimerTaskClass get() = TimerTask::class.java

/**
 * 获得 [Thread] 类型
 * @return [Class]
 */
val ThreadClass get() = Thread::class.java

/**
 * 获得 [Base64] 类型
 *
 * - ❗在 Android O (26) 及以上系统加入
 * @return [Class]
 */
val Base64Class_Java get() = Base64::class.java

/**
 * 获得 [Observer] 类型
 * @return [Class]
 */
val ObserverClass get() = Observer::class.java

/**
 * 获得 [Set] 类型
 * @return [Class]
 */
val SetClass get() = Set::class.java

/**
 * 获得 [JSONObject] 类型
 * @return [Class]
 */
val JSONObjectClass get() = JSONObject::class.java

/**
 * 获得 [JSONArray] 类型
 * @return [Class]
 */
val JSONArrayClass get() = JSONArray::class.java

/**
 * 获得 [StringBuilder] 类型
 * @return [Class]
 */
val StringBuilderClass get() = StringBuilder::class.java

/**
 * 获得 [StringBuffer] 类型
 * @return [Class]
 */
val StringBufferClass get() = StringBuffer::class.java

/**
 * 获得 [ZipEntry] 类型
 * @return [Class]
 */
val ZipEntryClass get() = ZipEntry::class.java

/**
 * 获得 [ZipFile] 类型
 * @return [Class]
 */
val ZipFileClass get() = ZipFile::class.java

/**
 * 获得 [ZipInputStream] 类型
 * @return [Class]
 */
val ZipInputStreamClass get() = ZipInputStream::class.java

/**
 * 获得 [ZipOutputStream] 类型
 * @return [Class]
 */
val ZipOutputStreamClass get() = ZipOutputStream::class.java

/**
 * 获得 [HttpURLConnection] 类型
 * @return [Class]
 */
val HttpURLConnectionClass get() = HttpURLConnection::class.java

/**
 * 获得 [HttpCookie] 类型
 * @return [Class]
 */
val HttpCookieClass get() = HttpCookie::class.java

/**
 * 获得 [HttpClient] 类型
 * @return [Class]
 */
val HttpClientClass get() = HttpClient::class.java

/**
 * 获得 [AtomicBoolean] 类型
 * @return [Class]
 */
val AtomicBooleanClass get() = AtomicBoolean::class.java

/**
 * 获得 [Supplier] 类型
 * @return [Class]
 */
val SupplierClass get() = Supplier::class.java

/**
 * 获得 [Class] 类型
 * @return [Class]
 */
val JavaClass get() = Class::class.java

/**
 * 获得 [ClassLoader] 类型
 * @return [ClassLoader]
 */
val JavaClassLoader get() = ClassLoader::class.java

/**
 * 获得 [Method] 类型
 * @return [Class]
 */
val JavaMethodClass get() = Method::class.java

/**
 * 获得 [Field] 类型
 * @return [Class]
 */
val JavaFieldClass get() = Field::class.java

/**
 * 获得 [Constructor] 类型
 * @return [Class]
 */
val JavaConstructorClass get() = Constructor::class.java

/**
 * 获得 [Member] 类型
 * @return [Class]
 */
val JavaMemberClass get() = Member::class.java

/**
 * 获得 [Annotation] 类型
 * @return [Class]
 */
val JavaAnnotationClass get() = Annotation::class.java

/**
 * 获得 [java.util.function.Function] 类型
 * @return [Class]
 */
val FunctionClass get() = java.util.function.Function::class.java

/**
 * 获得 [Optional] 类型
 * @return [Class]
 */
val OptionalClass get() = Optional::class.java

/**
 * 获得 [OptionalInt] 类型
 * @return [Class]
 */
val OptionalIntClass get() = OptionalInt::class.java

/**
 * 获得 [OptionalLong] 类型
 * @return [Class]
 */
val OptionalLongClass get() = OptionalLong::class.java

/**
 * 获得 [OptionalDouble] 类型
 * @return [Class]
 */
val OptionalDoubleClass get() = OptionalDouble::class.java

/**
 * 获得 [Objects] 类型
 * @return [Class]
 */
val ObjectsClass get() = Objects::class.java

/**
 * 获得 [Runtime] 类型
 * @return [Class]
 */
val RuntimeClass get() = Runtime::class.java

/**
 * 获得 [NullPointerException] 类型
 * @return [Class]
 */
val NullPointerExceptionClass get() = NullPointerException::class.java

/**
 * 获得 [NumberFormatException] 类型
 * @return [Class]
 */
val NumberFormatExceptionClass get() = NumberFormatException::class.java

/**
 * 获得 [IllegalStateException] 类型
 * @return [Class]
 */
val IllegalStateExceptionClass get() = IllegalStateException::class.java

/**
 * 获得 [RuntimeException] 类型
 * @return [Class]
 */
val RuntimeExceptionClass get() = RuntimeException::class.java

/**
 * 获得 [NoSuchMethodError] 类型
 * @return [Class]
 */
val NoSuchMethodErrorClass get() = NoSuchMethodError::class.java

/**
 * 获得 [NoSuchFieldError] 类型
 * @return [Class]
 */
val NoSuchFieldErrorClass get() = NoSuchFieldError::class.java

/**
 * 获得 [Error] 类型
 * @return [Class]
 */
val ErrorClass get() = Error::class.java

/**
 * 获得 [Exception] 类型
 * @return [Class]
 */
val ExceptionClass get() = Exception::class.java

/**
 * 获得 [Throwable] 类型
 * @return [Class]
 */
val ThrowableClass get() = Throwable::class.java