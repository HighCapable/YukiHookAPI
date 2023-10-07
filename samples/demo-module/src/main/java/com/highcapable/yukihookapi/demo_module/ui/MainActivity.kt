/*
 * YukiHookAPI - An efficient Hook API and Xposed Module solution built in Kotlin.
 * Copyright (C) 2019-2023 HighCapable
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

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.demo_module.R
import com.highcapable.yukihookapi.demo_module.data.DataConst
import com.highcapable.yukihookapi.demo_module.databinding.ActivityMainBinding
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.prefs
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base.ModuleAppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : ModuleAppCompatActivity() {

    override val moduleTheme get() = R.style.Theme_Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            moduleEnvironment {
                dataChannel(packageName = "com.highcapable.yukihookapi.demo_app").with {
                    wait(DataConst.TEST_CN_DATA) {
                        Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                    }
                }
            }
            moduleDemoActiveText.text = "Module is Active: ${YukiHookAPI.Status.isModuleActive}"
            moduleDemoActiveZhText.text = "Xposed 模块激活状态"
            moduleDemoFrameworkText.text = "Hook Framework: ${YukiHookAPI.Status.Executor.name}"
            moduleDemoFrameworkZhText.text = "当前的 Hook 框架"
            moduleDemoApiVersionText.text = "Xposed API Version: ${YukiHookAPI.Status.Executor.apiLevel}"
            moduleDemoApiVersionZhText.text = "Xposed API 版本"
            moduleDemoYukiHookApiVersionText.text = "${YukiHookAPI.TAG} Version: ${YukiHookAPI.VERSION}"
            moduleDemoYukiHookApiVersionZhText.text = "${YukiHookAPI.TAG} 版本"
            moduleDemoNewXshareText.text =
                "${if (YukiHookAPI.Status.isXposedEnvironment) "XSharedPreferences Readable" else "New XSharedPreferences"}: ${prefs().isPreferencesAvailable}"
            moduleDemoNewXshareZhText.text =
                if (YukiHookAPI.Status.isXposedEnvironment) "XSharedPreferences 是否可用" else "New XSharedPreferences 支持状态"
            moduleDemoResHookText.text = "Support Resources Hook: ${YukiHookAPI.Status.isSupportResourcesHook}"
            moduleDemoResHookZhText.text = "资源钩子支持状态"
            moduleDemoComTimeStampText.text =
                "Compiled Time：${SimpleDateFormat.getDateTimeInstance().format(Date(YukiHookAPI.Status.compiledTimestamp))}"
            moduleDemoEditText.also {
                hostEnvironment {
                    it.isEnabled = false
                    moduleDemoButton.isEnabled = false
                }
                it.setText(prefs().get(DataConst.TEST_KV_DATA))
                moduleDemoButton.setOnClickListener { _ ->
                    moduleEnvironment {
                        if (it.text.toString().isNotEmpty()) {
                            prefs().edit { put(DataConst.TEST_KV_DATA, it.text.toString()) }
                            Toast.makeText(applicationContext, "Saved", Toast.LENGTH_SHORT).show()
                        } else Toast.makeText(applicationContext, "Please enter the text", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            moduleDemoFrgButton.setOnClickListener { startActivity(Intent(this@MainActivity, PreferenceActivity::class.java)) }
        }
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