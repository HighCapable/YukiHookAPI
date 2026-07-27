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
 * This file is created by fankes on 2022/2/13.
 */
@file:Suppress("unused", "KDocUnresolvedReference", "DEPRECATION", "DeprecatedCallableAddReplaceWith")

package com.highcapable.yukihookapi.hook.type.android

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Matrix
import android.graphics.NinePatch
import android.graphics.Outline
import android.graphics.Paint
import android.graphics.Point
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.Icon
import android.os.Build
import android.text.Editable
import android.text.GetChars
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.TextPaint
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Size
import android.util.SizeF
import com.highcapable.yukihookapi.hook.core.finder.ReflectionMigration
import com.highcapable.yukihookapi.hook.factory.classOf

/**
 * Gets the [Typeface] type.
 * @return [Class]<[Typeface]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TypefaceClass get() = classOf<Typeface>()

/**
 * Gets the [Bitmap] type.
 * @return [Class]<[Bitmap]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BitmapClass get() = classOf<Bitmap>()

/**
 * Gets the [Icon] type.
 *
 * - Available on Android M (23) and later.
 * @return [Class]<[Icon]> or null.
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val IconClass get() = if (Build.VERSION.SDK_INT >= 23) classOf<Icon>() else null

/**
 * Gets the [Outline] type.
 * @return [Class]<[Outline]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val OutlineClass get() = classOf<Outline>()

/**
 * Gets the [Drawable] type.
 * @return [Class]<[Drawable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val DrawableClass get() = classOf<Drawable>()

/**
 * Gets the [GradientDrawable] type.
 * @return [Class]<[GradientDrawable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val GradientDrawableClass get() = classOf<GradientDrawable>()

/**
 * Gets the [ColorDrawable] type.
 * @return [Class]<[ColorDrawable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ColorDrawableClass get() = classOf<ColorDrawable>()

/**
 * Gets the [BitmapDrawable] type.
 * @return [Class]<[BitmapDrawable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BitmapDrawableClass get() = classOf<BitmapDrawable>()

/**
 * Gets the [Size] type.
 * @return [Class]<[Size]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SizeClass get() = classOf<Size>()

/**
 * Gets the [SizeF] type.
 * @return [Class]<[SizeF]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SizeFClass get() = classOf<SizeF>()

/**
 * Gets the [Rect] type.
 * @return [Class]<[Rect]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RectClass get() = classOf<Rect>()

/**
 * Gets the [RectF] type.
 * @return [Class]<[RectF]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val RectFClass get() = classOf<RectF>()

/**
 * Gets the [NinePatch] type.
 * @return [Class]<[NinePatch]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val NinePatchClass get() = classOf<NinePatch>()

/**
 * Gets the [Paint] type.
 * @return [Class]<[Paint]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PaintClass get() = classOf<Paint>()

/**
 * Gets the [TextPaint] type.
 * @return [Class]<[TextPaint]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TextPaintClass get() = classOf<TextPaint>()

/**
 * Gets the [Canvas] type.
 * @return [Class]<[Canvas]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val CanvasClass get() = classOf<Canvas>()

/**
 * Gets the [Point] type.
 * @return [Class]<[Point]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PointClass get() = classOf<Point>()

/**
 * Gets the [PointF] type.
 * @return [Class]<[PointF]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val PointFClass get() = classOf<PointF>()

/**
 * Gets the [Matrix] type.
 * @return [Class]<[Matrix]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val MatrixClass get() = classOf<Matrix>()

/**
 * Gets the [ColorMatrix] type.
 * @return [Class]<[ColorMatrix]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ColorMatrixClass get() = classOf<ColorMatrix>()

/**
 * Gets the [ColorMatrixColorFilter] type.
 * @return [Class]<[ColorMatrixColorFilter]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val ColorMatrixColorFilterClass get() = classOf<ColorMatrixColorFilter>()

/**
 * Gets the [TextUtils] type.
 * @return [Class]<[TextUtils]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TextUtilsClass get() = classOf<TextUtils>()

/**
 * Gets the [Editable] type.
 * @return [Class]<[Editable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val EditableClass get() = classOf<Editable>()

/**
 * Gets the [TextWatcher] type.
 * @return [Class]<[TextWatcher]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val TextWatcherClass get() = classOf<TextWatcher>()

/**
 * Gets the [Editable.Factory] type.
 * @return [Class]<[Editable.Factory]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val Editable_FactoryClass get() = classOf<Editable.Factory>()

/**
 * Gets the [GetChars] type.
 * @return [Class]<[GetChars]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val GetCharsClass get() = classOf<GetChars>()

/**
 * Gets the [Spannable] type.
 * @return [Class]<[Spannable]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SpannableClass get() = classOf<Spannable>()

/**
 * Gets the [SpannableStringBuilder] type.
 * @return [Class]<[SpannableStringBuilder]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val SpannableStringBuilderClass get() = classOf<SpannableStringBuilder>()

/**
 * Gets the [BitmapFactory] type.
 * @return [Class]<[BitmapFactory]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BitmapFactoryClass get() = classOf<BitmapFactory>()

/**
 * Gets the [BitmapFactory.Options] type.
 * @return [Class]<[BitmapFactory.Options]>
 */
@Deprecated(ReflectionMigration.KAVAREF_INFO)
val BitmapFactory_OptionsClass get() = classOf<BitmapFactory.Options>()