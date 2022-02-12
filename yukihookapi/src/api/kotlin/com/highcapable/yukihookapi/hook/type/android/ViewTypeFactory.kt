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
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.type.android

import android.view.View
import android.view.ViewGroup
import android.widget.*

/**
 * 获得 [View] 类型
 * @return [Class]
 */
val ViewClass get() = View::class.java

/**
 * 获得 [ViewGroup] 类型
 * @return [Class]
 */
val ViewGroupClass get() = ViewGroup::class.java

/**
 * 获得 [TextView] 类型
 * @return [Class]
 */
val TextViewClass get() = TextView::class.java

/**
 * 获得 [ImageView] 类型
 * @return [Class]
 */
val ImageViewClass get() = ImageView::class.java

/**
 * 获得 [EditText] 类型
 * @return [Class]
 */
val EditTextClass get() = EditText::class.java

/**
 * 获得 [Button] 类型
 * @return [Class]
 */
val ButtonClass get() = Button::class.java

/**
 * 获得 [CheckBox] 类型
 * @return [Class]
 */
val CheckBoxClass get() = CheckBox::class.java

/**
 * 获得 [CompoundButton] 类型
 * @return [Class]
 */
val CompoundButtonClass get() = CompoundButton::class.java