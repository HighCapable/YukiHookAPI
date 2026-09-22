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
package com.highcapable.yukihookapi.demo.module.modern.ui

import android.os.Bundle
import android.text.TextUtils
import android.view.Gravity
import android.view.MenuItem
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.view.updateMargins
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.highcapable.betterandroid.ui.extension.component.base.getThemeAttrsColorStateList
import com.highcapable.betterandroid.ui.extension.component.startActivity
import com.highcapable.betterandroid.ui.extension.insets.factory.handleOnWindowInsetsChanged
import com.highcapable.betterandroid.ui.extension.insets.factory.setInsetsPadding
import com.highcapable.betterandroid.ui.extension.view.padding
import com.highcapable.betterandroid.ui.extension.view.textToString
import com.highcapable.betterandroid.ui.extension.view.toast
import com.highcapable.betterandroid.ui.extension.view.updateMargins
import com.highcapable.betterandroid.ui.extension.view.updateText
import com.highcapable.hikage.core.attribute.android
import com.highcapable.hikage.core.attribute.app
import com.highcapable.hikage.core.layout.LayoutParams
import com.highcapable.hikage.extension.setContentView
import com.highcapable.hikage.runtime.attribute.AttributeSetResolver
import com.highcapable.hikage.runtime.attribute.entity.AttributeResolverParams
import com.highcapable.hikage.widget.android.widget.Button
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.hikage.widget.androidx.core.widget.NestedScrollView
import com.highcapable.hikage.widget.com.google.android.material.appbar.MaterialToolbar
import com.highcapable.hikage.widget.com.google.android.material.textfield.TextInputEditText
import com.highcapable.hikage.widget.com.google.android.material.textfield.TextInputLayout
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.demo.module.modern.R
import com.highcapable.yukihookapi.demo.module.modern.data.DataConst
import com.highcapable.yukihookapi.demo.module.modern.generated.DemoProperties
import com.highcapable.yukihookapi.demo.module.modern.ui.base.BaseActivity
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.preferences
import java.text.SimpleDateFormat
import java.util.Date
import com.google.android.material.R as Material_R

class MainActivity : BaseActivity() {

    private companion object {
        const val SETTINGS_PACKAGE_NAME = "com.android.settings"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        moduleEnvironment {
            dataChannel(DemoProperties.PROJECT_SAMPLES_DEMO_APP_PACKAGE_NAME).with {
                wait(DataConst.TEST_CN_DATA) {
                    toast(it)
                }
            }
        }
        hostEnvironment {
            AttributeSetResolver.defaultResolverParams = AttributeResolverParams(
                resourcePackageName = DemoProperties.PROJECT_SAMPLES_DEMO_MODULE_MODERN_PACKAGE_NAME
            )
        }

        val hikage = setContentView {
            LinearLayout(
                lparams = LayoutParams(matchParent = true),
                init = {
                    orientation = LinearLayout.VERTICAL
                }
            ) {
                MaterialToolbar(
                    lparams = LayoutParams(widthMatchParent = true),
                    attrs = {
                        app {
                            set("title", "@string/app_name")
                        }
                    },
                    init = {
                        moduleEnvironment {
                            menu.add(R.string.demo_module_scope_title).apply {
                                setIcon(R.drawable.ic_microscope)
                                iconTintList = getThemeAttrsColorStateList(Material_R.attr.colorOnSurface)
                                padding.end = 10.dp

                                setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM)
                                setOnMenuItemClickListener {
                                    showModuleScopeDialog()
                                    true
                                }
                            }
                        }
                    }
                )
                NestedScrollView(
                    lparams = LayoutParams(matchParent = true),
                    attrs = {
                        android {
                            set("fillViewport", true)
                            set("scrollbars", "none")
                            set("requiresFadingEdge", "vertical")
                            set("fadingEdgeLength", 15.dp)
                        }
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
                            repeat(7) { index ->
                                TextView(
                                    id = "sample_title_text_$index",
                                    lparams = LayoutParams {
                                        bottomMargin = 5.dp
                                    }
                                ) {
                                    textSize = 16f
                                    isSingleLine = true
                                    ellipsize = TextUtils.TruncateAt.END
                                    gravity = Gravity.CENTER or Gravity.START
                                }
                                TextView(
                                    id = "sample_subtitle_text_$index",
                                    lparams = LayoutParams {
                                        bottomMargin = if (index < 7) 10.dp else 15.dp
                                    }
                                ) {
                                    alpha = 0.85f
                                    textSize = 13f
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
                                text = stringResource(R.string.demo_saved_text_hint)
                            }
                            TextInputLayout(
                                lparams = LayoutParams(widthMatchParent = true)
                            ) {
                                editText = TextInputEditText(
                                    lparams = LayoutParams(widthMatchParent = true)
                                ) {
                                    hint = stringResource(R.string.demo_enter_text)
                                    isSingleLine = true
                                    textSize = 18f
                                    hostEnvironment { isEnabled = false }
                                    updateText(
                                        preferences().get(
                                            DataConst.TEST_KV_DATA,
                                            stringResource(R.string.demo_test_data_default)
                                        )
                                    )
                                }
                            }
                        }
                        Button(
                            lparams = LayoutParams {
                                bottomMargin = 15.dp
                            }
                        ) {
                            text = stringResource(R.string.demo_save_test_data)
                            hostEnvironment { isEnabled = false }
                            setOnClickListener { _ ->
                                moduleEnvironment {
                                    if (editText.textToString().isNotEmpty()) {
                                        preferences().edit { put(DataConst.TEST_KV_DATA, editText.textToString()) }
                                        toast(stringResource(R.string.demo_saved))
                                    } else toast(stringResource(R.string.demo_enter_text))
                                }
                            }
                        }
                        Button {
                            text = stringResource(R.string.demo_open_preferences)
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
                            text = stringResource(
                                R.string.demo_compiled_time,
                                SimpleDateFormat.getDateTimeInstance().format(Date(YukiHookAPI.Status.compiledTimestamp))
                            )
                        }
                    }
                }
            }
        }

        hikage.root.handleOnWindowInsetsChanged(animated = true) { view, insetsWrapper ->
            view.setInsetsPadding(insetsWrapper.safeDrawing)
        }

        fun refreshStatus() {
            hikage.get<TextView>("sample_title_text_0").text = getString(R.string.demo_status_module_active, YukiHookAPI.Status.isModuleActive)
            hikage.get<TextView>("sample_subtitle_text_0").text = getString(R.string.demo_status_module_active_summary)
            hikage.get<TextView>("sample_title_text_1").text = getString(R.string.demo_status_hook_framework, YukiHookAPI.Status.Executor.name)
            hikage.get<TextView>("sample_subtitle_text_1").text = getString(R.string.demo_status_hook_framework_summary)
            hikage.get<TextView>("sample_title_text_2").text = getString(R.string.demo_status_hook_framework_version, "${YukiHookAPI.Status.Executor.version} (${YukiHookAPI.Status.Executor.versionCode})")
            hikage.get<TextView>("sample_subtitle_text_2").text = getString(R.string.demo_status_hook_framework_version_summary)
            hikage.get<TextView>("sample_title_text_3").text = getString(R.string.demo_status_xposed_api_version, YukiHookAPI.Status.Executor.apiVersion)
            hikage.get<TextView>("sample_subtitle_text_3").text = getString(R.string.demo_status_xposed_api_version_summary)
            hikage.get<TextView>("sample_title_text_4").text = getString(R.string.demo_status_yukihookapi_version, YukiHookAPI.TAG, YukiHookAPI.VERSION)
            hikage.get<TextView>("sample_subtitle_text_4").text = getString(R.string.demo_status_yukihookapi_version_summary)
            hikage.get<TextView>("sample_title_text_5").text = getString(R.string.demo_status_runtime, YukiHookAPI.Status.runtime.explicitName)
            hikage.get<TextView>("sample_subtitle_text_5").text = getString(R.string.demo_status_runtime_summary)
            hikage.get<TextView>("sample_title_text_6").text = getString(R.string.demo_status_prefs_writable, preferences().isWritable)
            hikage.get<TextView>("sample_subtitle_text_6").text = getString(R.string.demo_status_prefs_writable_summary)
        }

        moduleEnvironment {
            YukiHookAPI.Status.observeFrameworkService(lifecycle) { available ->
                if (available) refreshStatus()
            }
        }

        refreshStatus()
    }

    private fun showModuleScopeDialog() {
        if (!YukiHookAPI.Status.isXposedModuleActive) {
            toast(getString(R.string.demo_module_scope_not_active))
            return
        }

        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.demo_module_scope_title)
            .setMessage(
                getString(
                    R.string.demo_module_scope_list,
                    YukiHookAPI.Module.scope.packages.joinToString("\n")
                )
            )
            .setPositiveButton(R.string.demo_module_scope_request) { _, _ ->
                showModuleScopeRequestDialog()
            }.setNegativeButton(R.string.demo_module_scope_remove) { _, _ ->
                YukiHookAPI.Module.scope.remove(SETTINGS_PACKAGE_NAME) { succeed, error, errorMessage, _ ->
                    if (succeed) toast(getString(R.string.demo_module_scope_remove_succeeded))
                    else showModuleScopeEventResultDialog(
                        title = R.string.demo_module_scope_remove_result,
                        message = getString(
                            R.string.demo_module_scope_remove_failed,
                            errorMessage,
                            error.toString()
                        )
                    )
                }
            }.setNeutralButton(R.string.demo_dialog_close, null)
            .show()
    }

    private fun showModuleScopeRequestDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle(R.string.demo_dialog_notice)
            .setMessage(R.string.demo_module_scope_request_message)
            .setPositiveButton(R.string.demo_dialog_ok) { _, _ ->
                YukiHookAPI.Module.scope.request(SETTINGS_PACKAGE_NAME) { approved, error, errorMessage, _ ->
                    val message = if (approved) getString(R.string.demo_module_scope_request_succeeded)
                    else getString(R.string.demo_module_scope_request_failed, errorMessage, error.toString())

                    showModuleScopeEventResultDialog(R.string.demo_module_scope_request_result, message)
                }
            }.setNegativeButton(R.string.demo_dialog_cancel, null)
            .show()
    }

    private fun showModuleScopeEventResultDialog(@StringRes title: Int, message: String) {
        MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton(R.string.demo_dialog_ok, null)
            .show()
    }

    private inline fun hostEnvironment(callback: () -> Unit) {
        if (YukiHookAPI.Status.isHostEnvironment) callback()
    }

    private inline fun moduleEnvironment(callback: () -> Unit) {
        if (!YukiHookAPI.Status.isHostEnvironment) callback()
    }
}