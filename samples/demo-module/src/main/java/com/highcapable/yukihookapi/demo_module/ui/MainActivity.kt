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
 * This file is created by fankes on 2022/1/29.
 */
@file:Suppress("SetTextI18n")

package com.highcapable.yukihookapi.demo_module.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.updateMargins
import com.highcapable.betterandroid.ui.extension.component.startActivity
import com.highcapable.betterandroid.ui.extension.view.textToString
import com.highcapable.betterandroid.ui.extension.view.toast
import com.highcapable.betterandroid.ui.extension.view.updateMargins
import com.highcapable.hikage.extension.setContentView
import com.highcapable.hikage.widget.android.widget.Button
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.hikage.widget.androidx.core.widget.NestedScrollView
import com.highcapable.hikage.widget.com.google.android.material.appbar.MaterialToolbar
import com.highcapable.hikage.widget.com.google.android.material.textfield.TextInputEditText
import com.highcapable.hikage.widget.com.google.android.material.textfield.TextInputLayout
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.demo_module.R
import com.highcapable.yukihookapi.demo_module.data.DataConst
import com.highcapable.yukihookapi.demo_module.ui.base.BaseActivity
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.prefs
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        moduleEnvironment {
            dataChannel(packageName = "com.highcapable.yukihookapi.demo_app").with {
                wait(DataConst.TEST_CN_DATA) {
                    toast(it)
                }
            }
        }
        val hikage = setContentView {
            LinearLayout(
                lparams = LayoutParams(widthMatchParent = true),
                init = {
                    orientation = LinearLayout.VERTICAL
                }
            ) {
                MaterialToolbar(
                    lparams = LayoutParams(widthMatchParent = true),
                    init = {
                        title = stringResource(R.string.app_name)
                    }
                )
                NestedScrollView(
                    lparams = LayoutParams(matchParent = true),
                    init = {
                        isFillViewport = true
                        isVerticalScrollBarEnabled = false
                        isVerticalFadingEdgeEnabled = true
                    }
                ) {
                    LinearLayout(
                        lparams = LayoutParams(widthMatchParent = true) {
                            updateMargins(vertical = 20.dp)
                        },
                        init = {
                            orientation = LinearLayout.VERTICAL
                            gravity = Gravity.CENTER
                        }
                    ) {
                        lateinit var editText: TextView
                        LinearLayout(
                            lparams = LayoutParams(widthMatchParent = true) {
                                updateMargins(horizontal = 50.dp)
                                updateMargins(bottom = 15.dp)
                            },
                            init = {
                                orientation = LinearLayout.VERTICAL
                                gravity = Gravity.CENTER or Gravity.START
                            }
                        ) {
                            repeat(6) { index ->
                                TextView(
                                    id = "sample_title_text_$index",
                                    lparams = LayoutParams {
                                        bottomMargin = 5.dp
                                    }
                                ) {
                                    textSize = 18f
                                    isSingleLine = true
                                    ellipsize = TextUtils.TruncateAt.END
                                    gravity = Gravity.CENTER or Gravity.START
                                }
                                TextView(
                                    id = "sample_subtitle_text_$index",
                                    lparams = LayoutParams {
                                        bottomMargin = if (index < 5) 15.dp else 25.dp
                                    }
                                ) {
                                    alpha = 0.85f
                                    textSize = 15f
                                    isSingleLine = true
                                    ellipsize = TextUtils.TruncateAt.END
                                    gravity = Gravity.CENTER or Gravity.START
                                }
                            }
                            TextView(
                                lparams = LayoutParams {
                                    leftMargin = 5.dp
                                    bottomMargin = 25.dp
                                }
                            ) {
                                textSize = 15f
                                isSingleLine = true
                                ellipsize = TextUtils.TruncateAt.END
                                gravity = Gravity.CENTER or Gravity.START
                                text = "Leave something in there"
                            }
                            TextInputLayout(
                                lparams = LayoutParams(widthMatchParent = true)
                            ) {
                                editText = TextInputEditText(
                                    lparams = LayoutParams(widthMatchParent = true)
                                ) {
                                    hint = "Please enter the text"
                                    isSingleLine = true
                                    textSize = 18f
                                    hostEnvironment { isEnabled = false }
                                    setText(prefs().get(DataConst.TEST_KV_DATA))
                                }
                            }
                        }
                        Button(
                            lparams = LayoutParams {
                                bottomMargin = 15.dp
                            }
                        ) {
                            text = "Save Test Data"
                            hostEnvironment { isEnabled = false }
                            setOnClickListener { _ ->
                                moduleEnvironment {
                                    if (editText.textToString().isNotEmpty()) {
                                        prefs().edit { put(DataConst.TEST_KV_DATA, editText.textToString()) }
                                        toast("Saved")
                                    } else toast("Please enter the text")
                                }
                            }
                        }
                        Button {
                            text = "Open PreferenceFragment"
                            setOnClickListener { startActivity<PreferenceActivity>() }
                        }
                        TextView(
                            lparams = LayoutParams {
                                topMargin = 25.dp
                            }
                        ) {
                            alpha = 0.45f
                            textSize = 13f
                            isSingleLine = true
                            ellipsize = TextUtils.TruncateAt.END
                            gravity = Gravity.CENTER or Gravity.START
                            text = "Compiled Time：${SimpleDateFormat.getDateTimeInstance().format(Date(YukiHookAPI.Status.compiledTimestamp))}"
                        }
                    }
                }
            }
        }
        hikage.get<TextView>("sample_title_text_0").text = "Module is Active: ${YukiHookAPI.Status.isModuleActive}"
        hikage.get<TextView>("sample_subtitle_text_0").text = "Xposed 模块激活状态"
        hikage.get<TextView>("sample_title_text_1").text = "Hook Framework: ${YukiHookAPI.Status.Executor.name}"
        hikage.get<TextView>("sample_subtitle_text_1").text = "当前的 Hook 框架"
        hikage.get<TextView>("sample_title_text_2").text = "Xposed API Version: ${YukiHookAPI.Status.Executor.apiLevel}"
        hikage.get<TextView>("sample_subtitle_text_2").text = "Xposed API 版本"
        hikage.get<TextView>("sample_title_text_3").text = "${YukiHookAPI.TAG} Version: ${YukiHookAPI.VERSION}"
        hikage.get<TextView>("sample_subtitle_text_3").text = "${YukiHookAPI.TAG} 版本"
        hikage.get<TextView>("sample_title_text_4").text = "${if (YukiHookAPI.Status.isXposedEnvironment)
            "XSharedPreferences Readable"
        else "New XSharedPreferences"}: ${prefs().isPreferencesAvailable}"
        hikage.get<TextView>("sample_subtitle_text_4").text = if (YukiHookAPI.Status.isXposedEnvironment)
            "XSharedPreferences 是否可用"
        else "New XSharedPreferences 支持状态"
        hikage.get<TextView>("sample_title_text_5").text = "Support Resources Hook: ${YukiHookAPI.Status.isSupportResourcesHook}"
        hikage.get<TextView>("sample_subtitle_text_5").text = "资源钩子支持状态"
    }

    /**
     * Running only in (Xposed) Host environment
     *
     * 仅在 (Xposed) 宿主环境执行
     * @param callback Running in the (Xposed) Host environment / 在宿主环境执行
     */
    private inline fun hostEnvironment(callback: () -> Unit) {
        if (YukiHookAPI.Status.isXposedEnvironment) callback()
    }

    /**
     * Running only in Module environment
     *
     * 仅在模块环境执行
     * @param callback Running in the Module environment / 在模块环境执行
     */
    private inline fun moduleEnvironment(callback: () -> Unit) {
        if (YukiHookAPI.Status.isXposedEnvironment.not()) callback()
    }
}