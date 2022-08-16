# 作为 Xposed 模块使用的相关配置

> 这里介绍了 `YukiHookAPI` 作为 Xposed 模块使用的相关配置方法。

## 依赖配置

> 作为 Xposed 模块，`YukiHookAPI` 提供了一个自动处理程序。

你需要在你的 `build.gradle` 中集成 `com.highcapable.yukihookapi:ksp-xposed` 依赖的最新版本。

## 自定义处理程序

> 你可以对 `YukiHookAPI` 将如何生成 `xposed_init` 入口进行相关配置。

### InjectYukiHookWithXposed 注解

```kotlin
annotation class InjectYukiHookWithXposed(
    val sourcePath: String,
    val modulePackageName: String,
    val entryClassName: String,
    val isUsingResourcesHook: Boolean
)
```

`@InjectYukiHookWithXposed` 注解是一个标记模块 Hook 入口的重要注解。

!> `@InjectYukiHookWithXposed` 注解的 `Class` 必须实现 `IYukiHookXposedInit` 接口。

!> 在你当前项目中的所有 `Class` 标记中**只能存在一次**，若存在多个声明自动处理程序<u>会在编译时抛出异常</u>，你可以自定义其相关参数。

#### sourcePath 参数

`sourcePath` 参数决定了自动处理程序自动查找并匹配你当前项目路径的重要标识，此参数的内容为相对路径匹配，默认参数为 `src/main`。

!> 如果你的项目不在 `...app/src/main...` 或你手动使用 `sourceSets` 设置了项目路径，你就需要手动设置 `sourcePath` 参数，否则自动处理程序将无法识别你的项目路径并<u>会在编译时抛出异常</u>。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(sourcePath = "src/custom")
```

`sourcePath` 使用的文件路径分隔符写法根据 `Windows` 和 `Unix` 将自动进行识别，使用 `/` 或 `\` 均可。

#### modulePackageName 参数

`modulePackageName` 是你当前项目的包名，也就是你的模块包名，默认留空自动处理程序将自动根据你注解的 `Class` 入口类的路径进行生成。

!> 若你想使用包名自动生成，你的 Hook 入口类 `HookEntryClass` 就要遵守包名的命名规范，格式为 `包名.hook.HookEntryClass` 或 `包名.hook.子包名.HookEntryClass`。

示例模块包名 `com.example.demo`

示例 1 `com.example.demo.hook.MainHook`

示例 2 `com.example.demo.hook.custom.CustomClass`

若你不想使用此格式定义入口类的包名，例如你的包名动态的，类似使用 `productFlavors` 进行多渠道打包，你可以直接设置 `modulePackageName` 的参数。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(modulePackageName = "com.example.demo")
```

你也可以直接设置为你的 `BuildConfig.APPLICATION_ID`。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(modulePackageName = BuildConfig.APPLICATION_ID)
```

!> 只要你自定义了 `modulePackageName` 的参数，你就会在编译时收到警告。

> 示例如下

```
You set the customize module package name to "com.example.demo", please check for yourself if it is correct
```

#### entryClassName 参数

`entryClassName` 决定了自动处理程序如何生成 `xposed_init` 中的入口类名，默认会使用你的入口类包名插入 `_YukiHookXposedInit` 后缀进行生成。

假设这是你的入口类。

> 示例如下

```kotlin
@InjectYukiHookWithXposed
class HookEntry: IYukiHookXposedInit
```

Xposed 入口类处理如下。

> 示例如下

```kotlin
class HookEntry_YukiHookXposedInit: IXposedHookZygoteInit, IXposedHookLoadPackage, ...
```

编译后的类名结构如下。

> 示例如下

```
...hook.HookEntry ← 你的入口类
...hook.HookEntry_Impl ← 自动生成的 Impl 类
...hook.HookEntry_YukiHookXposedInit ← 自动生成的 Xposed 入口类
```

我们现在定义入口类名称为 `HookXposedEntry`。

> 示例如下

```kotlin
@InjectYukiHookWithXposed(entryClassName = "HookXposedEntry")
class HookEntry: IYukiHookXposedInit
```

Xposed 入口类处理如下。

> 示例如下

```kotlin
class HookXposedEntry: IXposedHookZygoteInit, IXposedHookLoadPackage, ...
```

编译后的类名结构如下。

> 示例如下

```
...hook.HookEntry ← 你的入口类
...hook.HookEntry_Impl ← 自动生成的 Impl 类
...hook.HookXposedEntry ← 自动生成的 Xposed 入口类
```

!> 你定义的 `entryClassName` 不可与 `xposed_init` 中的类名相同，否则自动处理程序<u>会在编译时抛出异常</u>。

#### isUsingResourcesHook 参数

`isUsingResourcesHook` 决定了自动处理程序是否生成针对 Resources Hook 的相关代码，此功能默认是启用的。

启用后生成的入口类将为如下所示。

> 示例如下

```kotlin
class _YukiHookXposedInit : IXposedHookZygoteInit, IXposedHookLoadPackage, IXposedHookInitPackageResources {

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
        // ...
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        // ...
    }

    override fun handleInitPackageResources(resparam: XC_InitPackageResources.InitPackageResourcesParam?) {
        // ...
    }
}
```

若你当前的项目并不需要用到 Reources Hook，可以设置 `isUsingResourcesHook = false` 来关闭自动生成。

>  示例如下

```kotlin
@InjectYukiHookWithXposed(isUsingResourcesHook = false)
```

关闭后生成的入口类将为如下所示。

> 示例如下

```kotlin
class _YukiHookXposedInit : IXposedHookZygoteInit, IXposedHookLoadPackage {

    override fun initZygote(sparam: IXposedHookZygoteInit.StartupParam?) {
        // ...
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        // ...
    }
}
```

### IYukiHookXposedInit 接口

`IYukiHookXposedInit` 接口为你的 `HookEntryClass` 必须实现的接口，这是你的模块开始 Hook 的起点。

若要了解更多可 [点击这里](api/document?id=iyukihookxposedinit-interface) 进行查看。

当你的模块被 Xposed 装载后，`onHook` 方法将会被回调，你需要在此方法中开始使用 `YukiHookAPI`。

> 基本的调用流程为 `_YukiHookXposedInit` → `IYukiHookXposedInit.onXposedEvent` → `IYukiHookXposedInit.onInit` → `IYukiHookXposedInit.onHook`

详情请参考 [API 基本配置](config/api-example)。

## 原生 Xposed API 事件

若你当前的 Xposed 模块使用了第三方的资源，但是短时间内可能无法转移它们，此时，你可以使用 `onXposedEvent` 实现监听原生 Xposed API 的全部装载事件。

> 示例如下

```kotlin
@InjectYukiHookWithXposed
class HookEntry: IYukiHookXposedInit {

    override fun onHook() {
        // Your code here.
    }

    override fun onXposedEvent() {
        // 监听原生 Xposed API 的装载事件
        YukiXposedEvent.events {
            onInitZygote {
                // it 对象即 [StartupParam]
            }
            onHandleLoadPackage {
                // it 对象即 [LoadPackageParam]
            }
            onHandleInitPackageResources {
                // it 对象即 [InitPackageResourcesParam]
            }
        }
    }
}
```

`onXposedEvent` 与 `onHook` 方法完全独立存在，互不影响，你可以继续在 `onHook` 方法中使用 `YukiHookAPI`。

若要了解更多可 [点击这里](api/document?id=onxposedevent-method) 进行查看。

<br/><br/>
[浏览下一篇 &nbsp;➡️](config/api-using.md)