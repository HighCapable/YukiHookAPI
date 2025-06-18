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
@file:Suppress("SetTextI18n", "SameParameterValue", "UsePropertyAccessSyntax")

package com.highcapable.yukihookapi.demo_app.ui

import android.os.Bundle
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.highcapable.betterandroid.ui.component.activity.AppViewsActivity
import com.highcapable.betterandroid.ui.extension.view.toast
import com.highcapable.betterandroid.ui.extension.view.updateMargins
import com.highcapable.hikage.extension.setContentView
import com.highcapable.hikage.widget.android.widget.Button
import com.highcapable.hikage.widget.android.widget.ImageView
import com.highcapable.hikage.widget.android.widget.LinearLayout
import com.highcapable.hikage.widget.android.widget.TextView
import com.highcapable.hikage.widget.androidx.core.widget.NestedScrollView
import com.highcapable.hikage.widget.com.google.android.material.appbar.MaterialToolbar
import com.highcapable.yukihookapi.demo_app.R
import com.highcapable.yukihookapi.demo_app.test.Main
import android.R as Android_R

class MainActivity : AppViewsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
                        ImageView(
                            lparams = LayoutParams(50.dp, 50.dp) {
                                bottomMargin = 15.dp
                            }
                        ) {
                            setImageResource(R.mipmap.ic_face_unhappy)
                        }
                        TextView(
                            lparams = LayoutParams {
                                bottomMargin = 25.dp
                            }
                        ) {
                            text = stringResource(R.string.test_string)
                            textSize = 17.5f
                        }
                        repeat(11) {
                            TextView(
                                id = "sample_text_$it",
                                lparams = LayoutParams {
                                    bottomMargin = 15.dp
                                }
                            ) {
                                text = stringResource(R.string.test_string)
                                textSize = 17.5f
                            }
                        }
                        ImageView(
                            lparams = LayoutParams(30.dp, 30.dp) {
                                bottomMargin = 15.dp
                            }
                        ) {
                            setImageResource(Android_R.mipmap.sym_def_app_icon)
                        }
                        Button {
                            text = "Click Me!"
                            setOnClickListener { toast() }
                        }
                    }
                }
            }
        }
        hikage.get<TextView>("sample_text_0").text = getFirstText()
        hikage.get<TextView>("sample_text_1").text = secondText
        hikage.get<TextView>("sample_text_2").text = Main("Feel real").getString()
        hikage.get<TextView>("sample_text_3").text = getRegularText("Have fun day")
        hikage.get<TextView>("sample_text_4").text = getDataText()
        hikage.get<TextView>("sample_text_5").text = getArray(arrayOf("apple", "banana")).let { "${it[0]}, ${it[1]}" }
        hikage.get<TextView>("sample_text_6").text = Main().getTestResultFirst()
        hikage.get<TextView>("sample_text_7").text = Main().getTestResultFirst("Find something interesting")
        hikage.get<TextView>("sample_text_8").text = Main().getTestResultLast()
        hikage.get<TextView>("sample_text_9").text = Main().getTestResultLast("This is the last sentence")
        hikage.get<TextView>("sample_text_10").text = Main().getSuperString()
    }

    private val secondText = "This is a miracle"

    private fun getArray(array: Array<String>) = array

    private fun getFirstText() = "Hello World!"

    private fun getRegularText(string: String) = string

    private fun getDataText() = "No data found"

    private fun toast() = toast("Nothing to show")
}