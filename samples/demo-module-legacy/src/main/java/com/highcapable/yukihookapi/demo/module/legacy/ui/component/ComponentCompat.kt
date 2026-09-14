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
 * This file is created by fankes on 2022/8/16.
 */
package com.highcapable.yukihookapi.demo.module.legacy.ui.component

import android.graphics.drawable.Drawable
import android.widget.Button
import androidx.appcompat.app.AlertDialog
import com.highcapable.betterandroid.ui.extension.component.base.getThemeAttrsColor
import com.highcapable.betterandroid.ui.extension.component.base.getThemeAttrsDrawable
import com.highcapable.betterandroid.ui.extension.view.textColor
import com.highcapable.kavaref.KavaRef.Companion.asResolver
import android.R as Android_R

/**
 * Fixed [AlertDialog] dialog button issue after injecting module's resources in some hosts.
 *
 * Reset button text color and background by reflection [Drawable].
 * @receiver [AlertDialog]
 * @return [AlertDialog]
 */
fun AlertDialog.compatStyle(): AlertDialog {
    asResolver().firstField {
        name = "mAlert"
    }.get()?.asResolver()?.apply {
        listOf(
            firstField { name = "mButtonPositive" }.get<Button>(),
            firstField { name = "mButtonNegative" }.get<Button>(),
            firstField { name = "mButtonNeutral" }.get<Button>()
        ).forEach {
            it?.background = context.getThemeAttrsDrawable(Android_R.attr.selectableItemBackground)
            it?.textColor = context.getThemeAttrsColor(Android_R.attr.colorPrimary)
        }
    }

    return this
}