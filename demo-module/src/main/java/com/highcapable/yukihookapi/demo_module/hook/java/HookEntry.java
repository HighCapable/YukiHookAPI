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
 * This file is Created by fankes on 2022/5/25.
 */
package com.highcapable.yukihookapi.demo_module.hook.java;

import android.app.Activity;
import android.os.Bundle;

import com.highcapable.yukihookapi.YukiHookAPI;
import com.highcapable.yukihookapi.hook.xposed.bridge.event.YukiXposedEvent;
import com.highcapable.yukihookapi.hook.xposed.proxy.IYukiHookXposedInit;

// 这里仅演示了 Java 情况下的使用方式 (仅限 Java 1.8+)
// 这里的代码仅供演示 - 并不代表今后都可以正常运行 - Demo 只会同步最新的 Kotlin 使用方法
// 建议还是使用 Kotlin 来完成 Hook 部分的编写
// 请删除下方的注释以使用此 Demo - 但要确保注释掉 Kotlin 一边的 HookEntry 的注解
// @InjectYukiHookWithXposed
public class HookEntry implements IYukiHookXposedInit {

    @Override
    public void onInit() {
        YukiHookAPI.Configs config = YukiHookAPI.Configs.INSTANCE;
        config.setDebugTag("YukiHookAPI-Demo");
        config.setDebug(true);
        config.setAllowPrintingLogs(true);
        config.setEnableModulePrefsCache(true);
        config.setEnableModuleAppResourcesCache(true);
        config.setEnableHookModuleStatus(true);
        config.setEnableDataChannel(true);
        config.setEnableMemberCache(true);
    }

    @Override
    public void onHook() {
        // 这里介绍了比较近似于 Kotlin 写法的 Java 写法 - 仅供参考
        // 在 Java 中调用 Kotlin 的 lambda 在 Unit 情况下也需要 return null
        YukiHookAPI.INSTANCE.encase(e -> {
            e.loadZygote(l -> {
                l.hook(Activity.class, true, h -> {
                    h.injectMember(h.getPRIORITY_DEFAULT(), "Default", i -> {
                        i.method(m -> {
                            m.setName("onCreate");
                            m.param(Bundle.class);
                            return null;
                        });
                        i.afterHook(a -> {
                            Activity instance = ((Activity) a.getInstance());
                            instance.setTitle(instance.getTitle() + " [Active]");
                            return null;
                        });
                        return null;
                    });
                    return null;
                });
                return null;
            });
            // 余下部分代码已略 - 可继续参考上述方式完成
            // ...
            return null;
        });
    }

    @Override
    public void onXposedEvent() {
        // 由于 Java 不支持不重写 Kotlin Interface 的部分方法
        // 所以不需要此方法这里可以不填写内容
        YukiXposedEvent event = YukiXposedEvent.INSTANCE;
        event.onInitZygote(startupParam -> {
            // 这里编写 startupParam 方法
            return null;
        });
        event.onHandleLoadPackage(loadPackageParam -> {
            // 这里编写 loadPackageParam 方法
            return null;
        });
        event.onHandleInitPackageResources(resourcesParam -> {
            // 这里编写 resourcesParam 方法
            return null;
        });
    }
}