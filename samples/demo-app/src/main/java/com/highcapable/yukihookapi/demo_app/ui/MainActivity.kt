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
 * This file is created by fankes on 2022/2/9.
 */
@file:Suppress("SameParameterValue", "UsePropertyAccessSyntax")

package com.highcapable.yukihookapi.demo_app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.highcapable.yukihookapi.demo_app.databinding.ActivityMainBinding
import com.highcapable.yukihookapi.demo_app.test.Main

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            appDemoFirstText.text = getFirstText()
            appDemoSecondText.text = secondText
            appDemoThirdText.text = Main("Feel real").getString()
            appDemoFourthText.text = getRegularText("Have fun day")
            appDemoFifthText.text = getDataText()
            appDemoSixthText.text = getArray(arrayOf("apple", "banana")).let { "${it[0]}, ${it[1]}" }
            appDemoSeventhText.text = Main().getTestResultFirst()
            appDemoEighthText.text = Main().getTestResultFirst("Find something interesting")
            appDemoNinthText.text = Main().getTestResultLast()
            appDemoTenthText.text = Main().getTestResultLast("This is the last sentence")
            appDemoEleventhText.text = Main().getSuperString()
            appDemoButton.setOnClickListener { toast() }
        }
    }

    private val secondText = "This is a miracle"

    private fun getArray(array: Array<String>) = array

    private fun getFirstText() = "Hello World!"

    private fun getRegularText(string: String) = string

    private fun getDataText() = "No data found"

    private fun toast() = Toast.makeText(this, "Nothing to show", Toast.LENGTH_SHORT).show()
}