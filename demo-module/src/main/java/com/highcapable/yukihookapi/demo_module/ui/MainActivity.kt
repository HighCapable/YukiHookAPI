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
import com.highcapable.yukihookapi.hook.factory.isModuleActive
import com.highcapable.yukihookapi.hook.factory.modulePrefs
import com.highcapable.yukihookapi.hook.xposed.YukiHookModuleStatus

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            moduleDemoActiveText.text = "Module is Active：$isModuleActive"
            moduleDemoFrameworkText.text = "Hook Framework：${YukiHookModuleStatus.executorName}"
            moduleDemoApiVersionText.text = "API Version：${YukiHookModuleStatus.executorVersion}"
            moduleDemoYukiHookApiVersionText.text = "YukiHookAPI Version：${YukiHookAPI.API_VERSION_NAME}(${YukiHookAPI.API_VERSION_CODE})"
            moduleDemoNewXshareText.text = "New XShare Mode：${modulePrefs.isRunInNewXShareMode}"
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