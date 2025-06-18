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
 * This file is created by fankes on 2022/8/8.
 * Thanks for providing https://github.com/cinit/QAuxiliary/blob/main/app/src/main/java/io/github/qauxv/lifecycle/Parasitics.java
 */
package com.highcapable.yukihookapi.hook.xposed.parasitic.activity.base

import android.content.res.Configuration
import android.os.Bundle
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatActivity
import com.highcapable.yukihookapi.hook.xposed.parasitic.activity.proxy.ModuleActivity

/**
 * 代理 [AppCompatActivity]
 *
 * - 由于超类继承者的不唯一性 - 此类已弃用 - 在之后的版本中将直接被删除
 *
 * - 请现在参考并迁移到 [ModuleActivity]
 */
@Deprecated(message = "请使用新方式来实现此功能")
open class ModuleAppCompatActivity : AppCompatActivity(), ModuleActivity {

    override fun getClassLoader() = delegate.getClassLoader()

    @CallSuper
    override fun onConfigurationChanged(newConfig: Configuration) {
        delegate.onConfigurationChanged(newConfig)
        super.onConfigurationChanged(newConfig)
    }

    @CallSuper
    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        delegate.onRestoreInstanceState(savedInstanceState)
        super.onRestoreInstanceState(savedInstanceState)
    }

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        delegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }
}