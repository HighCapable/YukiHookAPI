/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2024 HighCapable
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