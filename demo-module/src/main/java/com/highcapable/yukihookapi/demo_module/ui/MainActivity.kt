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
 * This file is Created by fankes on 2022/1/29.
 */
@file:Suppress("SetTextI18n")

package com.highcapable.yukihookapi.demo_module.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.highcapable.yukihookapi.YukiHookAPI
import com.highcapable.yukihookapi.demo_module.data.DataConst
import com.highcapable.yukihookapi.demo_module.databinding.ActivityMainBinding
import com.highcapable.yukihookapi.hook.factory.dataChannel
import com.highcapable.yukihookapi.hook.factory.modulePrefs

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            dataChannel(packageName = "com.highcapable.yukihookapi.demo_app").with {
                wait(DataConst.TEST_CN_DATA) {
                    Toast.makeText(applicationContext, it, Toast.LENGTH_SHORT).show()
                }
            }
            moduleDemoActiveText.text = "Module is Active：${YukiHookAPI.Status.isModuleActive}"
            moduleDemoActiveZhText.text = "Xposed 模块激活状态"
            moduleDemoFrameworkText.text = "Hook Framework：${YukiHookAPI.Status.executorName}"
            moduleDemoFrameworkZhText.text = "当前的 Hook 框架"
            moduleDemoApiVersionText.text = "Xposed API Version：${YukiHookAPI.Status.executorVersion}"
            moduleDemoApiVersionZhText.text = "Xposed API 版本"
            moduleDemoYukiHookApiVersionText.text = "YukiHookAPI Version：${YukiHookAPI.API_VERSION_NAME}(${YukiHookAPI.API_VERSION_CODE})"
            moduleDemoYukiHookApiVersionZhText.text = "YukiHookAPI 版本"
            moduleDemoNewXshareText.text = "New XShare Mode：${modulePrefs.isRunInNewXShareMode}"
            moduleDemoNewXshareZhText.text = "New XShare 模式支持状态"
            moduleDemoResHookText.text = "Support Resources Hook：${YukiHookAPI.Status.isSupportResourcesHook}"
            moduleDemoResHookZhText.text = "资源钩子支持状态"
            moduleDemoEditText.also {
                it.setText(modulePrefs.get(DataConst.TEST_KV_DATA))
                moduleDemoButton.setOnClickListener { _ ->
                    if (it.text.toString().isNotEmpty()) {
                        modulePrefs.put(DataConst.TEST_KV_DATA, it.text.toString())
                        Toast.makeText(applicationContext, "Saved", Toast.LENGTH_SHORT).show()
                    } else Toast.makeText(applicationContext, "Please enter the text", Toast.LENGTH_SHORT).show()
                }
            }
            moduleDemoFrgButton.setOnClickListener { startActivity(Intent(this@MainActivity, PreferenceActivity::class.java)) }
        }
    }
}