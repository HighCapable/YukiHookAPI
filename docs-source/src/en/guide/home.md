# Introduce

> `YukiHookAPI` is an integrated Hook API Framework, which does not provide any Hook functions, and needs the support of Xposed related basic APIs.

## Background

This is an efficient Hook API rebuilt based on the Xposed API using `Kotlin`, and creates rich function extensions for the development of Xposed Modules.

The name is taken from ["ももくり" heroine Yuki Kurihara](https://www.bilibili.com/bangumi/play/ss5016).

Formerly the Innocent Xposed API used in [Development Learning Project](https://github.com/fankes/TMore), now renamed and open sourced.

## Usage

`YukiHookAPI` is built entirely with `Kotlin` `lambda` syntax.

Abandoning the original less friendly `XposedHelpers`, you can use it to easily create Xposed Modules and easily implement custom Hook API.

## Language Requirement

Please use `Kotlin`, the framework part of the code composition is also compatible with `Java` but the implementation of the basic Hook scene **may not work at all**.

All demo code in this document will be described using `Kotlin`, if you don't know how to use `Kotlin` then you may not be able to use `YukiHookAPI`.

Part of the Java Demo code can be found [here](https://github.com/fankes/YukiHookAPI/tree/master/samples/demo-module/src/main/java/com/highcapable/yukihookapi/demo_module/hook/java), but not recommended.

## Source of Inspiration

Previously, when we built an Xposed Module, we first needed to create an `xposed_init` file under `assets`.

Then, manually fill in your own entry class name into the file and use `XposedHelpers` to implement our Hook logic.

Since `Kotlin` is the main Android development language, this API is really not very elegant to use.

Is there any **easy to use, light, elegant** solution?

With this idea, `YukiHookAPI` was born.

Now, we only need to write a small amount of code, and all the time and expense are handed over to automation.

With `Kotlin`'s elegant `lambda` writing and `YukiHookAPI`, you can make your Hook logic more beautiful and clear.

> The following example

:::: code-group
::: code-group-item Yuki Hook API

```kotlin
@InjectYukiHookWithXposed
object HookEntry : IYukiHookXposedInit {

    override fun onHook() = encase {
        loadZygote {
            ActivityClass.hook {
                injectMember {
                    method {
                        name = "onCreate"
                        param(BundleClass)
                    }
                    beforeHook {
                        // Your code here.
                    }
                    afterHook {
                        // Your code here.
                    }
                }
            }
            resources().hook {
                injectResource {
                    conditions {
                        name = "sym_def_app_icon"
                        mipmap()
                    }
                    replaceToModuleResource(R.mipmap.ic_launcher)
                }
            }
        }
        loadApp(name = "com.android.browser") {
            ActivityClass.hook {
                injectMember {
                    method {
                        name = "onCreate"
                        param(BundleClass)
                    }
                    beforeHook {
                        // Your code here.
                    }
                    afterHook {
                        // Your code here.
                    }
                }
            }
            resources().hook {
                injectResource {
                    conditions {
                        name = "ic_launcher"
                        mipmap()
                    }
                    replaceToModuleResource(R.mipmap.ic_launcher)
                }
            }
        }
    }
}
```

:::
::: code-group-item Xposed API

```kotlin
class HookEntry : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    private lateinit var moduleResources: XModuleResources

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam) {
        moduleResources = XModuleResources.createInstance(sparam.modulePath, null)
        XResources.setSystemWideReplacement(
            "android", "mipmap", "sym_def_app_icon",
            moduleResources.fwd(R.mipmap.ic_launcher)
        )
        XposedHelpers.findAndHookMethod(
                Activity::class.java.name,
                null, "onCreate",
                Bundle::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }

                    override fun afterHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }
                })
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        if (lpparam.packageName == "com.android.browser")
            XposedHelpers.findAndHookMethod(
                Activity::class.java.name,
                lpparam.classLoader, "onCreate",
                Bundle::class.java,
                object : XC_MethodHook() {
                    override fun beforeHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }

                    override fun afterHookedMethod(param: MethodHookParam?) {
                        // Your code here.
                    }
                })
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam) {
        if (resparam.packageName == "com.android.browser")
            resparam.res.setReplacement(
                "com.android.browser", "mipmap", "ic_launcher",
                moduleResources.fwd(R.mipmap.ic_launcher)
            )
    }
}
```

:::
::::

Yes, you read that right, just needing these codes can completely replace the Xposed API to achieve the same function.

Now, with the help of the efficient and powerful `YukiHookAPI`, you can implement a very simple Xposed Module.

## Suppored Hook Framework

The following are the `Hook Framework` and Xposed framework supported by `YukiHookAPI`.

| Hook Framework                                            | ST  | Description                                                                               |
| --------------------------------------------------------- | --- | ----------------------------------------------------------------------------------------- |
| [LSPosed](https://github.com/LSPosed/LSPosed)             | ✅   | Stable use in multiple scenarios                                                          |
| [LSPatch](https://github.com/LSPosed/LSPatch)             | ⭕   | WIP after this project is improved                                                        |
| [EdXposed](https://github.com/ElderDrivers/EdXposed)      | ❎   | Maintenance has stopped, no longer recommended                                            |
| [Pine](https://github.com/canyie/pine)                    | ⭕   | Only available                                                                            |
| [SandHook](https://github.com/asLody/SandHook)            | ⭕   | Only available                                                                            |
| [Whale](https://github.com/asLody/whale)                  | ⭕   | Need [xposed-hook-based-on-whale](https://github.com/WindySha/xposed-hook-based-on-whale) |
| [YAHFA](https://github.com/PAGalaxyLab/YAHFA)             | ❗   | Need to implement the Xposed API yourself                                                 |
| [FastHook](https://github.com/turing-technician/FastHook) | ❗   | Need to implement the Xposed API yourself                                                 |
| [Epic](https://github.com/tiann/epic)                     | ❗   | Need [Dexposed](https://github.com/alibaba/dexposed) by yourself                          |
| [TaiChi](https://github.com/taichi-framework/TaiChi)      | ⭕   | Only available for Xposed Module                                                          |
| [Xposed](https://github.com/rovo89/Xposed)                | ⭕   | Recommended minimum system version is Android 7.0                                         |