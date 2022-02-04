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
 * This file is Created by fankes on 2022/2/2.
 */
@file:Suppress("unused")

package com.highcapable.yukihookapi.hook.type

import android.app.Activity
import android.app.Application
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.res.Resources
import android.os.Bundle

/**
 * 获得 [Context] 类型
 * @return [Class]
 */
val ContextClass get() = Context::class.java

/**
 * 获得 [Activity] 类型
 * @return [Class]
 */
val ActivityClass get() = Activity::class.java

/**
 * 获得 [Service] 类型
 * @return [Class]
 */
val ServiceClass get() = Service::class.java

/**
 * 获得 [BroadcastReceiver] 类型
 * @return [Class]
 */
val BroadcastReceiverClass get() = BroadcastReceiver::class.java

/**
 * 获得 [Bundle] 类型
 * @return [Class]
 */
val BundleClass get() = Bundle::class.java

/**
 * 获得 [Resources] 类型
 * @return [Class]
 */
val ResourcesClass get() = Resources::class.java

/**
 * 获得 [Application] 类型
 * @return [Class]
 */
val ApplicationClass get() = Application::class.java