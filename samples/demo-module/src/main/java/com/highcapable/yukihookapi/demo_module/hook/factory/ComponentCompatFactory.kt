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
 * This file is created by fankes on 2022/8/16.
 */
package com.highcapable.yukihookapi.demo_module.hook.factory

import android.graphics.drawable.Drawable
import android.util.TypedValue
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.highcapable.yukihookapi.hook.factory.current

/**
 * Fixed [AlertDialog] dialog button issue after injecting Module App's Resources in some Host Apps
 *
 * Reset button text color and background by reflection [Drawable]
 *
 * 修复 [AlertDialog] 对话框按钮在一些宿主中注入模块资源后会发生问题
 *
 * 通过反射重新设置按钮的文字颜色和背景 [Drawable]
 * @return [AlertDialog]
 */
fun AlertDialog.compatStyle(): AlertDialog {
    current().field { name = "mAlert" }.current {
        arrayOf(
            field { name = "mButtonPositive" }.cast<Button>(),
            field { name = "mButtonNegative" }.cast<Button>(),
            field { name = "mButtonNeutral" }.cast<Button>()
        ).forEach {
            it?.setBackgroundResource(TypedValue().apply {
                context.theme.resolveAttribute(android.R.attr.selectableItemBackground, this, true)
            }.resourceId)
            it?.setTextColor(TypedValue().apply {
                context.theme.resolveAttribute(android.R.attr.colorPrimary, this, true)
            }.data)
        }
    }
    return this
}