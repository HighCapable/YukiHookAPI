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
 * This file is Created by fankes on 2022/2/13.
 */
@file:Suppress("unused", "KDocUnresolvedReference", "NewApi")

package com.highcapable.yukihookapi.hook.type.android

import android.graphics.*
import android.graphics.drawable.*
import android.text.*
import android.util.Size
import android.util.SizeF

/**
 * 获得 [Typeface] 类型
 * @return [Class]
 */
val TypefaceClass get() = Typeface::class.java

/**
 * 获得 [Bitmap] 类型
 * @return [Class]
 */
val BitmapClass get() = Bitmap::class.java

/**
 * 获得 [Icon] 类型
 *
 * - ❗在 Android M (23) 及以上系统加入
 * @return [Class]
 */
val IconClass get() = Icon::class.java

/**
 * 获得 [Outline] 类型
 * @return [Class]
 */
val OutlineClass get() = Outline::class.java

/**
 * 获得 [Drawable] 类型
 * @return [Class]
 */
val DrawableClass get() = Drawable::class.java

/**
 * 获得 [GradientDrawable] 类型
 * @return [Class]
 */
val GradientDrawableClass get() = GradientDrawable::class.java

/**
 * 获得 [ColorDrawable] 类型
 * @return [Class]
 */
val ColorDrawableClass get() = ColorDrawable::class.java

/**
 * 获得 [BitmapDrawable] 类型
 * @return [Class]
 */
val BitmapDrawableClass get() = BitmapDrawable::class.java

/**
 * 获得 [Size] 类型
 * @return [Class]
 */
val SizeClass get() = Size::class.java

/**
 * 获得 [SizeF] 类型
 * @return [Class]
 */
val SizeFClass get() = SizeF::class.java

/**
 * 获得 [Rect] 类型
 * @return [Class]
 */
val RectClass get() = Rect::class.java

/**
 * 获得 [RectF] 类型
 * @return [Class]
 */
val RectFClass get() = RectF::class.java

/**
 * 获得 [NinePatch] 类型
 * @return [Class]
 */
val NinePatchClass get() = NinePatch::class.java

/**
 * 获得 [Paint] 类型
 * @return [Class]
 */
val PaintClass get() = Paint::class.java

/**
 * 获得 [TextPaint] 类型
 * @return [Class]
 */
val TextPaintClass get() = TextPaint::class.java

/**
 * 获得 [Canvas] 类型
 * @return [Class]
 */
val CanvasClass get() = Canvas::class.java

/**
 * 获得 [Point] 类型
 * @return [Class]
 */
val PointClass get() = Point::class.java

/**
 * 获得 [PointF] 类型
 * @return [Class]
 */
val PointFClass get() = PointF::class.java

/**
 * 获得 [Matrix] 类型
 * @return [Class]
 */
val MatrixClass get() = Matrix::class.java

/**
 * 获得 [ColorMatrix] 类型
 * @return [Class]
 */
val ColorMatrixClass get() = ColorMatrix::class.java

/**
 * 获得 [ColorMatrixColorFilter] 类型
 * @return [Class]
 */
val ColorMatrixColorFilterClass get() = ColorMatrixColorFilter::class.java

/**
 * 获得 [Editable] 类型
 * @return [Class]
 */
val EditableClass get() = Editable::class.java

/**
 * 获得 [TextWatcher] 类型
 * @return [Class]
 */
val TextWatcherClass get() = TextWatcher::class.java

/**
 * 获得 [Editable.Factory] 类型
 * @return [Class]
 */
val Editable_FactoryClass get() = Editable.Factory::class.java

/**
 * 获得 [GetChars] 类型
 * @return [Class]
 */
val GetCharsClass get() = GetChars::class.java

/**
 * 获得 [Spannable] 类型
 * @return [Class]
 */
val SpannableClass get() = Spannable::class.java

/**
 * 获得 [BitmapFactory] 类型
 * @return [Class]
 */
val BitmapFactoryClass get() = BitmapFactory::class.java

/**
 * 获得 [BitmapFactory.Options] 类型
 * @return [Class]
 */
val BitmapFactory_OptionsClass get() = BitmapFactory.Options::class.java