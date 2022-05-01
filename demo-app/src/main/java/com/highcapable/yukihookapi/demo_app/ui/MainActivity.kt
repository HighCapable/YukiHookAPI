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
 * This file is Created by fankes on 2022/2/9.
 */
@file:Suppress("SameParameterValue")

package com.highcapable.yukihookapi.demo_app.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.highcapable.yukihookapi.demo_app.databinding.ActivityMainBinding
import com.highcapable.yukihookapi.demo_app.utils.Main

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityMainBinding.inflate(layoutInflater).apply {
            setContentView(root)
            appDemoFirstText.text = getFirstText()
            appDemoSecondText.text = secondText
            appDemoThirdText.text = Main(content = "Feel real").getString()
            appDemoFourthText.text = getRegularText(string = "Have fun day")
            appDemoFifthText.text = getDataText()
            appDemoSixthText.text = getArray(arrayOf("apple", "banana")).let { "${it[0]}, ${it[1]}" }
            appDemoSeventhText.text = Main().getTestResultFirst()
            appDemoEighthText.text = Main().getTestResultFirst(string = "Find something interesting")
            appDemoNinthText.text = Main().getTestResultLast()
            appDemoTenthText.text = Main().getTestResultLast(string = "This is the last sentence")
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