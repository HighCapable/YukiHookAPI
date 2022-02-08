/**
 * MIT License
 *
 * Copyright (C) 2022 HighCapable
 *
 * This file is part of YukiHookAPI.
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
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.highcapable.yukihookapi.demo_app.R
import com.highcapable.yukihookapi.demo_app.utils.Main

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<TextView>(R.id.app_demo_first_text).text = getFirstText()
        findViewById<TextView>(R.id.app_demo_second_text).text = secondText
        findViewById<TextView>(R.id.app_demo_third_text).text = Main("Feel real").getString()
        findViewById<TextView>(R.id.app_demo_fourth_text).text = getRegularText("Have fun day")
        findViewById<TextView>(R.id.app_demo_fifth_text).text = getDataText()
        findViewById<Button>(R.id.app_demo_button).setOnClickListener { toast() }
    }

    private val secondText = "This is a miracle"

    private fun getFirstText() = "Hello World!"

    private fun getRegularText(string: String) = string

    private fun getDataText() = "No data found"

    private fun toast() = Toast.makeText(this, "Nothing to show", Toast.LENGTH_SHORT).show()
}